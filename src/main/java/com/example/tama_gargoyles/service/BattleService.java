package com.example.tama_gargoyles.service;

import com.example.tama_gargoyles.battle.BattleEngine;
import com.example.tama_gargoyles.battle.BattleMove;
import com.example.tama_gargoyles.battle.BattleState;
import com.example.tama_gargoyles.battle.RoundOutcome;
import org.springframework.stereotype.Service;

/**
 * BattleService handles the logic for each turn in a battle.
 */
@Service
public class BattleService {

    private final BattleEngine engine;

    public BattleService() {
        this.engine = new BattleEngine();
    }

    /**
     * Play a single turn of the battle.
     *
     * @param state    The session-scoped battle state.
     * @param userMove The move chosen by the user.
     */
    public void playTurn(BattleState state, BattleMove userMove) {

        // Opponent randomly picks a move
        BattleMove opponentMove = randomOpponentMove();

        // Decide round outcome
        RoundOutcome outcome = engine.decide(userMove, opponentMove);

        // Apply the round to the state (updates scores & last moves)
        state.applyRound(userMove, opponentMove, outcome);
    }

    /**
     * Simple AI: opponent randomly chooses a move.
     */
    private BattleMove randomOpponentMove() {
        BattleMove[] moves = BattleMove.values();
        int index = (int) (Math.random() * moves.length);
        return moves[index];
    }
}
