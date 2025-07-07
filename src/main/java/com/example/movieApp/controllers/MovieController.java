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

    @GetMapping("/html")
    public String getMoviesHtml() {
        String movieList = "<ul>";
        List<Movie> movies = movieRepository.findAll();
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
        Movie newMovie = new Movie();
        newMovie.setTitle(title);
        newMovie.setYear(year);
        newMovie.setRating(rating);
        newMovie.setDescription(generateDescription(newMovie));
        movieRepository.save(newMovie);
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
