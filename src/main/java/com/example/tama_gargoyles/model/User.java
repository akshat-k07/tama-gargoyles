package com.example.tama_gargoyles.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


import lombok.Generated;
//@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private Integer rocks;
    private Integer bugs;
    private Integer mystery_food;

    public User() {}

    public User(String email) {
        this.username = "null";
        this.email = email;
        this.rocks = 0;
        this.bugs = 0;
        this.mystery_food = 0;
    }
}
