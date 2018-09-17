package com.elon.graphql.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor @Data
public class Book {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String title;

    private String isbn;

    private int pageCount;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    private Author author;

    public Book(String title, String isbn, int pageCount, Author author) {
        this.title = title;
        this.isbn = isbn;
        this.pageCount = pageCount;
        this.author = author;
    }
}
