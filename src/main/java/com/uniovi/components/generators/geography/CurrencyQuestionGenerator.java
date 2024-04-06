package com.uniovi.components.generators.geography;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

public class CurrencyQuestionGenerator extends AbstractGeographyGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "What is the currency of ");
            put("es", "¿Cuál es la moneda de ");
            put("fr", "Quelle est la monnaie de ");
        }
    };

    public CurrencyQuestionGenerator(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?country ?countryLabel ?currency ?currencyLabel\n" +
                "WHERE {" +
                "  ?country wdt:P31 wd:Q3624078 ." +
                "  FILTER NOT EXISTS {?country wdt:P31 wd:Q3024240}" +
                "  FILTER NOT EXISTS {?country wdt:P31 wd:Q28171280}" +
                "  ?country wdt:P38 ?currency ." +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]," + language + "\" }" +
                "}";
    }

    private List<String> getAllCurrencies(JsonNode resultsNode, String correctCurrency) {
        // Obtener todas las monedas del JSON (distintas a la moneda correcta)
        List<String> allCurrencies = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            String currency = result.path("currencyLabel").path("value").asText();
            if (!currency.equals(correctCurrency)) {
                allCurrencies.add(currency);
            }
        }
        return allCurrencies;
    }

    private List<String> selectRandomIncorrectCurrencies(List<String> allCurrencies, String correctCurrency, int count) {
        List<String> incorrectCurrencies = new ArrayList<>();
        while (incorrectCurrencies.size() < count && allCurrencies.size() > 0) {
            int randomIndex = random.nextInt(allCurrencies.size());
            String selectedCurrency = allCurrencies.remove(randomIndex);
            if (!selectedCurrency.equals(correctCurrency) && !incorrectCurrencies.contains(selectedCurrency)) {
                incorrectCurrencies.add(selectedCurrency);
            }
        }
        return incorrectCurrencies;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String currencyLabel = result.path("currencyLabel").path("value").asText();
        return selectRandomIncorrectCurrencies(getAllCurrencies(results, currencyLabel), currencyLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("currencyLabel").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("countryLabel").path("value").asText() + "?";
    }
}
