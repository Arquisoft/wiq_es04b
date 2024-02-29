package com.uniovi.components.generators.geography;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CapitalQuestionGenerator extends AbstractGeographyGenerator{
    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?country ?countryLabel ?capital ?capitalLabel\n" +
                "WHERE {\n" +
                "  ?country wdt:P31 wd:Q3624078 .\n" +
                "  FILTER NOT EXISTS {?country wdt:P31 wd:Q3024240}\n" +
                "  FILTER NOT EXISTS {?country wdt:P31 wd:Q28171280}\n" +
                "  OPTIONAL { ?country wdt:P36 ?capital } .\n" +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE],en\" }\n" +
                "}\n" +
                "ORDER BY ?countryLabel";
    }

    private List<String> getAllCapitals(JsonNode resultsNode, String countryLabel, String correctCapital) {
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
        // Seleccionar aleatoriamente tres capitales incorrectas distintas a la capital correcta
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
}
