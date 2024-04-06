package com.uniovi.components.generators.science;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScientificAchievementQuestionGenerator extends AbstractScienceGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "What significant achievement is associated with ");
            put("es", "¿Qué logro significativo está asociado con ");
            put("fr", "Quelle réalisation significative est associée à ");
        }
    };

    public ScientificAchievementQuestionGenerator(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?scientist ?scientistLabel ?achievement ?achievementLabel\n" +
                "WHERE {" +
                "  ?scientist wdt:P31 wd:Q5 ." + // Instance of Human
                "  ?scientist wdt:P166 ?achievement ." + // Notable achievement
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]," + language + "\" }" +
                "}";
    }

    private List<String> getAllAchievements(JsonNode resultsNode, String correctAchievement) {
        List<String> allAchievements = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            String achievement = result.path("achievementLabel").path("value").asText();
            if (!achievement.equals(correctAchievement)) {
                allAchievements.add(achievement);
            }
        }
        return allAchievements;
    }

    private List<String> selectRandomIncorrectAchievements(List<String> allAchievements, String correctAchievement, int count) {
        List<String> incorrectAchievements = new ArrayList<>();
        while (incorrectAchievements.size() < count && allAchievements.size() > 0) {
            int randomIndex = random.nextInt(allAchievements.size());
            String selectedAchievement = allAchievements.remove(randomIndex);
            if (!selectedAchievement.equals(correctAchievement) && !incorrectAchievements.contains(selectedAchievement)) {
                incorrectAchievements.add(selectedAchievement);
            }
        }
        return incorrectAchievements;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String achievementLabel = result.path("achievementLabel").path("value").asText();
        return selectRandomIncorrectAchievements(getAllAchievements(results, achievementLabel), achievementLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("achievementLabel").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("scientistLabel").path("value").asText() + "?";
    }
}
