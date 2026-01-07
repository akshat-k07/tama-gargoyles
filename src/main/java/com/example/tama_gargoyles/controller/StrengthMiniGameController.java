package com.example.tama_gargoyles.controller;

import com.example.tama_gargoyles.model.Gargoyle;
import com.example.tama_gargoyles.model.User;
import com.example.tama_gargoyles.repository.GargoyleRepository;
import com.example.tama_gargoyles.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/minigame/strength")
public class StrengthMiniGameController {

    private final GargoyleRepository gargoyleRepository;
    private final UserRepository userRepository;

    public StrengthMiniGameController(GargoyleRepository gargoyleRepository, UserRepository userRepository) {
        this.gargoyleRepository = gargoyleRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showGame(@RequestParam("gargoyleId") Long gargoyleId, Model model) {
        model.addAttribute("gargoyleId", gargoyleId);
        return "strengthGame";
    }

    // KEEP THIS: Background save for STATS (Strength)
    @PostMapping("/complete")
    @ResponseBody
    public ResponseEntity<String> saveStrengthResult(@RequestBody Map<String, Object> payload) {
        Long gargoyleId = Long.valueOf(payload.get("gargoyleId").toString());
        Gargoyle g = gargoyleRepository.findById(gargoyleId)
                .orElseThrow(() -> new RuntimeException("Gargoyle not found"));

        g.setStrength(Math.min(g.getStrength() + 10, 100)); // Standard boost
        gargoyleRepository.save(g);
        return ResponseEntity.ok("Stats Saved");
    }

    // ADD THIS: Handles the RESULTS page and INVENTORY (Bugs, Rocks, etc.)
    @GetMapping("/result-page")
    public String showStrengthResult(@RequestParam("time") int completionTime,
                                     @RequestParam("gargoyleId") Long gargoyleId,
                                     Model model) {

        Gargoyle g = gargoyleRepository.findById(gargoyleId)
                .orElseThrow(() -> new RuntimeException("Gargoyle not found"));
        User user = g.getUser();

        String rewardMessage;

        // --- REWARD LOGIC ---
        if (completionTime <= 4) {
            rewardMessage = "Incredible Speed! You earned a rare mystery food!";
            user.setMysteryFood(user.getMysteryFood() + 1);
        }
        else if (completionTime <= 10) {
            // Updated code to match the message: 3 Rocks, 2 Fruits, 1 Bug
            rewardMessage = "Great workout! You earned 3 Rocks and 2 Fruit and 1 bug!";
            user.setRocks(user.getRocks() + 3);
            user.setFruits(user.getFruits() + 2);
            user.setBugs(user.getBugs() + 1);
        }
        else if (completionTime <= 16) {
            rewardMessage = "Great workout! You earned 2 Rocks and 1 Fruit!";
            user.setRocks(user.getRocks() + 2);
            user.setFruits(user.getFruits() + 1);
        }
        else {
            rewardMessage = "Nice job! You earned 1 Rock for your effort.";
            user.setRocks(user.getRocks() + 1);
        }

        // Boost Happiness
        g.setHappiness(Math.min(100, g.getHappiness() + 20));

        // Save both entities to the database
        gargoyleRepository.save(g);
        userRepository.save(user);

        // Add to model for the pong-result.html template
        model.addAttribute("survivalTime", completionTime);
        model.addAttribute("rewardMessage", rewardMessage);

        return "pong-result";
    }
}