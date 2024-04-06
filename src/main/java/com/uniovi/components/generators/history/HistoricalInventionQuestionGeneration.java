package com.uniovi.components.generators.history;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;

public class HistoricalInventionQuestionGeneration extends AbstractHistoryGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "Which historical invention is associated with ");
            put("es", "¿Con qué invención histórica se asocia ");
            put("fr", "Quelle invention historique est associée à ");
        }
    };

    public HistoricalInventionQuestionGeneration(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    private List<String> getAllInventions(JsonNode resultsNode, String correctInvention) {
        // Obtaining all historical inventions from the JSON (different from the correct invention)
        List<String> allInventions = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            String invention = result.path("inventionLabel").path("value").asText();
            if (!invention.equals(correctInvention)) {
                allInventions.add(invention);
            }
        }
        return allInventions;
    }

    private List<String> selectRandomIncorrectInventions(List<String> allInventions, String correctInvention, int count) {
        List<String> incorrectInventions = new ArrayList<>();
        while (incorrectInventions.size() < count && allInventions.size() > 0) {
            int randomIndex = random.nextInt(allInventions.size());
            String selectedInvention = allInventions.remove(randomIndex);
            if (!selectedInvention.equals(correctInvention) && !incorrectInventions.contains(selectedInvention)) {
                incorrectInventions.add(selectedInvention);
            }
        }
        return incorrectInventions;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String inventionLabel = result.path("inventionLabel").path("value").asText();
        return selectRandomIncorrectInventions(getAllInventions(results, inventionLabel), inventionLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("inventionLabel").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("historicalEventLabel").path("value").asText() + "?";
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?invention ?inventionLabel ?historicalEvent ?historicalEventLabel\n" +
                "WHERE {" +
                "  ?invention wdt:P31 wd:Q39614 ." +
                "  ?historicalEvent wdt:P31 wd:Q198 ." +
                "  OPTIONAL { ?invention wdt:P921 ?historicalEvent } ." +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]," + language + "\" }" +
                "}" +
                "ORDER BY ?inventionLabel";
    }
}
