package com.example.movieApp.controllers;

import com.example.movieApp.models.Movie;
import com.example.movieApp.services.MovieService;
import org.apache.coyote.BadRequestException;
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

    @GetMapping("/api")
    public ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.status(HttpStatus.OK).body(movieService.getAllMovies());
    }

    @PostMapping("/api")
    public ResponseEntity<Movie> addMovie(@RequestBody Movie newMovie) {
        String title = newMovie.getTitle();
        int year = newMovie.getYear();
        double rating = newMovie.getRating();
        // error handling?
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.createMovie(title,year,rating));
    }

    @GetMapping
    public String getMoviesHtml() {
        String movieList = "<ul>";
        List<Movie> movies = movieService.getAllMovies();
        for (Movie movie : movies) {
            movieList += "<li>" + movie.toHtml() + "</li>";
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
                        <p><a href='/movies/add'>Add a movie</a></p>
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
                <input type='text' name='title' placeholder='Title' /> <br>
                <input type='text' name='year' placeholder='Year' /> <br>
                <input type='text' name='rating' placeholder='Rating' /> <br>
                <button type='submit'>Submit</button>
                </form>
                <p><a href='/movies'>View the movie list</a></p>
                </body>
                </html>
                """;
    }

    @PostMapping("/add")
    public String processAddMovieForm(
            @RequestParam(value="title") String title,
            @RequestParam(value="year") int year,
            @RequestParam(value="rating") double rating
            ) throws BadRequestException {
        // Validate input
        if (title.isEmpty()) {
            throw new BadRequestException("The fields cannot be blank");
        }
        // error handling?
        movieService.createMovie(title, year, rating);
        return """
                <html>
                <body>
                <h3>MOVIE ADDED</h3>
                """ +
                "<p>You have successfully added " + title + " to the collection.</p>" +
                """
                <p><a href='/movies'>View the updated movie list</a></p>
                <p><a href='/movies/add'>Add another movie</a></p>
                </body>
                </html>
                """;
    }
}
