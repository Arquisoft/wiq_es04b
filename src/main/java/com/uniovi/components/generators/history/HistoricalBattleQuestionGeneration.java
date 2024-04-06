package com.uniovi.components.generators.history;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;

public class HistoricalBattleQuestionGeneration extends AbstractHistoryGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "Which historical battle is associated with ");
            put("es", "¿Con qué batalla histórica se asocia ");
            put("fr", "Quelle bataille historique est associée à ");
        }
    };

    public HistoricalBattleQuestionGeneration(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    private List<String> getAllBattles(JsonNode resultsNode, String correctBattle) {
        // Obtaining all historical battles from the JSON (different from the correct battle)
        List<String> allBattles = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            String battle = result.path("battleLabel").path("value").asText();
            if (!battle.equals(correctBattle)) {
                allBattles.add(battle);
            }
        }
        return allBattles;
    }

    private List<String> selectRandomIncorrectBattles(List<String> allBattles, String correctBattle, int count) {
        List<String> incorrectBattles = new ArrayList<>();
        while (incorrectBattles.size() < count && allBattles.size() > 0) {
            int randomIndex = random.nextInt(allBattles.size());
            String selectedBattle = allBattles.remove(randomIndex);
            if (!selectedBattle.equals(correctBattle) && !incorrectBattles.contains(selectedBattle)) {
                incorrectBattles.add(selectedBattle);
            }
        }
        return incorrectBattles;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String battleLabel = result.path("battleLabel").path("value").asText();
        return selectRandomIncorrectBattles(getAllBattles(results, battleLabel), battleLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("battleLabel").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("historicalEventLabel").path("value").asText() + "?";
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?battle ?battleLabel ?historicalEvent ?historicalEventLabel\n" +
                "WHERE {" +
                "  ?battle wdt:P31 wd:Q178561 ." +
                "  ?historicalEvent wdt:P31 wd:Q198 ." +
                "  OPTIONAL { ?battle wdt:P361 ?historicalEvent } ." +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]," + language + "\" }" +
                "}" +
                "ORDER BY ?battleLabel";
    }
}
