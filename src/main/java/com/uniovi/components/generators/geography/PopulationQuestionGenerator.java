package com.uniovi.components.generators.geography;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

public class PopulationQuestionGenerator extends AbstractGeographyGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "What is the population of ");
            put("es", "¿Cuál es la población de ");
            put("fr", "Quelle est la population de ");
        }
    };

    public PopulationQuestionGenerator(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?country ?countryLabel ?population\n" +
                "WHERE {" +
                "  ?country wdt:P31 wd:Q3624078 ." +
                "  FILTER NOT EXISTS {?country wdt:P31 wd:Q3024240}" +
                "  FILTER NOT EXISTS {?country wdt:P31 wd:Q28171280}" +
                "  ?country wdt:P1082 ?population ." +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]," + language + "\" }" +
                "}";
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        // Este generador de preguntas no necesita opciones, ya que la respuesta correcta es la población.
        return Collections.emptyList();
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("population").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("countryLabel").path("value").asText() + "?";
    }
}