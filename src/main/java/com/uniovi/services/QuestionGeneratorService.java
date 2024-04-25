package com.uniovi.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniovi.components.generators.QuestionGenerator;
import com.uniovi.components.generators.QuestionGeneratorV2;
import com.uniovi.dto.QuestionDto;
import com.uniovi.entities.Category;
import com.uniovi.entities.Question;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@Service
public class QuestionGeneratorService {

    private final QuestionService questionService;

    @Value("${question.json.path:src/main/resources/static/JSON/QuestionTemplates.json}")
    private String jsonFilePath = "src/main/resources/static/JSON/QuestionTemplates.json";

    private Deque<QuestionType> types = new ArrayDeque<>();

    private JsonNode json;

    public QuestionGeneratorService(QuestionService questionService) {
        this.questionService = questionService;
        parseQuestionTypes();
    }

    private void parseQuestionTypes() {
        try {
            File jsonFile = new File(jsonFilePath);
            ObjectMapper objectMapper = new ObjectMapper();
            json = objectMapper.readTree(jsonFile);
            JsonNode categories = json.findValue("categories");
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
    @Transactional
    public void generateQuestions() throws IOException {
        if (types.isEmpty()) {
            return;
        }
        QuestionGenerator qgen = new QuestionGeneratorV2(json);
        QuestionType type = types.pop();
        List<QuestionDto> questions;

        List<Question> qsp = qgen.getQuestions(Question.SPANISH, type.getQuestion(), type.getCategory());
        questions = qsp.stream().map(QuestionDto::new).toList();
        questions.forEach(questionService::addNewQuestion);

        List<Question> qen = qgen.getQuestions(Question.ENGLISH,  type.getQuestion(), type.getCategory());
        questions = qen.stream().map(QuestionDto::new).toList();
        questions.forEach(questionService::addNewQuestion);

        List<Question> qfr = qgen.getQuestions(Question.FRENCH,  type.getQuestion(), type.getCategory());
        questions = qfr.stream().map(QuestionDto::new).toList();
        questions.forEach(questionService::addNewQuestion);
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
