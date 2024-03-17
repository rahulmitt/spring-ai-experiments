package com.example.mdbvectorsearch.service;

import com.example.mdbvectorsearch.model.Movie;
import com.example.mdbvectorsearch.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final OpenAIService embedder;

    @Autowired
    public MovieService(MovieRepository movieRepository, OpenAIService embedder) {
        this.movieRepository = movieRepository;
        this.embedder = embedder;
    }

    public List<Movie> getMoviesSemanticSearch(String plotDescription) {
        List<Double> embedding =  embedder.createEmbedding(plotDescription);
        return movieRepository.findMoviesByVector(embedding);
    }
}
