package com.uniovi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.uniovi.components.generators.geography.BorderQuestionGenerator;
import com.uniovi.components.generators.geography.CapitalQuestionGenerator;
import com.uniovi.components.generators.geography.ContinentQuestionGeneration;
import com.uniovi.entities.*;
import com.uniovi.services.*;
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
    @Autowired
    private InsertSampleDataService sampleDataService;

    private Player createPlayer(){
        return new Player("name","test@email.com","password");
    }
    @Test
    @Order(1)
    public void testPlayerService() {
        List<Player> players = playerService.getUsersByRole("ROLE_USER");
        Assertions.assertEquals(1, players.size());
    }
    @Test
    @Order(2)
    public void testQuestions(){
        sampleDataService.insertSampleQuestions();
        sampleDataService.generateSampleData();
        List<Question> questions = questionService.getAllQuestions();
        Assertions.assertFalse(questions.isEmpty());

    }
    @Test
    @Order(2)
    public void testRandomQuestions(){
        sampleDataService.insertSampleQuestions();
        sampleDataService.generateSampleData();
        List<Question> questions = questionService.getRandomQuestions(5);
        Assertions.assertEquals(5,questions.size());
    }

    @Test
    @Order(3)
    public void testBorderQuestionsGenerator(){
        BorderQuestionGenerator borderQuestionGenerator=new BorderQuestionGenerator(categoryService,Question.SPANISH);
        List<Question> questions = borderQuestionGenerator.getQuestions();
        Assertions.assertFalse(questions.isEmpty());

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
        Assertions.assertFalse(questions.isEmpty());

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
        Assertions.assertFalse(questions.isEmpty());

        for (Question question : questions) {
            Assertions.assertNotNull(question.getCorrectAnswer());
            Assertions.assertEquals(4, question.getOptions().size());
            Assertions.assertTrue(question.getOptions().contains(question.getCorrectAnswer()));
        }
    }

    @Test
    @Order(6)
    public void testAddRole() {
        Player player = createPlayer();
        Role role = new Role();
        Associations.PlayerRole.addRole(player, role);
        Assertions.assertTrue(player.getRoles().contains(role));
        Assertions.assertTrue(role.getPlayers().contains(player));
    }

    @Test
    @Order(7)
    public void testRemoveRole() {
        Player player = createPlayer();
        Role role = new Role();
        Associations.PlayerRole.addRole(player, role);
        Associations.PlayerRole.removeRole(player, role);
        Assertions.assertFalse(player.getRoles().contains(role));
        Assertions.assertFalse(role.getPlayers().contains(player));
    }

    @Test
    @Order(8)
    public void testAddApiKey() {
        Player player = createPlayer();
        ApiKey apiKey = new ApiKey();
        Associations.PlayerApiKey.addApiKey(player, apiKey);
        Assertions.assertEquals(player.getApiKey(), apiKey);
        Assertions.assertEquals(apiKey.getPlayer(), player);
    }

    @Test
    @Order(9)
    void testRemoveApiKey() {
        Player player = createPlayer();
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
        Player player = createPlayer();
        GameSession gameSession = new GameSession();
        Associations.PlayerGameSession.addGameSession(player, gameSession);
        Assertions.assertTrue(player.getGameSessions().contains(gameSession));
        Assertions.assertEquals(gameSession.getPlayer(), player);
    }

    @Test
    @Order(12)
    public void testRemoveGameSession() {
        Player player = createPlayer();
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
        Player player = createPlayer();
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
        Player player = createPlayer();
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
        Player player = createPlayer();
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
        Player player = createPlayer();
        List<Question> questions = new ArrayList<>();
        GameSession gameSession = new GameSession(player, questions);
        gameSession.setCreatedAt(createdAt);
        gameSession.setFinishTime(finishTime);

        Assertions.assertEquals("00:05:00", gameSession.getDuration());
    }
    @Test
    @Order(22)
    public void testPlayerToJson() {
        Role role1 = new Role("ROLE_USER");
        Role role2 = new Role("ROLE_ADMIN");

        Set<Role> roles = new HashSet<>();
        roles.add(role1);
        roles.add(role2);

        Player player = createPlayer();
        player.setId(1L);
        player.setRoles(roles);

        GameSession gameSession = new GameSession(player, new ArrayList<>());
        gameSession.setId(0L);
        Set<GameSession> gameSessions = new HashSet<>();
        gameSessions.add(gameSession);


        player.setGameSessions(gameSessions);

        JsonNode json = player.toJson();

        Assertions.assertEquals(1L, json.get("id").asLong());
        Assertions.assertEquals("name", json.get("username").asText());
        Assertions.assertEquals("test@email.com", json.get("email").asText());

        ArrayNode rolesArray = (ArrayNode) json.get("roles");
        Assertions.assertEquals(2, rolesArray.size());

        ArrayNode gameSessionsArray = (ArrayNode) json.get("gameSessions");
        Assertions.assertEquals(1, gameSessionsArray.size());
        // Se verifica que la sesión de juego está presente en el JSON
        Assertions.assertEquals(gameSession.getId(), gameSessionsArray.get(0).get("id").asLong());
    }

    @Test
    @Order(23)
    public void testAddOption() {
        Question question = new Question();
        Answer option = new Answer("Option A", false);
        question.addOption(option);
        Assertions.assertTrue(question.getOptions().contains(option));
    }

    @Test
    @Order(24)
    public void testRemoveOption() {
        Question question = new Question();
        Answer option = new Answer("Option A", false);
        question.addOption(option);
        question.removeOption(option);
        Assertions.assertFalse(question.getOptions().contains(option));
    }

    @Test
    @Order(25)
    public void testScrambleOptions() {
        Question question = new Question();
        Answer option1 = new Answer("Option A", false);
        Answer option2 = new Answer("Option B", false);
        Answer option3 = new Answer("Option C", false);
        question.addOption(option1);
        question.addOption(option2);
        question.addOption(option3);

        question.scrambleOptions();
        List<Answer> scrambledOptions = question.getOptions();

        Assertions.assertTrue(scrambledOptions.contains(option1));
        Assertions.assertTrue(scrambledOptions.contains(option2));
        Assertions.assertTrue(scrambledOptions.contains(option3));
    }

    @Test
    @Order(26)
    public void testHasEmptyOptions() {
        Question question = new Question();
        Answer option1 = new Answer("Option A", false);
        Answer option2 = new Answer("", false); // Empty option
        question.addOption(option1);
        question.addOption(option2);

        Assertions.assertTrue(question.hasEmptyOptions());
    }

    @Test
    @Order(27)
    public void testToJson() {
        Category category = new Category("Category", "Description");

        List<Answer> options = new ArrayList<>();
        Answer option1 = new Answer("Option A", false);
        Answer option2 = new Answer("Option B", false);
        options.add(option1);
        options.add(option2);

        Answer correctAnswer = option1;

        Question question = new Question("Sample question", options, correctAnswer, category, "en");
        question.setId(1L);

        JsonNode json = question.toJson();

        Assertions.assertTrue(json.toString().contains("Sample question"));
        Assertions.assertTrue(json.toString().contains("Category"));
        Assertions.assertTrue(json.toString().contains("Option A"));
        Assertions.assertTrue(json.toString().contains("Option B"));
    }


}
