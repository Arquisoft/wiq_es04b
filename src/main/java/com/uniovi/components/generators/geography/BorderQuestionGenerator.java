package com.uniovi.components.generators.geography;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;

import java.util.*;

public class BorderQuestionGenerator extends AbstractGeographyGenerator{
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "Which countries share a border with ");
            put("es", "¿Con qué países comparte frontera ");
        }
    };
    private Set<String> usedCountries = new HashSet<>();

    public BorderQuestionGenerator(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    private List<String> getAllBorderingCountries(JsonNode resultsNode, String correctCountry) {
        List<String> allBorderingCountries = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            String borderingCountry = result.path("borderingCountryLabel").path("value").asText();
            if (!borderingCountry.equals(correctCountry)) {
                allBorderingCountries.add(borderingCountry);
            }
        }
        return allBorderingCountries;
    }

    private List<String> selectRandomIncorrectBorderingCountries(List<String> allBorderingCountries, String correctCountry, int count) {
        List<String> incorrectBorderingCountries = new ArrayList<>();
        Random random = new Random();
        while (incorrectBorderingCountries.size() < count && allBorderingCountries.size() > 0) {
            int randomIndex = random.nextInt(allBorderingCountries.size());
            String selectedBorderingCountry = allBorderingCountries.remove(randomIndex);
            if (!selectedBorderingCountry.equals(correctCountry) && !incorrectBorderingCountries.contains(selectedBorderingCountry)) {
                incorrectBorderingCountries.add(selectedBorderingCountry);
            }
        }
        return incorrectBorderingCountries;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String borderingCountryLabel = result.path("borderingCountryLabel").path("value").asText();
        return selectRandomIncorrectBorderingCountries(
                getAllBorderingCountries(results, borderingCountryLabel),
                borderingCountryLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("borderingCountryLabel").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("countryLabel").path("value").asText() + "?";
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?country ?countryLabel ?borderingCountry ?borderingCountryLabel\n" +
                "WHERE {" +
                "  ?country wdt:P31 wd:Q3624078 ." +
                "  FILTER NOT EXISTS {?country wdt:P31 wd:Q3024240}" +
                "  FILTER NOT EXISTS {?country wdt:P31 wd:Q28171280}" +
                "  ?country wdt:P47 ?borderingCountry ." +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]," + language + "\" }" +
                "}";
    }
}
