package com.example.tama_gargoyles.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private Integer rocks = 10;
    private Integer bugs = 10;
    private Integer fruits = 10;
    @Column(name = "mystery_food")
    private Integer mysteryFood = 5;

    public User(String email) {
        this.email = email;
    }
}
