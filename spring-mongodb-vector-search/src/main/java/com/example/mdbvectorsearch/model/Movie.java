package com.example.mdbvectorsearch.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Data
public class Movie {
    @BsonProperty("_id")
    private ObjectId Id;
    private String plot;
    private List<String> genres;
    private int runtime;
    private List<String> cast;
    private String poster;
    private String title;
    private String fullplot;
    private List<String> languages;
    private LocalDate released;
    private List<String> directors;
    private String rated;
    private Awards awards;
    private String lastupdated;
    private int year;
    private Imdb imdb;
    private List<String> countries;
    private String type;
    private Tomatoes tomatoes;
    private int num_mflix_comments;
}
