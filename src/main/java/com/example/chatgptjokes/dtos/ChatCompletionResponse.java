package com.example.chatgptjokes.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
 This class was completely generated by Chat GPT
 See prompt and reply here: https://chat.openai.com/share/a48933a3-73d6-4fab-b4c9-fb46d6909b81
 */
@Getter
@Setter
public class ChatCompletionResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;

    @Getter
    @Setter
    public static class Choice {
        private int index;
        private Message message;
        private String finish_reason;
    }

    @Getter
    @Setter
    public static class Message {
        private String role;
        private String content;
    }

    @Getter
    @Setter
    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;
    }
}
