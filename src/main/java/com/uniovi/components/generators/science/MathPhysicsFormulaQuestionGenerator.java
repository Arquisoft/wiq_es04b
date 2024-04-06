package com.uniovi.components.generators.science;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MathPhysicsFormulaQuestionGenerator extends AbstractScienceGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "What is the formula for ");
            put("es", "¿Cuál es la fórmula para ");
            put("fr", "Quelle est la formule pour ");
        }
    };

    public MathPhysicsFormulaQuestionGenerator(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?formula ?formulaLabel\n" +
                "WHERE {" +
                "  ?formula wdt:P31 wd:Q5185279 ." + // Instance of Mathematical or physical formula
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]," + language + "\" }" +
                "}";
    }

    private List<String> getAllFormulas(JsonNode resultsNode, String correctFormula) {
        List<String> allFormulas = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            String formula = result.path("formulaLabel").path("value").asText();
            if (!formula.equals(correctFormula)) {
                allFormulas.add(formula);
            }
        }
        return allFormulas;
    }

    private List<String> selectRandomIncorrectFormulas(List<String> allFormulas, String correctFormula, int count) {
        List<String> incorrectFormulas = new ArrayList<>();
        while (incorrectFormulas.size() < count && allFormulas.size() > 0) {
            int randomIndex = random.nextInt(allFormulas.size());
            String selectedFormula = allFormulas.remove(randomIndex);
            if (!selectedFormula.equals(correctFormula) && !incorrectFormulas.contains(selectedFormula)) {
                incorrectFormulas.add(selectedFormula);
            }
        }
        return incorrectFormulas;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String formulaLabel = result.path("formulaLabel").path("value").asText();
        return selectRandomIncorrectFormulas(getAllFormulas(results, formulaLabel), formulaLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("formulaLabel").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("formulaLabel").path("value").asText() + "?";
    }
}
