package com.uniovi.components.generators;

import com.uniovi.entities.Category;
import com.uniovi.entities.Question;

import java.util.List;

public interface QuestionGenerator {

    String getQuery();
    List<Question> getQuestions();

    Category getCategory();


}
