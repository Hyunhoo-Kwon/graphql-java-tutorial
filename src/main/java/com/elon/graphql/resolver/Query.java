package com.elon.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.elon.graphql.model.Author;
import com.elon.graphql.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Query implements GraphQLQueryResolver {
    @Autowired
    private AuthorRepository authorRepository;

    public Iterable<Author> findAllAuthors() {
        return authorRepository.findAll();
    }

    public long countAuthors() {
        return authorRepository.count();
    }
}
