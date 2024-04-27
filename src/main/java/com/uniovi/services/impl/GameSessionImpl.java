package com.uniovi.services.impl;

import com.uniovi.entities.Associations;
import com.uniovi.entities.Player;
import com.uniovi.repositories.GameSessionRepository;
import com.uniovi.entities.GameSession;
import com.uniovi.services.GameSessionService;
import com.uniovi.services.QuestionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class GameSessionImpl implements GameSessionService {
    public static final Integer NORMAL_GAME_QUESTION_NUM = 4;

    private final GameSessionRepository gameSessionRepository;
    private final QuestionService questionService;

    public GameSessionImpl(GameSessionRepository gameSessionRepository, QuestionService questionService) {
        this.gameSessionRepository = gameSessionRepository;
        this.questionService = questionService;
    }

    @Override
    public List<GameSession> getGameSessions() {
        return gameSessionRepository.findAll();
    }

    @Override
    public List<GameSession> getGameSessionsByPlayer(Player player) {

        return gameSessionRepository.findAllByPlayer(player);
    }

    public Page<Object[]> getGlobalRanking(Pageable pageable) {
        return gameSessionRepository.findTotalScoresByPlayer(pageable);
    }

    @Override
    public Page<GameSession> getPlayerRanking(Pageable pageable, Player player) {
        return gameSessionRepository.findAllByPlayerOrderByScoreDesc(pageable, player);
    }

    @Override
    public GameSession startNewGame(Player player) {
        return new GameSession(player, questionService.getRandomQuestions(NORMAL_GAME_QUESTION_NUM));
    }

    @Override
    public GameSession startNewMultiplayerGame(Player player, int code) {
        return new GameSession(player, questionService.getRandomMultiplayerQuestions(NORMAL_GAME_QUESTION_NUM,code));
    }

    @Override
    public void endGame(GameSession gameSession) {
        Associations.PlayerGameSession.addGameSession(gameSession.getPlayer(), gameSession);
        gameSession.setFinishTime(LocalDateTime.now());
        gameSessionRepository.save(gameSession);
    }
}
