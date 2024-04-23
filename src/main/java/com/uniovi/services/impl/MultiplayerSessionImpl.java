package com.uniovi.services.impl;

import com.uniovi.entities.MultiplayerSession;
import com.uniovi.entities.Player;
import com.uniovi.repositories.MultiplayerSessionRepository;
import com.uniovi.repositories.PlayerRepository;
import com.uniovi.services.MultiplayerSessionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public class MultiplayerSessionImpl implements MultiplayerSessionService {

    private MultiplayerSessionRepository multiplayerSessionRepository;
    private PlayerRepository playerRepository;
    @Override
    public Page<MultiplayerSession> getMultiplayerPlayerRanking(Pageable pageable, int multiplayerCode) {
        return multiplayerSessionRepository.findPlayersByMultiplayerCode(pageable, multiplayerCode);
    }

    @Override
    public void multiCreate(String code, Long id) {
        Player p = playerRepository.findById(id).get();
        multiplayerSessionRepository.save(new MultiplayerSession(code,p));
    }

    @Override
    public void addToLobby(String code, Long id) {
        Player p = playerRepository.findById(id).get();
        MultiplayerSession ms=multiplayerSessionRepository.findByCode(code);
        ms.addPlayer(p);
        multiplayerSessionRepository.save(ms);
    }
}
