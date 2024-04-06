package com.uniovi.services.impl;

import com.mysql.cj.util.StringUtils;
import com.uniovi.entities.*;
import com.uniovi.repositories.RestApiLogRepository;
import com.uniovi.services.CategoryService;
import com.uniovi.services.PlayerService;
import com.uniovi.services.QuestionService;
import com.uniovi.services.RestApiService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.*;

@Service
@Transactional // makes hibernate open transaction context automatically to avoid lazy-loading issues
public class RestApiServiceImpl implements RestApiService {
    private final PlayerService playerService;
    private final RestApiLogRepository restApiLogRepository;
    private final QuestionService questionService;
    private final CategoryService categoryService;

    @Autowired
    public RestApiServiceImpl(PlayerService playerService, RestApiLogRepository restApiLogRepository,
                              QuestionService questionService, CategoryService categoryService) {
        this.playerService = playerService;
        this.restApiLogRepository = restApiLogRepository;
        this.questionService = questionService;
        this.categoryService = categoryService;
    }

    @Override
    public List<Player> getPlayers(Map<String, String> params) {
        if (params.size() == 1)
            return playerService.getUsers();

        boolean ranOtherParams = false;


        Set<Player> players = new HashSet<>();
        if (params.containsKey("username")) {
            ranOtherParams = true;
            Optional<Player> found = playerService.getUserByUsername(params.get("username"));
            found.ifPresent(players::add);
        }

        if (params.containsKey("email")) {
            ranOtherParams = true;
            Optional<Player> found = playerService.getUserByEmail(params.get("email"));
            found.ifPresent(players::add);
        }

        if (params.containsKey("id")) {
            ranOtherParams = true;
            try {
                Optional<Player> found = playerService.getUser(Long.parseLong(params.get("id")));
                found.ifPresent(players::add);
            } catch (NumberFormatException ignored) {
            }
        }

        if (params.containsKey("usernames")) {
            ranOtherParams = true;
            String[] usernames = params.get("usernames").split(",");
            for (String username : usernames) {
                Optional<Player> found = playerService.getUserByUsername(username);
                found.ifPresent(players::add);
            }
        }

        if (params.containsKey("emails")) {
            ranOtherParams = true;
            String[] emails = params.get("emails").split(",");
            for (String email : emails) {
                Optional<Player> found = playerService.getUserByEmail(email);
                found.ifPresent(players::add);
            }
        }

        if (params.containsKey("role"))
        {
            if (!ranOtherParams)
                return playerService.getUsersByRole(params.get("role"));
            else
                players.removeIf(p -> !p.getRoles().stream().anyMatch(r -> r.getName().equals(params.get("role"))));
        }

        return players.stream().toList();
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
    public List<Question> getQuestions(Map<String, String> params, Pageable pageable) {
        String lang = LocaleContextHolder.getLocale().getLanguage();
        if (params.containsKey("lang")) {
            lang = params.get("lang");
        }

        if (params.containsKey("category")) {
            String category = params.get("category");
            Category cat = null;
            if (StringUtils.isStrictlyNumeric(category)) {
                Optional<Category> optCat = categoryService.getCategory(Long.parseLong(category));
                if (optCat.isPresent())
                    cat = optCat.get();
            } else {
                cat = categoryService.getCategoryByName(category);
            }

            return questionService.getQuestionsByCategory(pageable, cat, lang);
        }
        if (params.containsKey("id")) {
            try {
                Optional<Question> found = questionService.getQuestion(Long.parseLong(params.get("id")));
                return found.map(List::of).orElseGet(List::of);
            } catch (NumberFormatException e) {
                return List.of();
            }
        }

        if (params.containsKey("statement")) {
            return questionService.getQuestionsByStatement(pageable, params.get("statement"), lang);
        }

        return questionService.getQuestions(pageable).toList();
    }
}
