package com.interviewpedia.spring.ai.openai.rag;

import com.interviewpedia.spring.ai.vectorstore.HanaVectorRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CricketWorldCupRepository extends HanaVectorRepository<CricketWorldCup> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO CRICKET_WORLD_CUP(_ID, EMBEDDING, CONTENT) VALUES " +
            "(:_id, TO_REAL_VECTOR(:embedding), :content)", nativeQuery = true)
    int save(@Param("_id") String id, @Param("embedding") String embedding, @Param("content") String content);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM CRICKET_WORLD_CUP WHERE _ID IN (:ids)", nativeQuery = true)
    int deleteAllById(@Param("ids") List<String> idList);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE CRICKET_WORLD_CUP", nativeQuery = true)
    void deleteAllEmbeddings();

    @Query(value = "SELECT TOP :topK * FROM CRICKET_WORLD_CUP ORDER BY COSINE_SIMILARITY(" +
            "EMBEDDING, TO_REAL_VECTOR(:queryEmbedding)) DESC", nativeQuery = true)
    List<CricketWorldCup> similaritySearch(@Param("topK") int topK,
                             @Param("queryEmbedding") String queryEmbedding);
}
