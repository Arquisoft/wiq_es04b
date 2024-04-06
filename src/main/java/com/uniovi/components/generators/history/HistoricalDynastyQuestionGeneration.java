package com.uniovi.components.generators.history;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;

public class HistoricalDynastyQuestionGeneration extends AbstractHistoryGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "Which historical dynasty is associated with ");
            put("es", "¿Con qué dinastía histórica se asocia ");
            put("fr", "Quelle dynastie historique est associée à ");
        }
    };

    public HistoricalDynastyQuestionGeneration(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    private List<String> getAllDynasties(JsonNode resultsNode, String correctDynasty) {
        // Obtaining all historical dynasties from the JSON (different from the correct dynasty)
        List<String> allDynasties = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            String dynasty = result.path("dynastyLabel").path("value").asText();
            if (!dynasty.equals(correctDynasty)) {
                allDynasties.add(dynasty);
            }
        }
        return allDynasties;
    }

    private List<String> selectRandomIncorrectDynasties(List<String> allDynasties, String correctDynasty, int count) {
        List<String> incorrectDynasties = new ArrayList<>();
        while (incorrectDynasties.size() < count && allDynasties.size() > 0) {
            int randomIndex = random.nextInt(allDynasties.size());
            String selectedDynasty = allDynasties.remove(randomIndex);
            if (!selectedDynasty.equals(correctDynasty) && !incorrectDynasties.contains(selectedDynasty)) {
                incorrectDynasties.add(selectedDynasty);
            }
        }
        return incorrectDynasties;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String dynastyLabel = result.path("dynastyLabel").path("value").asText();
        return selectRandomIncorrectDynasties(getAllDynasties(results, dynastyLabel), dynastyLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("dynastyLabel").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("historicalEventLabel").path("value").asText() + "?";
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?dynasty ?dynastyLabel ?historicalEvent ?historicalEventLabel\n" +
                "WHERE {" +
                "  ?dynasty wdt:P31 wd:Q11410 ." +
                "  ?historicalEvent wdt:P31 wd:Q198 ." +
                "  OPTIONAL { ?dynasty wdt:P108 ?historicalEvent } ." +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]," + language + "\" }" +
                "}" +
                "ORDER BY ?dynastyLabel";
    }
}
