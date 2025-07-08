package com.example.movieApp;

import com.google.genai.Client;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MovieAppApplication {

	@Bean
	public Client aiApiClient() {
		return new Client();
	}

	public static void main(String[] args) {
		SpringApplication.run(MovieAppApplication.class, args);
	}

}
