package com.uniovi.services;

import com.uniovi.entities.GameSession;
import com.uniovi.entities.Player;
import com.uniovi.services.impl.GameSessionImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

public interface GameSessionService {

    /**
     * Return the list of GameSessions
     *
     * @return the list of GameSessions
     */
    List<GameSession> getGameSessions();

    /**
     * Return the global ranking
     *
     * @param pageable the pageable
     * @return the global ranking
     */
    Page<Object[]> getGlobalRanking(Pageable pageable);

    /**
     * Return the player ranking
     *
     * @param pageable the pageable
     * @param player the player
     * @return the player ranking
     */
    Page<GameSession> getPlayerRanking(Pageable pageable, Player player);

    GameSession startNewGame(Player player);
    void endGame(GameSession gameSession);
}
