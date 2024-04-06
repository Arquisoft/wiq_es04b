package com.uniovi.components.generators.science;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScientificDiscoveryQuestionGenerator extends AbstractScienceGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "Who discovered ");
            put("es", "¿Quién descubrió ");
            put("fr", "Qui a découvert ");
        }
    };

    public ScientificDiscoveryQuestionGenerator(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?scientist ?scientistLabel ?discovery ?discoveryLabel\n" +
                "WHERE {" +
                "  ?scientist wdt:P31 wd:Q5 ." + // Instance of Human
                "  ?scientist wdt:P61 ?discovery ." + // Discovery
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]," + language + "\" }" +
                "}";
    }

    private List<String> getAllDiscoveries(JsonNode resultsNode, String correctDiscovery) {
        List<String> allDiscoveries = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            String discovery = result.path("discoveryLabel").path("value").asText();
            if (!discovery.equals(correctDiscovery)) {
                allDiscoveries.add(discovery);
            }
        }
        return allDiscoveries;
    }

    private List<String> selectRandomIncorrectDiscoveries(List<String> allDiscoveries, String correctDiscovery, int count) {
        List<String> incorrectDiscoveries = new ArrayList<>();
        while (incorrectDiscoveries.size() < count && allDiscoveries.size() > 0) {
            int randomIndex = random.nextInt(allDiscoveries.size());
            String selectedDiscovery = allDiscoveries.remove(randomIndex);
            if (!selectedDiscovery.equals(correctDiscovery) && !incorrectDiscoveries.contains(selectedDiscovery)) {
                incorrectDiscoveries.add(selectedDiscovery);
            }
        }
        return incorrectDiscoveries;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String discoveryLabel = result.path("discoveryLabel").path("value").asText();
        return selectRandomIncorrectDiscoveries(getAllDiscoveries(results, discoveryLabel), discoveryLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("scientistLabel").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("discoveryLabel").path("value").asText() + "?";
    }
}
