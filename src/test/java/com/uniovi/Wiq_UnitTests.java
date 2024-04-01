package com.uniovi;

import com.uniovi.dto.*;
import com.uniovi.entities.*;
import com.uniovi.repositories.*;
import com.uniovi.services.*;
import com.uniovi.services.impl.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Tag("unit")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class Wiq_UnitTests {

    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
    }

    @Test
    @Order(1)
    void PlayerServiceImpl_addNewPlayer_UsedEmail() {
        RoleService roleService = new RoleServiceImpl(roleRepository);
        PlayerServiceImpl playerService = new PlayerServiceImpl(playerRepository, roleService, passwordEncoder);

        Player p1 = new Player("a", "abcd@gmai.com", "1234");
        playerRepository.save(p1);

        PlayerDto dto = new PlayerDto("b", "abcd@gmail.com", "1221", "1221", null);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> playerService.addNewPlayer(dto));
        Assertions.assertEquals("Email already in use", exception.getMessage());
    }

    @Test
    @Order(2)
    void PlayerServiceImpl_addNewPlayer_UsedUsername() {
        RoleService roleService = new RoleServiceImpl(roleRepository);
        PlayerServiceImpl playerService = new PlayerServiceImpl(playerRepository, roleService, passwordEncoder);

        Player p1 = new Player("a", "abcd@gmai.com", "1234");
        playerRepository.save(p1);

        PlayerDto dto = new PlayerDto("a", "a@gmail.com", "1221", "1221", null);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> playerService.addNewPlayer(dto));
        Assertions.assertEquals("Username already in use", exception.getMessage());
    }

    @Test
    @Order(3)
    void PlayerServiceImpl_addNewPlayer_AddedCorrectly() {
        RoleService roleService = new RoleServiceImpl(roleRepository);
        PlayerServiceImpl playerService = new PlayerServiceImpl(playerRepository, roleService, passwordEncoder);

        PlayerDto dto = new PlayerDto("a", "a@gmail.com", "1221", "1221", null);

        Player player = playerService.addNewPlayer(dto);

        Assertions.assertNotNull(player);
        Assertions.assertEquals(dto.getUsername(), player.getUsername());
        Assertions.assertEquals(dto.getEmail(), player.getEmail());
        Assertions.assertTrue(passwordEncoder.matches(dto.getPassword(), player.getPassword()));
    }

    @Test
    @Order(4)
    void PlayerServiceImpl_addNewPlayer_RoleExists() {
        RoleService roleService = new RoleServiceImpl(roleRepository);
        PlayerServiceImpl playerService = new PlayerServiceImpl(playerRepository, roleService, passwordEncoder);

        PlayerDto dto = new PlayerDto("a", "a@gmail.com", "1221", "1221", new String[]{"ROLE_USER"});
        roleService.addRole(new RoleDto(dto.getRoles()[0]));

        Player player = playerService.addNewPlayer(dto);

        Assertions.assertNotNull(player);
        Assertions.assertEquals(dto.getUsername(), player.getUsername());
        Assertions.assertEquals(dto.getEmail(), player.getEmail());
        Assertions.assertTrue(passwordEncoder.matches(dto.getPassword(), player.getPassword()));
    }

}
