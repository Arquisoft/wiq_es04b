package com.uniovi.services;

import com.uniovi.components.MultipleQuestionGenerator;
import com.uniovi.components.generators.QuestionGenerator;
import com.uniovi.components.generators.geography.*;
import com.uniovi.components.generators.history.*;
import com.uniovi.dto.PlayerDto;
import com.uniovi.entities.Associations;
import com.uniovi.entities.GameSession;
import com.uniovi.entities.Player;
import com.uniovi.entities.Question;
import com.uniovi.repositories.GameSessionRepository;
import com.uniovi.repositories.QuestionRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.hibernate.validator.constraints.Currency;
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
    public void insertSampleQuestions() {
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
    public void generateSampleData() {
        questionRepository.deleteAll();

        // Generar preguntas de geografía en inglés
        MultipleQuestionGenerator geographyQuestionsEn = new MultipleQuestionGenerator(
                new ContinentQuestionGeneration(categoryService, Question.ENGLISH),
                new CapitalQuestionGenerator(categoryService, Question.ENGLISH),
                new BorderQuestionGenerator(categoryService, Question.ENGLISH),
                new AreaQuestionGenerator(categoryService, Question.ENGLISH),
                new CurrencyQuestionGenerator(categoryService, Question.ENGLISH),
                new LanguageQuestionGenerator(categoryService, Question.ENGLISH),
                new PopulationQuestionGenerator(categoryService, Question.ENGLISH)
        );
        List<Question> geographyQuestions = geographyQuestionsEn.getQuestions();
        geographyQuestions.forEach(questionService::addNewQuestion);

        // Generar preguntas de geografía en español
        MultipleQuestionGenerator geographyQuestionsEs = new MultipleQuestionGenerator(
                new ContinentQuestionGeneration(categoryService, Question.SPANISH),
                new CapitalQuestionGenerator(categoryService, Question.SPANISH),
                new BorderQuestionGenerator(categoryService, Question.SPANISH),
                new AreaQuestionGenerator(categoryService, Question.SPANISH),
                new CurrencyQuestionGenerator(categoryService, Question.SPANISH),
                new LanguageQuestionGenerator(categoryService, Question.SPANISH),
                new PopulationQuestionGenerator(categoryService, Question.SPANISH)
        );
        List<Question> geographyQuestionsSpanish = geographyQuestionsEs.getQuestions();
        geographyQuestionsSpanish.forEach(questionService::addNewQuestion);

        // Generar preguntas de geografía en francés
        MultipleQuestionGenerator geographyQuestionsFr = new MultipleQuestionGenerator(
                new ContinentQuestionGeneration(categoryService, Question.FRENCH),
                new CapitalQuestionGenerator(categoryService, Question.FRENCH),
                new BorderQuestionGenerator(categoryService, Question.FRENCH),
                new AreaQuestionGenerator(categoryService, Question.FRENCH),
                new CurrencyQuestionGenerator(categoryService, Question.FRENCH),
                new LanguageQuestionGenerator(categoryService, Question.FRENCH),
                new PopulationQuestionGenerator(categoryService, Question.FRENCH)
        );
        List<Question> geographyQuestionsFrench = geographyQuestionsFr.getQuestions();
        geographyQuestionsFrench.forEach(questionService::addNewQuestion);

        // Generar preguntas de historia en inglés
        MultipleQuestionGenerator historyQuestionsEn = new MultipleQuestionGenerator(
                new HistoricalArtifactQuestionGeneration(categoryService, Question.ENGLISH),
                new HistoricalBattleQuestionGeneration(categoryService, Question.ENGLISH),
                new HistoricalDynastyQuestionGeneration(categoryService, Question.ENGLISH),
                new HistoricalEventQuestionGeneration(categoryService, Question.ENGLISH),
                new HistoricalInventionQuestionGeneration(categoryService, Question.ENGLISH),
                new HistoricalPeriodQuestionGeneration(categoryService, Question.ENGLISH),
                new HistoricalLeaderQuestionGeneration(categoryService, Question.ENGLISH)
        );
        List<Question> historyQuestions = historyQuestionsEn.getQuestions();
        historyQuestions.forEach(questionService::addNewQuestion);

        // Generar preguntas de historia en español
        MultipleQuestionGenerator historyQuestionsEs = new MultipleQuestionGenerator(
                new HistoricalArtifactQuestionGeneration(categoryService, Question.SPANISH),
                new HistoricalBattleQuestionGeneration(categoryService, Question.SPANISH),
                new HistoricalDynastyQuestionGeneration(categoryService, Question.SPANISH),
                new HistoricalEventQuestionGeneration(categoryService, Question.SPANISH),
                new HistoricalInventionQuestionGeneration(categoryService, Question.SPANISH),
                new HistoricalPeriodQuestionGeneration(categoryService, Question.SPANISH),
                new HistoricalLeaderQuestionGeneration(categoryService, Question.SPANISH)
        );
        List<Question> historyQuestionsSpanish = historyQuestionsEs.getQuestions();
        historyQuestionsSpanish.forEach(questionService::addNewQuestion);

        // Generar preguntas de historia en francés
        MultipleQuestionGenerator historyQuestionsFr = new MultipleQuestionGenerator(
                new HistoricalArtifactQuestionGeneration(categoryService, Question.FRENCH),
                new HistoricalBattleQuestionGeneration(categoryService, Question.FRENCH),
                new HistoricalDynastyQuestionGeneration(categoryService, Question.FRENCH),
                new HistoricalEventQuestionGeneration(categoryService, Question.FRENCH),
                new HistoricalInventionQuestionGeneration(categoryService, Question.FRENCH),
                new HistoricalPeriodQuestionGeneration(categoryService, Question.FRENCH),
                new HistoricalLeaderQuestionGeneration(categoryService, Question.FRENCH)
        );
        List<Question> historyQuestionsFrench = historyQuestionsFr.getQuestions();
        historyQuestionsFrench.forEach(questionService::addNewQuestion);

        log.info("Sample questions inserted");
    }

}
