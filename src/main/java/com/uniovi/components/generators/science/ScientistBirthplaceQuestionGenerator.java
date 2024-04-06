package com.uniovi.components.generators.science;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ScientistBirthplaceQuestionGenerator extends AbstractScienceGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "Where was the scientist ");
            put("es", "¿Dónde nació el científico ");
            put("fr", "Où est né le scientifique ");
        }
    };

    public ScientistBirthplaceQuestionGenerator(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?scientist ?scientistLabel ?birthplace ?birthplaceLabel\n" +
                "WHERE {" +
                "  ?scientist wdt:P31 wd:Q5 ." + // Instance of Human
                "  ?scientist wdt:P19 ?birthplace ." + // Place of birth
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]," + language + "\" }" +
                "}";
    }

    private List<String> getAllBirthplaces(JsonNode resultsNode, String correctBirthplace) {
        List<String> allBirthplaces = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            String birthplace = result.path("birthplaceLabel").path("value").asText();
            if (!birthplace.equals(correctBirthplace)) {
                allBirthplaces.add(birthplace);
            }
        }
        return allBirthplaces;
    }

    private List<String> selectRandomIncorrectBirthplaces(List<String> allBirthplaces, String correctBirthplace, int count) {
        List<String> incorrectBirthplaces = new ArrayList<>();
        while (incorrectBirthplaces.size() < count && allBirthplaces.size() > 0) {
            int randomIndex = random.nextInt(allBirthplaces.size());
            String selectedBirthplace = allBirthplaces.remove(randomIndex);
            if (!selectedBirthplace.equals(correctBirthplace) && !incorrectBirthplaces.contains(selectedBirthplace)) {
                incorrectBirthplaces.add(selectedBirthplace);
            }
        }
        return incorrectBirthplaces;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String birthplaceLabel = result.path("birthplaceLabel").path("value").asText();
        return selectRandomIncorrectBirthplaces(getAllBirthplaces(results, birthplaceLabel), birthplaceLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("birthplaceLabel").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("scientistLabel").path("value").asText() + "?";
    }
}
