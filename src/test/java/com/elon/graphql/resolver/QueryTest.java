package com.elon.graphql.resolver;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
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

}
