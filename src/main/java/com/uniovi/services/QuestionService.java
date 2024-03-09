package com.uniovi.services;

import com.uniovi.dto.QuestionDto;
import com.uniovi.entities.Question;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface QuestionService {
    public static final Integer SECONDS_PER_QUESTION = 25;
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

    /**
     * Get a random question
     *
     * @return The question selected
     */
    Optional<Question> getRandomQuestion();

    /**
     * Get a random question from any category
     *
     * @param num The number of questions to get
     * @return The questions selected
     */
    List<Question> getRandomQuestions(int num);

    /**
     * Check if the answer is correct
     * @param idquestion The id of the question
     * @param idanswer The id of the answer
     * @return True if the answer is correct, false otherwise
     */
    boolean checkAnswer(Long idquestion, Long idanswer);
}
