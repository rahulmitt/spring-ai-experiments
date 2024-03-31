package com.interviewpedia.spring.ai.openai.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class CricketWorldCupMongoController {
    private final VectorStore mongoDbVectorStore;

    private final VectorStore simpleVectorStore;

    private final ChatClient chatClient;

    @Autowired
    public CricketWorldCupMongoController(ChatClient chatClient, VectorStore mongoDbVectorStore, VectorStore simpleVectorStore) {
        this.chatClient = chatClient;
        this.mongoDbVectorStore = mongoDbVectorStore;
        this.simpleVectorStore = simpleVectorStore;
    }

    @PostMapping("/ai/simple-vector-store/cricket-world-cup/upload")
    public ResponseEntity<String> simpleVectorStoreFileUpload(@RequestParam("pdf") MultipartFile file) throws IOException {
        Resource pdf = file.getResource();
        Supplier<List<Document>> reader = new PagePdfDocumentReader(pdf);
        Function<List<Document>, List<Document>> splitter = new TokenTextSplitter();
        List<Document> documents = splitter.apply(reader.get());
        log.info("{} documents created from pdf file: {}", documents.size(), pdf.getFilename());
        simpleVectorStore.accept(documents);
        return ResponseEntity.ok().body(String.format("%d documents created from pdf file: %s",
                documents.size(), pdf.getFilename()));
    }

    @GetMapping("/ai/simple-vector-store/cricket-world-cup")
    public Map<String, String> simpleVectorStoreSearch(@RequestParam(value = "message") String message) {

        var documents = this.simpleVectorStore.similaritySearch(message);
        var inlined = documents.stream().map(Document::getContent).collect(Collectors.joining(System.lineSeparator()));
        var similarDocsMessage = new SystemPromptTemplate("Based on the following: {documents}")
                .createMessage(Map.of("documents", inlined));

        var userMessage = new UserMessage(message);
        Prompt prompt = new Prompt(List.of(similarDocsMessage, userMessage));
        return Map.of("generation", chatClient.call(prompt).getResult().getOutput().getContent());
    }

    @PostMapping("/ai/mongodb-vector-store/cricket-world-cup/upload")
    public ResponseEntity<String> mongoVectorStoreFileUpload(@RequestParam("pdf") MultipartFile file) throws IOException {
        Resource pdf = file.getResource();
        Supplier<List<Document>> reader = new PagePdfDocumentReader(pdf);
        Function<List<Document>, List<Document>> splitter = new TokenTextSplitter();
        List<Document> documents = splitter.apply(reader.get());
        log.info("{} documents created from pdf file: {}", documents.size(), pdf.getFilename());
        mongoDbVectorStore.accept(documents);
        return ResponseEntity.ok().body(String.format("%d documents created from pdf file: %s",
                documents.size(), pdf.getFilename()));
    }

    @GetMapping("/ai/mongodb-vector-store/cricket-world-cup")
    public Map<String, String> mongodbVectorStoreSearch(@RequestParam(value = "message") String message) {

        var documents = this.mongoDbVectorStore.similaritySearch(message);
        var inlined = documents.stream().map(Document::getContent).collect(Collectors.joining(System.lineSeparator()));
        var similarDocsMessage = new SystemPromptTemplate("Based on the following: {documents}")
                .createMessage(Map.of("documents", inlined));

        var userMessage = new UserMessage(message);
        Prompt prompt = new Prompt(List.of(similarDocsMessage, userMessage));
        return Map.of("generation", chatClient.call(prompt).getResult().getOutput().getContent());
    }
}


