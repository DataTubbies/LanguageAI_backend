package com.example.chatgptjokes.api;

import com.example.chatgptjokes.dtos.PexelsApiResponse;
import com.example.chatgptjokes.service.PexelsApiClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class PhotoController {

    private final PexelsApiClient pexelsApiClient;

    public PhotoController (PexelsApiClient pexelsApiClient) {
        this.pexelsApiClient = pexelsApiClient;
    }

    @GetMapping("/photos/search")
    public Mono<PexelsApiResponse> searchPhotos(@RequestParam String query, @RequestParam(defaultValue = "1") int perPage) {
        return pexelsApiClient.searchPhotos(query, perPage);
    }
}
