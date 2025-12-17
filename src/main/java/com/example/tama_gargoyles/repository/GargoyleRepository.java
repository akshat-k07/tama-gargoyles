package com.example.tama_gargoyles.repository;

import com.example.tama_gargoyles.model.Gargoyle;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface GargoyleRepository extends CrudRepository<Gargoyle, Long> {
    List<Gargoyle> findAllByUserId(Long userId);

    // Optional: nice MVP helper if you want to pick CHILD first
    List<Gargoyle> findAllByUserIdOrderByIdAsc(Long userId);
}
