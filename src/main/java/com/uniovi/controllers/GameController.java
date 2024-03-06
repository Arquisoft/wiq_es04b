package com.uniovi.controllers;

import com.uniovi.services.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GameController {

    private final QuestionService questionService;

    public GameController(QuestionService questionService) {
        this.questionService = questionService;
    }


    /**
     * This method is used to get the game view and to start the game
     * @param model The model to be used
     * @return The view to be shown
     */
    @GetMapping("/game")
    public String getGame(Model model) {
        model.addAttribute("question", questionService.getRandomQuestion().get());
        return "game/basicGame";
    }


    /**
     * This method is used to check the answer for a specific question
     * @param idQuestion The id of the question.
     * @param idAnswer The id of the answer. If the id is -1, it means that the user has not selected any answer and the
     *                 time has run out.
     * @param model The model to be used.
     * @return The view to be shown, if the answer is correct, the success view is shown, otherwise the failure view is
     * shown or the timeOutFailure view is shown.
     */
    @GetMapping("/game/{idQuestion}/{idAnswer}")
    public String getCheckResult(@PathVariable Long idQuestion, @PathVariable Long idAnswer, Model model) {
        if(idAnswer == -1) {
            model.addAttribute("correctAnswer", questionService.getQuestion(idQuestion).get().getCorrectAnswer());
            return "game/fragments/timeRunOut";
        }
        else if(questionService.checkAnswer(idQuestion, idAnswer)) {
            return "game/fragments/correctAnswer";
        } else {
            model.addAttribute("correctAnswer", questionService.getQuestion(idQuestion).get().getCorrectAnswer());
            return "game/fragments/failedAnswer";
        }
    }

    @GetMapping("/game/update")
    public String updateGame(Model model) {
        model.addAttribute("question", questionService.getRandomQuestion().get());
        return "game/fragments/gameFrame";
    }
}
