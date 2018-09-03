package com.elon.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.elon.graphql.model.Author;
import com.elon.graphql.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Mutation implements GraphQLMutationResolver {
    @Autowired
    private AuthorRepository authorRepository;

    public Author newAuthor(String firstName, String lastName) {
        Author author = new Author(firstName, lastName);
        authorRepository.save(author);
        return author;
    }
}
