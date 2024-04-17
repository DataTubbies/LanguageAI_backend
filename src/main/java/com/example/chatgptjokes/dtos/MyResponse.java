package com.example.chatgptjokes.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyResponse {
    String answer;
    List<Map<String, String>> messages;
    String cityPhoto;

    public MyResponse(String answer,String cityPhoto) {
        this.answer = answer;
        this.cityPhoto = cityPhoto;
    }


    public MyResponse(String answer, List<Map<String, String>> messages) {
        this.answer = answer;
        this.messages = messages;
    }
}
