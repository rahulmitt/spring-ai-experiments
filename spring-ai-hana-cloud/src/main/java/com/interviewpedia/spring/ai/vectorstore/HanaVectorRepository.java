package com.interviewpedia.spring.ai.vectorstore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HanaVectorRepository<T> extends JpaRepository<T, String> {
    int save(@Param("_id") String id, @Param("embedding") String embedding, @Param("content") String content);

    int deleteEmbeddingsById(@Param("ids") List<String> idList);

    void deleteAllEmbeddings();

    List<T> cosineSimilaritySearch(@Param("topK") int topK, @Param("queryEmbedding") String queryEmbedding);
}
