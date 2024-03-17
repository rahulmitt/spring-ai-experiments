package com.example.mdbvectorsearch.repository;

import com.example.mdbvectorsearch.model.Movie;
import reactor.core.publisher.Flux;

import java.util.List;

public interface MovieRepository {
    Flux<Movie> findMoviesByVector(List<Double> embedding);
}
