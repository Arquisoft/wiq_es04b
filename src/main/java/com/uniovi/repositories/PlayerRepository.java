package com.uniovi.repositories;

import com.uniovi.entities.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, Long> {
    Player findByEmail(String email);
    Player findByNickname(String nickname);
}
