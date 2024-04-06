package com.uniovi.components.generators.history;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;

public class HistoricalEventQuestionGeneration extends AbstractHistoryGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "In what historical event did ");
            put("es", "¿En qué evento histórico participó ");
            put("fr", "Dans quel événement historique a participé ");
        }
    };

    public HistoricalEventQuestionGeneration(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    private List<String> getAllEvents(JsonNode resultsNode, String correctEvent) {
        // Obtener todos los eventos históricos del JSON (diferentes al evento correcto)
        List<String> allEvents = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            String event = result.path("eventLabel").path("value").asText();
            if (!event.equals(correctEvent)) {
                allEvents.add(event);
            }
        }
        return allEvents;
    }

    private List<String> selectRandomIncorrectEvents(List<String> allEvents, String correctEvent, int count) {
        List<String> incorrectEvents = new ArrayList<>();
        while (incorrectEvents.size() < count && allEvents.size() > 0) {
            int randomIndex = random.nextInt(allEvents.size());
            String selectedEvent = allEvents.remove(randomIndex);
            if (!selectedEvent.equals(correctEvent) && !incorrectEvents.contains(selectedEvent)) {
                incorrectEvents.add(selectedEvent);
            }
        }
        return incorrectEvents;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String eventLabel = result.path("eventLabel").path("value").asText();
        return selectRandomIncorrectEvents(getAllEvents(results, eventLabel), eventLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("eventLabel").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("personLabel").path("value").asText() + "?";
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?person ?personLabel ?event ?eventLabel\n" +
                "WHERE {" +
                "  ?person wdt:P31 wd:Q5 ." +
                "  ?event wdt:P31 wd:Q198 ." +
                "  OPTIONAL { ?person wdt:P106 ?event } ." +
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]," + language + "\" }" +
                "}" +
                "ORDER BY ?personLabel";
    }
}
