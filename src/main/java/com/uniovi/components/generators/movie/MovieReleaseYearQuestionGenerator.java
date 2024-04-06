package com.uniovi.components.generators.movie;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieReleaseYearQuestionGenerator extends AbstractMovieGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "In which year was the movie ");
            put("es", "¿En qué año se lanzó la película ");
            put("fr", "En quelle année est sorti le film ");
        }
    };

    public MovieReleaseYearQuestionGenerator(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?movie ?movieLabel ?releaseYear\n" +
                "WHERE {" +
                "  ?movie wdt:P31 wd:Q11424 ." + // Instance of Film
                "  ?movie wdt:P577 ?releaseDate ." + // Release date
                "  BIND(YEAR(?releaseDate) AS ?releaseYear)" + // Extract year
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

    private List<String> selectRandomIncorrectYears(List<String> allMovies, String correctMovie, int count) {
        List<String> incorrectYears = new ArrayList<>();
        while (incorrectYears.size() < count && allMovies.size() > 0) {
            int randomIndex = random.nextInt(allMovies.size());
            String selectedMovie = allMovies.remove(randomIndex);
            if (!selectedMovie.equals(correctMovie) && !incorrectYears.contains(selectedMovie)) {
                incorrectYears.add(selectedMovie);
            }
        }
        return incorrectYears;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String movieLabel = result.path("movieLabel").path("value").asText();
        return selectRandomIncorrectYears(getAllMovies(results, movieLabel), movieLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("releaseYear").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("movieLabel").path("value").asText() + "?";
    }
}
