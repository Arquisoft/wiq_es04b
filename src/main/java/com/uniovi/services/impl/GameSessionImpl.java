package com.uniovi.services.impl;

import com.uniovi.entities.Player;
import com.uniovi.repositories.GameSessionRepository;
import com.uniovi.entities.GameSession;
import com.uniovi.services.GameSessionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
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
    public Page<GameSession> getGameSessionsByPlayer(Pageable pageable, Player player) {

        return gameSessionRepository.findAllByPlayer(pageable,player);
    }

    @Override
    public HashMap<Player, Integer> getSortedPlayersScores() {
        List<GameSession> gameSessions = gameSessionRepository.findAll();
        HashMap<Player, Integer> ranking = getRanking(gameSessions);
        // Ordenar las entradas del ranking por puntuación
        List<Map.Entry<Player, Integer>> sortedEntries = new ArrayList<>(ranking.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Crear un LinkedHashMap para mantener el orden de inserción
        LinkedHashMap<Player, Integer> sortedRanking = new LinkedHashMap<>();
        for (Map.Entry<Player, Integer> entry : sortedEntries) {
            sortedRanking.put(entry.getKey(), entry.getValue());
        }

        return sortedRanking;
    }

    private static HashMap<Player, Integer> getRanking(List<GameSession> gameSessions) {
        HashMap<Player, Integer> ranking = new HashMap<>();

        // Iterar a través de las sesiones de juego
        for (GameSession gameSession : gameSessions) {
            Player player = gameSession.getPlayer();
            int score = gameSession.getScore();

            // Si el jugador ya está en el ranking, sumar la puntuación, de lo contrario, agregarlo al ranking
            if (ranking.containsKey(player)) {
                int currentScore = ranking.get(player) + score;
                ranking.put(player, currentScore);
            } else {
                ranking.put(player, score);
            }
        }
        return ranking;
    }
}
