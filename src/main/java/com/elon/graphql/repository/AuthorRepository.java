package com.elon.graphql.repository;

import com.elon.graphql.model.Author;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {
    default Author findOneAuthor(Long id) {
        return findById(id).orElse(null);
    }
}
