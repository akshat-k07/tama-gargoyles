package com.example.tama_gargoyles.battle;

/**
 * WHAT IT DOES:
 * - Pure rules engine for deciding the winner of a round.
 *
 * NOTE:
 * - This MVP uses "same move = draw".
 * - If you want the "same move => higher stat wins" rule later,
 *   we can extend this method to accept user/opponent stats.
 */
public class BattleEngine {

    public RoundOutcome decide(BattleMove user, BattleMove opponent) {
        if (user == opponent) return RoundOutcome.DRAW;

        // SLAM beats DASH
        if (user == BattleMove.SLAM && opponent == BattleMove.DASH) return RoundOutcome.USER_WINS;
        if (opponent == BattleMove.SLAM && user == BattleMove.DASH) return RoundOutcome.OPPONENT_WINS;

        // DASH beats SNEAK
        if (user == BattleMove.DASH && opponent == BattleMove.SNEAK) return RoundOutcome.USER_WINS;
        if (opponent == BattleMove.DASH && user == BattleMove.SNEAK) return RoundOutcome.OPPONENT_WINS;

        // SNEAK beats SLAM
        if (user == BattleMove.SNEAK && opponent == BattleMove.SLAM) return RoundOutcome.USER_WINS;
        if (opponent == BattleMove.SNEAK && user == BattleMove.SLAM) return RoundOutcome.OPPONENT_WINS;

        // Should be unreachable
        return RoundOutcome.DRAW;
    }
}
