package com.elon.graphql.repository;

import com.elon.graphql.model.Book;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookRepositoryTest {
    @Autowired
    BookRepository bookRepository;

    @Test
    public void findAll() {
        Assert.assertNotNull(bookRepository.findAll());
    }

    @Test
    public void findOneBook() {
        assertThat(bookRepository.findOneBook((long) 1),
                anyOf(instanceOf(Book.class), nullValue()));
        assertThat(bookRepository.findOneBook((long) 10),
                anyOf(instanceOf(Book.class), nullValue()));
    }

}
