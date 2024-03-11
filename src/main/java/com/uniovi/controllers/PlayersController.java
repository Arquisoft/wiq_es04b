package com.uniovi.controllers;

import com.uniovi.configuration.SecurityConfig;
import com.uniovi.entities.Player;
import com.uniovi.services.GameSessionService;
import com.uniovi.services.PlayerService;
import com.uniovi.validators.SignUpValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
    private final SignUpValidator signUpValidator;

    private final GameSessionService gameSessionService;

    @Autowired
    public PlayersController(PlayerService playerService, SignUpValidator signUpValidator, GameSessionService gameSessionService) {
        this.playerService = playerService;
        this.signUpValidator =  signUpValidator;
        this.gameSessionService = gameSessionService;
    }

    @GetMapping("/signup")
    public String showRegistrationForm(Model model){
        if (SecurityConfig.isAuthenticated())
            return "redirect:/home";

        if (model.containsAttribute("user")) {
            model.addAttribute("user", model.getAttribute("user"));
            return "player/signup";
        }

        model.addAttribute("user", new PlayerDto());
        return "player/signup";
    }

    @PostMapping("/signup")
    public String registerUserAccount(HttpServletRequest request, @Validated @ModelAttribute("user") PlayerDto user, BindingResult result, Model model){
        if (SecurityConfig.isAuthenticated())
            return "redirect:/home";

        signUpValidator.validate(user, result);

        if(result.hasErrors()) {
            model.addAttribute("user", user);
            return "player/signup";
        }

        playerService.addNewPlayer(user);

        try {
            request.login(user.getUsername(), user.getPassword());
        } catch (ServletException e) {
            return "redirect:/signup";
        }
        return "redirect:/home";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model, @RequestParam(value = "error", required = false) String error,
                                HttpSession session) {
        if (error != null) {
            model.addAttribute("error", session.getAttribute("loginErrorMessage"));
            System.out.println(session.getAttribute("loginErrorMessage"));
        }

        if (SecurityConfig.isAuthenticated())
            return "redirect:/home";

        return "player/login";
    }

    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        return "player/home";
    }

    @GetMapping("/playerRanking")
    public String showRanking(Model model, Pageable pageable, Principal principal) {
        Player player = playerService.getUserByUsername(principal.getName()).get();
        model.addAttribute("ranking", gameSessionService.getGameSessionsByPlayer(pageable,player));
        return "fragments/playerRanking";
    }

    @GetMapping("/globalRanking")
    public String showGlobalRanking(Model model) {
        model.addAttribute("ranking", gameSessionService.getSortedPlayersScores());
        return "fragments/globalRanking";
    }
}
