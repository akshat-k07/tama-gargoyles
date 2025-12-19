package com.example.tama_gargoyles.battle;

/**
 * WHAT IT DOES:
 * - Defines the 3 battle moves.
 *
 * MAPPING TO YOUR RULES (recommended):
 * - SLAM  = Strength
 * - DASH  = Speed
 * - SNEAK = Intelligence
 *
 * Win cycle (rock-paper-scissors):
 * - SLAM beats DASH
 * - DASH beats SNEAK
 * - SNEAK beats SLAM
 */
public enum BattleMove {
    SLAM,
    SNEAK,
    DASH
}
