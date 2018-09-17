package com.elon.graphql.resolver;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QueryTest {
    @Autowired
    Query query;

    @Test
    public void findAllAuthors() {
        Assert.assertNotNull(query.findAllAuthors());
    }

    @Test
    public void countAuthors() {
        Assert.assertNotNull(query.countAuthors());
    }

    @Test
    public void findAllBooks() {
        Assert.assertNotNull(query.findAllBooks());
    }

    @Test
    public void countBooks() {
        Assert.assertNotNull(query.countBooks());
    }

}
