package com.uniovi.components.generators.art;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArtStyleQuestionGenerator extends AbstractArtGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "Which art style is associated with ");
            put("es", "¿Con qué estilo artístico se asocia ");
            put("fr", "Quel style artistique est associé à ");
        }
    };

    public ArtStyleQuestionGenerator(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?style ?styleLabel\n" +
                "WHERE {" +
                "  ?style wdt:P31 wd:Q179337 ." + // Instance of Art style
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]," + language + "\" }" +
                "}";
    }

    private List<String> getAllStyles(JsonNode resultsNode, String correctStyle) {
        List<String> allStyles = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            String style = result.path("styleLabel").path("value").asText();
            if (!style.equals(correctStyle)) {
                allStyles.add(style);
            }
        }
        return allStyles;
    }

    private List<String> selectRandomIncorrectStyles(List<String> allStyles, String correctStyle, int count) {
        List<String> incorrectStyles = new ArrayList<>();
        while (incorrectStyles.size() < count && allStyles.size() > 0) {
            int randomIndex = random.nextInt(allStyles.size());
            String selectedStyle = allStyles.remove(randomIndex);
            if (!selectedStyle.equals(correctStyle) && !incorrectStyles.contains(selectedStyle)) {
                incorrectStyles.add(selectedStyle);
            }
        }
        return incorrectStyles;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String styleLabel = result.path("styleLabel").path("value").asText();
        return selectRandomIncorrectStyles(getAllStyles(results, styleLabel), styleLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("styleLabel").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("styleLabel").path("value").asText() + "?";
    }
}
