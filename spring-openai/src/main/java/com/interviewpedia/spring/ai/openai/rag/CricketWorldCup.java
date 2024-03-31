package com.interviewpedia.spring.ai.openai.rag;

import com.interviewpedia.spring.ai.vectorstore.HanaVectorEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Entity
@Table(name = "CRICKET_WORLD_CUP")
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class CricketWorldCup extends HanaVectorEntity {
    @Column(name = "content")
    private String content;
}

