package com.elon.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.elon.graphql.model.Author;
import com.elon.graphql.model.Book;
import com.elon.graphql.repository.AuthorRepository;
import com.elon.graphql.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Query implements GraphQLQueryResolver {
    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    public Iterable<Author> findAllAuthors() {
        return authorRepository.findAll();
    }

    public long countAuthors() {
        return authorRepository.count();
    }

    public Iterable<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    public long countBooks() {
        return bookRepository.count();
    }
}
