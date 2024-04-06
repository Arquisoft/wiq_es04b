package com.uniovi.components.generators.geography;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

public class LanguageQuestionGenerator extends AbstractGeographyGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "What is the official language of ");
            put("es", "¿Cuál es el idioma oficial de ");
            put("fr", "Quelle est la langue officielle de ");
        }
    };

    public LanguageQuestionGenerator(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?country ?countryLabel ?language ?languageLabel\n" +
                "WHERE {" +
                "  ?country wdt:P31 wd:Q3624078 ." +
                "  FILTER NOT EXISTS {?country wdt:P31 wd:Q3024240}" +
                "  FILTER NOT EXISTS {?country wdt:P31 wd:Q28171280}" +
                "  ?country wdt:P37 ?language ." +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]," + language + "\" }" +
                "}";
    }

    private List<String> getAllLanguages(JsonNode resultsNode, String correctLanguage) {
        // Obtener todos los idiomas del JSON (distintos al idioma correcto)
        List<String> allLanguages = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            String language = result.path("languageLabel").path("value").asText();
            if (!language.equals(correctLanguage)) {
                allLanguages.add(language);
            }
        }
        return allLanguages;
    }

    private List<String> selectRandomIncorrectLanguages(List<String> allLanguages, String correctLanguage, int count) {
        List<String> incorrectLanguages = new ArrayList<>();
        while (incorrectLanguages.size() < count && allLanguages.size() > 0) {
            int randomIndex = random.nextInt(allLanguages.size());
            String selectedLanguage = allLanguages.remove(randomIndex);
            if (!selectedLanguage.equals(correctLanguage) && !incorrectLanguages.contains(selectedLanguage)) {
                incorrectLanguages.add(selectedLanguage);
            }
        }
        return incorrectLanguages;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String languageLabel = result.path("languageLabel").path("value").asText();
        return selectRandomIncorrectLanguages(getAllLanguages(results, languageLabel), languageLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("languageLabel").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("countryLabel").path("value").asText() + "?";
    }
}
