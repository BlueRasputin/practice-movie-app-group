package com.example.movieApp.controllers;

import com.example.movieApp.models.Movie;
import com.example.movieApp.repositories.MovieRepository;
import com.example.movieApp.services.MovieService;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.status(HttpStatus.OK).body(movieService.getAllMovies());
    }

    @PostMapping
    public ResponseEntity<Movie> addMovie(@RequestBody Movie newMovie) {
        String title = newMovie.getTitle();
        int year = newMovie.getYear();
        double rating = newMovie.getRating();
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.createMovie(title,year,rating));
    }

    @GetMapping("/html")
    public String getMoviesHtml() {
        String movieList = "<ul>";
        List<Movie> movies = movieService.getAllMovies();
        for (Movie movie : movies) {
            movieList += "<li>" + movie + "</li>";
        }
        movieList += "</ul>";

        return """
                <html>
                    <body>
                        <h1>Movies</h1>
                        <ul>
                """ +
                movieList +
                """
                        </ul>
                    </body>
                """;
    }

    @GetMapping("/add")
    public String renderAddMovieForm() {
        return """
                <html>
                <body>
                <form action='/movies/add' method='POST'>
                <p>Enter the movie title, year, and rating:</p>
                <input type='text' name='title' placeholder='Title' />
                <input type='text' name='year' placeholder='Year' />
                <input type='text' name='rating' placeholder='Rating' />
                <button type='submit'>Submit</button>
                </form>
                </body>
                </html>
                """;
    }

    @PostMapping("/add")
    public String processAddMovieForm(
            @RequestParam(value="title") String title,
            @RequestParam(value="year") int year,
            @RequestParam(value="rating") double rating
            ) {
        movieService.createMovie(title, year, rating);
        return """
                <html>
                <body>
                <h3>MOVIE ADDED</h3>
                """ +
                "<p>You have successfully added " + title + " to the collection.</p>" +
                """
                <p>View the <a href='/movies/html'>updated list</a> of movies.</p>
                </body>
                </html>
                """;
    }
}
