package com.uniovi.services;

import com.uniovi.entities.ApiKey;
import com.uniovi.entities.Player;

import java.util.List;
import java.util.Map;

public interface RestApiService {
    List<Player> getPlayers(Map<String, String> params);

    void logAccess(ApiKey apiKey, String path, Map<String, String> params);
}
