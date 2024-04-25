package com.uniovi.components.generators;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniovi.entities.Answer;
import com.uniovi.entities.Category;
import com.uniovi.entities.Question;

import java.io.IOException;
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
    public List<Question> getQuestions() throws IOException {
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

    private List<Question> generateQuestion(JsonNode question, Category cat) throws IOException {
        // Get the SPARQL query from the JSON
        String query = question.get("sparqlQuery").textValue();

        // Get the question and answer words from the JSON
        String questionLabel = question.get("question").textValue();
        String answerLabel= question.get("answer").textValue();

        // Replace the placeholders in the query with the actual values
        query = query.replace(language_placeholder, language).
                replace(question_placeholder, questionLabel).
                replace(answer_placeholder, answerLabel);

        // Execute the query and get the results
        JsonNode results = getQueryResult(query);
        List<Question> questions = new ArrayList<>();

        // Prepare the statement base based on the language
        String statement = this.prepareStatement(question);

        for (JsonNode result : results) {
            // Generate the correct answer
            String correctAnswer = result.path(answerLabel).path("value").asText();
            Answer correct = new Answer(correctAnswer, true);

            // Generate the options
            List<Answer> options = this.generateOptions(results, correctAnswer, answerLabel);
            options.add(correct);

            // Generate the question statement
            String questionStatement = statement.replace(question_placeholder, result.path(questionLabel).path("value").asText());

            // Generate the question
            questions.add(new Question(questionStatement, options, correct, cat, language));
        }
        return questions;
    }

    private List<Answer> generateOptions(JsonNode results, String correctAnswer, String answerLabel) {
        List<Answer> options = new ArrayList<>();
        for (JsonNode result : results) {
            String option = result.path(answerLabel).path("value").asText();
            if (!option.equals(correctAnswer)) {
                options.add(new Answer(option, false));
            }
        }
        return options;
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

    private JsonNode getQueryResult(String query) throws IOException {

        System.out.println("Query: " + query);
        HttpClient client = HttpClient.newHttpClient();
        JsonNode resultsNode;
        try {

            String endpointUrl = "https://query.wikidata.org/sparql?query=" +
                    URLEncoder.encode(query, StandardCharsets.UTF_8) +
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
        }
        return resultsNode;

    }

    private static class QuestionGeneratorException extends RuntimeException {
        public QuestionGeneratorException(String message) {
            super(message);
        }
    }
}
