package com.uniovi.controllers;

import com.uniovi.services.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Random;

@Controller
public class GameController {

    private final QuestionService questionService;

    public GameController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/game")
    public String getGame(Model model) {
        model.addAttribute("question", questionService.getAllQuestions().get(new Random().nextInt(questionService.getAllQuestions().size())));
        return "game/basicGame";
    }
}
