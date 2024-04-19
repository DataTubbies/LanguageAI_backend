package com.example.chatgptjokes.api;

import com.example.chatgptjokes.dtos.MyResponse;
import com.example.chatgptjokes.dtos.TravelDto;
import com.example.chatgptjokes.service.OpenAiService;
import org.springframework.web.bind.annotation.*;

/**
 * This class handles fetching a joke via the ChatGPT API
 */
@RestController
@RequestMapping("/api/v1/travel")
@CrossOrigin(origins = "*")
public class TravelController {

    private final OpenAiService service;

    /**
     * This contains the message to the ChatGPT API, telling the AI how it should act in regard to the requests it gets.
     */
    final static String SYSTEM_MESSAGE = "You are a helpful travel planner that delivers a travel destination, transport, accommodation and activity suggestions." +
            " Try to keep the answer short to a maximum of 1000 words, and within these 4 sections: Destination, Transport, Accommodation, and Activities." +
            " Always start with the destination as the first word in a format of: city, country" +
            " The user should provide a start location, budget, number of people, destination, time of year, and duration. Destination can be optional, in which case you must provide a fitting choice." +
            " if starting location is optional, you need to only provide an activity guide" +
            " If an input is lacking anything except the destination or the starting location, ignore the content of the question and ask the user to provide the necessary inputs.";

    /**
     * The controller called from the browser client.
     *
     * @param service
     */
    public TravelController(OpenAiService service) {
        this.service = service;
    }

    /**
     * Handles the request from the browser client.
     *
     * @param about contains the input that ChatGPT uses to make a joke about.
     * @return the response from ChatGPT.
     */
    @PostMapping
    public MyResponse postTravel(@RequestBody TravelDto about) {

       String userPrompt =  service.generateUserPrompt(about);
        return service.makeRequest(userPrompt, SYSTEM_MESSAGE);
    }
}


