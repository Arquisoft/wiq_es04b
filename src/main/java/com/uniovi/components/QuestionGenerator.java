package com.uniovi.components;

import com.uniovi.components.generators.geography.CapitalQuestionGenerator;
import org.springframework.stereotype.Component;

@Component
public class QuestionGenerator {

    public static void main(String[] args) {
        com.uniovi.components.generators.QuestionGenerator qgen =  new CapitalQuestionGenerator();
        System.out.println(qgen.getQuestions());
    }

}
