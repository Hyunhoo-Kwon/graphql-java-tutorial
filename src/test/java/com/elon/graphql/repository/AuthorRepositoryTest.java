package com.elon.graphql.repository;

import com.elon.graphql.model.Author;
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
public class AuthorRepositoryTest {
    @Autowired
    AuthorRepository authorRepository;

    @Test
    public void findAll() {
        Assert.assertNotNull(authorRepository.findAll());
    }

    @Test
    public void findOneAuthor() {
        assertThat(authorRepository.findOneAuthor((long) 1),
                anyOf(instanceOf(Author.class), nullValue()));
        assertThat(authorRepository.findOneAuthor((long) 10),
                anyOf(instanceOf(Author.class), nullValue()));
    }

}
