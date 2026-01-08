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

    public void addRocks(Integer amount){
        Integer current = getRocks();
        if (current == null){
            setRocks(0);
            current = 0;
        }

        if (current + amount >= 0){
            setRocks(current + amount);
        }else{
            setRocks(0);
        }
    }

    public void addBugs(Integer amount){
        Integer current = getBugs();
        if (current == null){
            setBugs(0);
            current = 0;
        }

        if (current + amount >= 0){
            setBugs(current + amount);
        }else{
            setBugs(0);
        }
    }

    public void addFruits(Integer amount){
        Integer current = getFruits();
        if (current == null){
            setFruits(0);
            current = 0;
        }

        if (current + amount >= 0){
            setFruits(current + amount);
        }else{
            setFruits(0);
        }
    }

    public void addMysteryFood(Integer amount){
        Integer current = getMysteryFood();
        if (current == null){
            setMysteryFood(0);
            current = 0;
        }

        if (current + amount >= 0){
            setMysteryFood(current + amount);
        }else{
            setMysteryFood(0);
        }
    }

    public User(String email) {
        this.email = email;
    }
}
