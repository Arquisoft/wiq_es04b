package com.uniovi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String statement;

    @OneToMany(mappedBy = "question")
    private List<Answer> options;

    @OneToOne
    private Answer correctAnswer;

    @ManyToOne
    private Category category;

    public Question(String statement, List<Answer> options, Answer correctAnswer, Category category) {
        Assert.isTrue(options.contains(correctAnswer), "Correct answer must be one of the options");
        this.statement = statement;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.category = category;
    }

    public void addOption(Answer option) {
        options.add(option);
    }

    public void removeOption(Answer option){
        options.remove(option);
    }

    public Answer getOption(int index){
        return options.get(index);
    }

    public Answer getOptions(String answer){
        for (Answer option : options) {
            if (option.getText().equals(answer)) {
                return option;
            }
        }
        return null;
    }

    public boolean isCorrectAnswer(Answer answer){
        return answer.isCorrect();
    }

    public void scrambleOptions(){
        Collections.shuffle(options);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(id, question.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
