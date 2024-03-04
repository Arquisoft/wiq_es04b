package com.uniovi.services.impl;

import com.uniovi.entities.ApiKey;
import com.uniovi.entities.Associations;
import com.uniovi.entities.Player;
import com.uniovi.entities.RestApiAccessLog;
import com.uniovi.repositories.RestApiLogRepository;
import com.uniovi.services.ApiKeyService;
import com.uniovi.services.PlayerService;
import com.uniovi.services.RestApiService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional // makes hibernate open transaction context automatically to avoid lazy-loading issues
public class RestApiServiceImpl implements RestApiService {
    private final PlayerService playerService;
    private final RestApiLogRepository restApiLogRepository;

    @Autowired
    public RestApiServiceImpl(PlayerService playerService, RestApiLogRepository restApiLogRepository) {
        this.playerService = playerService;
        this.restApiLogRepository = restApiLogRepository;
    }

    @Override
    public List<Player> getPlayers(Map<String, String> params) {
        if (params.containsKey("username")) {
            Optional<Player> found = playerService.getUserByUsername(params.get("username"));
            if (found.isPresent())
                return List.of(found.get());
            else
                return List.of();
        }

        if (params.containsKey("email")) {
            Optional<Player> found = playerService.getUserByEmail(params.get("email"));
            if (found.isPresent())
                return List.of(found.get());
            else
                return List.of();
        }

        if (params.containsKey("role")) {
            return playerService.getUsersByRole(params.get("role"));
        }

        if (params.containsKey("id")) {
            Optional<Player> found = playerService.getUser(Long.parseLong(params.get("id")));
            if (found.isPresent())
                return List.of(found.get());
            else
                return List.of();
        }

        if (params.containsKey("usernames")) {
            String[] usernames = params.get("usernames").split(",");
            List<Player> players = new ArrayList<>();
            for (String username : usernames) {
                Optional<Player> found = playerService.getUserByUsername(username);
                if (found.isPresent())
                    players.add(found.get());
            }
            return players;
        }

        if (params.containsKey("emails")) {
            String[] emails = params.get("emails").split(",");
            List<Player> filtered = new ArrayList<>();
            for (String email : emails) {
                Optional<Player> found = playerService.getUserByEmail(email);
                if (found.isPresent())
                    filtered.add(found.get());
            }
            return filtered;
        }

        return playerService.getUsers();
    }

    @Override
    public void logAccess(ApiKey apiKey, String path, Map<String, String> params) {
        RestApiAccessLog log = new RestApiAccessLog();
        log.setApiKey(apiKey);
        log.setPath(path);
        log.setDetails(params.toString());
        Associations.ApiKeyAccessLog.addAccessLog(apiKey, log);
        restApiLogRepository.save(log);
    }
}
