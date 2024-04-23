package com.uniovi.controllers;

import com.uniovi.entities.GameSession;
import com.uniovi.entities.Player;
import com.uniovi.entities.Question;
import com.uniovi.services.GameSessionService;
import com.uniovi.services.MultiplayerSessionService;
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
import java.util.*;

@Controller
public class GameController {
    private final QuestionService questionService;
    private final GameSessionService gameSessionService;
    private final PlayerService playerService;

    private final MultiplayerSessionService multiplayerSessionService;

    public GameController(QuestionService questionService, GameSessionService gameSessionService,
                          PlayerService playerService, MultiplayerSessionService multiplayerSessionService) {
        this.questionService = questionService;
        this.gameSessionService = gameSessionService;
        this.playerService = playerService;
        this.multiplayerSessionService = multiplayerSessionService;
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
            playerService.deleteMultiplayerCode(gameSession.getPlayer().getId());
            session.setAttribute("gameSession", gameSession);

        }

        model.addAttribute("question", gameSession.getCurrentQuestion());
        model.addAttribute("questionDuration", getRemainingTime(gameSession));
        return "game/basicGame";
    }




    @GetMapping("/multiplayerGame")
    public String getMultiplayerGame() {
        //EL elminar el multiplaterCode del jugador se puede hacer cuando comienze el proximo
        //juego con amigos o cuando acaba la partida, lo suyo seria cuando acabe
        //ya mirare como
        return "game/multiplayerGame";
    }

    @GetMapping("/multiplayerGame/{code}")
    public String joinMultiplayerGame(@PathVariable String code, HttpSession session, Principal principal, Model model) {
        Optional<Player> player = playerService.getUserByUsername(principal.getName());
        Player p = player.orElse(null);
        if(playerService.changeMultiplayerCode(p.getId(),code)){
            multiplayerSessionService.addToLobby(code,p.getId());
            model.addAttribute("multiplayerGameCode",code);
            session.setAttribute("multiplayerCode",code);
            return "redirect:/game/lobby";
        } else {
            //Hay q tratarlo como como se hace en sinUp, hacienado validate y tal
            return "redirect:/multiplayerGame";
        }
    }

    @GetMapping("/multiplayerGame/createGame")
    public String createMultiplayerGame(HttpSession session, Principal principal, Model model) {
        Optional<Player> player = playerService.getUserByUsername(principal.getName());
        Player p = player.orElse(null);
        String code=""+playerService.createMultiplayerGame(p.getId());
        multiplayerSessionService.multiCreate(code,p.getId());
        session.setAttribute("multiplayerCode",code);
        return "redirect:/game/lobby";
    }

    @GetMapping("/startMultiplayerGame")
    public String startMultiplayerGame(HttpSession session, Model model, Principal principal) {
        GameSession gameSession = (GameSession) session.getAttribute("gameSession");

        if (gameSession != null) {
            if (checkUpdateGameSession(gameSession, session)) {
                return "game/fragments/gameFinished";
            }
        } else {
            gameSession = gameSessionService.startNewMultiplayerGame(getLoggedInPlayer(principal),
                    playerService.getUserByUsername(principal.getName()).get().getMultiplayerCode());
            session.setAttribute("gameSession", gameSession);

        }

        model.addAttribute("question", gameSession.getCurrentQuestion());
        model.addAttribute("questionDuration", getRemainingTime(gameSession));
        return "game/basicGame";
    }

//    @GetMapping("/games/multiFinished")
//    public String createFinishedLobby( HttpSession session, Model model) {
//        int code = Integer.parseInt((String)session.getAttribute("multiplayerCode"));
//        List<Player> players=playerService.getUsersByMultiplayerCode(code);
//        model.addAttribute("players",players);
//        model.addAttribute("code",session.getAttribute("multiplayerCode"));
//        session.removeAttribute("gameSession");
//        return "/game/multiFinished";
//    }


//    @GetMapping("/multiplayerGame/finishedGame")
//    public String goToFinishedLobby(HttpSession session,Principal principal) {
//        Optional<Player> player = playerService.getUserByUsername(principal.getName());
//        Player p = player.orElse(null);
//        GameSession gameSession = (GameSession) session.getAttribute("gameSession");
//        playerService.setScoreMultiplayerCode(p.getId(),""+gameSession.getScore());
//        return "redirect:/game/multiFinished";
//    }
//


    @GetMapping("/game/multiFinished/{code}")
    @ResponseBody
    public Map<String,String> updateFinishedGame(@PathVariable String code, HttpSession session, Principal principal) {
        List<Player> players = playerService.getUsersByMultiplayerCode(Integer.parseInt(code));
        Map<String, String> playerInfo = new HashMap<>();
        for (Player player : players) {
            String playerName = player.getUsername();
            String playerScore = player.getScoreMultiplayerCode();
            if(playerScore==null){
                playerScore="N/A";
            }
            playerInfo.put(playerName, playerScore);
        }
        return playerInfo;
    }

//    @GetMapping("/multiplayerGame/endGame/{code}")
//    public String endMultiplayerGame(@PathVariable String code) {
//        List<Player> players = playerService.getUsersByMultiplayerCode(Integer.parseInt(code));
//        for (Player player : players) {
//            player.setScoreMultiplayerCode(null);
//        }
//        return "redirect:/index.html";
//    }



    @GetMapping("/game/lobby/{code}")
    @ResponseBody
    public List<String> updatePlayerList(@PathVariable String code) {
        List<Player> players = playerService.getUsersByMultiplayerCode(Integer.parseInt(code));
        List<String> playerNames = new ArrayList<>();
        for (Player player : players) {
            playerNames.add(player.getUsername());
        }
        return playerNames;
    }
    @GetMapping("/game/lobby")
    public String createLobby( HttpSession session, Model model) {
        int code = Integer.parseInt((String)session.getAttribute("multiplayerCode"));
        List<Player> players=playerService.getUsersByMultiplayerCode(code);
        model.addAttribute("players",players);
        model.addAttribute("code",session.getAttribute("multiplayerCode"));
        return "/game/lobby";
    }

    @GetMapping("/game/startMultiplayerGame")
    public String startMultiplayerGame( HttpSession session, Model model) {
        //La idea seria q dando uno al boton de empezar empezasen todos
        return "/game/lobby";
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
    public String updateGame(Model model, HttpSession session, Principal principal) {
        GameSession gameSession = (GameSession) session.getAttribute("gameSession");
        Question nextQuestion = gameSession.getCurrentQuestion();
        if(nextQuestion == null && gameSession.getPlayer().getMultiplayerCode()!=null/*session.getAttribute("multiplayerCode") !=null*/){
            gameSessionService.endGame(gameSession);

            int code = Integer.parseInt((String)session.getAttribute("multiplayerCode"));
            List<Player> players=playerService.getUsersByMultiplayerCode(code);
            model.addAttribute("players",players);
            model.addAttribute("code",session.getAttribute("multiplayerCode"));
            session.removeAttribute("gameSession");

            Optional<Player> player = playerService.getUserByUsername(principal.getName());
            Player p = player.orElse(null);
            playerService.setScoreMultiplayerCode(p.getId(),""+gameSession.getScore());

            return "game/multiFinished";
        }
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
