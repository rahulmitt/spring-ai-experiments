package com.interviewpedia.spring.ai.openai.helloworld;

import org.springframework.ai.chat.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HelloWorldAiController {
    private final ChatClient chatClient;

    @Autowired
    public HelloWorldAiController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/ai/hello-world")
    public Map<String, String> completion(@RequestParam(value = "message") String message) {
        return Map.of("generation", chatClient.call(message));
    }
}
