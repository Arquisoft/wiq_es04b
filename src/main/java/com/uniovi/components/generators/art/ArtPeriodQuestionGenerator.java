package com.uniovi.components.generators.art;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArtPeriodQuestionGenerator extends AbstractArtGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "Which art period is associated with ");
            put("es", "¿Con qué período artístico se asocia ");
            put("fr", "Quelle période artistique est associée à ");
        }
    };

    public ArtPeriodQuestionGenerator(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?period ?periodLabel\n" +
                "WHERE {" +
                "  ?period wdt:P31 wd:Q187437 ." + // Instance of Art period
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]," + language + "\" }" +
                "}";
    }

    private List<String> getAllPeriods(JsonNode resultsNode, String correctPeriod) {
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
        return this.statement + result.path("periodLabel").path("value").asText() + "?";
    }
}
