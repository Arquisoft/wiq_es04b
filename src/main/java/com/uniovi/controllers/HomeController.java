package com.uniovi.controllers;

import com.uniovi.services.PlayerService;
import com.uniovi.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Random;

@Controller
public class HomeController{
    private final PlayerService playerService;

    @Autowired
    public HomeController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/")
    public String home(){
        return "index";
    }

    @GetMapping("/api")
    public String apiHome() {
        return "api/apiHome";
    }
}
