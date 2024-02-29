package com.uniovi.components.generators;

import com.uniovi.entities.Answer;
import com.uniovi.entities.Category;
import com.uniovi.entities.Question;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractQuestionGenerator implements QuestionGenerator{

    private List<Question> questions;

    public void questionGenerator(String statement, List<String> options, String correctAnswer, Category category){
        List<Answer> answers = new ArrayList<>();
        //Generamos las respuestas y las añadimos a la lista
        for(String s: options){
            Answer answer = new Answer(s, false);
            answers.add(answer);
        }
        //Generamos la respuesta correcta y la añadimos a la lista
        Answer correct = new Answer(correctAnswer, true);
        answers.add(correct);

        Question question = new Question(statement, answers, correct, category);
        System.out.println(question);
    }

    public List<Question> getQuestions() {
        return questions;
    }

}
