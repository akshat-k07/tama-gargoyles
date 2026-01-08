package com.example.tama_gargoyles.controller;

import com.example.tama_gargoyles.battle.BattleMove;
import com.example.tama_gargoyles.battle.BattleState;
import com.example.tama_gargoyles.model.Gargoyle;
import com.example.tama_gargoyles.model.User;
import com.example.tama_gargoyles.repository.GargoyleRepository;
import com.example.tama_gargoyles.repository.UserRepository;
import com.example.tama_gargoyles.service.BattleService;
import com.example.tama_gargoyles.service.CurrentUserService;
import com.example.tama_gargoyles.service.EvolutionService;
import com.example.tama_gargoyles.service.GargoyleTimeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

@Controller
@SessionAttributes("battleState")
public class BattleController {

    private static final long ADULT_AT_GAME_DAYS = 3;

    private final CurrentUserService currentUserService;
    private final GargoyleRepository gargoyleRepository;
    private final BattleService battleService;
    private final GargoyleTimeService timeService;
    private final UserRepository userRepository;
    private final EvolutionService evolutionService;

    public BattleController(CurrentUserService currentUserService,
                            GargoyleRepository gargoyleRepository,
                            BattleService battleService,
                            GargoyleTimeService timeService,
                            UserRepository userRepository) {
        this.currentUserService = currentUserService;
        this.gargoyleRepository = gargoyleRepository;
        this.battleService = battleService;
        this.timeService = timeService;
        this.userRepository = userRepository;
        this.evolutionService = new EvolutionService();
    }

    @ModelAttribute("battleState")
    public BattleState battleState() {
        return new BattleState();
    }

    @GetMapping("/battle")
    public String battlePage(Authentication authentication,
                             @ModelAttribute("battleState") BattleState state,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        User user = currentUserService.getCurrentUser(authentication);

        List<Gargoyle> gargoyles = gargoyleRepository.findAllByUserIdOrderByIdAsc(user.getId());
        if (gargoyles.isEmpty()) {
            redirectAttributes.addFlashAttribute("battleError", "You don't have a gargoyle yet.");
            return "redirect:/game";
        }

        // Pick an adult-eligible gargoyle
        Gargoyle battler = gargoyles.stream()
                .filter(this::isBattleEligible)
                .findFirst()
                .orElse(null);

        if (battler == null) {
            redirectAttributes.addFlashAttribute(
                    "battleError",
                    "Your gargoyle must an adult to enter battle. Keep playing to grow up!"
            );
            return "redirect:/game";
        }

        // ✅ Reset battle state if previous game was finished
        if (state.isFinished()) {
            state.reset();
        }

        // Keep time consistent
        timeService.resume(battler);
        timeService.tick(battler);
        gargoyleRepository.save(battler);

        model.addAttribute("gargoyle", battler);
        model.addAttribute("state", state);
        model.addAttribute("winnerText", state.winnerText());

        model.addAttribute("image_path", evolutionService.getGargoyleImagePath(battler));

        model.addAttribute("gameDaysOld", timeService.gameDaysOld(battler));
        model.addAttribute("adultAtDays", ADULT_AT_GAME_DAYS);

        return "battle";
    }

    @PostMapping("/battle/move")
    public RedirectView playMove(Authentication authentication,
                                 @RequestParam("move") String move,
                                 @ModelAttribute("battleState") BattleState state,
                                 RedirectAttributes redirectAttributes) {

        User user = currentUserService.getCurrentUser(authentication);

        List<Gargoyle> gargoyles = gargoyleRepository.findAllByUserIdOrderByIdAsc(user.getId());
        Gargoyle battler = gargoyles.stream()
                .filter(this::isBattleEligible)
                .findFirst()
                .orElse(null);

        if (battler == null) {
            redirectAttributes.addFlashAttribute(
                    "battleError",
                    "Battle blocked: your gargoyle is not an adult yet."
            );
            return new RedirectView("/game");
        }

        BattleMove userMove = BattleMove.valueOf(move);
        battleService.playTurn(state, userMove);

        if (state.isFinished()) {
            return new RedirectView("/battle-result");
        }

        return new RedirectView("/battle");
    }

    @PostMapping("/battle/reset")
    public RedirectView reset(@ModelAttribute("battleState") BattleState state) {
        state.reset();
        return new RedirectView("/battle");
    }

    private boolean isBattleEligible(Gargoyle g) {
        return g.getType() != Gargoyle.Type.CHILD;
    }

    @GetMapping("/battle-result")
    public String battleResult(Authentication authentication,
                               @ModelAttribute("battleState") BattleState state,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (!state.isFinished()) {
            redirectAttributes.addFlashAttribute(
                    "battleError",
                    "Battle is not finished yet."
            );
            return "redirect:/battle";
        }

        User user = currentUserService.getCurrentUser(authentication);

        Gargoyle battler = gargoyleRepository
                .findAllByUserIdOrderByIdAsc(user.getId())
                .stream()
                .filter(this::isBattleEligible)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No eligible gargoyle found"));

        boolean userWon = state.getUserScore() >= BattleState.WIN_SCORE;

        // List to hold rewards for the UI
        List<String> rewards = new ArrayList<>();

        if (!state.rewardsApplied()) {
            if (userWon) {
                battler.setHappiness(100);
                rewards.add("😊 Happiness 100%");

                user.addRocks(1);
                rewards.add("🪨 1 Rock");

                user.addBugs(1);
                rewards.add("🐛 1 Bug");

                user.addFruits(1);
                rewards.add("🍎 1 Fruit");

                rewards.add("\uD83C\uDF56 -10% Hunger");

                battler.setHunger(Math.max(battler.getHunger() - 10, 0));
//
//                user.addMysteryFood(1);
//                rewards.add("❓ 1 Mystery Food");

            } else {
                battler.setHealth(Math.max(0, battler.getHealth() - 20));
                battler.setHunger(Math.max(battler.getHunger() - 30, 0));
                rewards.add("❤️ -20% Health");
                rewards.add("\uD83C\uDF56 -30% Hunger");
            }

            gargoyleRepository.save(battler);
            userRepository.save(user);

            state.markRewardsApplied();
        }

        model.addAttribute("winnerText", state.winnerText());
        model.addAttribute("rewards", rewards);
        model.addAttribute("userWon", userWon);

        state.reset(); // reset after showing result

        return "battle-result";
    }


    @GetMapping("/battle/reset-and-exit")
    public String resetAndExit(@ModelAttribute("battleState") BattleState state) {
        state.reset();
        return "redirect:/game";
    }
}
