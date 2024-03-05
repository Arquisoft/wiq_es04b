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
    private final QuestionService questionService;

    @Autowired
    public HomeController(PlayerService playerService, QuestionService questionService) {
        this.playerService = playerService;
        this.questionService = questionService;
    }

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("question", questionService.getAllQuestions().get(new Random().nextInt(questionService.getAllQuestions().size())));
        return "index";
    }

    @GetMapping("/api")
    public String apiHome() {
        return "api/apiHome";
    }
}
