package com.example.tama_gargoyles.battle;

import java.io.Serializable;

public class BattleState implements Serializable {

    public static final int WIN_SCORE = 3;

    private int userScore = 0;
    private int opponentScore = 0;

    private BattleMove lastUserMove;
    private BattleMove lastOpponentMove;
    private RoundOutcome lastOutcome;

    private boolean rewardsApplied = false;

    public int getUserScore() { return userScore; }
    public int getOpponentScore() { return opponentScore; }

    public BattleMove getLastUserMove() { return lastUserMove; }
    public BattleMove getLastOpponentMove() { return lastOpponentMove; }
    public RoundOutcome getLastOutcome() { return lastOutcome; }

    public boolean isFinished() {
        return userScore >= WIN_SCORE || opponentScore >= WIN_SCORE;
    }

    public String winnerText() {
        if (!isFinished()) return null;
        return (userScore >= WIN_SCORE) ? "🎉 YOU WIN THE GAME! 🎉" : "💻 OPPONENT WINS THE GAME 💻";
    }

    public void applyRound(BattleMove userMove, BattleMove opponentMove, RoundOutcome outcome) {
        this.lastUserMove = userMove;
        this.lastOpponentMove = opponentMove;
        this.lastOutcome = outcome;

        if (outcome == RoundOutcome.USER_WINS) userScore++;
        if (outcome == RoundOutcome.OPPONENT_WINS) opponentScore++;
    }

    public boolean rewardsApplied() {
        return rewardsApplied;
    }

    public void markRewardsApplied() {
        this.rewardsApplied = true;
    }

    public void reset() {
        userScore = 0;
        opponentScore = 0;
        lastUserMove = null;
        lastOpponentMove = null;
        lastOutcome = null;
        rewardsApplied = false;
    }
}
