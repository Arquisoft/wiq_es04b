package com.uniovi.components;

import com.uniovi.components.generators.geography.CapitalQuestionGenerator;
import com.uniovi.entities.Question;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuestionGeneratorTest {

    public static void main(String[] args) {
        com.uniovi.components.generators.QuestionGenerator qgen =  new CapitalQuestionGenerator();
        List<Question> q = qgen.getQuestions();
        for(Question question : q){
            System.out.println(question);
        }
    }
}
