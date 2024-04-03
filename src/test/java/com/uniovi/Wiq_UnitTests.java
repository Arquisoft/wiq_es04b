package com.uniovi;

import com.fasterxml.jackson.databind.JsonNode;
import com.uniovi.components.generators.geography.BorderQuestionGenerator;
import com.uniovi.components.generators.geography.CapitalQuestionGenerator;
import com.uniovi.components.generators.geography.ContinentQuestionGeneration;
import com.uniovi.entities.*;
import com.uniovi.services.AnswerService;
import com.uniovi.services.CategoryService;
import com.uniovi.services.PlayerService;
import com.uniovi.services.QuestionService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Tag("unit")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class Wiq_UnitTests {
    @Autowired
    private PlayerService playerService;
    @Autowired
    private AnswerService answerService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private CategoryService categoryService;
    @Test
    @Order(1)
    public void testPlayerService() {
        List<Player> players = playerService.getUsersByRole("ROLE_USER");
        Assertions.assertEquals(1, players.size());
    }
    @Test
    @Order(2)
    public void testQuestions(){
        List<Question> questions = questionService.getAllQuestions();
        Assertions.assertEquals(3802,questions.size());

    }
    @Test
    @Order(2)
    public void testRandomQuestions(){
        List<Question> questions = questionService.getRandomQuestions(5);
        Assertions.assertEquals(5,questions.size());
    }

    @Test
    @Order(3)
    public void testBorderQuestionsGenerator(){
        BorderQuestionGenerator borderQuestionGenerator=new BorderQuestionGenerator(categoryService,Question.SPANISH);
        List<Question> questions = borderQuestionGenerator.getQuestions();
        Assertions.assertEquals(855,questions.size());

        for (Question question : questions) {
            Assertions.assertNotNull(question.getCorrectAnswer());
            Assertions.assertEquals(4, question.getOptions().size());
            Assertions.assertTrue(question.getOptions().contains(question.getCorrectAnswer()));
        }
    }

    @Test
    @Order(4)
    public void testCapitalQuestionsGenerator(){
        CapitalQuestionGenerator capitalQuestionGenerator=new CapitalQuestionGenerator(categoryService,Question.SPANISH);
        List<Question> questions = capitalQuestionGenerator.getQuestions();
        Assertions.assertEquals(206,questions.size());

        for (Question question : questions) {
            Assertions.assertNotNull(question.getCorrectAnswer());
            Assertions.assertEquals(4, question.getOptions().size());
            Assertions.assertTrue(question.getOptions().contains(question.getCorrectAnswer()));
        }
    }

    @Test
    @Order(5)
    public void testContinentQuestionsGenerator(){
        ContinentQuestionGeneration continentQuestionGenerator=new ContinentQuestionGeneration(categoryService,Question.SPANISH);
        List<Question> questions = continentQuestionGenerator.getQuestions();
        Assertions.assertEquals(207,questions.size());

        for (Question question : questions) {
            Assertions.assertNotNull(question.getCorrectAnswer());
            Assertions.assertEquals(4, question.getOptions().size());
            Assertions.assertTrue(question.getOptions().contains(question.getCorrectAnswer()));
        }
    }

    @Test
    @Order(6)
    public void testAddRole() {
        Player player = new Player("name","email","password");
        Role role = new Role();
        Associations.PlayerRole.addRole(player, role);
        Assertions.assertTrue(player.getRoles().contains(role));
        Assertions.assertTrue(role.getPlayers().contains(player));
    }

    @Test
    @Order(7)
    public void testRemoveRole() {
        Player player = new Player("name","email","password");
        Role role = new Role();
        Associations.PlayerRole.addRole(player, role);
        Associations.PlayerRole.removeRole(player, role);
        Assertions.assertFalse(player.getRoles().contains(role));
        Assertions.assertFalse(role.getPlayers().contains(player));
    }

    @Test
    @Order(8)
    public void testAddApiKey() {
        Player player = new Player("name","email","password");
        ApiKey apiKey = new ApiKey();
        Associations.PlayerApiKey.addApiKey(player, apiKey);
        Assertions.assertEquals(player.getApiKey(), apiKey);
        Assertions.assertEquals(apiKey.getPlayer(), player);
    }

    @Test
    @Order(9)
    void testRemoveApiKey() {
        Player player = new Player("name","email","password");
        ApiKey apiKey = new ApiKey();
        Associations.PlayerApiKey.addApiKey(player, apiKey);
        Associations.PlayerApiKey.removeApiKey(player, apiKey);
        Assertions.assertNull(player.getApiKey());
        Assertions.assertNull(apiKey.getPlayer());
    }

    @Test
    @Order(9)
    public void testAddAccessLog() {
        ApiKey apiKey = new ApiKey();
        RestApiAccessLog accessLog = new RestApiAccessLog();
        Associations.ApiKeyAccessLog.addAccessLog(apiKey, accessLog);
        Assertions.assertTrue(apiKey.getAccessLogs().contains(accessLog));
        Assertions.assertEquals(accessLog.getApiKey(), apiKey);
    }

    @Test
    @Order(10)
    public void testRemoveAccessLog() {
        ApiKey apiKey = new ApiKey();
        RestApiAccessLog accessLog = new RestApiAccessLog();
        Associations.ApiKeyAccessLog.addAccessLog(apiKey, accessLog);
        Associations.ApiKeyAccessLog.removeAccessLog(apiKey, accessLog);
        Assertions.assertFalse(apiKey.getAccessLogs().contains(accessLog));
        Assertions.assertNull(accessLog.getApiKey());
    }

    @Test
    @Order(11)
    public void testAddGameSession() {
        Player player = new Player("name","email","password");
        GameSession gameSession = new GameSession();
        Associations.PlayerGameSession.addGameSession(player, gameSession);
        Assertions.assertTrue(player.getGameSessions().contains(gameSession));
        Assertions.assertEquals(gameSession.getPlayer(), player);
    }

    @Test
    @Order(12)
    public void testRemoveGameSession() {
        Player player = new Player("name","email","password");
        GameSession gameSession = new GameSession();
        Associations.PlayerGameSession.addGameSession(player, gameSession);
        Associations.PlayerGameSession.removeGameSession(player, gameSession);
        Assertions.assertFalse(player.getGameSessions().contains(gameSession));
        Assertions.assertNull(gameSession.getPlayer());
    }

    @Test
    @Order(13)
    public void testAddAnswer() {
        Question question = new Question();
        List<Answer> answers = new ArrayList<>();
        Answer answer1 = new Answer();
        Answer answer2 = new Answer();
        answers.add(answer1);
        answers.add(answer2);
        Associations.QuestionAnswers.addAnswer(question, answers);
        Assertions.assertTrue(question.getOptions().contains(answer1));
        Assertions.assertTrue(question.getOptions().contains(answer2));
        Assertions.assertEquals(answer1.getQuestion(), question);
        Assertions.assertEquals(answer2.getQuestion(), question);
    }

    @Test
    @Order(14)
    public void testRemoveAnswer() {
        Question question = new Question();
        List<Answer> answers = new ArrayList<>();
        Answer answer1 = new Answer();
        Answer answer2 = new Answer();
        answers.add(answer1);
        answers.add(answer2);
        Associations.QuestionAnswers.addAnswer(question, answers);
        Associations.QuestionAnswers.removeAnswer(question, answers);
        Assertions.assertFalse(question.getOptions().contains(answer1));
        Assertions.assertFalse(question.getOptions().contains(answer2));
        Assertions.assertNull(answer1.getQuestion());
        Assertions.assertNull(answer2.getQuestion());
    }

    @Test
    @Order(15)
    public void testCategoryCreation() {
        Category category = new Category("Test Category", "This is a test category");
        Assertions.assertEquals("Test Category", category.getName());
        Assertions.assertEquals("This is a test category", category.getDescription());
    }

    @Test
    @Order(16)
    public void testJsonGeneration() {
        Category category = new Category("Test Category", "This is a test category");
        JsonNode json = category.toJson();
        Assertions.assertEquals("Test Category", json.get("name").asText());
        Assertions.assertEquals("This is a test category", json.get("description").asText());
    }

    @Test
    @Order(17)
    public void testQuestionAssociation() {
        Category category = new Category("Test Category", "This is a test category");
        Question question = new Question();
        question.setCategory(category);

        Set<Question> questions = new HashSet<>();
        questions.add(question);
        category.setQuestions(questions);

        Assertions.assertEquals(1, category.getQuestions().size());
        Assertions.assertTrue(category.getQuestions().contains(question));
    }

    @Test
    @Order(18)
    public void testAnswerToJson() {
        Question question = new Question();
        question.setId(1L);

        Answer answer = new Answer("Sample Answer", true);
        answer.setQuestion(question);
        answer.setId(1L);

        JsonNode json = answer.toJson();

        Assertions.assertEquals(1L, json.get("id").asLong());
        Assertions.assertEquals("Sample Answer", json.get("text").asText());
        Assertions.assertTrue(json.get("correct").asBoolean());
        Assertions.assertEquals(1L, json.get("question").asLong());
    }

    @Test
    @Order(19)
    public void testAddQuestion_Correct() {
        Player player = new Player("name","email","password");
        List<Question> questions = new ArrayList<>();
        GameSession gameSession = new GameSession(player, questions);

        int initialScore = gameSession.getScore();
        gameSession.addQuestion(true, 20);
        Assertions.assertEquals(initialScore + 30, gameSession.getScore());
        Assertions.assertEquals(1, gameSession.getCorrectQuestions());
        Assertions.assertEquals(1, gameSession.getTotalQuestions());
    }

    @Test
    @Order(20)
    public void testAddQuestion_Incorrect() {
        Player player = new Player("name","email","password");
        List<Question> questions = new ArrayList<>();
        GameSession gameSession = new GameSession(player, questions);

        int initialScore = gameSession.getScore();
        gameSession.addQuestion(false, 20);
        Assertions.assertEquals(initialScore, gameSession.getScore());
        Assertions.assertEquals(0, gameSession.getCorrectQuestions());
        Assertions.assertEquals(1, gameSession.getTotalQuestions());
    }

    @Test
    @Order(20)
    public void testAddAnsweredQuestion() {
        Player player = new Player("name","email","password");
        List<Question> questions = new ArrayList<>();
        Question question = new Question();
        questions.add(question);
        GameSession gameSession = new GameSession(player, questions);

        Assertions.assertTrue(gameSession.getQuestionsToAnswer().contains(question));
        Assertions.assertFalse(gameSession.getAnsweredQuestions().contains(question));
        gameSession.addAnsweredQuestion(question);
        Assertions.assertFalse(gameSession.getQuestionsToAnswer().contains(question));
        Assertions.assertTrue(gameSession.getAnsweredQuestions().contains(question));
    }

    @Test
    @Order(21)
    public void testGetDuration() {
        LocalDateTime createdAt = LocalDateTime.of(2022, 1, 1, 10, 0); // Assuming game started at 10:00 AM
        LocalDateTime finishTime = LocalDateTime.of(2022, 1, 1, 10, 5); // Assuming game finished at 10:05 AM
        Player player = new Player("name","email","password");
        List<Question> questions = new ArrayList<>();
        GameSession gameSession = new GameSession(player, questions);
        gameSession.setCreatedAt(createdAt);
        gameSession.setFinishTime(finishTime);

        Assertions.assertEquals("00:05:00", gameSession.getDuration());
    }


}
