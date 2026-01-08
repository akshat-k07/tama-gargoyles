package com.example.tama_gargoyles.controller;

import com.example.tama_gargoyles.model.Gargoyle;
import com.example.tama_gargoyles.model.User;
import com.example.tama_gargoyles.repository.GargoyleRepository;
import com.example.tama_gargoyles.repository.UserRepository;
import com.example.tama_gargoyles.service.CurrentUserService;
import com.example.tama_gargoyles.service.EvolutionService;
import com.example.tama_gargoyles.service.GargoyleTimeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
public class GargoyleController {

    private final GargoyleRepository gargoyleRepository;
    private final CurrentUserService currentUserService;
    private final GargoyleTimeService timeService;
    private final EvolutionService evolutionService;

    @Autowired
    private UserRepository userRepository;

    public GargoyleController(
            GargoyleRepository gargoyleRepository,
            CurrentUserService currentUserService,
            GargoyleTimeService timeService
    ) {
        this.gargoyleRepository = gargoyleRepository;
        this.currentUserService = currentUserService;
        this.timeService = timeService;
        this.evolutionService = new EvolutionService();
    }

    // ----------------------------------------------------------
    // Testing Mode Toggle (stored in session; per-user; survives refresh)
    // ----------------------------------------------------------
    @PostMapping("/testing-mode/toggle")
    public RedirectView toggleTestingMode(
            HttpSession session,
            @RequestParam(required = false) String returnTo
    ) {
        Boolean current = (Boolean) session.getAttribute("testingMode");
        boolean next = (current == null) ? true : !current;
        session.setAttribute("testingMode", next);

        String target = (returnTo != null && !returnTo.isBlank()) ? returnTo : "/game";
        return new RedirectView(target);
    }

