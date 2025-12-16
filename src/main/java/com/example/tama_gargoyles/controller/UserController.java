package com.example.tama_gargoyles.controller;


import com.example.tama_gargoyles.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

    @Autowired
    UserRepository userRepository;
}
