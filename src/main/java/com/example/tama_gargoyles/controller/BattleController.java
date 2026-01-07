package com.example.tama_gargoyles.controller;

import com.example.tama_gargoyles.battle.BattleMove;
import com.example.tama_gargoyles.battle.BattleState;
import com.example.tama_gargoyles.model.Gargoyle;
import com.example.tama_gargoyles.model.User;
import com.example.tama_gargoyles.repository.GargoyleRepository;
import com.example.tama_gargoyles.service.BattleService;
import com.example.tama_gargoyles.service.CurrentUserService;
import com.example.tama_gargoyles.service.GargoyleTimeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.security.core.Authentication;
import com.example.tama_gargoyles.repository.UserRepository;

import java.util.List;

/**
 * WHAT IT DOES:
 * - GET  /battle        -> render battle page
 * - POST /battle/move   -> user clicks a move button (slam/sneak/dash)
 * - POST /battle/reset  -> reset the battle
 *
 * IMPORTANT:
 * - Uses @SessionAttributes so BattleState persists across requests.
 * - Uses your CurrentUserService + GargoyleRepository to get the user's creature.
 */
@Controller
@SessionAttributes("battleState")
public class BattleController {

    // ---- TUNING KNOB: decide adulthood here ----
    private static final long ADULT_AT_GAME_DAYS = 3;

    private final CurrentUserService currentUserService;
    private final GargoyleRepository gargoyleRepository;
    private final BattleService battleService;
    private final GargoyleTimeService timeService;
    private final UserRepository userRepository;

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
    }

    /**
     * Creates the session-scoped state object the first time /battle is visited.
     */
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
            return "redirect:/game"; // or "/" depending on your flow
        }

        // Pick an adult-eligible gargoyle (derived from virtual time)
        Gargoyle battler = gargoyles.stream()
                .filter(this::isBattleEligible)
                .findFirst()
                .orElse(null);

        // Enforce adult-only rule
        if (battler == null) {
            redirectAttributes.addFlashAttribute(
                    "battleError",
                    "Your gargoyle must an adult to enter battle. Keep playing to grow up!"
            );
            return "redirect:/game";
        }
        // keep time consistent when entering battle (prevents offline gap)
        timeService.resume(battler);
        timeService.tick(battler);
        gargoyleRepository.save(battler);

        model.addAttribute("gargoyle", battler);
        model.addAttribute("state", state);
        model.addAttribute("winnerText", state.winnerText());

        // Helpful UI facts
        model.addAttribute("gameDaysOld", timeService.gameDaysOld(battler));
        model.addAttribute("adultAtDays", ADULT_AT_GAME_DAYS);

        return "battle";
    }

    @PostMapping("/battle/move")
    public RedirectView playMove(Authentication authentication,
                                 @RequestParam("move") String move,
                                 @ModelAttribute("battleState") BattleState state,
                                 RedirectAttributes redirectAttributes) {

        // Server-side enforcement again (don't trust UI)
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

        // ✅ HARD TRANSITION LIKE PONG
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

    // ---- Helper: adulthood derived from vertual time ----
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

        String rewardMessage;

        if (userWon) {
            // 🏆 WIN REWARDS
            battler.setHappiness(100);          // Full happiness
            // Health unchanged

            user.setRocks(user.getRocks() + 3);
            user.setBugs(user.getBugs() + 3);
            user.setFruits(user.getFruits() + 3);
            user.setMysteryFood(user.getMysteryFood() + 1);

            rewardMessage = "Victory! Your gargoyle is thrilled and earned rich rewards!";
        } else {
            // 💀 LOSS PENALTIES
            battler.setHealth(Math.max(0, battler.getHealth() - 20));

            user.setRocks(user.getRocks() + 1);
            user.setBugs(user.getBugs() + 1);
            user.setFruits(user.getFruits() + 1);

            rewardMessage = "Defeat… your gargoyle is hurt but learned from the battle.";
        }

        // Persist changes
        gargoyleRepository.save(battler);
        userRepository.save(user);

        // UI data
        model.addAttribute("winnerText", state.winnerText());
        model.addAttribute("rewardMessage", rewardMessage);

        // ✅ IMPORTANT: reset battle AFTER rewards
        state.reset();

        return "battle-result";
    }


}
