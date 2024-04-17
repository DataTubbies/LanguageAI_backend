package com.example.chatgptjokes.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PexelsApiResponse {

    private int page;
    @JsonProperty("per_page")
    private int perPage;
    private List<Photo> photos;
    @JsonProperty("total_results")
    private int totalResults;
    @JsonProperty("next_page")
    private String nextPage;

    @Data
    public static class Photo {
        private int id;
        private int width;
        private int height;
        private String url;
        private String photographer;
        @JsonProperty("photographer_url")
        private String photographerUrl;
        @JsonProperty("photographer_id")
        private int photographerId;
        @JsonProperty("avg_color")
        private String avgColor;
        private Src src;
        private boolean liked;
        private String alt;

        @Data
        public static class Src {
            private String original;
            private String large2x;
            private String large;
            private String medium;
            private String small;
            private String portrait;
            private String landscape;
            private String tiny;
        }
    }
}
