package com.example.tama_gargoyles.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/minigame/strength")
public class StrengthMiniGameController {

    @GetMapping
    public String showGame() {
        return "strengthGame";
    }

    @PostMapping("/complete")
    @ResponseBody
    public void completeGame(@AuthenticationPrincipal OAuth2User user) {
        // Increase strength stat, save to DB, etc.
    }
}
