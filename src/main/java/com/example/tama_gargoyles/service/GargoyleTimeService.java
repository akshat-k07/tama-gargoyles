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
    public static final int HEALTH_DECAY_PER_MIN = 10;

    // These below are currently not functional - but could be called into play
    // if we want to have a logic where stats increase with the passage of time.
    public static final int HUNGER_RAISE_PER_MIN = 2;
    public static final int HAPPINESS_RAISE_PER_MIN = 3;
    public static final int HEALTH_RAISE_PER_MIN = 10;

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
        // Health does not decay with time unless other conditions are met
        int healthDrop = (int) (0 * HEALTH_DECAY_PER_MIN);

        // Simple “emergent” rule: if hungry, happiness falls faster.
        if (game.getHunger() < 30) {
            happinessDrop += (int) minutes; // extra -1 per minute
        }

        // Simple “emergent” rule: if hungry and unhappy, health falls.
        if (game.getHunger() < 30 && game.getHappiness() < 30) {
            healthDrop += minutes * 1; // extra -1 per minute
        }

        // Simple “emergent” rule: if hunger and happiness are  near zero, health falls rapidly.
        if (game.getHunger() < 10 && game.getHappiness() < 10) {
            healthDrop += minutes * 3; // extra -3 per minute
        }

        // Core Health improvement
        // These don't do anything yet - see the comment at the beginning of the file
        int hungerRaise = (int) (0 * HUNGER_RAISE_PER_MIN);
        int happinessRaise = (int) (0 * HAPPINESS_RAISE_PER_MIN);
        int healthRaise = (int) (0 * HEALTH_RAISE_PER_MIN);

        int BASE_HEALTH_GAIN = 1;
        int EXTRA_HEALTH_GAIN = 3;

        if (game.getHunger() > 30) {
            healthRaise += (int) (minutes * BASE_HEALTH_GAIN); // extra +1 per minute
        }
        if (game.getHappiness() > 30) {
            healthRaise += (int) (minutes * BASE_HEALTH_GAIN); // extra +1 per minute
        }
        if (game.getHunger() > 60 && game.getHappiness() > 60) {
            healthRaise += (int) (minutes * EXTRA_HEALTH_GAIN); // extra +3 per minute
        }

        // the values for hunger, happiness and health take into account both the drop and the raise
        game.setHunger(clamp01to100(game.getHunger() - hungerDrop + hungerRaise));
        game.setHappiness(clamp01to100(game.getHappiness() - happinessDrop + happinessRaise));
        game.setHealth(clamp01to100(game.getHealth() - healthDrop + healthRaise));

        Integer updatedAge = (int) Math.floor((double) game.getActiveMinutes() / MINUTES_PER_GAME_DAY);

        game.setAge(updatedAge);
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

    public boolean isBattleEligible(Gargoyle g) {
        return gameDaysOld(g) >= 3; // 5 days old = adult
    }

    public void maybePromoteToAdult(Gargoyle g) {
        if (g.getType() != Gargoyle.Type.CHILD) return;

        long daysOld = gameDaysOld(g);
        if (daysOld >= 3) {
            g.setType(Gargoyle.Type.GOOD); // or GOOD/BAD based on behaviour
        }
    }
}
