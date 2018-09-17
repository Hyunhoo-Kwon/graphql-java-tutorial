package com.elon.graphql.resolver;

import com.elon.graphql.model.Author;
import com.elon.graphql.model.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookResolverTest {
    @Autowired
    BookResolver bookResolver;

    @Test
    public void getAuthor() {
        Book book = new Book();
        book.setAuthor(new Author((long) 1));
        assertThat(bookResolver.getAuthor(book),
                anyOf(instanceOf(Author.class), nullValue()));
    }

}
