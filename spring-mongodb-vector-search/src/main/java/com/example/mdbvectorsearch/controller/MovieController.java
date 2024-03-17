package com.example.mdbvectorsearch.controller;

import com.example.mdbvectorsearch.model.Movie;
import com.example.mdbvectorsearch.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/movies/semantic-search")
    public ResponseEntity<List<Movie>> performSemanticSearch(@RequestParam("plotDescription") String plotDescription) {
        List<Movie> movieList = movieService.getMoviesSemanticSearch(plotDescription);
        return ResponseEntity.ok().body(movieList);
    }
}
