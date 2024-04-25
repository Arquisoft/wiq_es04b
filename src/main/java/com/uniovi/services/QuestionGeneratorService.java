package com.uniovi.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniovi.components.generators.QuestionGeneratorV2;
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

    private Stack<JsonNode> types = new Stack<>();

    public QuestionGeneratorService(QuestionService questionService) {
        this.questionService = questionService;
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
            List<Question> qen = qgen.getQuestions(Question.SPANISH);
            qen.forEach(questionService::addNewQuestion);
            List<Question> qfr = qgen.getQuestions(Question.SPANISH);
            qfr.forEach(questionService::addNewQuestion);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
