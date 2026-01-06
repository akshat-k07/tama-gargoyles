package com.example.tama_gargoyles.controller;

import com.example.tama_gargoyles.model.Gargoyle;
import com.example.tama_gargoyles.model.User;
import com.example.tama_gargoyles.repository.GargoyleRepository;
import com.example.tama_gargoyles.repository.UserRepository;
import com.example.tama_gargoyles.service.CurrentUserService;
import com.example.tama_gargoyles.service.GargoyleTimeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.Authentication;


import java.util.*;

import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

@Controller
public class GargoyleController {

    private final GargoyleRepository gargoyleRepository;
    private final CurrentUserService currentUserService;
    private final GargoyleTimeService timeService;

    private static final long ADULT_AT_GAME_DAYS = 3;

    // Testing mode session flag key
    private static final String TESTING_MODE_KEY = "TESTING_MODE";

    public GargoyleController(
            GargoyleRepository gargoyleRepository,
            CurrentUserService currentUserService,
            GargoyleTimeService timeService
    ) {
        this.gargoyleRepository = gargoyleRepository;
        this.currentUserService = currentUserService;
        this.timeService = timeService;
    }

    // -------------------------
    // TESTING MODE (TOGGLE)
    // -------------------------
    @PostMapping("/testing-mode/toggle")
    public String toggleTestingMode(HttpSession session, RedirectAttributes redirectAttributes) {
        boolean current = Boolean.TRUE.equals(session.getAttribute(TESTING_MODE_KEY));
        boolean next = !current;
        session.setAttribute(TESTING_MODE_KEY, next);

        redirectAttributes.addFlashAttribute(
                "flashMessage",
                next ? "🧪 Testing mode enabled" : "✅ Testing mode disabled"
        );

        return "redirect:/game";
    }

