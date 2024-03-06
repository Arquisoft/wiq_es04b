package com.uniovi.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.uniovi.interfaces.JsonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Question implements JsonEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String statement;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> options = new ArrayList<>();

    @OneToOne
    private Answer correctAnswer;

    @ManyToOne
    private Category category;

    public Question(String statement, List<Answer> options, Answer correctAnswer, Category category) {
        Assert.isTrue(options.contains(correctAnswer), "Correct answer must be one of the options");
        this.statement = statement;
        Associations.QuestionAnswers.addAnswer(this, options);
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

    @Override
    public String toString() {
        return "Question{" +
                "statement='" + statement + '\'' +
                ", options=" + options.toString() +
                ", correctAnswer=" + correctAnswer.toString() +
                ", category=" + category +
                '}';
    }

    @Override
    public JsonNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode
                obj = mapper.createObjectNode()
                    .put("id", id)
                    .put("statement", statement);
                obj .put("category", category.toJson());
        ArrayNode optionsArray = mapper.createArrayNode();
        options.forEach(option -> optionsArray.add(option.toJson()));
        obj         .put("options", optionsArray);
        return obj;
    }
}
