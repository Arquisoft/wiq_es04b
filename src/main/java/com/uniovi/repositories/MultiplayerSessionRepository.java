package com.uniovi.repositories;

import com.uniovi.entities.GameSession;
import com.uniovi.entities.MultiplayerSession;
import com.uniovi.entities.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MultiplayerSessionRepository extends CrudRepository<MultiplayerSession, Long> {
    //List<MultiplayerSession> findAll();

    @Query("SELECT m FROM MultiplayerSession m JOIN FETCH m.players p WHERE m.multiplayerCode = :multiplayerCode ORDER BY p.scoreMultiplayerCode")
    Page<MultiplayerSession> findPlayersByMultiplayerCode(Pageable pageable, String multiplayerCode);

    MultiplayerSession findByMultiplayerCode(String code);
}

