package com.uniovi.components.generators;

import com.uniovi.entities.Category;
import com.uniovi.entities.Question;

import java.util.Dictionary;
import java.util.List;

public interface QuestionGenerator {

    public String getQuery();
    public List<Question> getQuestions();

    public Category getCategory();


}
