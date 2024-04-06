package com.uniovi.components.generators.movie;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.services.CategoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieGenreQuestionGenerator extends AbstractMovieGenerator {
    private static final Map<String, String> STATEMENTS = new HashMap<>() {
        {
            put("en", "Which genre does the movie ");
            put("es", "¿A qué género pertenece la película ");
            put("fr", "À quel genre appartient le film ");
        }
    };

    public MovieGenreQuestionGenerator(CategoryService categoryService, String language) {
        super(categoryService);
        this.statement = STATEMENTS.get(language);
        this.language = language;
    }

    @Override
    public String getQuery() {
        return "SELECT DISTINCT ?movie ?movieLabel ?genre ?genreLabel\n" +
                "WHERE {" +
                "  ?movie wdt:P31 wd:Q11424 ." + // Instance of Film
                "  ?movie wdt:P136 ?genre ." + // Genre
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

    private List<String> selectRandomIncorrectGenres(List<String> allMovies, String correctMovie, int count) {
        List<String> incorrectGenres = new ArrayList<>();
        while (incorrectGenres.size() < count && allMovies.size() > 0) {
            int randomIndex = random.nextInt(allMovies.size());
            String selectedMovie = allMovies.remove(randomIndex);
            if (!selectedMovie.equals(correctMovie) && !incorrectGenres.contains(selectedMovie)) {
                incorrectGenres.add(selectedMovie);
            }
        }
        return incorrectGenres;
    }

    @Override
    protected List<String> generateOptions(JsonNode results, JsonNode result) {
        String movieLabel = result.path("movieLabel").path("value").asText();
        return selectRandomIncorrectGenres(getAllMovies(results, movieLabel), movieLabel, 3);
    }

    @Override
    protected String generateCorrectAnswer(JsonNode result) {
        return result.path("genreLabel").path("value").asText();
    }

    @Override
    protected String getQuestionSubject(JsonNode result) {
        return this.statement + result.path("movieLabel").path("value").asText() + "?";
    }
}
