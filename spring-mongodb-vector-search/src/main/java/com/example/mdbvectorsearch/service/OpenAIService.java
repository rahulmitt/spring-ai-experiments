package com.example.mdbvectorsearch.service;

import com.example.mdbvectorsearch.model.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    private static final String OPENAI_API_URL = "https://api.openai.com";

    @Value("${openai.api.key}")
    private String OPENAI_API_KEY;

    private RestTemplate restTemplate = new RestTemplate();

    public List<Double> createEmbedding(String text) {
        Map<String, Object> body = Map.of(
                "model", "text-embedding-ada-002",
                "input", text
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(OPENAI_API_KEY);
        headers.set("model", "text-embedding-ada-002");
        headers.set("input", text);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<EmbeddingResponse> exchange = restTemplate.exchange(OPENAI_API_URL + "/v1/embeddings", HttpMethod.POST, entity, EmbeddingResponse.class);
        return exchange.getBody().getEmbedding();
    }
}
