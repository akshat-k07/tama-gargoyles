package com.example.tama_gargoyles.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "gargoyles")
public class Gargoyle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private enum Type {
        BAD,
        GOOD,
        CHILD
    }

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    private Integer age;

    private enum Status {
        ACTIVE,
        RETIRED
    }

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Integer hunger;
    private Integer happiness;
    private Integer health;
    private Integer experience;
    private Integer strength;
    private Integer speed;
    private Integer intelligence;
    private Float last_fed;
    private Float last_played;
    private Float left_at;

    public Gargoyle(){

    }

    public Gargoyle(User user, Type type, Status status) {
        this.user = user;
        this.type = Type.CHILD;
        this.status = Status.ACTIVE;
    }
}
