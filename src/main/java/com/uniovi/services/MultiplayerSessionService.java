package com.uniovi.services;

import com.uniovi.entities.GameSession;
import com.uniovi.entities.MultiplayerSession;
import com.uniovi.entities.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface MultiplayerSessionService {

    Page<MultiplayerSession> getMultiplayerPlayerRanking(Pageable pageable, int multiplayerCode);

    void multiCreate(String code, Long id);

    void addToLobby(String code, Long id);
}
