package com.uniovi.services.impl;

import com.uniovi.entities.MultiplayerSession;
import com.uniovi.entities.Player;
import com.uniovi.repositories.MultiplayerSessionRepository;
import com.uniovi.repositories.PlayerRepository;
import com.uniovi.services.MultiplayerSessionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MultiplayerSessionImpl implements MultiplayerSessionService {
    private PlayerRepository playerRepository;
    private MultiplayerSessionRepository multiplayerSessionRepository;


    public MultiplayerSessionImpl(PlayerRepository playerRepository, MultiplayerSessionRepository multiplayerSessionRepository) {
        this.playerRepository = playerRepository;
        this.multiplayerSessionRepository = multiplayerSessionRepository;
    }

    @Override
    public Page<MultiplayerSession> getMultiplayerPlayerRanking(Pageable pageable, int multiplayerCode) {
        return multiplayerSessionRepository.findPlayersByMultiplayerCode(pageable, ""+multiplayerCode);
    }

    @Override
    public void multiCreate(String code, Long id) {
        Player p = playerRepository.findById(id).get();
        multiplayerSessionRepository.save(new MultiplayerSession(code,p));
    }

    @Override
    public void addToLobby(String code, Long id) {
        Player p = playerRepository.findById(id).get();
        MultiplayerSession ms=multiplayerSessionRepository.findByMultiplayerCode(code);
        ms.addPlayer(p);
        multiplayerSessionRepository.save(ms);
    }
}
