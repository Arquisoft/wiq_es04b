package com.uniovi.services;

import com.uniovi.components.generators.QuestionGenerator;
import com.uniovi.components.generators.geography.BorderQuestionGenerator;
import com.uniovi.components.generators.geography.CapitalQuestionGenerator;
import com.uniovi.components.generators.geography.ContinentQuestionGeneration;
import com.uniovi.dto.PlayerDto;
import com.uniovi.entities.Associations;
import com.uniovi.entities.GameSession;
import com.uniovi.entities.Player;
import com.uniovi.repositories.GameSessionRepository;
import com.uniovi.repositories.QuestionRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InsertSampleDataService {
    private final PlayerService playerService;
    private final QuestionService questionService;
    private final CategoryService categoryService;
    private final QuestionRepository questionRepository;

    public InsertSampleDataService(PlayerService playerService, QuestionService questionService,
                                   CategoryService categoryService, QuestionRepository questionRepository) {
        this.playerService = playerService;
        this.questionService = questionService;
        this.categoryService = categoryService;
        this.questionRepository = questionRepository;
    }

    @PostConstruct
    public void init() {
        if (playerService.getUserByEmail("test@test.com").isPresent())
            return;

        PlayerDto player = new PlayerDto();
        player.setEmail("test@test.com");
        player.setUsername("test");
        player.setPassword("test");
        player.setRoles(new String[]{"ROLE_USER"});
        playerService.generateApiKey(playerService.addNewPlayer(player));
    }

    @EventListener(ApplicationReadyEvent.class) // Uncomment this line to insert sample data on startup
    public void insertSampleQuestions() {
        questionRepository.deleteAll();

        QuestionGenerator border = new BorderQuestionGenerator(categoryService);
        border.getQuestions().forEach(questionService::addNewQuestion);

        QuestionGenerator capital = new CapitalQuestionGenerator(categoryService);
        capital.getQuestions().forEach(questionService::addNewQuestion);

        QuestionGenerator continent = new ContinentQuestionGeneration(categoryService);
        continent.getQuestions().forEach(questionService::addNewQuestion);
    }
}
