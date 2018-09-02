package com.elon.graphql;

import com.elon.graphql.model.Author;
import com.elon.graphql.repository.AuthorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GraphqlApplication {

	public static void main(String[] args) {
		SpringApplication.run(GraphqlApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(AuthorRepository authorRepository) {
		return (args) -> {
			authorRepository.save(new Author("elon", "kwon"));
		};
	}

}
