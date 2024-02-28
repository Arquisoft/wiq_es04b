package com.uniovi.services.impl;

import com.uniovi.entities.Question;
import com.uniovi.repositories.QuestionRepository;
import com.uniovi.services.QuestionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public void addNewQuestion(Question question) {
        questionRepository.save(question);
    }

    @Override
    public List<Question> getAllQuestions() {
        List<Question> l = new ArrayList<>();
        questionRepository.findAll().forEach(l::add);
        return l;
    }

    @Override
    public Optional<Question> getQuestion(Long id) {
        return questionRepository.findById(id);
    }
}
