package com.example.tama_gargoyles.controller;

import com.example.tama_gargoyles.model.Gargoyle;
import com.example.tama_gargoyles.model.User;
import com.example.tama_gargoyles.repository.GargoyleRepository;
import com.example.tama_gargoyles.service.CurrentUserService;
import com.example.tama_gargoyles.service.GargoyleTimeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class GargoyleController {

    private final GargoyleRepository gargoyleRepository;
    private final CurrentUserService currentUserService;
    private final GargoyleTimeService timeService;

    public GargoyleController(
            GargoyleRepository gargoyleRepository,
            CurrentUserService currentUserService,
            GargoyleTimeService timeService
    ) {
        this.gargoyleRepository = gargoyleRepository;
        this.currentUserService = currentUserService;
        this.timeService = timeService;
    }

    @GetMapping("/game")
    public String game(Model model) {
        User user = currentUserService.getCurrentUser();
        Gargoyle g = gargoyleRepository.findByUserId(user.getId()).orElseThrow();

        // ROCK-SOLID ORDER:
        // 1) Resume first (prevents offline gap)
        // 2) Then tick (applies only active time)
        timeService.resume(g);
        timeService.tick(g);

        gargoyleRepository.save(g);

        model.addAttribute("gargoyle", g);
        model.addAttribute("gameDaysOld", timeService.gameDaysOld(g));
        model.addAttribute("minutesIntoDay", timeService.minutesIntoCurrentDay(g));

        return "game";
    }

    @PostMapping("/gargoyles/pause")
    public String pause() {
        User user = currentUserService.getCurrentUser();
        Gargoyle g = gargoyleRepository.findByUserId(user.getId()).orElseThrow();

        timeService.pause(g);
        gargoyleRepository.save(g);

        return "redirect:/";
    }
}
