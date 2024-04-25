package com.uniovi.components.generators;

import com.uniovi.entities.Category;
import com.uniovi.entities.Question;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public interface QuestionGenerator {
    List<Question> getQuestions(String language) throws IOException;
}
