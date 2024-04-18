package com.example.chatgptjokes.service;

import com.example.chatgptjokes.dtos.*;
import com.example.chatgptjokes.entity.ApiUsage;
import com.example.chatgptjokes.repository.ApiUsageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.sql.SQLOutput;
import java.util.List;

/*
This code utilizes WebClient along with several other classes from org.springframework.web.reactive.
However, the code is NOT reactive due to the use of the block() method, which bridges the reactive code (WebClient)
to our imperative code (the way we have used Spring Boot up until now).

You will not truly benefit from WebClient unless you need to make several external requests in parallel.
Additionally, the WebClient API is very clean, so if you are familiar with HTTP, it should be easy to
understand what's going on in this code.
*/

@Service
public class OpenAiService {
    public static final Logger logger = LoggerFactory.getLogger(OpenAiService.class);

    @Value("${app.api-key}")
    private String API_KEY;

    //See here for a decent explanation of the parameters send to the API via the requestBody
    //https://platform.openai.com/docs/api-reference/completions/create

    @Value("${app.url}")
    public String URL;

    @Value("${app.model}")
    public String MODEL;

    @Value("${app.temperature}")
    public double TEMPERATURE;

    @Value("${app.max_tokens}")
    public int MAX_TOKENS;

    @Value("${app.frequency_penalty}")
    public double FREQUENCY_PENALTY;

    @Value("${app.presence_penalty}")
    public double PRESENCE_PENALTY;

    @Value("${app.top_p}")
    public double TOP_P;

    private WebClient client;

    private final ApiUsageRepository apiUsageRepository;

    private final PexelsApiClient pexelsApiClient;

    public OpenAiService(ApiUsageRepository apiUsageRepository, PexelsApiClient pexelsApiClient) {
        this.client = WebClient.create();
        this.apiUsageRepository = apiUsageRepository;
        this.pexelsApiClient = pexelsApiClient;
    }

    //Use this constructor for testing, to inject a mock client
    /*
    public OpenAiService(WebClient client, ApiUsageRepository apiUsageRepository) {
        this.client = client;
        this.apiUsageRepository = apiUsageRepository;
    }
    */


    public String generateUserPrompt(TravelDto about) {

        String userPrompt;

        if (about.getDestination()==null) {
            userPrompt = "I want to travel with a budget of " +
                    about.getBudget() + "danish kroner " +
                    " for " + about.getNumberOfPeople() + " people" +
                    " from " + about.getStartingLocation() +
                    " in " + about.getMonth() +
                    " for " + about.getDuration() + " days, give me a suggestion for a destination.";

        } else if (about.getStartingLocation()==null) {
            userPrompt = "I want an activity guide with a budget of " +
                    about.getBudget() + "danish kroner " +
                    " for " + about.getNumberOfPeople() + " people" +
                    " in " + about.getDestination() +
                    " in " + about.getMonth() +
                    " for " + about.getDuration() + " days.";
        } else {
            userPrompt = "I want to travel with a budget of " +
                    about.getBudget() + "danish kroner " +
                    " for " + about.getNumberOfPeople() + " people" +
                    " to " + about.getDestination() +
                    " from " + about.getStartingLocation() +
                    " in " + about.getMonth() +
                    " for " + about.getDuration() + " days.";
        }

        return userPrompt;
    }
    public MyResponse makeRequest(String userPrompt, String _systemMessage) {

        ChatCompletionRequest requestDto = new ChatCompletionRequest();
        requestDto.setModel(MODEL);
        requestDto.setTemperature(TEMPERATURE);
        requestDto.setMax_tokens(MAX_TOKENS);
        requestDto.setTop_p(TOP_P);
        requestDto.setFrequency_penalty(FREQUENCY_PENALTY);
        requestDto.setPresence_penalty(PRESENCE_PENALTY);
        requestDto.getMessages().add(new ChatCompletionRequest.Message("system", _systemMessage));
        requestDto.getMessages().add(new ChatCompletionRequest.Message("user", userPrompt));

        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        String err = null;
        try {
            json = mapper.writeValueAsString(requestDto);
            System.out.println(json);
            ChatCompletionResponse response = client.post()
                    .uri(new URI(URL))
                    .header("Authorization", "Bearer " + API_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(json))
                    .retrieve()
                    .bodyToMono(ChatCompletionResponse.class)
                    .block();
            String responseMsg = response.getChoices().get(0).getMessage().getContent();

            String cityNameSubString = responseMsg.split(" ")[1].trim();
            String cityName = cityNameSubString.substring(0, cityNameSubString.length() - 1);

            int tokensUsed = response.getUsage().getTotal_tokens();
            System.out.print("Tokens used: " + tokensUsed);
            System.out.print(". Cost ($0.0015 / 1K tokens) : $" + String.format("%6f", (tokensUsed * 0.0015 / 1000)));
            System.out.println(". For 1$, this is the amount of similar requests you can make: " + Math.round(1 / (tokensUsed * 0.0015 / 1000)));


            ApiUsage apiUsage = new ApiUsage();
            apiUsage.setPrompt(userPrompt);
            apiUsage.setPromptTokens(requestDto.getMessages().get(1).getContent().split(" ").length);
            apiUsage.setCompletionTokens(tokensUsed);
            apiUsage.setTotalTokens(tokensUsed + requestDto.getMessages().get(0).getContent().split(" ").length);
            apiUsageRepository.save(apiUsage);

            Mono<PexelsApiResponse> responseMono = pexelsApiClient.searchPhotos(cityName, 1);
            return responseMono.flatMap(res -> {
                List<PexelsApiResponse.Photo> photos = res.getPhotos();
                String cityPhoto = photos.isEmpty() ? null : photos.get(0).getSrc().getOriginal();
                System.out.println("City photo: " + cityPhoto);
                return Mono.just(new MyResponse(responseMsg, cityPhoto));
            }).block();




        } catch (WebClientResponseException e) {
            //This is how you can get the status code and message reported back by the remote API
            logger.error("Error response status code: " + e.getRawStatusCode());
            logger.error("Error response body: " + e.getResponseBodyAsString());
            logger.error("WebClientResponseException", e);
            err = "Internal Server Error, due to a failed request to external service. You could try again" +
                    "( While you develop, make sure to consult the detailed error message on your backend)";
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, err);
        } catch (Exception e) {
            logger.error("Exception", e);
            err = "Internal Server Error - You could try again" +
                    "( While you develop, make sure to consult the detailed error message on your backend)";
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, err);
        }
    }
}
