package com.uniovi.components.generators.art;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ArtWorkQuestionGenerator extends AbstractArtGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "Who created the artwork ");
            put("es", "¿Quién creó la obra de arte ");
            put("fr", "Qui a créé l'œuvre d'art ");
        }
    };

    public ArtWorkQuestionGenerator(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?artist ?artistLabel ?artwork ?artworkLabel\n" +
                "WHERE {" +
                "  ?artist wdt:P31 wd:Q5 ." + // Instance of Human
                "  ?artwork wdt:P31 wd:Q3305213 ." + // Instance of Artwork
                "  ?artwork wdt:P170 ?artist ." + // Creator
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]," + language + "\" }" +
                "}";
    }

    private List<String> getAllArtworks(JsonNode resultsNode, String correctArtwork) {
        List<String> allArtworks = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            String artwork = result.path("artworkLabel").path("value").asText();
            if (!artwork.equals(correctArtwork)) {
                allArtworks.add(artwork);
            }
        }
        return allArtworks;
    }

    private List<String> selectRandomIncorrectArtworks(List<String> allArtworks, String correctArtwork, int count) {
        List<String> incorrectArtworks = new ArrayList<>();
        while (incorrectArtworks.size() < count && allArtworks.size() > 0) {
            int randomIndex = random.nextInt(allArtworks.size());
            String selectedArtwork = allArtworks.remove(randomIndex);
            if (!selectedArtwork.equals(correctArtwork) && !incorrectArtworks.contains(selectedArtwork)) {
                incorrectArtworks.add(selectedArtwork);
            }
        }
        return incorrectArtworks;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String artworkLabel = result.path("artworkLabel").path("value").asText();
        return selectRandomIncorrectArtworks(getAllArtworks(results, artworkLabel), artworkLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("artistLabel").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("artworkLabel").path("value").asText() + "?";
    }
}
