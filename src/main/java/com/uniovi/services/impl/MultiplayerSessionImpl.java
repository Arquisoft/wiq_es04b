package com.uniovi.services.impl;

import com.uniovi.entities.MultiplayerSession;
import com.uniovi.repositories.MultiplayerSessionRepository;
import com.uniovi.services.MultiplayerSessionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Comparator;
import java.util.List;

public class MultiplayerSessionImpl implements MultiplayerSessionService {

    private MultiplayerSessionRepository multiplayerSessionRepository;
    @Override
    public Page<MultiplayerSession> getMultiplayerPlayerRanking(Pageable pageable, int multiplayerCode) {
        return multiplayerSessionRepository.findPlayersByMultiplayerCode(pageable, multiplayerCode);
    }
}
