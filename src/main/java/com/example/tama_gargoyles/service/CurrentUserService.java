package com.example.tama_gargoyles.service;

import com.example.tama_gargoyles.model.User;
import com.example.tama_gargoyles.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * TEMPORARY MVP SERVICE.
 * Replace implementation when Auth0 is integrated.
 *
 * Team contribution idea:
 * - When Auth0 is ready, this class becomes:
 *   "get current user id from Auth0 principal -> find or create user in DB".
 */
@Service
public class CurrentUserService {
    private final UserRepository users;

    public CurrentUserService(UserRepository users) {
        this.users = users;
    }

    // MVP shortcut: always return user id 1 (stoneking)
    public User getCurrentUser() {
        return users.findById(1L).orElseThrow();
    }
}
