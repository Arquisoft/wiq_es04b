package com.uniovi.services;

import com.uniovi.components.MultipleQuestionGenerator;
import com.uniovi.components.generators.QuestionGenerator;
import com.uniovi.components.generators.geography.BorderQuestionGenerator;
import com.uniovi.components.generators.geography.CapitalQuestionGenerator;
import com.uniovi.components.generators.geography.ContinentQuestionGeneration;
import com.uniovi.dto.PlayerDto;
import com.uniovi.entities.Associations;
import com.uniovi.entities.GameSession;
import com.uniovi.entities.Player;
import com.uniovi.entities.Question;
import com.uniovi.repositories.GameSessionRepository;
import com.uniovi.repositories.QuestionRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class InsertSampleDataService {
    private final PlayerService playerService;
    private final QuestionService questionService;
    private final CategoryService categoryService;
    private final QuestionRepository questionRepository;
    private final GameSessionRepository gameSessionRepository;
    private Environment environment;

    private Logger log = LoggerFactory.getLogger(InsertSampleDataService.class);

    public InsertSampleDataService(PlayerService playerService, QuestionService questionService,
                                   CategoryService categoryService, QuestionRepository questionRepository,
                                   GameSessionRepository gameSessionRepository, Environment environment) {
        this.playerService = playerService;
        this.questionService = questionService;
        this.categoryService = categoryService;
        this.questionRepository = questionRepository;
        this.gameSessionRepository = gameSessionRepository;
        this.environment = environment;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class) // Uncomment this line to insert sample data on startup
    public void insertSampleQuestions() throws InterruptedException {
        if (!playerService.getUserByEmail("test@test.com").isPresent()) {
            PlayerDto player = new PlayerDto();
            player.setEmail("test@test.com");
            player.setUsername("test");
            player.setPassword("test");
            player.setRoles(new String[]{"ROLE_USER"});
            playerService.generateApiKey(playerService.addNewPlayer(player));
        }

        if (Arrays.stream(environment.getActiveProfiles()).anyMatch(env -> (env.equalsIgnoreCase("test")))) {
            log.info("Test profile active, skipping sample data insertion");
            return;
        }

        generateSampleData();
    }

    @Transactional
    public void generateTestQuestions() {
        questionRepository.deleteAll();
        questionService.testQuestions(4);
    }

    @Transactional
    public void generateSampleData() throws InterruptedException {

        questionRepository.deleteAll();

        MultipleQuestionGenerator allQuestionGenerator = new MultipleQuestionGenerator(
                new ContinentQuestionGeneration(categoryService, Question.ENGLISH),
                new CapitalQuestionGenerator(categoryService, Question.ENGLISH),
                new BorderQuestionGenerator(categoryService, Question.ENGLISH)
        );
        List<Question> questionsEn = allQuestionGenerator.getQuestions();
        questionsEn.forEach(questionService::addNewQuestion);

        allQuestionGenerator = new MultipleQuestionGenerator(
                new ContinentQuestionGeneration(categoryService, Question.SPANISH),
                new CapitalQuestionGenerator(categoryService, Question.SPANISH),
                new BorderQuestionGenerator(categoryService, Question.SPANISH)
        );
        List<Question> questionsEs = allQuestionGenerator.getQuestions();
        questionsEs.forEach(questionService::addNewQuestion);

        allQuestionGenerator = new MultipleQuestionGenerator(
                new ContinentQuestionGeneration(categoryService, Question.FRENCH),
                new CapitalQuestionGenerator(categoryService, Question.FRENCH),
                new BorderQuestionGenerator(categoryService, Question.FRENCH)
        );
        List<Question> questionsFr = allQuestionGenerator.getQuestions();
        questionsFr.forEach(questionService::addNewQuestion);

        log.info("Sample questions inserted");
    }
}
