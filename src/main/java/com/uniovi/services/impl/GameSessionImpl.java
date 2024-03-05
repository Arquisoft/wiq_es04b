package com.uniovi.services.impl;

import com.uniovi.entities.Player;
import com.uniovi.repositories.GameSessionRepository;
import com.uniovi.entities.GameSession;
import com.uniovi.services.GameSessionService;

import java.util.List;

public class GameSessionImpl implements GameSessionService {

    private final GameSessionRepository gameSessionRepository;

    public GameSessionImpl(GameSessionRepository gameSessionRepository) {
        this.gameSessionRepository = gameSessionRepository;
    }

    @Override
    public List<GameSession> getGameSessions() {
        return gameSessionRepository.findAll();
    }

    @Override
    public List<GameSession> getGameSessionsByPlayer(Player player) {

        return gameSessionRepository.findAllByPlayer(player);
    }

}
