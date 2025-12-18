package com.example.tama_gargoyles.controller;

import com.example.tama_gargoyles.model.Gargoyle;
import com.example.tama_gargoyles.model.User;
import com.example.tama_gargoyles.repository.GargoyleRepository;
import com.example.tama_gargoyles.repository.UserRepository;
import com.example.tama_gargoyles.service.CurrentUserService;
import com.example.tama_gargoyles.service.GargoyleTimeService;
import jakarta.persistence.GeneratedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

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

    @PostMapping("/hunger-increase")
    public RedirectView increaseHunger(@RequestParam Integer delta, @RequestParam Long gargoyleId){
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).get();
        gargoyle.setHunger(Math.min(gargoyle.getHunger() + delta, 100));
        gargoyleRepository.save(gargoyle);
        return new RedirectView("/game");
    }

    @PostMapping("/entertainment-increase")
    public RedirectView increaseHappiness(@RequestParam Integer delta, @RequestParam Long gargoyleId){
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).get();
        gargoyle.setHappiness(Math.min(gargoyle.getHappiness() + delta, 100));
        gargoyleRepository.save(gargoyle);
        return new RedirectView("/game");
    }

    @PostMapping("/entertainment-decrease")
    public RedirectView decreaseHappiness(@RequestParam Integer delta, @RequestParam Long gargoyleId){
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).get();
        gargoyle.setHappiness(Math.max(gargoyle.getHappiness() - delta, 0));
        gargoyleRepository.save(gargoyle);
        return new RedirectView("/game");
    }

    @PostMapping("/hunger-decrease")
    public RedirectView decreaseHunger(@RequestParam Integer delta, @RequestParam Long gargoyleId){
        Gargoyle gargoyle = gargoyleRepository.findById(gargoyleId).get();
        gargoyle.setHunger(Math.max(gargoyle.getHunger() - delta, 0));
        gargoyleRepository.save(gargoyle);
        return new RedirectView("/game");
    }

    @GetMapping("/game")
    public String game(Model model) {
        User user = currentUserService.getCurrentUser();

        var gargoyles = gargoyleRepository.findAllByUserIdOrderByIdAsc(user.getId());
        if (gargoyles.isEmpty()) {
            // MVP: no creature yet - send somewhere sensible
            return "redirect:";
        }

        // MVP selection rule:
        // Prefer CHILD if one exists, otherwise pick the first by id.
        Gargoyle g = gargoyles.stream()
                .filter(x -> x.getType() == Gargoyle.Type.CHILD)
                        .findFirst()
                                .orElse(gargoyles.get(0));

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
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/gargoyles")
    public ModelAndView allGargoyles() {
        ModelAndView modelAndView = new ModelAndView("gargoyles/gargoyles");
        modelAndView.addObject("gargoyles", gargoyleRepository.findAll());
        return modelAndView;
    }

    @GetMapping("/gargoyles/new")
    public ModelAndView newGargoyleForm() {
        // this is the space referred to in th:object (look back at the form code)
        Gargoyle gargoyle = new Gargoyle();
        ModelAndView newGargoyleForm = new ModelAndView("gargoyles/new");
        newGargoyleForm.addObject("gargoyle", gargoyle);
        return newGargoyleForm;
    }
    @PostMapping("/gargoyles")
    // Spring Boot uses the form data to create an instance of gargoyle
    // which is then passed in as an arg here
    public RedirectView create(Gargoyle gargoyle) {
        User user = userRepository.findById(1L).orElseThrow();
        gargoyle.setUser(user);
        if (gargoyle.getName().isEmpty() || gargoyle.getName().length() > 30){
            return new RedirectView("/gargoyles");
        }else{
            gargoyleRepository.save(gargoyle);
        }
        return new RedirectView("/gargoyles");
    }

    @GetMapping("/gargoyle/{id}/rename")
    public ModelAndView showRenameForm(@PathVariable Long id) {
        Gargoyle gargoyle = gargoyleRepository.findById(id)
                .orElseThrow();

        return new ModelAndView("gargoyles/renameGargoyle", "gargoyle", gargoyle);
    }


    @PostMapping("/gargoyle/{id}/rename")
    public String renameGargoyle(
            @PathVariable Long id,
            @RequestParam String name
    ) {
        Gargoyle gargoyle = gargoyleRepository.findById(id)
                .orElseThrow();


        // backend name validation
        if (name.isEmpty() || name.length() > 30){
            return "redirect:/game";
        }else{
            System.out.println(name.length());
            gargoyle.setName(name);
            gargoyleRepository.save(gargoyle);
        }

        return "redirect:/game";
    }



}
