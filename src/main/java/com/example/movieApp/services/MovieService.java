package com.example.movieApp.services;

import com.example.movieApp.models.Movie;
import com.example.movieApp.repositories.MovieRepository;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final Client client;

    public  MovieService(MovieRepository movieRepository, Client client) {
        this.movieRepository = movieRepository;
        this.client = client;
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie createMovie(String title, int year) {
        Movie newMovie = new Movie();
        newMovie.setTitle(title);
        newMovie.setYear(year);

        newMovie.setRating(generateRating(newMovie));
        newMovie.setDescription(generateDescription(newMovie));
        return movieRepository.save(newMovie);
    }

    public String generateDescription(Movie movie) {
        String query = "Give a description of this movie in one sentence: " + movie.getTitle() + ", " + movie.getYear();
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.0-flash-001",
                        query,
                        null);
        return response.text();
    }

    public double generateRating(Movie movie) {
        String query = "Give me the rotten tomatoes rating for the movie " + movie.getTitle() + " as a percentage, only numbers, no text";
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.0-flash-001",
                        query,
                        null);
        return Double.parseDouble(response.text());
    }
}
