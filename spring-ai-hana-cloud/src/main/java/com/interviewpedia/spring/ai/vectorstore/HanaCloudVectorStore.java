package com.interviewpedia.spring.ai.vectorstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class HanaCloudVectorStore implements VectorStore {
    private final HanaVectorRepository<? extends HanaVectorEntity> repository;
    private final EmbeddingClient embeddingClient;

    public HanaCloudVectorStore(HanaVectorRepository<? extends HanaVectorEntity> repository,
                                EmbeddingClient embeddingClient) {
        this.repository = repository;
        this.embeddingClient = embeddingClient;
    }

    @Override
    public void add(List<Document> documents) {
        int count = 1;
        for (Document document : documents) {
            log.info("[{}/{}] Calling EmbeddingClient for document id = {}", count++, documents.size(), document.getId());
            String content = document.getContent().replaceAll("\\s+", " ");
            String embedding = getEmbedding(document);
            repository.save(document.getId(), embedding, content);
        }
        log.info("Embeddings saved in HanaCloudVectorStore for {} documents", count - 1);
    }

    @Override
    public Optional<Boolean> delete(List<String> idList) {
        int deleteCount = repository.deleteEmbeddingsById(idList);
        log.info("{} embeddings deleted", deleteCount);
        return Optional.of(deleteCount == idList.size());
    }

    public void purgeEmbeddings() {
        repository.deleteAllEmbeddings();
        log.info("All embeddings deleted");
    }

    @Override
    public List<Document> similaritySearch(String query) {
        return similaritySearch(SearchRequest.query(query).withTopK(1));
    }

    @Override
    public List<Document> similaritySearch(SearchRequest request) {
        String queryEmbedding = getEmbedding(request);
        List<? extends HanaVectorEntity> searchResult = repository.cosineSimilaritySearch(request.getTopK(), queryEmbedding);
        log.info("Hana cosine-similarity returned {} results for topK={}", searchResult.size(), request.getTopK());
        return searchResult.stream()
                .map(c -> {
                    try {
                        return new Document(c.get_id(), c.toJson(), Collections.emptyMap());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private String getEmbedding(SearchRequest searchRequest) {
        return "[" + this.embeddingClient.embed(searchRequest.getQuery()).stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ")) + "]";
    }

    private String getEmbedding(Document document) {
        return "[" + this.embeddingClient.embed(document).stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ")) + "]";
    }
}
