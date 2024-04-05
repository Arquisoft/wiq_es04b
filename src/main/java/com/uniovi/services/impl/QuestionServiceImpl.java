package com.uniovi.services.impl;

import com.uniovi.entities.Question;
import com.uniovi.repositories.QuestionRepository;
import com.uniovi.services.QuestionService;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final Random random = new Random();

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

    @Override
    public List<Question> getRandomQuestions(int num) {
        List<Question> allQuestions = questionRepository.findAll().stream()
                .filter(question -> question.getLanguage().equals(LocaleContextHolder.getLocale().getLanguage())).toList();
        List<Question> res = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            int idx = random.nextInt(allQuestions.size());
            while (allQuestions.get(idx).hasEmptyOptions()){
                idx = random.nextInt(allQuestions.size());
            }
            res.add(allQuestions.get(idx));
        }
        return res;
    }

    @Override
    public boolean checkAnswer(Long idquestion, Long idanswer) {
        Optional<Question> q = questionRepository.findById(idquestion);
        if (q.isPresent()) {
            return q.get().getCorrectAnswer().getId().equals(idanswer);
        }
        return false;
    }

}
