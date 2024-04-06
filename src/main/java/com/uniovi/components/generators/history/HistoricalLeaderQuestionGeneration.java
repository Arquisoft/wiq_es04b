package com.uniovi.components.generators.history;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;

public class HistoricalLeaderQuestionGeneration extends AbstractHistoryGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "Which historical leader is associated with ");
            put("es", "¿Con qué líder histórico se asocia ");
            put("fr", "Quel leader historique est associé à ");
        }
    };

    public HistoricalLeaderQuestionGeneration(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    private List<String> getAllLeaders(JsonNode resultsNode, String correctLeader) {
        // Obtaining all historical leaders from the JSON (different from the correct leader)
        List<String> allLeaders = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            String leader = result.path("leaderLabel").path("value").asText();
            if (!leader.equals(correctLeader)) {
                allLeaders.add(leader);
            }
        }
        return allLeaders;
    }

    private List<String> selectRandomIncorrectLeaders(List<String> allLeaders, String correctLeader, int count) {
        List<String> incorrectLeaders = new ArrayList<>();
        while (incorrectLeaders.size() < count && allLeaders.size() > 0) {
            int randomIndex = random.nextInt(allLeaders.size());
            String selectedLeader = allLeaders.remove(randomIndex);
            if (!selectedLeader.equals(correctLeader) && !incorrectLeaders.contains(selectedLeader)) {
                incorrectLeaders.add(selectedLeader);
            }
        }
        return incorrectLeaders;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String leaderLabel = result.path("leaderLabel").path("value").asText();
        return selectRandomIncorrectLeaders(getAllLeaders(results, leaderLabel), leaderLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("leaderLabel").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("historicalEventLabel").path("value").asText() + "?";
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?leader ?leaderLabel ?historicalEvent ?historicalEventLabel\n" +
                "WHERE {" +
                "  ?leader wdt:P31 wd:Q5 ." +
                "  ?historicalEvent wdt:P31 wd:Q198 ." +
                "  OPTIONAL { ?leader wdt:P6 ?historicalEvent } ." +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]," + language + "\" }" +
                "}" +
                "ORDER BY ?leaderLabel";
    }
}
