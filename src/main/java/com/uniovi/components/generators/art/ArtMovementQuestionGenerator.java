package com.uniovi.components.generators.art;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArtMovementQuestionGenerator extends AbstractArtGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "Which art movement is associated with ");
            put("es", "¿Con qué movimiento artístico se asocia ");
            put("fr", "Quel mouvement artistique est associé à ");
        }
    };

    public ArtMovementQuestionGenerator(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?movement ?movementLabel\n" +
                "WHERE {" +
                "  ?movement wdt:P31 wd:Q968159 ." + // Instance of Art movement
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]," + language + "\" }" +
                "}";
    }

    private List<String> getAllMovements(JsonNode resultsNode, String correctMovement) {
        List<String> allMovements = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            String movement = result.path("movementLabel").path("value").asText();
            if (!movement.equals(correctMovement)) {
                allMovements.add(movement);
            }
        }
        return allMovements;
    }

    private List<String> selectRandomIncorrectMovements(List<String> allMovements, String correctMovement, int count) {
        List<String> incorrectMovements = new ArrayList<>();
        while (incorrectMovements.size() < count && allMovements.size() > 0) {
            int randomIndex = random.nextInt(allMovements.size());
            String selectedMovement = allMovements.remove(randomIndex);
            if (!selectedMovement.equals(correctMovement) && !incorrectMovements.contains(selectedMovement)) {
                incorrectMovements.add(selectedMovement);
            }
        }
        return incorrectMovements;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String movementLabel = result.path("movementLabel").path("value").asText();
        return selectRandomIncorrectMovements(getAllMovements(results, movementLabel), movementLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("movementLabel").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("movementLabel").path("value").asText() + "?";
    }
}
