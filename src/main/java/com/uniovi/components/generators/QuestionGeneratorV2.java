package com.uniovi.components.generators;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.entities.Category;
import com.uniovi.entities.Question;

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
    }
}
