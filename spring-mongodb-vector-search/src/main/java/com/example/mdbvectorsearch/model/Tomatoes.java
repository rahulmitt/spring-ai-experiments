package com.example.mdbvectorsearch.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Data
public class Tomatoes {
    private Viewer viewer;
    private Date lastUpdated;
}
