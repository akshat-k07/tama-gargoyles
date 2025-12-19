package com.example.tama_gargoyles.service;

import com.example.tama_gargoyles.battle.BattleEngine;
import com.example.tama_gargoyles.battle.BattleMove;
import com.example.tama_gargoyles.battle.BattleState;
import com.example.tama_gargoyles.battle.RoundOutcome;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * WHAT IT DOES:
 * - Runs a battle turn:
 *   1) accepts user's move
 *   2) generates opponent move
 *   3) uses BattleEngine to decide outcome
 *   4) updates BattleState
 */
@Service
public class BattleService {

    private final Random random = new Random();
    private final BattleEngine engine = new BattleEngine();

    public void playTurn(BattleState state, BattleMove userMove) {
        if (state.isFinished()) return;

        BattleMove opponentMove = randomMove();
        RoundOutcome outcome = engine.decide(userMove, opponentMove);

        state.applyRound(userMove, opponentMove, outcome);
    }

    private BattleMove randomMove() {
        int n = random.nextInt(3);
        return switch (n) {
            case 0 -> BattleMove.SLAM;
            case 1 -> BattleMove.SNEAK;
            default -> BattleMove.DASH;
        };
    }
}
