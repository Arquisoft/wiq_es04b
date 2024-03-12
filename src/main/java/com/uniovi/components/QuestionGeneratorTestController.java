package com.uniovi.components;

import com.uniovi.components.generators.geography.CapitalQuestionGenerator;
import com.uniovi.entities.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QuestionGeneratorTestController {

    /*@RequestMapping("/test")
    public void test() {
        List<Question> q = qgen.getQuestions();
        for(Question question : q){
            System.out.println(question);
        }
    }*/
}
