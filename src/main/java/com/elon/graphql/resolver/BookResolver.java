package com.elon.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.elon.graphql.model.Author;
import com.elon.graphql.model.Book;
import com.elon.graphql.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookResolver implements GraphQLResolver<Book> {
    @Autowired
    AuthorRepository authorRepository;

    public Author getAuthor(Book book) {
        return authorRepository.findOneAuthor(book.getAuthor().getId());
    }
}
