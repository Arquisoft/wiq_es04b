package com.uniovi.components.generators;

import com.uniovi.entities.Category;
import com.uniovi.entities.Question;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface QuestionGenerator {

    String getQuery();
    List<Question> getQuestions() throws InterruptedException;

    Category getCategory();


}
