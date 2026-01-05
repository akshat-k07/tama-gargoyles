package com.example.tama_gargoyles.games.model;

public class Cell {
    private String emoji;
    private boolean revealed;
    private boolean matched;

    public Cell(String emoji) {
        this.emoji = emoji;
        this.revealed = false;
        this.matched = false;
    }

    public String getEmoji() { return emoji; }
    public boolean isRevealed() { return revealed; }
    public void setRevealed(boolean revealed) { this.revealed = revealed; }
    public boolean isMatched() { return matched; }
    public void setMatched(boolean matched) { this.matched = matched; }
}
