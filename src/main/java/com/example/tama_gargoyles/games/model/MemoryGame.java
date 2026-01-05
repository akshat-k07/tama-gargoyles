package com.example.tama_gargoyles.games.model;

import java.util.*;

public class MemoryGame {
    private Map<String, Cell> cellMap = new HashMap<>();
    private long startTime;

    public MemoryGame() {
        startTime = System.currentTimeMillis();
        initializeGrid();
    }

    private void initializeGrid() {
        String[] emojis = {"😀","😎","🎮","🐱","🌟","🍕","🚀","🌈"};
        List<String> pairList = new ArrayList<>();
        for(String e : emojis){
            pairList.add(e);
            pairList.add(e);
        }
        Collections.shuffle(pairList);

        String[] rows = {"A","B","C","D"};
        int index = 0;
        for(String r : rows){
            for(int c = 1; c <= 4; c++){
                Cell cell = new Cell(pairList.get(index++));
                cellMap.put(r+c, cell);
            }
        }
    }

    public Cell reveal(String cellId){
        Cell c = cellMap.get(cellId);
        if(c != null) c.setRevealed(true);
        return c;
    }

    public boolean checkMatch(String firstId, String secondId){
        Cell f = cellMap.get(firstId);
        Cell s = cellMap.get(secondId);
        if(f != null && s != null && f.getEmoji().equals(s.getEmoji())){
            f.setMatched(true);
            s.setMatched(true);
            return true;
        }
        return false;
    }

    public boolean allMatched(){
        return cellMap.values().stream().allMatch(Cell::isMatched);
    }

    public int calculateScore(){
        long elapsed = System.currentTimeMillis() - startTime;
        return (int)Math.max(0, 20 - elapsed/1000);
    }

    public Map<String, Cell> getCellMap(){
        return cellMap;
    }
}
