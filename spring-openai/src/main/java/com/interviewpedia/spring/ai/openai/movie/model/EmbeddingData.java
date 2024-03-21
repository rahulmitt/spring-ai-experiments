package com.interviewpedia.spring.ai.openai.movie.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class EmbeddingData {
    private List<Double> embedding;
}
