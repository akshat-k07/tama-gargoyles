package com.example.tama_gargoyles.games;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/games/memoryGame")
public class MemoryGameController {

    private static final String SESSION_KEY = "memoryGame";

    private static final String[] EMOJIS = {"😀","😎","🎮","🐱","🌟","🍕","🚀","🌈"};

    private static class GameState {
        String[][] grid = new String[4][4];   // Emoji grid
        boolean[][] matched = new boolean[4][4];
        String firstPickId = null;
        int score = 0;
    }

    @GetMapping
    public String memoryGame(HttpSession session) {
        if(session.getAttribute(SESSION_KEY) == null){
            resetGame(session);
        }
        return "memoryGame"; // Thymeleaf template
    }

    @GetMapping("/reset")
    @ResponseBody
    public void resetGame(HttpSession session) {
        GameState game = new GameState();
        List<String> pairEmojis = new ArrayList<>();
        for(String e : EMOJIS){
            pairEmojis.add(e);
            pairEmojis.add(e);
        }
        Collections.shuffle(pairEmojis);

        for(int r=0;r<4;r++){
            for(int c=0;c<4;c++){
                game.grid[r][c] = pairEmojis.remove(0);
                game.matched[r][c] = false;
            }
        }

        game.firstPickId = null;
        game.score = 0;

        session.setAttribute(SESSION_KEY, game);
    }

    @GetMapping("/reveal")
    @ResponseBody
    public Map<String,Object> revealCell(@RequestParam String cellId, HttpSession session){
        GameState game = (GameState) session.getAttribute(SESSION_KEY);
        if(game==null){
            resetGame(session);
            game = (GameState) session.getAttribute(SESSION_KEY);
        }

        int row = cellId.charAt(0) - 'A';
        int col = Character.getNumericValue(cellId.charAt(1)) - 1;

        Map<String,Object> response = new HashMap<>();
        String emoji = game.grid[row][col];
        response.put("emoji", emoji);
        response.put("matched", false); // default
        response.put("wrong", null);

        if(game.firstPickId == null){
            // First pick
            game.firstPickId = cellId;
        } else {
            // Second pick
            int r1 = game.firstPickId.charAt(0) - 'A';
            int c1 = Character.getNumericValue(game.firstPickId.charAt(1)) - 1;
            String firstEmoji = game.grid[r1][c1];

            if(firstEmoji.equals(emoji) && !cellId.equals(game.firstPickId)){
                // It's a match
                game.matched[r1][c1] = true;
                game.matched[row][col] = true;
                response.put("matched", true);
                response.put("pair", Arrays.asList(game.firstPickId, cellId));
                game.score += 2;
            } else {
                response.put("wrong", Arrays.asList(game.firstPickId, cellId));
            }

            game.firstPickId = null;
        }

        response.put("score", game.score);
        session.setAttribute(SESSION_KEY, game);
        return response;
    }
}
