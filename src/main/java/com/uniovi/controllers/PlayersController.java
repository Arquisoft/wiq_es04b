package com.uniovi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.uniovi.dto.PlayerDto;

@Controller
public class PlayersController {
    @GetMapping("/signup")
    public String showRegistrationForm(Model model){
        // create model object to store form data
        PlayerDto user = new PlayerDto();
        model.addAttribute("user", user);
        return "signup";
    }
}
