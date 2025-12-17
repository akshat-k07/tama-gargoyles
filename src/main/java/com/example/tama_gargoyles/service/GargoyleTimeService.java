package com.example.tama_gargoyles.service;

import com.example.tama_gargoyles.model.Gargoyle;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Service
public class GargoyleTimeService {

    // Tuning knobs (easy team contribution: balance these later)
    public static final int MINUTES_PER_GAME_DAY = 15;
    public static final int HUNGER_DECAY_PER_MIN = 2;
    public static final int HAPPINESS_DECAY_PER_MIN = 3;

    private final Clock clock;

    public GargoyleTimeService(Clock clock) {
        this.clock = clock;
    }

    /**
     * ROCK-SOLID RULE:
     * - Call resume() BEFORE tick() when a user returns.
     * - tick() should never apply "offline time" because resume() resets lastTickAt.
     */
    public void tick(Gargoyle game) {
        if (game.isPaused()) return;

        Instant now = Instant.now(clock);
        long minutes = Duration.between(game.getLastTickAt(), now).toMinutes();

        // Nothing to do if no real time has passed.
        if (minutes <= 0) return;

        // Age only increases while active (logged in / not paused).
        game.setActiveMinutes(game.getActiveMinutes() + minutes);

        // Core decay
        int hungerDrop = (int) (minutes * HUNGER_DECAY_PER_MIN);
        int happinessDrop = (int) (minutes * HAPPINESS_DECAY_PER_MIN);

        // Simple “emergent” rule: if hungry, happiness falls faster.
        if (game.getHunger() < 30) {
            happinessDrop += (int) minutes; // extra -1 per minute
        }

        game.setHunger(clamp01to100(game.getHunger() - hungerDrop));
        game.setHappiness(clamp01to100(game.getHappiness() - happinessDrop));

        // Move time forward.
        game.setLastTickAt(now);
    }

    /**
     * Freeze the game clock: no decay while paused.
     */
    public void pause(Gargoyle game) {
        if (game.isPaused()) return;
        game.setPaused(true);
        game.setPausedAt(Instant.now(clock));
    }

    /**
     * Unfreeze the game clock.
     * CRITICAL: reset lastTickAt so we do NOT apply offline time later.
     */
    public void resume(Gargoyle game) {
        if (!game.isPaused()) return;
        game.setPaused(false);
        game.setPausedAt(null);

        // THIS is the “offline gap prevention”
        game.setLastTickAt(Instant.now(clock));
    }

    public long gameDaysOld(Gargoyle game) {
        return game.getActiveMinutes() / MINUTES_PER_GAME_DAY;
    }

    public long minutesIntoCurrentDay(Gargoyle game) {
        return game.getActiveMinutes() % MINUTES_PER_GAME_DAY;
    }

    private int clamp01to100(int value) {
        if (value < 0) return 0;
        if (value > 100) return 100;
        return value;
    }
}
