package com.elon.graphql.resolver;

import com.elon.graphql.model.Author;
import graphql.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MutationTest {
    @Autowired
    Mutation mutation;

    @Test
    public void newAuthor() {
        Author author = mutation.newAuthor("star", "bucks");
        Assert.assertNotNull(author);
    }

}
