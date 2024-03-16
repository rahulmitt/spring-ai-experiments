package com.interviewpedia.spring.ai.openai.worldcup;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class CricketWorldCupController {
    private final VectorStore vector;

    private final ChatClient chatClient;

    @Autowired
    public CricketWorldCupController(ChatClient chatClient, VectorStore vector) {
        this.chatClient = chatClient;
        this.vector = vector;
    }

    @GetMapping("/ai/cricket-world-cup")
    public Map<String, String> completion(@RequestParam(value = "message") String message) {

        var documents = this.vector.similaritySearch(message);
        var inlined = documents.stream().map(Document::getContent).collect(Collectors.joining(System.lineSeparator()));
        var similarDocsMessage = new SystemPromptTemplate("Based on the following: {documents}")
                .createMessage(Map.of("documents", inlined));

        var userMessage = new UserMessage(message);
        Prompt prompt = new Prompt(List.of(similarDocsMessage, userMessage));
        return Map.of("generation", chatClient.call(prompt).getResult().getOutput().getContent());
    }
}


