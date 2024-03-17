package com.example.mdbvectorsearch.service;

import com.example.mdbvectorsearch.model.Movie;
import com.example.mdbvectorsearch.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final OpenAIService openAiService;

    @Autowired
    public MovieService(MovieRepository movieRepository, OpenAIService openAiService) {
        this.movieRepository = movieRepository;
        this.openAiService = openAiService;
    }

    public List<Movie> getMoviesSemanticSearch(String plotDescription) {
        List<Double> embedding =  openAiService.createEmbedding(plotDescription);
        return movieRepository.findMoviesByVector(embedding);
    }
}
