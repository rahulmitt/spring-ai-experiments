package com.interviewpedia.spring.ai.vectorstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@MappedSuperclass
@NoArgsConstructor
public abstract class HanaVectorEntity {
    @Id
    @Column(name = "_id")
    protected String _id;
//    @Column(name = "metadata")
//    protected Map<String, Object> metadata;
//    @Column(name = "embedding")
//    protected Document embedding;

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);

    }
}
