package com.uniovi.components.generators.history;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;

public class HistoricalPeriodQuestionGeneration extends AbstractHistoryGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "Which historical period is associated with ");
            put("es", "¿Con qué período histórico se asocia ");
            put("fr", "Quelle période historique est associée à ");
        }
    };

    public HistoricalPeriodQuestionGeneration(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    private List<String> getAllPeriods(JsonNode resultsNode, String correctPeriod) {
        // Obtaining all historical periods from the JSON (different from the correct period)
        List<String> allPeriods = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            String period = result.path("periodLabel").path("value").asText();
            if (!period.equals(correctPeriod)) {
                allPeriods.add(period);
            }
        }
        return allPeriods;
    }

    private List<String> selectRandomIncorrectPeriods(List<String> allPeriods, String correctPeriod, int count) {
        List<String> incorrectPeriods = new ArrayList<>();
        while (incorrectPeriods.size() < count && allPeriods.size() > 0) {
            int randomIndex = random.nextInt(allPeriods.size());
            String selectedPeriod = allPeriods.remove(randomIndex);
            if (!selectedPeriod.equals(correctPeriod) && !incorrectPeriods.contains(selectedPeriod)) {
                incorrectPeriods.add(selectedPeriod);
            }
        }
        return incorrectPeriods;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String periodLabel = result.path("periodLabel").path("value").asText();
        return selectRandomIncorrectPeriods(getAllPeriods(results, periodLabel), periodLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("periodLabel").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("historicalEventLabel").path("value").asText() + "?";
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?period ?periodLabel ?historicalEvent ?historicalEventLabel\n" +
                "WHERE {" +
                "  ?period wdt:P31 wd:Q186067 ." +
                "  ?historicalEvent wdt:P31 wd:Q198 ." +
                "  OPTIONAL { ?period wdt:P155 ?historicalEvent } ." +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]," + language + "\" }" +
                "}" +
                "ORDER BY ?periodLabel";
    }
}
