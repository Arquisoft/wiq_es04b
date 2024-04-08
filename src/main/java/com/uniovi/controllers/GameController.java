package com.uniovi.controllers;

import com.uniovi.entities.GameSession;
import com.uniovi.entities.Player;
import com.uniovi.entities.Question;
import com.uniovi.services.GameSessionService;
import com.uniovi.services.PlayerService;
import com.uniovi.services.QuestionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class GameController {
    private final QuestionService questionService;
    private final GameSessionService gameSessionService;
    private final PlayerService playerService;

    public GameController(QuestionService questionService, GameSessionService gameSessionService,
                          PlayerService playerService) {
        this.questionService = questionService;
        this.gameSessionService = gameSessionService;
        this.playerService = playerService;
    }


    /**
     * This method is used to get the game view and to start the game
     * @param model The model to be used
     * @return The view to be shown
     */
    @GetMapping("/game")
    public String getGame(HttpSession session, Model model, Principal principal) {
        GameSession gameSession = (GameSession) session.getAttribute("gameSession");
        if (gameSession != null) {
            if (checkUpdateGameSession(gameSession, session)) {
                return "game/fragments/gameFinished";
            }
        } else {
            gameSession = gameSessionService.startNewGame(getLoggedInPlayer(principal));
            session.setAttribute("gameSession", gameSession);
        }

        model.addAttribute("question", gameSession.getCurrentQuestion());
        model.addAttribute("questionDuration", getRemainingTime(gameSession));
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
    public String getCheckResult(@PathVariable Long idQuestion, @PathVariable Long idAnswer, Model model, HttpSession session) {
        GameSession gameSession = (GameSession) session.getAttribute("gameSession");
        if (gameSession == null) {
            return "redirect:/game";
        }

        if (!gameSession.hasQuestionId(idQuestion)) {
            model.addAttribute("score", gameSession.getScore());
            session.removeAttribute("gameSession");
            return "redirect:/game"; // if someone wants to exploit the game, just redirect to the game page
        }

        if(idAnswer == -1
            || getRemainingTime(gameSession) <= 0) {
            model.addAttribute("correctAnswer", gameSession.getCurrentQuestion().getCorrectAnswer());
            model.addAttribute("messageKey", "timeRunOut.result");
            model.addAttribute("logoImage", "/images/logo_incorrect.svg");
            gameSession.addAnsweredQuestion(gameSession.getCurrentQuestion());
            gameSession.addQuestion(false, 0);
        }
        else if(questionService.checkAnswer(idQuestion, idAnswer)) {
            model.addAttribute("messageKey", "correctAnswer.result");
            model.addAttribute("logoImage", "/images/logo_correct.svg");

            if (!gameSession.isAnswered(gameSession.getCurrentQuestion())) {
                gameSession.addQuestion(true, getRemainingTime(gameSession));
                gameSession.addAnsweredQuestion(gameSession.getCurrentQuestion());
            }

        } else {
            model.addAttribute("correctAnswer", gameSession.getCurrentQuestion().getCorrectAnswer());
            model.addAttribute("messageKey", "failedAnswer.result");
            model.addAttribute("logoImage", "/images/logo_incorrect.svg");
            gameSession.addAnsweredQuestion(gameSession.getCurrentQuestion());
            gameSession.addQuestion(false, 0);
        }

        session.setAttribute("hasJustAnswered", true);
        gameSession.getNextQuestion();
        return "game/fragments/questionResult";
    }

    @GetMapping("/game/update")
    public String updateGame(Model model, HttpSession session) {
        GameSession gameSession = (GameSession) session.getAttribute("gameSession");
        Question nextQuestion = gameSession.getCurrentQuestion();
        if (nextQuestion == null) {
            gameSessionService.endGame(gameSession);
            session.removeAttribute("gameSession");
            model.addAttribute("score", gameSession.getScore());
            return "game/fragments/gameFinished";
        }

        if (session.getAttribute("hasJustAnswered") != null) {
            if ((boolean) session.getAttribute("hasJustAnswered"))
                gameSession.setFinishTime(LocalDateTime.now());
            session.removeAttribute("hasJustAnswered");
        }
        model.addAttribute("question", gameSession.getCurrentQuestion());
        model.addAttribute("questionDuration", getRemainingTime(gameSession));
        return "game/fragments/gameFrame";
    }

    @GetMapping("/game/finished/{points}")
    public String finishGame(@PathVariable int points, Model model, HttpSession session) {
        GameSession gameSession = (GameSession) session.getAttribute("gameSession");
        if (gameSession != null) {
            gameSessionService.endGame(gameSession);
            session.removeAttribute("gameSession");
        }
        model.addAttribute("score", points);
        return "game/gameFinished";
    }

    @GetMapping("/game/points")
    @ResponseBody
    public String getPoints(HttpSession session) {
        GameSession gameSession = (GameSession) session.getAttribute("gameSession");
        if (gameSession != null)
            return String.valueOf(gameSession.getScore());
        else
            return "0";
    }

    @GetMapping("/game/currentQuestion")
    @ResponseBody
    public String getCurrentQuestion(HttpSession session) {
        GameSession gameSession = (GameSession) session.getAttribute("gameSession");
        if (gameSession != null)
            return String.valueOf(gameSession.getAnsweredQuestions().size()+1);
        else
            return "0";
    }

    private Player getLoggedInPlayer(Principal principal) {
        Optional<Player> player = playerService.getUserByUsername(principal.getName());
        return player.orElse(null);
    }

    /**
     * This method is used to check if the game session has to be updated
     * @param gameSession The game session to be checked
     * @param session The session to be used
     * @return True if the game session has been ended, false otherwise
     */
    private boolean checkUpdateGameSession(GameSession gameSession, HttpSession session) {
        // if time since last question started is greater than the time per question, add a new question (or check for game finish)
        if (getRemainingTime(gameSession) <= 0
                && gameSession.getQuestionsToAnswer().isEmpty()
                && gameSession.getCurrentQuestion() != null) {
            gameSession.addQuestion(false, 0);
            gameSession.addAnsweredQuestion(gameSession.getCurrentQuestion());
            if (gameSession.getQuestionsToAnswer().isEmpty()) {
                gameSessionService.endGame(gameSession);
                session.removeAttribute("gameSession");
                return true;
            }
        }

        return false;
    }

    private int getRemainingTime(GameSession gameSession) {
        return (int) Duration.between(LocalDateTime.now(),
                gameSession.getFinishTime().plusSeconds(QuestionService.SECONDS_PER_QUESTION)).toSeconds();
    }
}
