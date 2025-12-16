package com.example.tama_gargoyles.controller;

import com.example.tama_gargoyles.repository.GargoyleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class GargoyleController {

    @Autowired
    GargoyleRepository gargoyleRepository;

}
