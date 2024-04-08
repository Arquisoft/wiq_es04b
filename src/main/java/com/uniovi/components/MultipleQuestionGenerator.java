package com.uniovi.components;

import com.uniovi.components.generators.QuestionGenerator;
import com.uniovi.entities.Question;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MultipleQuestionGenerator {
    private QuestionGenerator[] generators;

    public MultipleQuestionGenerator(QuestionGenerator... generators) {
        this.generators = generators;
    }

    public List<Question> getQuestions() throws InterruptedException, IOException {
        List<Question> questions = new ArrayList<>();
        for (QuestionGenerator generator : generators) {
            questions.addAll(generator.getQuestions());
        }
        return questions;
    }
}
