package com.uniovi.services;

import com.uniovi.entities.GameSession;
import com.uniovi.entities.Player;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
@Service
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

    HashMap<Player,Integer> getSortedPlayersScores();

}
