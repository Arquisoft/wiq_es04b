package com.uniovi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Answer {

    @Id
    @GeneratedValue
    private Long id;

    private String text;
    private boolean correct;

    @ManyToOne
    private Question question;

    public Answer(String text, boolean correct) {
        this.text = text;
        this.correct = correct;
    }

}