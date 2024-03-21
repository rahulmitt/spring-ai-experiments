package com.interviewpedia.spring.ai.openai.movie.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Awards {
    private int wins;
    private int nominations;
    private String text;
}
