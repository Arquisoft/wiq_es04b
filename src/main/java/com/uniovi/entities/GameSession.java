package com.uniovi.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class GameSession {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Player player;

    private Integer correctQuestions;
    private Integer totalQuestions;

    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    public void addQuestion(boolean correct) {
        if(correct)
            correctQuestions++;
        totalQuestions++;
    }

}
