package com.uniovi.services.impl;

import com.mysql.cj.util.StringUtils;
import com.uniovi.entities.*;
import com.uniovi.repositories.RestApiLogRepository;
import com.uniovi.services.ApiKeyService;
import com.uniovi.services.PlayerService;
import com.uniovi.services.QuestionService;
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
    private final QuestionService questionService;

    @Autowired
    public RestApiServiceImpl(PlayerService playerService, RestApiLogRepository restApiLogRepository,
                              QuestionService questionService) {
        this.playerService = playerService;
        this.restApiLogRepository = restApiLogRepository;
        this.questionService = questionService;
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
            try {
                Optional<Player> found = playerService.getUser(Long.parseLong(params.get("id")));
                if (found.isPresent())
                    return List.of(found.get());
                else
                    return List.of();
            } catch (NumberFormatException e) {
                return List.of();
            }
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

    @Override
    public List<Question> getQuestions(Map<String, String> params) {
        if (params.containsKey("category")) {
            String category = params.get("category");
            if (StringUtils.isStrictlyNumeric(category)) {
                return questionService.getAllQuestions().stream()
                        .filter(q -> q.getCategory().getId() == Long.parseLong(category))
                        .toList();
            } else {
                return questionService.getAllQuestions().stream()
                        .filter(q -> q.getCategory().getName().equals(category))
                        .toList();
            }
        }
        if (params.containsKey("id")) {
            try {
                Optional<Question> found = questionService.getQuestion(Long.parseLong(params.get("id")));
                if (found.isPresent())
                    return List.of(found.get());
                else
                    return List.of();
            } catch (NumberFormatException e) {
                return List.of();
            }
        }

        if (params.containsKey("statement")) {
            return questionService.getAllQuestions().stream()
                    .filter(q -> q.getStatement().contains(params.get("statement")))
                    .toList();
        }

        return questionService.getAllQuestions();
    }
}
