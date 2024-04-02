package com.uniovi.components.generators.geography;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;

public class ContinentQuestionGeneration extends AbstractGeographyGenerator{
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "In which continent is ");
            put("es", "¿En qué continente se encuentra ");
        }
    };

    public ContinentQuestionGeneration(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    private List<String> getAllContinents(JsonNode resultsNode, String correctContinent) {
        // Obtener todas las capitales del JSON (distintas a la capital correcta)
        List<String> allContinents = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            String continent = result.path("continentLabel").path("value").asText();
            if (!continent.equals(correctContinent)) {
                allContinents.add(continent);
            }
        }
        return allContinents;
    }

    private List<String> selectRandomIncorrectContinents(List<String> allContinents, String correctContinent, int count) {
        List<String> incorrectContinents = new ArrayList<>();
        while (incorrectContinents.size() < count && allContinents.size() > 0) {
            int randomIndex = random.nextInt(allContinents.size());
            String selectedCapital = allContinents.remove(randomIndex);
            if (!selectedCapital.equals(correctContinent) && !incorrectContinents.contains(selectedCapital)) {
                incorrectContinents.add(selectedCapital);
            }
        }
        return incorrectContinents;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String continentLabel = result.path("continentLabel").path("value").asText();
        return selectRandomIncorrectContinents(getAllContinents(results, continentLabel), continentLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("continentLabel").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("countryLabel").path("value").asText() + "?";
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?country ?countryLabel ?continent ?continentLabel\n" +
                "WHERE {" +
                "  ?country wdt:P31 wd:Q3624078 " +
                "  FILTER NOT EXISTS {?country wdt:P31 wd:Q3024240}" +
                "  FILTER NOT EXISTS {?country wdt:P31 wd:Q28171280}" +
                "  OPTIONAL { ?country wdt:P30 ?continent } ." +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]," + language + "\" }" +
                "}" +
                "ORDER BY ?countryLabel";
    }
}
