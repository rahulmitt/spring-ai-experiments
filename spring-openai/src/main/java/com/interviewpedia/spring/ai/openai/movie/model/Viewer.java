package com.interviewpedia.spring.ai.openai.movie.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Viewer {
    private double rating;
    private int numReviews;
}
