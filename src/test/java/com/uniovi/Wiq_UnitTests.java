package com.uniovi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.uniovi.components.generators.geography.BorderQuestionGenerator;
import com.uniovi.components.generators.geography.CapitalQuestionGenerator;
import com.uniovi.components.generators.geography.ContinentQuestionGeneration;
import com.uniovi.entities.*;
import com.uniovi.services.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

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

    private final HttpClient httpClient = HttpClient.newHttpClient();

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

    @Test
    @Order(28)
    public void testGetPlayerNoApiKey() throws IOException, InterruptedException, JSONException {
        HttpResponse<String> response = sendRequest("GET", "/api/players", Map.of(), Map.of());

        Assertions.assertEquals(401, response.statusCode());
        JSONObject json = parseJsonResponse(response);
        Assertions.assertEquals("Invalid API key", json.getString("error"));
    }

    @Test
    public void testGetPlayerInvalidApiKey() throws IOException, InterruptedException, JSONException {
        HttpResponse<String> response = sendRequest("GET", "/api/players", Map.of("API-KEY", "zzzz"), Map.of());

        Assertions.assertEquals(401, response.statusCode());
        JSONObject json = parseJsonResponse(response);
        Assertions.assertEquals("Invalid API key", json.getString("error"));
    }

    @Test
    public void testGetAllPlayers() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        HttpResponse<String> response = sendRequest("GET", "/api/players", Map.of(), Map.of("apiKey", apiKey.getKeyToken()));

        Assertions.assertEquals(200, response.statusCode());
        JSONObject json = parseJsonResponse(response);
        Assertions.assertTrue(json.has("players"));
        Assertions.assertTrue(json.getJSONArray("players").length() > 0);
    }

    @Test
    public void testGetPlayerById() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        HttpResponse<String> response = sendRequest("GET", "/api/players", Map.of(),
                Map.of("apiKey", apiKey.getKeyToken(),
                        "id", String.valueOf(player.getId())));

        Assertions.assertEquals(200, response.statusCode());
        JSONObject json = parseJsonResponse(response);
        JSONObject playerJson = json.getJSONArray("players").getJSONObject(0);
        Assertions.assertEquals(player.getId(), playerJson.getLong("id"));
        Assertions.assertEquals(player.getUsername(), playerJson.getString("username"));
        Assertions.assertEquals(player.getEmail(), playerJson.getString("email"));
    }

    @Test
    public void testGetPlayerByEmail() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        HttpResponse<String> response = sendRequest("GET", "/api/players", Map.of(),
                Map.of("apiKey", apiKey.getKeyToken(),
                        "email", player.getEmail()));

        Assertions.assertEquals(200, response.statusCode());
        JSONObject json = parseJsonResponse(response);
        JSONObject playerJson = json.getJSONArray("players").getJSONObject(0);
        Assertions.assertEquals(player.getId(), playerJson.getLong("id"));
        Assertions.assertEquals(player.getUsername(), playerJson.getString("username"));
        Assertions.assertEquals(player.getEmail(), playerJson.getString("email"));
    }

    @Test
    public void testGetPlayerByUsername() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        HttpResponse<String> response = sendRequest("GET", "/api/players", Map.of(),
                Map.of("apiKey", apiKey.getKeyToken(),
                        "username", player.getUsername()));

        Assertions.assertEquals(200, response.statusCode());
        JSONObject json = parseJsonResponse(response);
        JSONObject playerJson = json.getJSONArray("players").getJSONObject(0);
        Assertions.assertEquals(player.getId(), playerJson.getLong("id"));
        Assertions.assertEquals(player.getUsername(), playerJson.getString("username"));
        Assertions.assertEquals(player.getEmail(), playerJson.getString("email"));
    }

    @Test
    public void testGetPlayerByRole() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        HttpResponse<String> response = sendRequest("GET", "/api/players", Map.of(),
                Map.of("apiKey", apiKey.getKeyToken(),
                        "role", "ROLE_USER"));

        Assertions.assertEquals(200, response.statusCode());
        JSONObject json = parseJsonResponse(response);
        System.out.println(json.toString());
        JSONArray players = json.getJSONArray("players");
        Assertions.assertTrue(players.length() > 0);
        for (int i = 0; i < players.length(); i++) {
            JSONObject playerJson = players.getJSONObject(i);
            Assertions.assertEquals("ROLE_USER", playerJson.getJSONArray("roles").getString(i));
        }
    }

    @Test
    public void testGetPlayersByUsernames() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        HttpResponse<String> response = sendRequest("GET", "/api/players", Map.of(),
                Map.of("apiKey", apiKey.getKeyToken(),
                        "usernames", player.getUsername()));

        Assertions.assertEquals(200, response.statusCode());
        JSONObject json = parseJsonResponse(response);
        JSONArray players = json.getJSONArray("players");
        Assertions.assertTrue(players.length() > 0);
        for (int i = 0; i < players.length(); i++) {
            JSONObject playerJson = players.getJSONObject(i);
            Assertions.assertEquals(player.getUsername(), playerJson.getString("username"));
        }
    }

    @Test
    public void testGetPlayersByEmails() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        HttpResponse<String> response = sendRequest("GET", "/api/players", Map.of(),
                Map.of("apiKey", apiKey.getKeyToken(),
                        "emails", player.getEmail()));

        Assertions.assertEquals(200, response.statusCode());
        JSONObject json = parseJsonResponse(response);
        JSONArray players = json.getJSONArray("players");
        Assertions.assertTrue(players.length() > 0);
        for (int i = 0; i < players.length(); i++) {
            JSONObject playerJson = players.getJSONObject(i);
            Assertions.assertEquals(player.getEmail(), playerJson.getString("email"));
        }
    }

    @Test
    public void testCreatePlayerEmptyApiKey() throws IOException, InterruptedException, JSONException {
        HttpResponse<String> response = sendRequest("POST", "/api/players", Map.of(),
                Map.of());

        Assertions.assertNotEquals(200, response.statusCode());
    }

    @Test
    public void testCreatePlayerInvalidApiKey() throws IOException, InterruptedException, JSONException {
        HttpResponse<String> response = sendRequest("POST", "/api/players", Map.of("API-KEY", "zzzz"),
                Map.of());

        Assertions.assertNotEquals(200, response.statusCode());
    }

    @Test
    public void testCreatePlayerValid() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        Map<String, Object> data = new HashMap<>();

        data.put("username", "newUser");
        data.put("email", "newUser@email.com");
        data.put("password", "password");
        data.put("roles", new String[] {"ROLE_USER"});

        HttpResponse<String> response = sendRequest("POST", "/api/players", Map.of("API-KEY", apiKey.getKeyToken()),
                data);

        Assertions.assertEquals(200, response.statusCode());
        JSONObject json = parseJsonResponse(response);
        Assertions.assertTrue(json.getBoolean("success"));
        Long newId = json.getLong("id");

        Optional<Player> newPlayer = playerService.getUser(newId);
        Assertions.assertTrue(newPlayer.isPresent());
        Assertions.assertEquals("newUser", newPlayer.get().getUsername());
        Assertions.assertEquals("newUser@email.com", newPlayer.get().getEmail());

        playerService.deletePlayer(newId);
    }

    @Test
    public void testCreateUserInvalidUsernameAndEmail() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        Map<String, Object> data = new HashMap<>();

        data.put("username", player.getUsername());
        data.put("email", player.getEmail());
        data.put("password", "password");
        data.put("roles", new String[]{"ROLE_USER"});

        HttpResponse<String> response = sendRequest("POST", "/api/players", Map.of("API-KEY", apiKey.getKeyToken()),
                data);

        Assertions.assertEquals(400, response.statusCode());
        JSONObject json = parseJsonResponse(response);
        Assertions.assertTrue(json.has("email"));
        Assertions.assertTrue(json.has("username"));
    }

    @Test
    public void testCreateUserInvalidEmail() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        Map<String, Object> data = new HashMap<>();

        data.put("username", "user1");
        data.put("email", "notavalidemail");
        data.put("password", "password");
        data.put("roles", new String[]{"ROLE_USER"});

        HttpResponse<String> response = sendRequest("POST", "/api/players", Map.of("API-KEY", apiKey.getKeyToken()),
                data);

        Assertions.assertEquals(400, response.statusCode());
        JSONObject json = parseJsonResponse(response);
        Assertions.assertTrue(json.has("email"));
    }

    @Test
    public void testModifyUser() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        Map<String, Object> data = new HashMap<>();
        data.put("username", "newUsername");
        data.put("email", "newEmail@email.com");
        data.put("password", "newPassword");
        data.put("roles", new String[]{"ROLE_USER"});

        HttpResponse<String> response = sendRequest("PATCH", "/api/players/" + player.getId(), Map.of("API-KEY", apiKey.getKeyToken()),
                data);

        Assertions.assertEquals(200, response.statusCode());
        JSONObject json = parseJsonResponse(response);
        Assertions.assertTrue(json.getBoolean("success"));

        Optional<Player> updatedPlayer = playerService.getUser(player.getId());
        Assertions.assertTrue(updatedPlayer.isPresent());
        Assertions.assertEquals("newUsername", updatedPlayer.get().getUsername());
        Assertions.assertEquals("newEmail@email.com", updatedPlayer.get().getEmail());
    }

    @Test
    public void testModifyInvalidApiKey() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);

        HttpResponse<String> response = sendRequest("PATCH", "/api/players/" + player.getId(), Map.of("API-KEY", "zzzz"),
                Map.of());

        Assertions.assertNotEquals(200, response.statusCode());
    }

    @Test
    public void testModifyUserAlreadyExisting() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        Map<String, Object> data = new HashMap<>();
        data.put("username", "test");
        data.put("email", "test@test.com");
        data.put("password", "newPassword");
        data.put("roles", new String[]{"ROLE_USER"});

        HttpResponse<String> response = sendRequest("PATCH", "/api/players/" + player.getId(), Map.of("API-KEY", apiKey.getKeyToken()),
                data);

        Assertions.assertNotEquals(200, response.statusCode());
        JSONObject json = parseJsonResponse(response);

        Assertions.assertTrue(json.has("email"));
        Assertions.assertTrue(json.has("username"));
    }

    @Test
    public void testModifyUserMissing() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        Map<String, Object> data = new HashMap<>();

        HttpResponse<String> response = sendRequest("PATCH", "/api/players/" + player.getId(), Map.of("API-KEY", apiKey.getKeyToken()),
                data);

        Assertions.assertNotEquals(200, response.statusCode());
    }

    @Test
    public void testModifyUserMissingSomeData() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        Map<String, Object> data = new HashMap<>();
        data.put("username", "test");
        //data.put("email", "test@test.com"); // Missing email
        data.put("password", "newPassword");
        data.put("roles", new String[]{"ROLE_USER"});

        HttpResponse<String> response = sendRequest("PATCH", "/api/players/" + player.getId(), Map.of("API-KEY", apiKey.getKeyToken()),
                data);

        Assertions.assertNotEquals(200, response.statusCode());
    }

    @Test
    public void testDeleteUserInvalidApiKey() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);

        HttpResponse<String> response = sendRequest("DELETE", "/api/players/" + player.getId(), Map.of("API-KEY", "zzzz"),
                Map.of());

        Assertions.assertNotEquals(200, response.statusCode());
    }

    @Test
    public void testDeleteUserNotFound() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        HttpResponse<String> response = sendRequest("DELETE", "/api/players/9999999", Map.of("API-KEY", apiKey.getKeyToken()),
                Map.of());

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteUser() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        HttpResponse<String> response = sendRequest("DELETE", "/api/players/" + player.getId(), Map.of("API-KEY", apiKey.getKeyToken()),
                Map.of());

        Assertions.assertEquals(200, response.statusCode());

        Optional<Player> deletedPlayer = playerService.getUser(player.getId());
        Assertions.assertTrue(deletedPlayer.isEmpty());
    }

    @Test
    public void testGetQuestionsInvalidApiKey() throws IOException, InterruptedException, JSONException {
        HttpResponse<String> response = sendRequest("GET", "/api/questions", Map.of("API-KEY", "zzzz"), Map.of());

        Assertions.assertEquals(401, response.statusCode());
        JSONObject json = parseJsonResponse(response);
        Assertions.assertEquals("Invalid API key", json.getString("error"));
    }

    @Test
    public void testGetQuestions() throws IOException, InterruptedException, JSONException {
        insertSomeQuestions();
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        HttpResponse<String> response = sendRequest("GET", "/api/questions", Map.of(),
                Map.of("apiKey", apiKey.getKeyToken()));

        Assertions.assertEquals(200, response.statusCode());
        JSONObject json = parseJsonResponse(response);
        Assertions.assertTrue(json.has("questions"));
        Assertions.assertTrue(json.getJSONArray("questions").length() > 0);
    }

    @Test
    public void testGetQuestionsByCategoryName() throws IOException, InterruptedException, JSONException {
        insertSomeQuestions();
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        HttpResponse<String> response = sendRequest("GET", "/api/questions", Map.of(),
                Map.of("apiKey", apiKey.getKeyToken(), "category", "Geography"));

        Assertions.assertEquals(200, response.statusCode());
        JSONObject json = parseJsonResponse(response);
        Assertions.assertTrue(json.has("questions"));
        Assertions.assertTrue(json.getJSONArray("questions").length() > 0);
    }

    @Test
    public void testGetQuestionsByCategoryId() throws IOException, InterruptedException, JSONException {
        insertSomeQuestions();
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();
        Category cat = categoryService.getCategoryByName("Geography");

        HttpResponse<String> response = sendRequest("GET", "/api/questions", Map.of(),
                Map.of("apiKey", apiKey.getKeyToken(), "category", cat.getId()));

        Assertions.assertEquals(200, response.statusCode());
        JSONObject json = parseJsonResponse(response);
        Assertions.assertTrue(json.has("questions"));
        Assertions.assertTrue(json.getJSONArray("questions").length() > 0);
    }

    @Test
    public void testGetQuestionById() throws IOException, InterruptedException, JSONException {
        insertSomeQuestions();
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();
        Question question = questionService.getAllQuestions().get(0);

        HttpResponse<String> response = sendRequest("GET", "/api/questions", Map.of(),
                Map.of("apiKey", apiKey.getKeyToken(),
                        "id", question.getId()));

        Assertions.assertEquals(200, response.statusCode());
        JSONObject json = parseJsonResponse(response);
        JSONObject questionJson = json.getJSONArray("questions").getJSONObject(0);
        Assertions.assertEquals(question.getId(), questionJson.getLong("id"));
        Assertions.assertEquals(question.getStatement(), questionJson.getString("statement"));
    }

    @Test
    public void testGetQuestionByStatement() throws IOException, InterruptedException, JSONException {
        insertSomeQuestions();
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();
        Question question = questionService.getAllQuestions().get(0);

        HttpResponse<String> response = sendRequest("GET", "/api/questions", Map.of(),
                Map.of("apiKey", apiKey.getKeyToken(),
                        "statement", question.getStatement()));

        Assertions.assertEquals(200, response.statusCode());
        JSONObject json = parseJsonResponse(response);
        System.out.println(json.toString());
        JSONObject questionJson = json.getJSONArray("questions").getJSONObject(0);
        Assertions.assertEquals(question.getId(), questionJson.getLong("id"));
        Assertions.assertEquals(question.getStatement(), questionJson.getString("statement"));
    }

    @Test
    public void testAddQuestionInvalidApiKey() throws IOException, InterruptedException, JSONException {
        HttpResponse<String> response = sendRequest("POST", "/api/questions", Map.of("API-KEY", "zzzz"),
                Map.of());

        Assertions.assertNotEquals(200, response.statusCode());
    }

    @Test
    public void testAddQuestionMissingData() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        HttpResponse<String> response = sendRequest("POST", "/api/questions", Map.of("API-KEY", apiKey.getKeyToken()),
                Map.of());

        Assertions.assertNotEquals(200, response.statusCode());
    }

    @Test
    public void testAddQuestion() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();
        Category category = categoryService.getCategoryByName("Geography");

        Map<String, Object> data = new HashMap<>();
        data.put("statement", "Sample question");

        List<Map<String, Object>> opts = new ArrayList<>();
        opts.add(Map.of("text", "Option A", "correct", true));
        opts.add(Map.of("text", "Option B", "correct", false));
        opts.add(Map.of("text", "Option C", "correct", false));
        opts.add(Map.of("text", "Option D", "correct", false));

        data.put("options", opts);
        data.put("category", Map.of("name", category.getName()));
        data.put("language", "en");

        HttpResponse<String> response = sendRequest("POST", "/api/questions", Map.of("API-KEY", apiKey.getKeyToken()),
                data);

        Assertions.assertEquals(200, response.statusCode());
        JSONObject json = parseJsonResponse(response);
        Assertions.assertTrue(json.getBoolean("success"));
        Long newId = json.getLong("id");

        Optional<Question> newQuestion = questionService.getQuestion(newId);
        Assertions.assertTrue(newQuestion.isPresent());
        Assertions.assertEquals("Sample question", newQuestion.get().getStatement());
        Assertions.assertEquals(4, newQuestion.get().getOptions().size());
        Assertions.assertTrue(newQuestion.get().getOptions().stream().anyMatch(Answer::isCorrect));
    }

    @Test
    public void testAddQuestionWithLessOptions() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();
        Category category = categoryService.getCategoryByName("Geography");

        Map<String, Object> data = new HashMap<>();
        data.put("statement", "Sample question");

        List<Map<String, Object>> opts = new ArrayList<>();
        opts.add(Map.of("text", "Option A", "correct", true));
        opts.add(Map.of("text", "Option B", "correct", false));

        data.put("options", opts);
        data.put("category", Map.of("name", category.getName()));
        data.put("language", "en");

        HttpResponse<String> response = sendRequest("POST", "/api/questions", Map.of("API-KEY", apiKey.getKeyToken()),
                data);

        Assertions.assertNotEquals(200, response.statusCode());
    }

    @Test
    public void testAddQuestionWithNoCorrect() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();
        Category category = categoryService.getCategoryByName("Geography");

        Map<String, Object> data = new HashMap<>();
        data.put("statement", "Sample question");

        List<Map<String, Object>> opts = new ArrayList<>();
        opts.add(Map.of("text", "Option A", "correct", false));
        opts.add(Map.of("text", "Option B", "correct", false));
        opts.add(Map.of("text", "Option C", "correct", false));
        opts.add(Map.of("text", "Option D", "correct", false));

        data.put("options", opts);
        data.put("category", Map.of("name", category.getName()));
        data.put("language", "en");

        HttpResponse<String> response = sendRequest("POST", "/api/questions", Map.of("API-KEY", apiKey.getKeyToken()),
                data);

        Assertions.assertNotEquals(200, response.statusCode());
    }

    @Test
    public void testAddQuestionMultipleCorrect() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();
        Category category = categoryService.getCategoryByName("Geography");

        Map<String, Object> data = new HashMap<>();
        data.put("statement", "Sample question");

        List<Map<String, Object>> opts = new ArrayList<>();
        opts.add(Map.of("text", "Option A", "correct", true));
        opts.add(Map.of("text", "Option B", "correct", true));
        opts.add(Map.of("text", "Option C", "correct", false));
        opts.add(Map.of("text", "Option D", "correct", false));

        data.put("options", opts);
        data.put("category", Map.of("name", category.getName()));
        data.put("language", "en");

        HttpResponse<String> response = sendRequest("POST", "/api/questions", Map.of("API-KEY", apiKey.getKeyToken()),
                data);

        Assertions.assertNotEquals(200, response.statusCode());
    }

    @Test
    public void testModifyQuestionInvalidApiKey() throws IOException, InterruptedException, JSONException {
        insertSomeQuestions();
        Question question = questionService.getAllQuestions().get(0);

        HttpResponse<String> response = sendRequest("PATCH", "/api/questions/" + question.getId(), Map.of("API-KEY", "zzzz"),
                Map.of());

        Assertions.assertNotEquals(200, response.statusCode());
    }

    @Test
    public void testModifyQuestionNotFound() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        HttpResponse<String> response = sendRequest("PATCH", "/api/questions/9999999", Map.of("API-KEY", apiKey.getKeyToken()),
                Map.of());

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void testModifyQuestionMissingData() throws IOException, InterruptedException, JSONException {
        insertSomeQuestions();
        Question question = questionService.getAllQuestions().get(0);
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        HttpResponse<String> response = sendRequest("PATCH", "/api/questions/" + question.getId(), Map.of("API-KEY", apiKey.getKeyToken()),
                Map.of());

        Assertions.assertNotEquals(200, response.statusCode());
    }

    @Test
    public void testModifyQuestion() throws IOException, InterruptedException, JSONException {
        insertSomeQuestions();
        Question question = questionService.getAllQuestions().get(0);
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();
        Category category = categoryService.getCategoryByName("Geography");

        Map<String, Object> data = new HashMap<>();
        data.put("statement", "Modified question");

        List<Map<String, Object>> opts = new ArrayList<>();
        opts.add(Map.of("text", "Option A", "correct", true));
        opts.add(Map.of("text", "Option B", "correct", false));
        opts.add(Map.of("text", "Option C", "correct", false));
        opts.add(Map.of("text", "Option D", "correct", false));

        data.put("options", opts);
        data.put("category", Map.of("name", category.getName()));
        data.put("language", "en");

        HttpResponse<String> response = sendRequest("PATCH", "/api/questions/" + question.getId(), Map.of("API-KEY", apiKey.getKeyToken()),
                data);

        Assertions.assertEquals(200, response.statusCode());
        JSONObject json = parseJsonResponse(response);
        Assertions.assertTrue(json.getBoolean("success"));

        Optional<Question> updatedQuestion = questionService.getQuestion(question.getId());
        Assertions.assertTrue(updatedQuestion.isPresent());
        Assertions.assertEquals("Modified question", updatedQuestion.get().getStatement());
    }

    @Test
    public void testModifyQuestionWithLessOptions() throws IOException, InterruptedException, JSONException {
        insertSomeQuestions();
        Question question = questionService.getAllQuestions().get(0);
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();
        Category category = categoryService.getCategoryByName("Geography");

        Map<String, Object> data = new HashMap<>();
        data.put("statement", "Modified question");

        List<Map<String, Object>> opts = new ArrayList<>();
        opts.add(Map.of("text", "Option A", "correct", true));
        opts.add(Map.of("text", "Option B", "correct", false));

        data.put("options", opts);
        data.put("category", Map.of("name", category.getName()));
        data.put("language", "en");

        HttpResponse<String> response = sendRequest("PATCH", "/api/questions/" + question.getId(), Map.of("API-KEY", apiKey.getKeyToken()),
                data);

        Assertions.assertNotEquals(200, response.statusCode());
    }

    @Test
    public void testModifyQuestionWithNoCorrect() throws IOException, InterruptedException, JSONException {
        insertSomeQuestions();
        Question question = questionService.getAllQuestions().get(0);
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();
        Category category = categoryService.getCategoryByName("Geography");

        Map<String, Object> data = new HashMap<>();
        data.put("statement", "Modified question");

        List<Map<String, Object>> opts = new ArrayList<>();
        opts.add(Map.of("text", "Option A", "correct", false));
        opts.add(Map.of("text", "Option B", "correct", false));
        opts.add(Map.of("text", "Option C", "correct", false));
        opts.add(Map.of("text", "Option D", "correct", false));

        data.put("options", opts);
        data.put("category", Map.of("name", category.getName()));
        data.put("language", "en");

        HttpResponse<String> response = sendRequest("PATCH", "/api/questions/" + question.getId(), Map.of("API-KEY", apiKey.getKeyToken()),
                data);

        Assertions.assertNotEquals(200, response.statusCode());
    }

    @Test
    public void testDeleteQuestionInvalidApiKey() throws IOException, InterruptedException, JSONException {
        insertSomeQuestions();
        Question question = questionService.getAllQuestions().get(0);

        HttpResponse<String> response = sendRequest("DELETE", "/api/questions/" + question.getId(), Map.of("API-KEY", "zzzz"),
                Map.of());

        Assertions.assertNotEquals(200, response.statusCode());
    }

    @Test
    public void testDeleteQuestionNotFound() throws IOException, InterruptedException, JSONException {
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        HttpResponse<String> response = sendRequest("DELETE", "/api/questions/9999999", Map.of("API-KEY", apiKey.getKeyToken()),
                Map.of());

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteQuestion() throws IOException, InterruptedException, JSONException {
        insertSomeQuestions();
        Question question = questionService.getAllQuestions().get(0);
        Player player = playerService.getUsersByRole("ROLE_USER").get(0);
        ApiKey apiKey = player.getApiKey();

        HttpResponse<String> response = sendRequest("DELETE", "/api/questions/" + question.getId(), Map.of("API-KEY", apiKey.getKeyToken()),
                Map.of());

        Assertions.assertEquals(200, response.statusCode());
        Optional<Question> deletedQuestion = questionService.getQuestion(question.getId());
        Assertions.assertTrue(deletedQuestion.isEmpty());
    }

    /**
     * Sends an HTTP request to the API
     * @param method HTTP method
     * @param uri URI to send the request to
     * @param headers Headers to include in the request
     * @param data Data to send in the request
     * @return The response from the server
     * @throws IOException
     * @throws InterruptedException
     */
    private HttpResponse<String> sendRequest(String method, String uri,
                                                   Map<String, String> headers,
                                                   Map<String, Object> data) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

        uri = Wiq_IntegrationTests.URL.substring(0, Wiq_IntegrationTests.URL.length() - 1) + uri;

        if ("GET".equalsIgnoreCase(method)) {
            if (!data.isEmpty()) {
                uri += "?" + buildQueryString(data);
            }
            requestBuilder.uri(URI.create(uri)).GET();
        } else if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method)) {
            JSONObject json = new JSONObject(data);
            requestBuilder.uri(URI.create(uri))
                    .method(method.toUpperCase(), HttpRequest.BodyPublishers.ofString(json.toString()))
                    .header("Content-Type", "application/json");
        } else if ("DELETE".equalsIgnoreCase(method)) {
            requestBuilder.uri(URI.create(uri)).DELETE();
        } else {
            throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        headers.forEach(requestBuilder::header);

        HttpRequest request = requestBuilder.build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Builds a query string from a map of data
     * @param data The data to include in the query string
     * @return The query string
     */
    private String buildQueryString(Map<String, Object> data) {
        StringJoiner sj = new StringJoiner("&");
        data.forEach((key, value) -> sj.add(URLEncoder.encode(key, StandardCharsets.UTF_8) + "="
                + URLEncoder.encode(value.toString(), StandardCharsets.UTF_8)));
        return sj.toString();
    }

    /**
     * Parses the JSON response from the server
     * @param response The response from the server
     * @return The JSON object
     * @throws JSONException
     */
    private JSONObject parseJsonResponse(HttpResponse<String> response) throws JSONException {
        return new JSONObject(response.body());
    }

    /**
     * Inserts some sample questions into the database
     */
    private void insertSomeQuestions() {
        List<Question> qs = new ContinentQuestionGeneration(categoryService, Question.SPANISH).getQuestions();
        qs.forEach(questionService::addNewQuestion);
    }
}
