package com.uniovi.controllers;

import com.uniovi.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
