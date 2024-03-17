package com.example.mdbvectorsearch.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class Tomatoes {
    private Viewer viewer;
    private LocalDateTime lastUpdated;
}
