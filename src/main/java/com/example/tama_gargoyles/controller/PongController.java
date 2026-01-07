package com.example.tama_gargoyles.controller;

import com.example.tama_gargoyles.dto.PongResultDTO;
import com.example.tama_gargoyles.model.Gargoyle;
import com.example.tama_gargoyles.model.User;
import com.example.tama_gargoyles.repository.GargoyleRepository;
import com.example.tama_gargoyles.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pong")
public class PongController {

    private final GargoyleRepository gargoyleRepository;
    private final UserRepository userRepository;

    public PongController(GargoyleRepository gargoyleRepository, UserRepository userRepository) {
        this.gargoyleRepository = gargoyleRepository;
        this.userRepository = userRepository;
    }

    // This handles the background save of Gargoyle stats (Speed, Strength, etc.)
    @PostMapping("/result")
    public ResponseEntity<String> savePongResult(@RequestBody PongResultDTO dto) {
        Gargoyle g = gargoyleRepository.findById(dto.getGargoyleId())
                .orElseThrow(() -> new RuntimeException("Gargoyle not found"));

        int time = dto.getSurvivalTime();

        // Stat rewards based on survival time
        if(time >= 20) g.setSpeed(Math.min(g.getSpeed() + 5, 100));
        if(time >= 40) g.setStrength(Math.min(g.getStrength() + 5, 100));
        if(time >= 60) g.setIntelligence(Math.min(g.getIntelligence() + 5, 100));

        gargoyleRepository.save(g);
        return ResponseEntity.ok("Saved Stats");
    }

    @GetMapping("/pong")
    public String showPong(@RequestParam("gargoyleId") Long gargoyleId, Model model) {
        model.addAttribute("gargoyleId", gargoyleId);
        return "pong";
    }

    // This handles the final page view and inventory (Bugs, Fruits, Rocks)
    @GetMapping("/pong-result")
    public String showPongResult(@RequestParam("time") int survivalTime,
                                 @RequestParam("gargoyleId") Long gargoyleId,
                                 Model model) {

        Gargoyle g = gargoyleRepository.findById(gargoyleId)
                .orElseThrow(() -> new RuntimeException("Gargoyle not found"));

        // Get the user owner to update inventory
        User user = g.getUser();

        String rewardMessage;

        // Inventory rewards logic
        if (survivalTime >= 140) {
            rewardMessage = "Incredible! You earned a rare mystery food!!!";
            user.setMysteryFood(user.getMysteryFood() + 1);
        }
        else if (survivalTime >= 80) {
            rewardMessage = "Incredible! You earned 3 Bugs, 2 Fruits, and 1 Rock!";
            user.setBugs(user.getBugs() + 3);
            user.setFruits(user.getFruits() + 2);
            user.setRocks(user.getRocks() + 1);
        } else if (survivalTime >= 35) {
            rewardMessage = "Great job! You earned 2 Bugs and a Fruit!";
            user.setBugs(user.getBugs() + 2);
            user.setFruits(user.getFruits() + 1);
        } else {
            rewardMessage = "Not bad! You earned a bug.";
            user.setBugs(user.getBugs() + 1);
        }

        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).get();
        gargoyle.setHappiness(Math.min(100, gargoyle.getHappiness() + 20));

        // Save the User inventory changes
        gargoyleRepository.save(gargoyle);
        userRepository.save(user);

        // Pass variables to pong-result.html
        model.addAttribute("survivalTime", survivalTime);
        model.addAttribute("rewardMessage", rewardMessage);

        return "pong-result";
    }
}