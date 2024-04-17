package com.example.chatgptjokes.service;

import com.example.chatgptjokes.dtos.PexelsApiResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@Service
public class PexelsApiClient {
    private static final String API_KEY = "Ori5z7xYnZd2LR0CLsUp6EeKcepyH958DJVCKSGoKxOhIRGupPl2FMMO";
    private static final String BASE_URL = "https://api.pexels.com/v1/";

    private final WebClient client;

    public PexelsApiClient() {
        this.client = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("Authorization", API_KEY)
                .build();
    }

    public Mono<PexelsApiResponse> searchPhotos(String query, int perPage) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/search")
                        .queryParam("query", query)
                        .queryParam("per_page", perPage)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(PexelsApiResponse.class);
    }
}
