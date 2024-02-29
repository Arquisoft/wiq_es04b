package com.uniovi.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniovi.entities.Answer;
import com.uniovi.entities.Category;
import com.uniovi.entities.Question;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class QuestionGenerator {

    private List<Question> test = new ArrayList<>();

    private static void questionGenerator(String statement, List<String> options, String correctAnswer, Category category){
        List<Answer> answers = new ArrayList<>();
        //Generamos las respuestas y las añadimos a la lista
        for(String s: options){
            Answer answer = new Answer(s, false);
            answers.add(answer);
        }
        //Generamos la respuesta correcta y la añadimos a la lista
        Answer correct = new Answer(correctAnswer, true);
        answers.add(correct);

        Question question = new Question(statement, answers, correct, category);
        System.out.println(question);
    }

    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();

        try {
            String sparqlQuery = "#List of present-day countries and capital(s)\n" +
                    "SELECT DISTINCT ?country ?countryLabel ?capital ?capitalLabel\n" +
                    "WHERE" +
                    "{" +
                    "  ?country wdt:P31 wd:Q3624078 ." +
                    "  FILTER NOT EXISTS {?country wdt:P31 wd:Q3024240}\n" +
                    "  FILTER NOT EXISTS {?country wdt:P31 wd:Q28171280}\n" +
                    "  OPTIONAL { ?country wdt:P36 ?capital } ." +
                    "" +
                    "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE],en\" }\n" +
                    "}" +
                    "ORDER BY ?countryLabel";


            String endpointUrl = "https://query.wikidata.org/sparql?query=" +
                    URLEncoder.encode(sparqlQuery, StandardCharsets.UTF_8.toString()) +
                    "&format=json";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpointUrl))
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Process the JSON response here
            // Process the JSON response using Jackson ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.body());

            // Access the data from the JSON response
            JsonNode resultsNode = jsonResponse.path("results").path("bindings");
            for (JsonNode result : resultsNode) {
                String countryLabel = result.path("countryLabel").path("value").asText();
                String capitalLabel = result.path("capitalLabel").path("value").asText();

                System.out.println("Country: " + countryLabel + ", Capital: " + capitalLabel);

                // Obtener otras capitales del JSON (distintas a la capital correcta)
                List<String> allCapitals = getAllCapitals(resultsNode, countryLabel, capitalLabel);

                // Seleccionar tres capitales incorrectas aleatorias
                List<String> incorrectCapitals = selectRandomIncorrectCapitals(allCapitals, capitalLabel, 3);

                // Realizar la lógica para generar la pregunta
                // Aquí, estoy llamando a tu método questionGenerator con los datos obtenidos
                questionGenerator("¿Cuál es la capital de " + countryLabel + "?",
                        incorrectCapitals,
                        capitalLabel,
                        new Category("Geography", "Geography questions")); // Ajusta la categoría según tus necesidades

                //System.out.println("Country: " + countryLabel + ", Capital: " + capitalLabel);


        }
    } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> getAllCapitals(JsonNode resultsNode, String countryLabel, String correctCapital) {
        // Obtener todas las capitales del JSON (distintas a la capital correcta)
        List<String> allCapitals = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            String capital = result.path("capitalLabel").path("value").asText();
            if (!capital.equals(correctCapital)) {
                allCapitals.add(capital);
            }
        }
        return allCapitals;
    }

    private static List<String> selectRandomIncorrectCapitals(List<String> allCapitals, String correctCapital, int count) {
        // Seleccionar aleatoriamente tres capitales incorrectas distintas a la capital correcta
        List<String> incorrectCapitals = new ArrayList<>();
        Random random = new Random();
        while (incorrectCapitals.size() < count && allCapitals.size() > 0) {
            int randomIndex = random.nextInt(allCapitals.size());
            String selectedCapital = allCapitals.remove(randomIndex);
            if (!selectedCapital.equals(correctCapital) && !incorrectCapitals.contains(selectedCapital)) {
                incorrectCapitals.add(selectedCapital);
            }
        }
        return incorrectCapitals;
    }
}
