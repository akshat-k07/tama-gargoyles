package com.example.tama_gargoyles.controller;

import com.example.tama_gargoyles.dto.PongResultDTO;
import com.example.tama_gargoyles.model.Gargoyle;
import com.example.tama_gargoyles.repository.GargoyleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pong")
public class PongController {

    private final GargoyleRepository gargoyleRepository;

    public PongController(GargoyleRepository gargoyleRepository) {
        this.gargoyleRepository = gargoyleRepository;
    }

    @PostMapping("/result")
    public ResponseEntity<String> savePongResult(@RequestBody PongResultDTO dto) {

        Gargoyle g = gargoyleRepository.findById(dto.getGargoyleId())
                .orElseThrow(() -> new RuntimeException("Gargoyle not found"));

        int time = dto.getSurvivalTime();

        if(time >= 20) g.setSpeed(Math.min(g.getSpeed() + 5, 100));
        if(time >= 40) g.setStrength(Math.min(g.getStrength() + 5, 100));
        if(time >= 60) g.setIntelligence(Math.min(g.getIntelligence() + 5, 100));

        gargoyleRepository.save(g);

        return ResponseEntity.ok("Saved");
    }
    @GetMapping("/pong")
    public String showPong(@RequestParam("gargoyleId") Long gargoyleId, Model model) {
        model.addAttribute("gargoyleId", gargoyleId);
        return "pong"; // This looks for templates/pong.html
    }

    @GetMapping("/pong-result")
    public String showPongResult(@RequestParam("time") int survivalTime, Model model) {
        model.addAttribute("survivalTime", survivalTime);
        return "pong-result";
    }
}

