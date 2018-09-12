package com.elon.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.elon.graphql.exception.BookNotFoundException;
import com.elon.graphql.model.Author;
import com.elon.graphql.model.Book;
import com.elon.graphql.repository.AuthorRepository;
import com.elon.graphql.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Mutation implements GraphQLMutationResolver {
    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    public Author newAuthor(String firstName, String lastName) {
        Author author = new Author(firstName, lastName);
        authorRepository.save(author);
        return author;
    }

    public Book newBook(String title, String isbn, Integer pageCount, Long authorId) {
        Book book = new Book();
        book.setAuthor(new Author(authorId));
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setPageCount(pageCount != null ? pageCount : 0);
        bookRepository.save(book);
        return book;
    }

    public boolean deleteBook(Long id) {
        bookRepository.deleteById(id);
        return true;
    }

    public Book updateBookPageCount(Integer pageCount, Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                ()->new BookNotFoundException("The book to be updated was not found", id));
        book.setPageCount(pageCount);
        bookRepository.save(book);
        return book;
    }
}
