package com.example.mdbvectorsearch.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class EmbeddingData {
    private List<Double> embedding;
}
