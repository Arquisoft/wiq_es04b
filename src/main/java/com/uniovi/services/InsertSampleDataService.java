package com.uniovi.services;

import com.uniovi.dto.PlayerDto;
import com.uniovi.entities.Player;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class InsertSampleDataService {
    private final PlayerService playerService;

    public InsertSampleDataService(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostConstruct
    public void init() {
        PlayerDto player = new PlayerDto();
        player.setEmail("test@test.com");
        player.setName("Test");
        player.setLastName("Test");
        player.setNickname("test");
        player.setPassword("test");
        player.setRoles(new String[]{"ROLE_USER"});
        Player p = playerService.addNewPlayer(player);
    }
}
