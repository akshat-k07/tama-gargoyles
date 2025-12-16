package com.example.tama_gargoyles.repository;

import com.example.tama_gargoyles.model.Gargoyle;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GargoyleRepository extends CrudRepository<Gargoyle, Long> {
    Optional<Gargoyle> findByUserId(Long userId);
}
