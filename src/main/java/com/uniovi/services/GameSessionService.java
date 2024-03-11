package com.uniovi.services;

import com.uniovi.entities.GameSession;
import com.uniovi.entities.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<GameSession> getGameSessionsByPlayer(Pageable pageable,Player player);

    HashMap<Player,Integer> getSortedPlayersScores();

}
