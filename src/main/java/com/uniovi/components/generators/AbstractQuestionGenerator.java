package com.uniovi.components.generators;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniovi.entities.Answer;
import com.uniovi.entities.Category;
import com.uniovi.entities.Question;
import com.uniovi.services.CategoryService;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractQuestionGenerator implements QuestionGenerator{
    private List<Question> questions = new ArrayList<>();
    protected final CategoryService categoryService;

    protected Random random = new SecureRandom();

    protected String statement;
    protected String language;

    protected AbstractQuestionGenerator(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public void questionGenerator(String statement, List<String> options, String correctAnswer, Category category){
        List<Answer> answers = new ArrayList<>();
        //Generamos las respuestas y las añadimos a la lista
        for(String s: options){
            Answer answer = new Answer(s, false);
            answers.add(answer);
        }
        //Generamos la respuesta correcta y la añadimos a la lista
        Answer correct = new Answer(correctAnswer, true);
        answers.add(correct);

        Question question = new Question(statement, answers, correct, category, language);
        question.scrambleOptions();
        questions.add(question);
    }

    public List<Question> getQuestions() throws InterruptedException, IOException {
        HttpClient client = HttpClient.newHttpClient();
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
                questionGenerator(questionStatement, options, correctAnswer, this.getCategory());

            }
        return questions;
    }

    protected abstract List<String> generateOptions(JsonNode results, JsonNode result);
    protected abstract String generateCorrectAnswer(JsonNode result);

    protected abstract String getQuestionSubject(JsonNode result);

}
