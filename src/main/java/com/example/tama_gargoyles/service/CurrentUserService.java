package com.example.tama_gargoyles.service;

import com.example.tama_gargoyles.model.User;
import com.example.tama_gargoyles.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CurrentUserService {
    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        Map<String, Object> attrs;
        Object principal = authentication.getPrincipal();

        if (principal instanceof OidcUser oidcUser) {
            attrs = oidcUser.getClaims();
        } else if (principal instanceof OAuth2User oAuth2User) {
            attrs = oAuth2User.getAttributes();
        } else {
            throw new IllegalStateException("Unsupported principal type: " + principal.getClass());
        }

        String email = (String) attrs.get("email");
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("Auth0 did not provide an email claim (check scopes/connection settings)");
        }

        return userRepository.findByEmail(email).orElseGet(() -> {
            User u = new User();
            u.setEmail(email);
            u.setRocks(0);
            u.setBugs(0);
            u.setMystery_food(0);

            String username = pickUsername(attrs, email);
            u.setUsername(makeUsernameUnique(username));

            return userRepository.save(u);
        });
    }

    private String pickUsername(Map<String, Object> attrs, String email) {
        // Try your custom claim first (you already have this in the logs)
        Object custom = attrs.get("https://myapp.com/username");
        if (custom instanceof String s && !s.isBlank()) return s;

        Object preferred = attrs.get("preferred_username");
        if (preferred instanceof String s && !s.isBlank()) return s;

        // fallback: email prefix
        String prefix = email.split("@")[0];
        return prefix.isBlank() ? "player" : prefix;
    }

    private String makeUsernameUnique(String base) {
        String candidate = base;

        int i = 0;
        while (userRepository.findByUsername(candidate).isPresent()) {
            i++;
            candidate = base + i; // e.g. claire, claire1, claire2
        }
        return candidate;
    }
}
