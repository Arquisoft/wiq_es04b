package com.uniovi.services.impl;

import com.uniovi.dto.PlayerDto;
import com.uniovi.dto.RoleDto;
import com.uniovi.entities.ApiKey;
import com.uniovi.entities.Associations;
import com.uniovi.entities.Player;
import com.uniovi.repositories.PlayerRepository;
import com.uniovi.services.PlayerService;
import com.uniovi.services.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.uniovi.entities.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepository playerRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

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

    @Override
    public List<Player> getUsersByRole(String role) {
        Role r = roleService.getRole(role);
        if (r == null)
            return new ArrayList<>();

        return new ArrayList<>(r.getPlayers());
    }

    @Override
    public void generateApiKey(Player player) {
        ApiKey apiKey = new ApiKey();
        Associations.PlayerApiKey.addApiKey(player, apiKey);
        playerRepository.save(player);
    }

    @Override
    public void updatePlayer(Long id, PlayerDto playerDto) {
        Optional<Player> player = playerRepository.findById(id);
        if (player.isEmpty())
            return;

        Player p = player.get();
        if (playerDto.getEmail() != null)
            p.setEmail(playerDto.getEmail());
        if (playerDto.getUsername() != null)
            p.setUsername(playerDto.getUsername());
        if (playerDto.getPassword() != null)
            p.setPassword(passwordEncoder.encode(playerDto.getPassword()));
        if (playerDto.getRoles() != null) {
            p.getRoles().clear();
            for (String roleStr : playerDto.getRoles()) {
                Role r = roleService.getRole(roleStr);
                if (r != null)
                    Associations.PlayerRole.addRole(p, r);
                else {
                    r = roleService.addRole(new RoleDto(roleStr));
                    Associations.PlayerRole.addRole(p, r);
                }
            }
        }

        playerRepository.save(p);
    }

    @Override
    public void deletePlayer(Long id) {
        playerRepository.deleteById(id);
    }

    @Override
    public Page<Player> getPlayersPage(Pageable pageable) {
        return playerRepository.findAll(pageable);
    }

    @Override
    public void updatePassword(Player player, String password) {
        player.setPassword(passwordEncoder.encode(password));
        playerRepository.save(player);
    }

    @Override
    public void savePlayer(Player player) {
        playerRepository.save(player);
    }
}
