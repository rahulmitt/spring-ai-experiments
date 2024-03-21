package com.interviewpedia.spring.ai.openai.movie.controller;

import com.interviewpedia.spring.ai.openai.movie.model.Movie;
import com.interviewpedia.spring.ai.openai.movie.model.MovieList;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class MovieController {

    private final VectorStore mongoDbVectorStore;
    private final ChatClient chatClient;

    @Autowired
    public MovieController(VectorStore mongoDbVectorStore, ChatClient chatClient) {
        this.mongoDbVectorStore = mongoDbVectorStore;
        this.chatClient = chatClient;
    }

    @GetMapping("/ai/movie")
    public ResponseEntity<Movie> searchMovieByPlot(@RequestParam("plotDescription") String plotDescription) {
        BeanOutputParser<Movie> parser = new BeanOutputParser<>(Movie.class);

        List<Document> documents = this.mongoDbVectorStore.similaritySearch(plotDescription);
        String inlined = documents.stream().map(Document::getContent).collect(Collectors.joining(System.lineSeparator()));

        String promptString = """
                Based on the following: {documents}
                {format}
                """;

        PromptTemplate template = new PromptTemplate(promptString);
        template.add("documents", inlined);
        template.add("format", parser.getFormat());
        template.setOutputParser(parser);

        Prompt prompt = template.create();
        ChatResponse response = chatClient.call(prompt);
        return ResponseEntity.ok().body(parser.parse(response.getResult().getOutput().getContent()));
    }

    @GetMapping("/ai/movies")
    public ResponseEntity<Map<String, String>> performSemanticSearch(@RequestParam("plotDescription") String plotDescription) {
        UserMessage userMessage = new UserMessage(plotDescription);

        List<Document> documents = this.mongoDbVectorStore.similaritySearch(plotDescription);
        String inlined = documents.stream().map(Document::getContent).collect(Collectors.joining(System.lineSeparator()));

        Message similarDocsMessage = new SystemPromptTemplate("Based on the following: {documents}")
                .createMessage(Map.of("documents", inlined));

        Prompt prompt = new Prompt(List.of(similarDocsMessage, userMessage));
        return ResponseEntity.ok().body(Map.of("generation", chatClient.call(prompt).getResult().getOutput().getContent()));
    }

    private MovieList parseChatResponse(ChatResponse chatResponse) {
        BeanOutputParser<MovieList> parser = new BeanOutputParser<>(MovieList.class);
        return parser.parse(chatResponse.getResult().getOutput().getContent());
    }
}
