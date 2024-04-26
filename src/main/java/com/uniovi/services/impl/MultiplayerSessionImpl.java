package com.uniovi.services.impl;

import com.uniovi.entities.MultiplayerSession;
import com.uniovi.entities.Player;
import com.uniovi.repositories.MultiplayerSessionRepository;
import com.uniovi.repositories.PlayerRepository;
import com.uniovi.services.MultiplayerSessionService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MultiplayerSessionImpl implements MultiplayerSessionService {
    private final PlayerRepository playerRepository;
    private final MultiplayerSessionRepository multiplayerSessionRepository;


    public MultiplayerSessionImpl(PlayerRepository playerRepository, MultiplayerSessionRepository multiplayerSessionRepository) {
        this.playerRepository = playerRepository;
        this.multiplayerSessionRepository = multiplayerSessionRepository;
    }

    @Override
    @Transactional
    public Map<Player, Integer> getPlayersWithScores(int multiplayerCode) {
        MultiplayerSession session = multiplayerSessionRepository.findByMultiplayerCode(String.valueOf(multiplayerCode));
        Map<Player, Integer> playerScores = session.getPlayerScores();

        // Ordenar los jugadores por puntuación de mayor a menor
        List<Player> sortedPlayers = playerScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Map<Player, Integer> playersSorted = new HashMap<>();
        for (Player player : sortedPlayers) {
            playersSorted.put(player,playerScores.get(player));
        }
        return playersSorted;
    }

    @Override
    public void multiCreate(String code, Long id) {
        Player p = playerRepository.findById(id).get();
        multiplayerSessionRepository.save(new MultiplayerSession(code,p));
    }

    @Override
    @Transactional
    public void addToLobby(String code, Long id) {
        Player p = playerRepository.findById(id).get();
        MultiplayerSession ms=multiplayerSessionRepository.findByMultiplayerCode(code);
        ms.addPlayer(p);
        multiplayerSessionRepository.save(ms);
    }

    @Override
    @Transactional
    public void changeScore(String code, Long id, int score) {
        Player p = playerRepository.findById(id).get();
        MultiplayerSession ms=multiplayerSessionRepository.findByMultiplayerCode(code);
        ms.getPlayerScores().put(p,score);
        multiplayerSessionRepository.save(ms);
    }
}
