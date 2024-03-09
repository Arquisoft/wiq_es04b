package com.uniovi.repositories;

import com.uniovi.entities.Answer;
import com.uniovi.entities.GameSession;
import org.springframework.data.repository.CrudRepository;

import com.uniovi.entities.Player;

import java.util.List;

public interface GameSessionRepository extends CrudRepository<GameSession, Long> {

    List<GameSession> findAll();

    List<GameSession> findAllByPlayer(Player player);
}
