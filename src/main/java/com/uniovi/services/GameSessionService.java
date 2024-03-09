package com.uniovi.services;

import com.uniovi.entities.GameSession;
import com.uniovi.entities.Player;

import java.util.List;

public interface GameSessionService {

    /**
     * Return the list of GameSessions
     *
     * @return the list of GameSessions
     */
    List<GameSession> getGameSessions();

    /**
     * Return the list of GameSessions by player
     *
     * @return the list of GameSessions by player
     */
    List<GameSession> getGameSessionsByPlayer(Player player);

}
