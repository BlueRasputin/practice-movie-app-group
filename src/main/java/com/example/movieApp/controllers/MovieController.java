package com.example.movieApp.controllers;

import com.example.movieApp.models.Movie;
import com.example.movieApp.repositories.MovieRepository;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieRepository movieRepository;
    private final Client client;

    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
        this.client = new Client();
    }

    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.status(HttpStatus.OK).body(movieRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Movie> addMovie(@RequestBody Movie newMovie) {
        // generate the description for the movie here
        newMovie.setDescription(generateDescription(newMovie));
        return ResponseEntity.status(HttpStatus.CREATED).body(movieRepository.save(newMovie));
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
}
