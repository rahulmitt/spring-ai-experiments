package com.example.mdbvectorsearch.repository;

import com.example.mdbvectorsearch.model.Movie;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.search.SearchPath;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MovieRepositoryImpl implements MovieRepository {

    @Value("${mongodb.collection.index-name}")
    private String INDEX_NAME;

    @Value("${mongodb.collection.search-field}")
    private String SEARCH_FIELD;

    private final MongoDatabase mongoDatabase;

    public MovieRepositoryImpl(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    @Override
    public List<Movie> findMoviesByVector(List<Double> embedding) {
        int numCandidates = 100;
        int limit = 5;

        List<Bson> pipeline = List.of(
                Aggregates.vectorSearch(
                        SearchPath.fieldPath(SEARCH_FIELD),
                        embedding,
                        INDEX_NAME,
                        numCandidates,
                        limit));

        AggregateIterable<Movie> aggregate = getMovieCollection().aggregate(pipeline, Movie.class);
        List<Movie> movieList = new ArrayList<>();
        for (Movie movie : aggregate) {
            movieList.add(movie);
        }

        return movieList;
    }

    private MongoCollection<Movie> getMovieCollection() {
        return mongoDatabase.getCollection("embedded_movies", Movie.class);
    }
}
