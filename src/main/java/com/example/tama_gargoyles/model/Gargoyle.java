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

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    private Integer age;

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
    public Gargoyle(User user) {
        this.user = user;
        this.type = Type.CHILD;
        this.status = Status.ACTIVE;

        // sensible defaults
        this.hunger = 100;
        this.happiness = 100;
        this.health = 100;
        this.experience = 0;
    }
}
