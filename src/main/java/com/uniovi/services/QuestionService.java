package com.uniovi.services;

import com.uniovi.dto.QuestionDto;
import com.uniovi.entities.Question;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface QuestionService {

    /**
     * Add a new question to the database
     *
     * @param question Question to be added
     */
    void addNewQuestion(Question question);

    /**
     * Get all the questions in the database
     *
     * @return A list with all the questions
     */
    List<Question> getAllQuestions();

    /**
     * Get a question by its id
     *
     * @param id The id of the question
     * @return The question with the given id
     */
    Optional<Question> getQuestion(Long id);
}