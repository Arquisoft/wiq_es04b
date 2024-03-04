package com.uniovi.repositories;

import com.uniovi.entities.GameSession;
import org.springframework.data.repository.CrudRepository;

public interface GameSessionRepository extends CrudRepository<GameSession, Long> {
}
