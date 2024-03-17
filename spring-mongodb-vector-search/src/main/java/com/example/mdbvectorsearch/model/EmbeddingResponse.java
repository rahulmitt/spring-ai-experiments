package com.example.mdbvectorsearch.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class EmbeddingResponse {
    private List<EmbeddingData> data;

    public List<Double> getEmbedding() {
        return data.get(0).getEmbedding();
    }

}
