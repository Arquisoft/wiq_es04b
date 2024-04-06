package com.uniovi.components.generators.history;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;

public class HistoricalArtifactQuestionGeneration extends AbstractHistoryGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "Which historical artifact is associated with ");
            put("es", "¿Con qué artefacto histórico se asocia ");
            put("fr", "Quel artefact historique est associé à ");
        }
    };

    public HistoricalArtifactQuestionGeneration(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    private List<String> getAllArtifacts(JsonNode resultsNode, String correctArtifact) {
        // Obtaining all historical artifacts from the JSON (different from the correct artifact)
        List<String> allArtifacts = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            String artifact = result.path("artifactLabel").path("value").asText();
            if (!artifact.equals(correctArtifact)) {
                allArtifacts.add(artifact);
            }
        }
        return allArtifacts;
    }

    private List<String> selectRandomIncorrectArtifacts(List<String> allArtifacts, String correctArtifact, int count) {
        List<String> incorrectArtifacts = new ArrayList<>();
        while (incorrectArtifacts.size() < count && allArtifacts.size() > 0) {
            int randomIndex = random.nextInt(allArtifacts.size());
            String selectedArtifact = allArtifacts.remove(randomIndex);
            if (!selectedArtifact.equals(correctArtifact) && !incorrectArtifacts.contains(selectedArtifact)) {
                incorrectArtifacts.add(selectedArtifact);
            }
        }
        return incorrectArtifacts;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String artifactLabel = result.path("artifactLabel").path("value").asText();
        return selectRandomIncorrectArtifacts(getAllArtifacts(results, artifactLabel), artifactLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("artifactLabel").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("historicalEventLabel").path("value").asText() + "?";
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?artifact ?artifactLabel ?historicalEvent ?historicalEventLabel\n" +
                "WHERE {" +
                "  ?artifact wdt:P31 wd:Q5314 ." +
                "  ?historicalEvent wdt:P31 wd:Q198 ." +
                "  OPTIONAL { ?artifact wdt:P1080 ?historicalEvent } ." +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]," + language + "\" }" +
                "}" +
                "ORDER BY ?artifactLabel";
    }
}
