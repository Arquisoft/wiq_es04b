package com.uniovi.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniovi.interfaces.JsonEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class GameSession implements JsonEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Player player;

    private Integer correctQuestions;
    private Integer totalQuestions;

    // When game started
    private LocalDateTime createdAt;

    // When the last question started, or when the game ended
    private LocalDateTime finishTime;

    private int score;

    @Transient
    private Set<Question> answeredQuestions = new HashSet<>();

    @Transient
    private List<Question> questionsToAnswer = new ArrayList<>();

    @Transient
    private Question currentQuestion;

    public GameSession(Player player, List<Question> questions) {
        this.player = player;
        this.questionsToAnswer = questions;
        this.createdAt = LocalDateTime.now();
        this.finishTime = LocalDateTime.now();
        this.correctQuestions = 0;
        this.totalQuestions = 0;
        getNextQuestion();
    }

    public void addQuestion(boolean correct, int timeLeft) {
        if(correct)
            correctQuestions++;
        totalQuestions++;

        // TODO: Calculate score
        if (correct) {
            score += timeLeft + 10 /* magic number TBD */;
        }
    }

    public void addAnsweredQuestion(Question question) {
        questionsToAnswer.remove(question);
        answeredQuestions.add(question);
    }

    public boolean isAnswered(Question question) {
        return answeredQuestions.contains(question);
    }

    public Question getNextQuestion() {
        if(questionsToAnswer.isEmpty()) {
            currentQuestion = null;
            return null;
        }
        Collections.shuffle(questionsToAnswer);
        currentQuestion = questionsToAnswer.get(0);
        return questionsToAnswer.get(0);
    }

    @Override
    public JsonNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.createObjectNode()
                .put("id", id)
                .put("player", player.getId())
                .put("correctQuestions", correctQuestions)
                .put("totalQuestions", totalQuestions)
                .put("createdAt", createdAt.toString())
                .put("finishTime", finishTime.toString())
                .put("score", score);
    }

    public boolean hasQuestionId(Long idQuestion) {
        for (Question q : questionsToAnswer) {
            if (q.getId().equals(idQuestion))
                return true;
        }

        for (Question q : answeredQuestions) {
            if (q.getId().equals(idQuestion))
                return true;
        }
        return false;
    }
}
