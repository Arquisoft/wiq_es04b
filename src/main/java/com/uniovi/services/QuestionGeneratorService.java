package com.uniovi.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniovi.components.generators.QuestionGeneratorV2;
import com.uniovi.entities.Category;
import com.uniovi.entities.Question;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

@Service
public class QuestionGeneratorService {

    private final QuestionService questionService;

    @Value("${question.json.path}")
    private String jsonFilePath;

    private Stack<QuestionType> types = new Stack<>();

    private JsonNode currentType;

    public QuestionGeneratorService(QuestionService questionService) {
        this.questionService = questionService;
        parseQuestionTypes();
    }

    private void parseQuestionTypes() {
        try {
            File jsonFile = new File(jsonFilePath);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonFile);
            JsonNode categories = jsonNode.findValue("categories");
            for (JsonNode category : categories) {
                String categoryName = category.get("name").textValue();
                Category cat = new Category(categoryName);
                JsonNode questionsNode = category.findValue("questions");
                for (JsonNode question : questionsNode) {
                    types.push(new QuestionType(question, cat));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(fixedRate = 150000)
    public void generateQuestions() {

        File jsonFile = new File(jsonFilePath);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonFile);
            QuestionGeneratorV2 qgen = new QuestionGeneratorV2(jsonNode);
            List<Question> qsp = qgen.getQuestions(Question.SPANISH);
            qsp.forEach(questionService::addNewQuestion);
            List<Question> qen = qgen.getQuestions(Question.ENGLISH);
            qen.forEach(questionService::addNewQuestion);
            List<Question> qfr = qgen.getQuestions(Question.FRENCH);
            qfr.forEach(questionService::addNewQuestion);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private class QuestionType {

        private JsonNode question;
        private Category category;

        public QuestionType(JsonNode question, Category category) {
            this.question = question;
            this.category = category;
        }

        public JsonNode getQuestion() {
            return question;
        }

        public Category getCategory() {
            return category;
        }
    }


}
