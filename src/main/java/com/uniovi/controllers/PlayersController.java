package com.uniovi.controllers;

import com.uniovi.entities.Player;
import com.uniovi.services.PlayerService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import com.uniovi.dto.PlayerDto;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Optional;

@Controller
public class PlayersController {
    private final PlayerService playerService;

    @Autowired
    public PlayersController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/signup")
    public String showRegistrationForm(Model model){
        if (model.containsAttribute("user")) {
            model.addAttribute("user", model.getAttribute("user"));
            return "/player/signup";
        }

        model.addAttribute("user", new PlayerDto());
        return "/player/signup";
    }

    @PostMapping("/signup")
    public String registerUserAccount(@Valid @ModelAttribute("user") PlayerDto user, BindingResult result, Model model){
        if(playerService.getUserByEmail(user.getEmail()).isPresent()){
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
        }

        if (playerService.getUserByUsername(user.getUsername()).isPresent()) {
            result.rejectValue("username", null,
                    "There is already an account registered with the same username");
        }

        if(result.hasErrors()){
            model.addAttribute("user", user);
            return "/player/signup";
        }

        playerService.addNewPlayer(user);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model, @RequestParam(value = "error", required = false) String error,
                                HttpSession session) {
        if (error != null) {
            model.addAttribute("error", session.getAttribute("loginErrorMessage"));
            System.out.println(session.getAttribute("loginErrorMessage"));
        }

        return "/player/login";
    }

    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        return "/player/home";
    }
}
