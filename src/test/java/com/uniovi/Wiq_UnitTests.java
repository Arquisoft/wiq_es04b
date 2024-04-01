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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Test
    @Order(5)
    void PlayerServiceImpl_getUsers_ReturnsPlayersList() {
        RoleService roleService = new RoleServiceImpl(roleRepository);
        PlayerServiceImpl playerService = new PlayerServiceImpl(playerRepository, roleService, passwordEncoder);

        List<Player> players = new ArrayList<>();
        players.add(new Player("a", "a@gmail.com", "1a"));
        players.add(new Player("b", "b@gmail.com", "1b"));

        playerRepository.save(new Player("a", "a@gmail.com", "1a"));
        playerRepository.save(new Player("b", "b@gmail.com", "1b"));

        List<Player> result = playerService.getUsers();

        Assertions.assertEquals(players.size(), result.size());
        for (int i = 0; i < players.size(); i++) {
            Assertions.assertEquals(players.get(i), result.get(i));
        }
    }

    @Test
    @Order(6)
    void PlayerServiceImpl_getUsers_ReturnsEmptyList() {
        RoleService roleService = new RoleServiceImpl(roleRepository);
        PlayerServiceImpl playerService = new PlayerServiceImpl(playerRepository, roleService, passwordEncoder);

        List<Player> result = playerService.getUsers();

        Assertions.assertEquals(0, result.size());
    }

    @Test
    @Order(7)
    void PlayerServiceImpl_getUserByEmail_ReturnsPlayer() {
        RoleService roleService = new RoleServiceImpl(roleRepository);
        PlayerServiceImpl playerService = new PlayerServiceImpl(playerRepository, roleService, passwordEncoder);

        String email = "a@gmail.com";
        Player player = new Player("a", email, "password");

        playerRepository.save(player);

        Optional<Player> result = playerService.getUserByEmail(email);

        Assertions.assertEquals(player, result.orElse(null));
    }

    @Test
    @Order(8)
    void PlayerServiceImpl_getUserByEmail_ReturnsEmpty() {
        RoleService roleService = new RoleServiceImpl(roleRepository);
        PlayerServiceImpl playerService = new PlayerServiceImpl(playerRepository, roleService, passwordEncoder);

        String email = "nonexist@gmail.com";

        Optional<Player> result = playerService.getUserByEmail(email);

        Assertions.assertEquals(Optional.empty(), result);
    }

    @Test
    @Order(9)
    void PlayerServiceImpl_getUserByUsername_ReturnsPlayer() {
        RoleService roleService = new RoleServiceImpl(roleRepository);
        PlayerServiceImpl playerService = new PlayerServiceImpl(playerRepository, roleService, passwordEncoder);

        String username = "abc";
        Player player = new Player(username, "a@gmail,com", "password");

        playerRepository.save(player);

        Optional<Player> result = playerService.getUserByUsername(username);

        Assertions.assertEquals(player, result.orElse(null));
    }

    @Test
    @Order(10)
    void PlayerServiceImpl_getUserByUsername_ReturnsEmpty() {
        RoleService roleService = new RoleServiceImpl(roleRepository);
        PlayerServiceImpl playerService = new PlayerServiceImpl(playerRepository, roleService, passwordEncoder);

        String username = "nonexist";

        Optional<Player> result = playerService.getUserByUsername(username);

        Assertions.assertEquals(Optional.empty(), result);
    }

}
