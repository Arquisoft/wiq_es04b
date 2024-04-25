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
    private String answer_placeholder;
    private String language;

    public QuestionGeneratorV2(JsonNode jsonNode, String language) {
        this.jsonNode = jsonNode;
        this.language_placeholder = jsonNode.get("language_placeholder").textValue();
        this.question_placeholder = jsonNode.get("question_placeholder").textValue();
        this.answer_placeholder = jsonNode.get("answer_placeholder").textValue();
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
        // Get the SPARQL query from the JSON
        String query = question.get("sparqlQuery").textValue();
        // Replace the placeholders in the query with the actual values
        query = query.replace(language_placeholder, language).
                replace(question_placeholder, question.get("question").textValue()).
                replace(answer_placeholder, question.get("answer").textValue());
        // Execute the query and get the results
        JsonNode results = getQueryResult(query);
        List<Question> questions = new ArrayList<>();
        // Prepare the statement base based on the language
        String statement = this.prepareStatement(question);
        for(JsonNode result : results){
            String statement = result.get("statement").textValue();
            List<String> options = new ArrayList<>();
            for(JsonNode option : result.get("options")){
                options.add(option.textValue());
            }
            String correctAnswer = result.get("correctAnswer").textValue();
            this.questionGenerator(statement, options, correctAnswer, cat, questions);
        }
    }

    /**
     * Generates a statement based on the language of the question
     * @param question The question node
     * @return The statement in the language of the question or null if the language is not found
     */
    private String prepareStatement(JsonNode question) {
        JsonNode statementNode = question.findValue("statements");
        for (JsonNode statement : statementNode) {
            if (statement.get("language").textValue().equals(language)) {
                return statement.get("statement").textValue();
            }
        }
        return null;
    }

    private JsonNode getQueryResult(String query) {

        HttpClient client = HttpClient.newHttpClient();
        JsonNode resultsNode;
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
            resultsNode = jsonResponse.path("results").path("bindings");

        } catch (InterruptedException e) {
            throw new QuestionGeneratorException("Generation of questions was interrupted");
        } catch (Exception e) {
            throw new QuestionGeneratorException("An error occurred while generating questions");
        }

        return resultsNode;

    }

    private static class QuestionGeneratorException extends RuntimeException {
        public QuestionGeneratorException(String message) {
            super(message);
        }
    }
}
