package com.uniovi;

import com.uniovi.dto.PlayerDto;
import com.uniovi.entities.Player;
import com.uniovi.services.PlayerService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest
@Tag("unit")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class Wiq_UnitTests {
    @Autowired
    private PlayerService playerService;

    @Test
    @Order(1)
    public void testPlayerService() {
        PlayerDto dto = new PlayerDto();
        dto.setUsername("test");
        dto.setPassword("test");
        dto.setEmail("test@test.com");
        dto.setRoles(new String[]{"ROLE_USER"});
        Player player = playerService.addNewPlayer(dto);
        Assertions.assertNotNull(player);
        Optional<Player> playerOptional = playerService.getUser(player.getId());
        Assertions.assertTrue(playerOptional.isPresent());
    }
}
