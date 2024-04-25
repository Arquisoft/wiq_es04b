package com.uniovi.components.generators;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniovi.entities.Category;
import com.uniovi.entities.Question;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class QuestionGeneratorV2 implements QuestionGenerator{

    private JsonNode jsonNode;
    private String language_placeholder;
    private String question_placeholder;
    private String language;

    public QuestionGeneratorV2(JsonNode jsonNode, String language) {
        this.jsonNode = jsonNode;
        this.language_placeholder = jsonNode.get("language_placeholder").textValue();
        this.question_placeholder = jsonNode.get("question_placeholder").textValue();
        this.language = language;
    }

    @Override
    public String getQuery() {
        return null;
    }

    @Override
    public List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();
        JsonNode categories = jsonNode.findValue("categories");
        for(JsonNode category : categories){
            String categoryName = category.get("name").textValue();
            Category cat = new Category(categoryName);
            JsonNode questionsNode = category.findValue("questions");
            for(JsonNode question : questionsNode){
                questions.addAll(this.generateQuestion(question, cat));
            }
        }
        return questions;
    }

    private List<Question> generateQuestion(JsonNode question, Category cat) {
        String query = question.get("sparqlQuery").textValue();
        query = query.replace(language_placeholder, language);
        JsonNode results = getQueryResult(query);

    }

    private JsonNode getQueryResult(String query) {

        HttpClient client = HttpClient.newHttpClient();
        try {

            String endpointUrl = "https://query.wikidata.org/sparql?query=" +
                    URLEncoder.encode(this.getQuery(), StandardCharsets.UTF_8) +
                    "&format=json";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpointUrl))
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Process the JSON response using Jackson ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.body());

            // Access the data from the JSON response
            JsonNode resultsNode = jsonResponse.path("results").path("bindings");

            for (JsonNode result : resultsNode) {

                List<String> options = this.generateOptions(resultsNode, result);
                String correctAnswer = this.generateCorrectAnswer(result);
                String questionStatement = this.getQuestionSubject(result);

            }
        } catch (InterruptedException e) {
            throw new QuestionGeneratorException("Generation of questions was interrupted");
        } catch (Exception e) {
            throw new QuestionGeneratorException("An error occurred while generating questions");
        }

        return questions;

    }

    private static class QuestionGeneratorException extends RuntimeException {
        public QuestionGeneratorException(String message) {
            super(message);
        }
    }
}
