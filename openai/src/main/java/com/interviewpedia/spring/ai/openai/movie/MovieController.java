package com.interviewpedia.spring.ai.openai.movie;

import org.springframework.ai.chat.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovieController {

    @Autowired
    private ChatClient chatClient;

//    @GetMapping("/movie/")
}
