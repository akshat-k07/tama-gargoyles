package com.example.tama_gargoyles.repository;

import com.example.tama_gargoyles.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
