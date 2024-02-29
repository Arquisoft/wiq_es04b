package com.uniovi.services.impl;

import com.uniovi.dto.PlayerDto;
import com.uniovi.dto.RoleDto;
import com.uniovi.entities.Associations;
import com.uniovi.entities.Player;
import com.uniovi.repositories.PlayerRepository;
import com.uniovi.repositories.RoleRepository;
import com.uniovi.services.PlayerService;
import com.uniovi.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.uniovi.entities.Role;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService {
    private PlayerRepository playerRepository;
    private RoleService roleService;
    private PasswordEncoder passwordEncoder;

    public PlayerServiceImpl(PlayerRepository playerRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Player addNewPlayer(PlayerDto dto) {
        if (playerRepository.findByEmail(dto.getEmail()) != null) {
            throw new IllegalArgumentException("Email already in use");
        }

        if (playerRepository.findByUsername(dto.getUsername()) != null) {
            throw new IllegalArgumentException("Username already in use");
        }

        Player player = new Player(
            dto.getUsername(),
            dto.getEmail(),
            passwordEncoder.encode(dto.getPassword())
        );

        if (dto.getRoles() == null)
            dto.setRoles(new String[] {"ROLE_USER"} );

        for (String roleStr : dto.getRoles()) {
            Role r = roleService.getRole(roleStr);
            if (r != null)
                Associations.PlayerRole.addRole(player, r);
            else {
                r = roleService.addRole(new RoleDto(roleStr));
                Associations.PlayerRole.addRole(player, r);
            }
        }

        playerRepository.save(player);
        return player;
    }

    @Override
    public List<Player> getUsers() {
        List<Player> l = new ArrayList<>();
        playerRepository.findAll().forEach(l::add);
        return l;
    }

    @Override
    public Optional<Player> getUser(Long id) {
        return playerRepository.findById(id);
    }

    @Override
    public Optional<Player> getUserByEmail(String email) {
        return Optional.ofNullable(playerRepository.findByEmail(email));
    }

    @Override
    public Optional<Player> getUserByUsername(String username) {
        return Optional.ofNullable(playerRepository.findByUsername(username));
    }
}
