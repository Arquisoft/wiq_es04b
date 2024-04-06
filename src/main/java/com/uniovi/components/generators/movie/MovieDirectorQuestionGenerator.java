package com.uniovi.components.generators.movie;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieDirectorQuestionGenerator extends AbstractMovieGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "Who directed the movie ");
            put("es", "¿Quién dirigió la película ");
            put("fr", "Qui a dirigé le film ");
        }
    };

    public MovieDirectorQuestionGenerator(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?director ?directorLabel ?movie ?movieLabel\n" +
                "WHERE {" +
                "  ?director wdt:P31 wd:Q5 ." + // Instance of Human
                "  ?movie wdt:P31 wd:Q11424 ." + // Instance of Film
                "  ?movie wdt:P57 ?director ." + // Director
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

    private List<String> selectRandomIncorrectMovies(List<String> allMovies, String correctMovie, int count) {
        List<String> incorrectMovies = new ArrayList<>();
        while (incorrectMovies.size() < count && allMovies.size() > 0) {
            int randomIndex = random.nextInt(allMovies.size());
            String selectedMovie = allMovies.remove(randomIndex);
            if (!selectedMovie.equals(correctMovie) && !incorrectMovies.contains(selectedMovie)) {
                incorrectMovies.add(selectedMovie);
            }
        }
        return incorrectMovies;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String movieLabel = result.path("movieLabel").path("value").asText();
        return selectRandomIncorrectMovies(getAllMovies(results, movieLabel), movieLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("directorLabel").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("movieLabel").path("value").asText() + "?";
    }
}
