package com.example.mdbvectorsearch.repository;

import com.example.mdbvectorsearch.model.Movie;

import java.util.List;

public interface MovieRepository {
    List<Movie> findMoviesByVector(List<Double> embedding);
}