    @PostMapping("/rocks-increase")
    public RedirectView increaseRocks(@RequestParam Integer strengthDelta, @RequestParam Integer speedDelta,
                                      @RequestParam Integer intelligenceDelta, @RequestParam Integer hungerDelta, @RequestParam Long gargoyleId, @RequestParam Long userId){
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).get();
        User user = userRepository.findById(userId).get();
        if (user.getRocks() > 0 && gargoyle.getHunger() < 100) {
            gargoyle.setStrength(Math.max(0, Math.min(gargoyle.getStrength() + strengthDelta, 100)));
            gargoyle.setSpeed(Math.max(0, Math.min(gargoyle.getSpeed() + speedDelta, 100)));
            gargoyle.setIntelligence(Math.max(0, Math.min(gargoyle.getIntelligence() + intelligenceDelta, 100)));
            gargoyle.setHunger(Math.max(0, Math.min(gargoyle.getHunger() + hungerDelta, 100)));
            user.addRocks(-1);
            userRepository.save(user);
            gargoyleRepository.save(gargoyle);
        }
        return new RedirectView("/game");
    }

    @PostMapping("/bugs-increase")
    public RedirectView increaseBugs(@RequestParam Integer strengthDelta, @RequestParam Integer speedDelta,
                                     @RequestParam Integer intelligenceDelta, @RequestParam Integer hungerDelta, @RequestParam Long gargoyleId, @RequestParam Long userId){
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).get();
        User user = userRepository.findById(userId).get();
        if (user.getBugs() > 0 && gargoyle.getHunger() < 100) {
            gargoyle.setStrength(Math.max(0, Math.min(gargoyle.getStrength() + strengthDelta, 100)));
            gargoyle.setSpeed(Math.max(0, Math.min(gargoyle.getSpeed() + speedDelta, 100)));
            gargoyle.setIntelligence(Math.max(0, Math.min(gargoyle.getIntelligence() + intelligenceDelta, 100)));
            gargoyle.setHunger(Math.max(0, Math.min(gargoyle.getHunger() + hungerDelta, 100)));
            user.addBugs(-1);
            userRepository.save(user);
            gargoyleRepository.save(gargoyle);
        }
        return new RedirectView("/game");
    }

    @PostMapping("/fruits-increase")
    public RedirectView increaseFruits(@RequestParam Integer strengthDelta, @RequestParam Integer speedDelta,
                                       @RequestParam Integer intelligenceDelta, @RequestParam Integer hungerDelta, @RequestParam Long gargoyleId, @RequestParam Long userId){
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).get();
        User user = userRepository.findById(userId).get();
        if (user.getFruits() > 0 && gargoyle.getHunger() < 100) {
            gargoyle.setStrength(Math.max(0, Math.min(gargoyle.getStrength() + strengthDelta, 100)));
            gargoyle.setSpeed(Math.max(0, Math.min(gargoyle.getSpeed() + speedDelta, 100)));
            gargoyle.setIntelligence(Math.max(0, Math.min(gargoyle.getIntelligence() + intelligenceDelta, 100)));
            gargoyle.setHunger(Math.max(0, Math.min(gargoyle.getHunger() + hungerDelta, 100)));
            user.addFruits(-1);
            userRepository.save(user);
            gargoyleRepository.save(gargoyle);
        }
        return new RedirectView("/game");
    }
    @PostMapping("/mystery-increase")
    public RedirectView increaseMystery(@RequestParam Long gargoyleId, @RequestParam Long userId){
        Random randomNum = new Random();
        int toIncrease = randomNum.nextInt(3);
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).get();
        User user = userRepository.findById(userId).get();
        if (user.getMysteryFood() > 0 && gargoyle.getHunger() < 100) {
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
            user.addMysteryFood(-1);
            userRepository.save(user);
            gargoyleRepository.save(gargoyle);
        }
        return new RedirectView("/game");
    }

    @PostMapping("/strength-increase")
    public RedirectView increaseStrength(@RequestParam Integer strengthDelta, @RequestParam Integer speedDelta,
                                         @RequestParam Integer intelligenceDelta, @RequestParam Integer happinessDelta, @RequestParam Long gargoyleId){
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
                                      @RequestParam Integer intelligenceDelta, @RequestParam Integer happinessDelta, @RequestParam Long gargoyleId){
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
                                             @RequestParam Integer intelligenceDelta, @RequestParam Integer happinessDelta, @RequestParam Long gargoyleId){
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).orElseThrow();
        gargoyle.setStrength(Math.max(0, Math.min(gargoyle.getStrength() + strengthDelta, 100)));
        gargoyle.setSpeed(Math.max(0, Math.min(gargoyle.getSpeed() + speedDelta, 100)));
        gargoyle.setIntelligence(Math.max(0, Math.min(gargoyle.getIntelligence() + intelligenceDelta, 100)));
        gargoyle.setHappiness(Math.max(0, Math.min(gargoyle.getHappiness() + happinessDelta, 100)));
        gargoyleRepository.save(gargoyle);
        return new RedirectView("/game");
    }

    @PostMapping("/hunger-increase")
    public RedirectView increaseHunger(@RequestParam Integer delta, @RequestParam Long gargoyleId){
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).orElseThrow();
        gargoyle.setHunger(Math.min(gargoyle.getHunger() + delta, 100));
        gargoyleRepository.save(gargoyle);
        return new RedirectView("/game");
    }

    @PostMapping("/entertainment-increase")
    public RedirectView increaseHappiness(@RequestParam Integer delta, @RequestParam Long gargoyleId){
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).orElseThrow();
        gargoyle.setHappiness(Math.min(gargoyle.getHappiness() + delta, 100));
        gargoyleRepository.save(gargoyle);
        return new RedirectView("/game");
    }

    @PostMapping("/entertainment-decrease")
    public RedirectView decreaseHappiness(@RequestParam Integer delta, @RequestParam Long gargoyleId){
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).orElseThrow();
        gargoyle.setHappiness(Math.max(0, gargoyle.getHappiness() - delta));
        gargoyleRepository.save(gargoyle);
        return new RedirectView("/game");
    }

    @PostMapping("/hunger-decrease")
    public RedirectView decreaseHunger(@RequestParam Integer delta, @RequestParam Long gargoyleId){
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).orElseThrow();
        gargoyle.setHunger(Math.max(0, gargoyle.getHunger() - delta));
        gargoyleRepository.save(gargoyle);
        return new RedirectView("/game");
    }

    @GetMapping("/game")
    public String game(Model model, Authentication authentication, HttpSession session) {
        User user = currentUserService.getCurrentUser(authentication);

        var gargoyles = gargoyleRepository.findAllByUserIdOrderByIdAsc(user.getId());

        if (gargoyles.isEmpty()) {
            Gargoyle newborn = new Gargoyle(user);
            newborn.setName("Egg-" + user.getId());
            gargoyleRepository.save(newborn);
            gargoyles = java.util.List.of(newborn);
        }

        Gargoyle g = gargoyles.stream()
                .filter(x -> x.getType() == Gargoyle.Type.CHILD)
                .findFirst()
                .orElse(gargoyles.get(0));

        timeService.resume(g);
        timeService.tick(g);

        boolean evolved = evolutionService.evolveGargoyle(g);
        gargoyleRepository.save(g);

        Boolean testingMode = (Boolean) session.getAttribute("testingMode");
        model.addAttribute("testingMode", testingMode != null && testingMode);

        model.addAttribute("gargoyle", g);
        model.addAttribute("gameDaysOld", timeService.gameDaysOld(g));
        model.addAttribute("minutesIntoDay", timeService.minutesIntoCurrentDay(g));

        // ---- Inventory counts ----
        model.addAttribute("rocksCount", user.getRocks());
        model.addAttribute("bugsCount", user.getBugs());
        model.addAttribute("fruitsCount", user.getFruits());
        model.addAttribute("mysteryCount", user.getMysteryFood());

        if (evolved) return "gargoyles/evolution";
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
    public String gameForOneGargoyle(@PathVariable Long id,
                                     Model model,
                                     Authentication authentication,
                                     HttpSession session) {
        User user = currentUserService.getCurrentUser(authentication);

        Gargoyle gargoyle = gargoyleRepository.findById(id).orElseThrow();

        // Ownership check (keep)
        if (!gargoyle.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        timeService.resume(gargoyle);
        timeService.tick(gargoyle);
        gargoyleRepository.save(gargoyle);

        Boolean testingMode = (Boolean) session.getAttribute("testingMode");
        model.addAttribute("testingMode", testingMode != null && testingMode);

        model.addAttribute("gargoyle", gargoyle);
        model.addAttribute("gameDaysOld", timeService.gameDaysOld(gargoyle));
        model.addAttribute("minutesIntoDay", timeService.minutesIntoCurrentDay(gargoyle));

        return "game";
    }

    @GetMapping("/gargoyles")
    public ModelAndView allGargoyles(Authentication authentication) {
        User user = currentUserService.getCurrentUser(authentication);

        if (user == null) {
            return new ModelAndView("redirect:/login");
        }

        ModelAndView modelAndView = new ModelAndView("gargoyles/gargoyles");
        modelAndView.addObject("gargoyles", gargoyleRepository.findAllByUserId(user.getId()));
        return modelAndView;
    }

    @GetMapping("/gargoyles/new")
    public ModelAndView newGargoyleForm(Authentication authentication) {
        Gargoyle gargoyle = new Gargoyle();
        ModelAndView newGargoyleForm = new ModelAndView("gargoyles/new");
        newGargoyleForm.addObject("gargoyle", gargoyle);
        return newGargoyleForm;
    }

    @PostMapping("/gargoyles")
    public RedirectView create(Gargoyle gargoyle, Authentication authentication) {
        User user = currentUserService.getCurrentUser(authentication);
        gargoyle.setUser(user);

        if (gargoyle.getName().isBlank() || gargoyle.getName().length() > 30) {
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

        if (name.isBlank() || name.length() > 30) {
            return new RedirectView("/game/");
        }

        gargoyle.setName(name);
        Gargoyle saved = gargoyleRepository.save(gargoyle);
        return new RedirectView("/game/" + saved.getId());
    }

    @GetMapping("/game/stats/{id}")
    @ResponseBody
    public Map<String, Object> getLiveStats(@PathVariable Long id, Authentication authentication) {
        User user = currentUserService.getCurrentUser(authentication);

        Gargoyle g = gargoyleRepository.findById(id).orElseThrow();

        if (!g.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        timeService.resume(g);
        timeService.tick(g);
        gargoyleRepository.save(g);

        Map<String, Object> stats = new HashMap<>();
        stats.put("activeSeconds", g.getActiveMinutes() * 60);
        stats.put("hunger", g.getHunger());
        stats.put("happiness", g.getHappiness());
        stats.put("health", g.getHealth());
        stats.put("activeMinutes", g.getActiveMinutes());
        stats.put("gameDaysOld", timeService.gameDaysOld(g));
        stats.put("minutesIntoDay", timeService.minutesIntoCurrentDay(g));
        stats.put("paused", g.isPaused());

        return stats;
    }

    @GetMapping("/game/check/evolution")
    @ResponseBody
    public boolean checkEvolution(Authentication authentication){

        User user = currentUserService.getCurrentUser(authentication);

        var gargoyles = gargoyleRepository.findAllByUserIdOrderByIdAsc(user.getId());

        Gargoyle g = gargoyles.stream()
                .filter(x -> x.getType() == Gargoyle.Type.CHILD)
                .findFirst()
                .orElse(gargoyles.get(0));

        timeService.resume(g);
        timeService.tick(g);
        gargoyleRepository.save(g);

        if (!g.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        return evolutionService.canEvolve(g);
    }

}
