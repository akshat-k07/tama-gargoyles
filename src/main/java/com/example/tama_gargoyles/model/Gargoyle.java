package com.example.tama_gargoyles.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "gargoyles")
public class Gargoyle {

    public enum Type { BAD, GOOD, CHILD }
    public enum Status { ACTIVE, RETIRED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    private Integer age = 0;

    @Enumerated(EnumType.STRING)
    private Type type = Type.CHILD;

    @Enumerated(EnumType.STRING)
    private Status status =Status.ACTIVE;

    private Integer hunger = 100;
    private Integer happiness = 100;
    private Integer health = 100;
    private Integer experience = 0;
    private Integer strength = 25;
    private Integer speed = 10;
    private Integer intelligence = 10;

    // Keeping your existing schema fields for now
    @Column(name="last_fed")
    private Float lastFed;

    @Column(name="last_played")
    private Float lastPlayed;

    @Column(name="left_at")
    private Float leftAt;

    // --- Virtual time / pause fields ---
    @Column(nullable = false)
    private boolean paused = false;

    @Column(name = "last_tick_at", nullable = false)
    private Instant lastTickAt = Instant.now();

    @Column(name = "paused_at")
    private Instant pausedAt;

    @Column(name = "active_minutes", nullable = false)
    private long activeMinutes = 0;

    // JPA requires a no-args constructor
    public Gargoyle() {}

    // Simple MVP constructor
//    public Gargoyle(User user) {
//        this.user = user;
//        this.type = Type.CHILD;
//        this.status = Status.ACTIVE;
//
//        // sensible defaults
//        this.hunger = 100;
//        this.happiness = 100;
//        this.health = 100;
//        this.experience = 0;
//    }
}
