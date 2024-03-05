package com.uniovi.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.uniovi.entities.*;
import com.uniovi.repositories.GameSessionRepository;
import com.uniovi.services.ApiKeyService;
import com.uniovi.services.RestApiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
public class RestApiController {
    private final ApiKeyService apiKeyService;
    private final RestApiService restApiService;

    @Autowired
    public RestApiController(ApiKeyService apiKeyService, RestApiService restApiService) {
        this.apiKeyService = apiKeyService;
        this.restApiService = restApiService;
    }

    @GetMapping("/api/players")
    public String getPlayers(HttpServletResponse response, @RequestParam Map<String, String> params) throws JsonProcessingException {
        response.setContentType("application/json");
        ApiKey apiKey = getApiKeyFromParams(params);
        if (apiKey == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> error = Map.of("error", "Invalid API key");
            return objectMapper.writeValueAsString(error);
        }

        List<Player> players = restApiService.getPlayers(params);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (Player player : players) {
            arrayNode.add(player.toJson());
        }
        root.put("players", arrayNode);
        restApiService.logAccess(apiKey, "/api/players", params);
        return root.toString();
    }

    @GetMapping("/api/questions")
    public String getQuestions(HttpServletResponse response, @RequestParam Map<String, String> params) throws JsonProcessingException {
        response.setContentType("application/json");
        ApiKey apiKey = getApiKeyFromParams(params);
        if (apiKey == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> error = Map.of("error", "Invalid API key");
            return objectMapper.writeValueAsString(error);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        List<Question> questions = restApiService.getQuestions(params);
        for (Question question : questions) {
            arrayNode.add(question.toJson());
        }
        root.set("questions", arrayNode);
        restApiService.logAccess(apiKey, "/api/questions", params);
        return root.toString();
    }

    private ApiKey getApiKeyFromParams(Map<String, String> params) {
        if (!params.containsKey("apiKey")) {
            return null;
        }

        String apiKey = params.get("apiKey");
        return apiKeyService.getApiKey(apiKey);
    }
}
