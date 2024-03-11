package com.uniovi.repositories;

import com.uniovi.entities.Answer;
import com.uniovi.entities.GameSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.uniovi.entities.Player;

import java.util.List;

public interface GameSessionRepository extends CrudRepository<GameSession, Long> {

    List<GameSession> findAll();

    Page<GameSession> findAllByPlayer(Pageable pageable, Player player);

}
