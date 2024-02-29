package com.uniovi.components.generators.geography;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CapitalQuestionGenerator extends AbstractGeographyGenerator{

    public CapitalQuestionGenerator(CategoryService categoryService) {
        super(categoryService);
        this.statement = "What is the capital of ";
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?country ?countryLabel ?capital ?capitalLabel\n" +
                "WHERE {" +
                "  ?country wdt:P31 wd:Q3624078 ." +
                "  FILTER NOT EXISTS {?country wdt:P31 wd:Q3024240}" +
                "  FILTER NOT EXISTS {?country wdt:P31 wd:Q28171280}" +
                "  OPTIONAL { ?country wdt:P36 ?capital } ." +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE],en\" }" +
                "}" +
                "ORDER BY ?countryLabel";
    }

    private List<String> getAllCapitals(JsonNode resultsNode, String correctCapital) {
        // Obtener todas las capitales del JSON (distintas a la capital correcta)
        List<String> allCapitals = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            String capital = result.path("capitalLabel").path("value").asText();
            if (!capital.equals(correctCapital)) {
                allCapitals.add(capital);
            }
        }
        return allCapitals;
    }

    private List<String> selectRandomIncorrectCapitals(List<String> allCapitals, String correctCapital, int count) {
        List<String> incorrectCapitals = new ArrayList<>();
        Random random = new Random();
        while (incorrectCapitals.size() < count && allCapitals.size() > 0) {
            int randomIndex = random.nextInt(allCapitals.size());
            String selectedCapital = allCapitals.remove(randomIndex);
            if (!selectedCapital.equals(correctCapital) && !incorrectCapitals.contains(selectedCapital)) {
                incorrectCapitals.add(selectedCapital);
            }
        }
        return incorrectCapitals;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String capitalLabel = result.path("capitalLabel").path("value").asText();
        return selectRandomIncorrectCapitals(getAllCapitals(results, capitalLabel), capitalLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("capitalLabel").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("countryLabel").path("value").asText() + "?";
    }
}
