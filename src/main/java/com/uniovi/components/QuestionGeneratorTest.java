package com.uniovi.components;

import com.uniovi.components.generators.QuestionGenerator;
import com.uniovi.entities.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class QuestionGeneratorTest {

    @Autowired
    QuestionGenerator qgen;

    @RequestMapping("/test")
    public void test() {
        List<Question> q = qgen.getQuestions();
        for(Question question : q){
            System.out.println(question);
        }
    }
}
