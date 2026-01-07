package com.example.tama_gargoyles.controller;

import com.example.tama_gargoyles.model.Gargoyle;
import com.example.tama_gargoyles.model.User;
import com.example.tama_gargoyles.repository.GargoyleRepository;
import com.example.tama_gargoyles.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/minigame/memory")
public class MemoryGameController {

    private final GargoyleRepository gargoyleRepository;
    private final UserRepository userRepository;
    private static final String SESSION_KEY = "memoryGame";
    private static final String[] EMOJIS = {"😀","😎","🎮","🐱","🌟","🍕","🚀","🌈"};

    public MemoryGameController(GargoyleRepository gargoyleRepository, UserRepository userRepository) {
        this.gargoyleRepository = gargoyleRepository;
        this.userRepository = userRepository;
    }

    private static class GameState {
        String[][] grid = new String[4][4];
        boolean[][] matched = new boolean[4][4];
        String firstPickId = null;
        int score = 0;
        long startTime = System.currentTimeMillis();
    }

    @GetMapping
    public String memoryGame(@RequestParam("gargoyleId") Long gargoyleId, HttpSession session, Model model) {
        resetGame(session);
        model.addAttribute("gargoyleId", gargoyleId);
        return "memoryGame";
    }

    @GetMapping("/reset")
    @ResponseBody
    public void resetGame(HttpSession session) {
        GameState game = new GameState();
        List<String> pairEmojis = new ArrayList<>();
        for(String e : EMOJIS) { pairEmojis.add(e); pairEmojis.add(e); }
        Collections.shuffle(pairEmojis);

        for(int r=0; r<4; r++) {
            for(int c=0; c<4; c++) {
                game.grid[r][c] = pairEmojis.remove(0);
                game.matched[r][c] = false;
            }
        }
        session.setAttribute(SESSION_KEY, game);
    }

    @GetMapping("/reveal")
    @ResponseBody
    public Map<String,Object> revealCell(@RequestParam String cellId, HttpSession session) {
        GameState game = (GameState) session.getAttribute(SESSION_KEY);
        int row = cellId.charAt(0) - 'A';
        int col = Character.getNumericValue(cellId.charAt(1)) - 1;

        Map<String,Object> response = new HashMap<>();
        response.put("emoji", game.grid[row][col]);
        response.put("matched", false);
        response.put("wrong", null);

        if(game.firstPickId == null) {
            game.firstPickId = cellId;
        } else {
            int r1 = game.firstPickId.charAt(0) - 'A';
            int c1 = Character.getNumericValue(game.firstPickId.charAt(1)) - 1;
            if(game.grid[r1][c1].equals(game.grid[row][col]) && !cellId.equals(game.firstPickId)) {
                game.matched[r1][c1] = true;
                game.matched[row][col] = true;
                response.put("matched", true);
                response.put("pair", Arrays.asList(game.firstPickId, cellId));
            } else {
                response.put("wrong", Arrays.asList(game.firstPickId, cellId));
            }
            game.firstPickId = null;
        }
        return response;
    }

    @PostMapping("/complete")
    @ResponseBody
    public void completeGame(@RequestBody Map<String, Object> payload) {
        Long gargoyleId = Long.valueOf(payload.get("gargoyleId").toString());
        Gargoyle g = gargoyleRepository.findById(gargoyleId).orElseThrow();
        g.setIntelligence(Math.min(g.getIntelligence() + 10, 100));
        gargoyleRepository.save(g);
    }

    @GetMapping("/result-page")
    public String showResult(@RequestParam("time") int time, @RequestParam("gargoyleId") Long id, Model model) {
        Gargoyle gargoyle = gargoyleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gargoyle not found"));
        User user = gargoyle.getUser();

        String rewardMessage;

        // Tier 1: 0 - 20 seconds
        if (time <= 20) {
            rewardMessage = "Incredible Focus! You earned a rare Mystery Food!";
            user.setMysteryFood(user.getMysteryFood() + 1);
        }
        // Tier 2: 21 - 30 seconds
        else if (time <= 30) {
            rewardMessage = "Great Memory! You earned 3 Fruits, 2 Rocks and 1 Bug!";
            user.setFruits(user.getFruits() + 3);
            user.setRocks(user.getRocks() + 2);
            user.setBugs(user.getBugs() + 1);
        }
        // Tier 3: 31 - 40 seconds
        else if (time <= 40) {
            rewardMessage = "Well done! You earned 2 Fruits and 1 Rock!";
            user.setFruits(user.getFruits() + 2);
            user.setRocks(user.getRocks() + 1);
        }
        // Tier 4: Over 40 seconds
        else {
            rewardMessage = "Nice effort! You earned 1 Fruit for your effort.";
            user.setFruits(user.getFruits() + 1);
        }

        // Always boost happiness
        gargoyle.setHappiness(Math.min(100, gargoyle.getHappiness() + 20));

        // Save both the inventory and the gargoyle stats
        userRepository.save(user);
        gargoyleRepository.save(gargoyle);

        model.addAttribute("survivalTime", time);
        model.addAttribute("rewardMessage", rewardMessage);

        return "pong-result";
    }
}