    // -------------------------
    // FEED
    // -------------------------
    @PostMapping("/rocks-increase")
    public RedirectView increaseRocks(@RequestParam Integer strengthDelta, @RequestParam Integer speedDelta,
                                      @RequestParam Integer intelligenceDelta, @RequestParam Integer hungerDelta,
                                      @RequestParam Long gargoyleId) {
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).orElseThrow();
        gargoyle.setStrength(Math.max(0, Math.min(gargoyle.getStrength() + strengthDelta, 100)));
        gargoyle.setSpeed(Math.max(0, Math.min(gargoyle.getSpeed() + speedDelta, 100)));
        gargoyle.setIntelligence(Math.max(0, Math.min(gargoyle.getIntelligence() + intelligenceDelta, 100)));
        gargoyle.setHunger(Math.max(0, Math.min(gargoyle.getHunger() + hungerDelta, 100)));
        gargoyleRepository.save(gargoyle);
        return new RedirectView("/game");
    }

    @PostMapping("/bugs-increase")
    public RedirectView increaseBugs(@RequestParam Integer strengthDelta, @RequestParam Integer speedDelta,
                                     @RequestParam Integer intelligenceDelta, @RequestParam Integer hungerDelta,
                                     @RequestParam Long gargoyleId) {
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).orElseThrow();
        gargoyle.setStrength(Math.max(0, Math.min(gargoyle.getStrength() + strengthDelta, 100)));
        gargoyle.setSpeed(Math.max(0, Math.min(gargoyle.getSpeed() + speedDelta, 100)));
        gargoyle.setIntelligence(Math.max(0, Math.min(gargoyle.getIntelligence() + intelligenceDelta, 100)));
        gargoyle.setHunger(Math.max(0, Math.min(gargoyle.getHunger() + hungerDelta, 100)));
        gargoyleRepository.save(gargoyle);
        return new RedirectView("/game");
    }

    @PostMapping("/fruits-increase")
    public RedirectView increaseFruits(@RequestParam Integer strengthDelta, @RequestParam Integer speedDelta,
                                       @RequestParam Integer intelligenceDelta, @RequestParam Integer hungerDelta,
                                       @RequestParam Long gargoyleId) {
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).orElseThrow();
        gargoyle.setStrength(Math.max(0, Math.min(gargoyle.getStrength() + strengthDelta, 100)));
        gargoyle.setSpeed(Math.max(0, Math.min(gargoyle.getSpeed() + speedDelta, 100)));
        gargoyle.setIntelligence(Math.max(0, Math.min(gargoyle.getIntelligence() + intelligenceDelta, 100)));
        gargoyle.setHunger(Math.max(0, Math.min(gargoyle.getHunger() + hungerDelta, 100)));
        gargoyleRepository.save(gargoyle);
        return new RedirectView("/game");
    }

    // Currently thymeleaf just gives zero values, need to program for randomisation
    @PostMapping("/mystery-increase")
    public RedirectView increaseMystery(@RequestParam Long gargoyleId){
        Random randomNum = new Random();
        int toIncrease = randomNum.nextInt(3);
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).get();
        if (toIncrease == 0){
            gargoyle.setStrength(Math.max(0, Math.min(gargoyle.getStrength() + randomNum.nextInt(1, 25), 100)));
            gargoyle.setSpeed((Math.min(100, Math.max(gargoyle.getSpeed() - randomNum.nextInt(1, 7), 0))));
            gargoyle.setIntelligence((Math.min(100, Math.max(gargoyle.getIntelligence() - randomNum.nextInt(1, 7), 0))));
        }
        else if (toIncrease == 1){
            gargoyle.setSpeed(Math.max(0, Math.min(gargoyle.getSpeed() + randomNum.nextInt(7, 25), 100)));
            gargoyle.setStrength((Math.min(100, Math.max(gargoyle.getStrength() - randomNum.nextInt(1, 7), 0))));
            gargoyle.setIntelligence((Math.min(100, Math.max(gargoyle.getIntelligence() - randomNum.nextInt(1, 7), 0))));
        }
        else{
            gargoyle.setIntelligence(Math.max(0, Math.min(gargoyle.getIntelligence() + randomNum.nextInt(1, 25), 100)));
            gargoyle.setSpeed((Math.min(100, Math.max(gargoyle.getSpeed() - randomNum.nextInt(1, 7), 0))));
            gargoyle.setStrength((Math.min(100, Math.max(gargoyle.getStrength() - randomNum.nextInt(1, 7), 0))));
        }
        gargoyle.setHunger(100);

        gargoyleRepository.save(gargoyle);
        return new RedirectView("/game");
    }

    // -------------------------
    // PLAY
    // -------------------------
    @PostMapping("/strength-increase")
    public RedirectView increaseStrength(@RequestParam Integer strengthDelta, @RequestParam Integer speedDelta,
                                         @RequestParam Integer intelligenceDelta, @RequestParam Integer happinessDelta,
                                         @RequestParam Long gargoyleId) {
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).orElseThrow();
        gargoyle.setStrength(Math.max(0, Math.min(gargoyle.getStrength() + strengthDelta, 100)));
        gargoyle.setSpeed(Math.max(0, Math.min(gargoyle.getSpeed() + speedDelta, 100)));
        gargoyle.setIntelligence(Math.max(0, Math.min(gargoyle.getIntelligence() + intelligenceDelta, 100)));
        gargoyle.setHappiness(Math.max(0, Math.min(gargoyle.getHappiness() + happinessDelta, 100)));
        gargoyleRepository.save(gargoyle);
        return new RedirectView("/game");
    }

    @PostMapping("/speed-increase")
    public RedirectView increaseSpeed(@RequestParam Integer strengthDelta, @RequestParam Integer speedDelta,
                                      @RequestParam Integer intelligenceDelta, @RequestParam Integer happinessDelta,
                                      @RequestParam Long gargoyleId) {
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).orElseThrow();
        gargoyle.setStrength(Math.max(0, Math.min(gargoyle.getStrength() + strengthDelta, 100)));
        gargoyle.setSpeed(Math.max(0, Math.min(gargoyle.getSpeed() + speedDelta, 100)));
        gargoyle.setIntelligence(Math.max(0, Math.min(gargoyle.getIntelligence() + intelligenceDelta, 100)));
        gargoyle.setHappiness(Math.max(0, Math.min(gargoyle.getHappiness() + happinessDelta, 100)));
        gargoyleRepository.save(gargoyle);
        return new RedirectView("/game");
    }

    @PostMapping("/intelligence-increase")
    public RedirectView increaseIntelligence(@RequestParam Integer strengthDelta, @RequestParam Integer speedDelta,
                                             @RequestParam Integer intelligenceDelta, @RequestParam Integer happinessDelta,
                                             @RequestParam Long gargoyleId) {
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).orElseThrow();
        gargoyle.setStrength(Math.max(0, Math.min(gargoyle.getStrength() + strengthDelta, 100)));
        gargoyle.setSpeed(Math.max(0, Math.min(gargoyle.getSpeed() + speedDelta, 100)));
        gargoyle.setIntelligence(Math.max(0, Math.min(gargoyle.getIntelligence() + intelligenceDelta, 100)));
        gargoyle.setHappiness(Math.max(0, Math.min(gargoyle.getHappiness() + happinessDelta, 100)));
        gargoyleRepository.save(gargoyle);
        return new RedirectView("/game");
    }

    // -------------------------
    // MISC DELTAS
    // -------------------------
    @PostMapping("/hunger-increase")
    public RedirectView increaseHunger(@RequestParam Integer delta, @RequestParam Long gargoyleId) {
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).orElseThrow();
        gargoyle.setHunger(Math.min(gargoyle.getHunger() + delta, 100));
        gargoyleRepository.save(gargoyle);
        return new RedirectView("/game");
    }

    @PostMapping("/entertainment-increase")
    public RedirectView increaseHappiness(@RequestParam Integer delta, @RequestParam Long gargoyleId) {
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).orElseThrow();
        gargoyle.setHappiness(Math.min(gargoyle.getHappiness() + delta, 100));
        gargoyleRepository.save(gargoyle);
        return new RedirectView("/game");
    }

    @PostMapping("/entertainment-decrease")
    public RedirectView decreaseHappiness(@RequestParam Integer delta, @RequestParam Long gargoyleId) {
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).orElseThrow();
        gargoyle.setHappiness(Math.max(gargoyle.getHappiness() - delta, 0));
        gargoyleRepository.save(gargoyle);
        return new RedirectView("/game");
    }

    @PostMapping("/hunger-decrease")
    public RedirectView decreaseHunger(@RequestParam Integer delta, @RequestParam Long gargoyleId) {
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).orElseThrow();
        gargoyle.setHunger(Math.max(gargoyle.getHunger() - delta, 0));
        gargoyleRepository.save(gargoyle);
        return new RedirectView("/game");
    }

    // -------------------------
    // GAME (DEFAULT SELECTION)
    // -------------------------
    @GetMapping("/game")
    public String game(Model model, Authentication authentication, HttpSession session) {
        User user = currentUserService.getCurrentUser(authentication);

        var gargoyles = gargoyleRepository.findAllByUserIdOrderByIdAsc(user.getId());

        // If this is a brand new Auth0 user, create their first creature.
        if (gargoyles.isEmpty()) {
            Gargoyle newborn = new Gargoyle(user);
            newborn.setName("Egg-" + user.getId()); // UNIQUE constraint safe
            gargoyleRepository.save(newborn);
            gargoyles = java.util.List.of(newborn);
        }

        // MVP selection rule:
        // Prefer CHILD if one exists, otherwise pick the first by id.
        Gargoyle g = gargoyles.stream()
                .filter(x -> x.getType() == Gargoyle.Type.CHILD)
                .findFirst()
                .orElse(gargoyles.get(0));

        // 1) Resume first (prevents offline gap)
        // 2) Then tick (applies only active time)
        timeService.resume(g);
        timeService.tick(g);

        // Promote to adult if old enough, based on how the child was treated
        long daysOld = timeService.gameDaysOld(g);
        boolean isOldEnough = daysOld >= ADULT_AT_GAME_DAYS;

        if (g.getType() == Gargoyle.Type.CHILD && isOldEnough) {
            boolean treatedWell = g.getHappiness() >= 60 && g.getHunger() >= 60;
            g.setType(treatedWell ? Gargoyle.Type.GOOD : Gargoyle.Type.BAD);
        }

        gargoyleRepository.save(g);

        model.addAttribute("gargoyle", g);
        model.addAttribute("gameDaysOld", timeService.gameDaysOld(g));
        model.addAttribute("minutesIntoDay", timeService.minutesIntoCurrentDay(g));

        // Expose testingMode to Thymeleaf
        boolean testingMode = Boolean.TRUE.equals(session.getAttribute(TESTING_MODE_KEY));
        model.addAttribute("testingMode", testingMode);

        // If you already have devMode elsewhere, keep it.
        // Otherwise, just use testingMode in the template for debug visibility.

        return "game";
    }

    @PostMapping("/gargoyles/pause")
    public String pause(Authentication authentication) {
        User user = currentUserService.getCurrentUser(authentication);

        var gargoyles = gargoyleRepository.findAllByUserIdOrderByIdAsc(user.getId());
        if (gargoyles.isEmpty()) return "redirect:/";

        Gargoyle g = gargoyles.stream()
                .filter(x -> x.getType() == Gargoyle.Type.CHILD)
                .findFirst()
                .orElse(gargoyles.get(0));

        timeService.pause(g);
        gargoyleRepository.save(g);

        return "redirect:/";
    }

    @GetMapping("/game/{id}")
    public String gameForOneGargoyle(@PathVariable Long id, Model model, Authentication authentication, HttpSession session) {
        User user = currentUserService.getCurrentUser(authentication);

        Gargoyle gargoyle = gargoyleRepository.findById(id).orElseThrow();

        timeService.resume(gargoyle);
        timeService.tick(gargoyle);

        gargoyleRepository.save(gargoyle);

        model.addAttribute("gargoyle", gargoyle);
        model.addAttribute("gameDaysOld", timeService.gameDaysOld(gargoyle));
        model.addAttribute("minutesIntoDay", timeService.minutesIntoCurrentDay(gargoyle));

        // Expose testingMode here too
        boolean testingMode = Boolean.TRUE.equals(session.getAttribute(TESTING_MODE_KEY));
        model.addAttribute("testingMode", testingMode);

        return "game";
    }

    // -------------------------
    // GARGOYLES CRUD
    // -------------------------
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/gargoyles")
    public ModelAndView allGargoyles(Authentication authentication) {
        User user = currentUserService.getCurrentUser(authentication);

        if (user == null) {
            return new ModelAndView("redirect:/login");
        }

        ModelAndView modelAndView = new ModelAndView("gargoyles/gargoyles");
        modelAndView.addObject(
                "gargoyles",
                gargoyleRepository.findAllByUserId(user.getId())
        );
        return modelAndView;
    }

    @GetMapping("/gargoyles/new")
    public ModelAndView newGargoyleForm(Authentication authentication) {
        User user = currentUserService.getCurrentUser(authentication);

        Gargoyle gargoyle = new Gargoyle();
        ModelAndView newGargoyleForm = new ModelAndView("gargoyles/new");
        newGargoyleForm.addObject("gargoyle", gargoyle);
        return newGargoyleForm;
    }

    @PostMapping("/gargoyles")
    public RedirectView create(Gargoyle gargoyle, Authentication authentication) {
        User user = currentUserService.getCurrentUser(authentication);
        gargoyle.setUser(user);

        if (gargoyle.getName().isEmpty() || gargoyle.getName().length() > 30) {
            return new RedirectView("/gargoyles");
        }

        Gargoyle saved = gargoyleRepository.save(gargoyle);
        return new RedirectView("/game/" + saved.getId());
    }

    @GetMapping("/gargoyle/{id}/rename")
    public ModelAndView showRenameForm(@PathVariable Long id) {
        Gargoyle gargoyle = gargoyleRepository.findById(id).orElseThrow();
        return new ModelAndView("gargoyles/renameGargoyle", "gargoyle", gargoyle);
    }

    @PostMapping("/gargoyle/{id}/rename")
    public RedirectView renameGargoyle(@PathVariable Long id, @RequestParam String name) {
        Gargoyle gargoyle = gargoyleRepository.findById(id).orElseThrow();

        if (name.isEmpty() || name.length() > 30) {
            return new RedirectView("/game/");
        }

        gargoyle.setName(name);
        Gargoyle saved = gargoyleRepository.save(gargoyle);

        return new RedirectView("/game/" + saved.getId());
    }
}
