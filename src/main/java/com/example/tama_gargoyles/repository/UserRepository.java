package com.example.tama_gargoyles.repository;

import com.example.tama_gargoyles.model.User;
import com.fasterxml.jackson.databind.introspect.AnnotationCollector;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}
