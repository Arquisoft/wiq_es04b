package com.uniovi.components.generators.movie;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OriginalTitleQuestionGenerator extends AbstractMovieGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "What is the original title of the movie ");
            put("es", "¿Cuál es el título original de la película ");
            put("fr", "Quel est le titre original du film ");
        }
    };

    public OriginalTitleQuestionGenerator(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?movie ?movieLabel ?originalTitle\n" +
                "WHERE {" +
                "  ?movie wdt:P31 wd:Q11424 ." + // Instance of Film
                "  ?movie wdt:P1476 ?originalTitle ." + // Original title
                "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE]," + language + "\" }" +
                "}";
    }

    private List<String> getAllMovies(JsonNode resultsNode, String correctMovie) {
        List<String> allMovies = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            String movie = result.path("movieLabel").path("value").asText();
            if (!movie.equals(correctMovie)) {
                allMovies.add(movie);
            }
        }
        return allMovies;
    }

    private List<String> selectRandomIncorrectTitles(List<String> allMovies, String correctMovie, int count) {
        List<String> incorrectTitles = new ArrayList<>();
        while (incorrectTitles.size() < count && allMovies.size() > 0) {
            int randomIndex = random.nextInt(allMovies.size());
            String selectedMovie = allMovies.remove(randomIndex);
            if (!selectedMovie.equals(correctMovie) && !incorrectTitles.contains(selectedMovie)) {
                incorrectTitles.add(selectedMovie);
            }
        }
        return incorrectTitles;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String movieLabel = result.path("movieLabel").path("value").asText();
        return selectRandomIncorrectTitles(getAllMovies(results, movieLabel), movieLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("originalTitle").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("movieLabel").path("value").asText() + "?";
    }
}
