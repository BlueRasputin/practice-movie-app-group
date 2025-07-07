package com.example.movieApp.controllers;

import com.example.movieApp.models.Movie;
import com.example.movieApp.repositories.MovieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieRepository movieRepository;

    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.status(HttpStatus.OK).body(movieRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Movie> addMovie(@RequestBody Movie newMovie) {
        // generate the description for the movie here
        return ResponseEntity.status(HttpStatus.CREATED).body(movieRepository.save(newMovie));
    }
}
