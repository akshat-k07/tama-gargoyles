package com.example.tama_gargoyles.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    @Value("${auth0.domain}")
    private String auth0Domain;

    @Value("${CLIENT_DOMAIN}")
    private String issuer;

    @Value("${auth0.client-id}")
    private String clientId;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/error",
                                "/favicon.ico",
                                "/apple-touch-icon.png",
                                "/apple-touch-icon-precomposed.png",
                                "/css/**", "/js/**", "/images/**", "/games/memoryGame/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .oauth2Login(Customizer.withDefaults())

                // Optional but recommended for MVP:
                // prevents “saved request” redirecting you to random assets after login
                .requestCache(cache -> cache.disable())

                .logout(logout -> logout.logoutSuccessHandler((request, response, authentication) -> {
                    String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                    response.sendRedirect(issuer + "v2/logout?client_id=" + clientId + "&returnTo=" + baseUrl);
                }));

        return http.build();
    }
}
