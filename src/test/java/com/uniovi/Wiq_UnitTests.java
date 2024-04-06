package com.uniovi;

import com.uniovi.dto.*;
import com.uniovi.entities.*;
import com.uniovi.repositories.*;
import com.uniovi.services.*;
import com.uniovi.services.impl.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Tag("unit")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class Wiq_UnitTests {

    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Test
    void contextLoads() {
    }

    @BeforeEach
    void tearDown() {
        playerRepository.deleteAll();
        roleRepository.deleteAll();
        answerRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @Order(1)
    void PlayerServiceImpl_addNewPlayer_UsedEmail() {
        RoleService roleService = new RoleServiceImpl(roleRepository);
        PlayerServiceImpl playerService = new PlayerServiceImpl(playerRepository, roleService, passwordEncoder);

        Player p1 = new Player("a", "abcd@gmai.com", "1234");
        playerRepository.save(p1);

        PlayerDto dto = new PlayerDto("b", "abcd@gmail.com", "1221", "1221", null);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> playerService.addNewPlayer(dto));
        Assertions.assertEquals("Email already in use", exception.getMessage());
    }

    @Test
    @Order(2)
    void PlayerServiceImpl_addNewPlayer_UsedUsername() {
        RoleService roleService = new RoleServiceImpl(roleRepository);
        PlayerServiceImpl playerService = new PlayerServiceImpl(playerRepository, roleService, passwordEncoder);

        Player p1 = new Player("a", "abcd@gmai.com", "1234");
        playerRepository.save(p1);

        PlayerDto dto = new PlayerDto("a", "a@gmail.com", "1221", "1221", null);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> playerService.addNewPlayer(dto));
        Assertions.assertEquals("Username already in use", exception.getMessage());
    }

    @Test
    @Order(3)
    void PlayerServiceImpl_addNewPlayer_AddedCorrectly() {
        RoleService roleService = new RoleServiceImpl(roleRepository);
        PlayerServiceImpl playerService = new PlayerServiceImpl(playerRepository, roleService, passwordEncoder);

        PlayerDto dto = new PlayerDto("a", "a@gmail.com", "1221", "1221", null);

        Player player = playerService.addNewPlayer(dto);

        Assertions.assertNotNull(player);
        Assertions.assertEquals(dto.getUsername(), player.getUsername());
        Assertions.assertEquals(dto.getEmail(), player.getEmail());
        Assertions.assertTrue(passwordEncoder.matches(dto.getPassword(), player.getPassword()));
    }

    @Test
    @Order(4)
    void PlayerServiceImpl_addNewPlayer_RoleExists() {
        RoleService roleService = new RoleServiceImpl(roleRepository);
        PlayerServiceImpl playerService = new PlayerServiceImpl(playerRepository, roleService, passwordEncoder);

        PlayerDto dto = new PlayerDto("a", "a@gmail.com", "1221", "1221", new String[]{"ROLE_USER"});
        roleService.addRole(new RoleDto(dto.getRoles()[0]));

        Player player = playerService.addNewPlayer(dto);

        Assertions.assertNotNull(player);
        Assertions.assertEquals(dto.getUsername(), player.getUsername());
        Assertions.assertEquals(dto.getEmail(), player.getEmail());
        Assertions.assertTrue(passwordEncoder.matches(dto.getPassword(), player.getPassword()));
    }

    @Test
    @Order(5)
    void PlayerServiceImpl_getUsers_ReturnsPlayersList() {
        RoleService roleService = new RoleServiceImpl(roleRepository);
        PlayerServiceImpl playerService = new PlayerServiceImpl(playerRepository, roleService, passwordEncoder);

        List<Player> players = new ArrayList<>();
        players.add(new Player("a", "a@gmail.com", "1a"));
        players.add(new Player("b", "b@gmail.com", "1b"));

        playerRepository.save(new Player("a", "a@gmail.com", "1a"));
        playerRepository.save(new Player("b", "b@gmail.com", "1b"));

        List<Player> result = playerService.getUsers();

        Assertions.assertEquals(players.size(), result.size());
        for (int i = 0; i < players.size(); i++) {
            Assertions.assertEquals(players.get(i), result.get(i));
        }
    }

    @Test
    @Order(6)
    void PlayerServiceImpl_getUsers_ReturnsEmptyList() {
        RoleService roleService = new RoleServiceImpl(roleRepository);
        PlayerServiceImpl playerService = new PlayerServiceImpl(playerRepository, roleService, passwordEncoder);

        List<Player> result = playerService.getUsers();

        Assertions.assertEquals(0, result.size());
    }

    @Test
    @Order(7)
    void PlayerServiceImpl_getUserByEmail_ReturnsPlayer() {
        RoleService roleService = new RoleServiceImpl(roleRepository);
        PlayerServiceImpl playerService = new PlayerServiceImpl(playerRepository, roleService, passwordEncoder);

        String email = "a@gmail.com";
        Player player = new Player("a", email, "password");

        playerRepository.save(player);

        Optional<Player> result = playerService.getUserByEmail(email);

        Assertions.assertEquals(player, result.orElse(null));
    }

    @Test
    @Order(8)
    void PlayerServiceImpl_getUserByEmail_ReturnsEmpty() {
        RoleService roleService = new RoleServiceImpl(roleRepository);
        PlayerServiceImpl playerService = new PlayerServiceImpl(playerRepository, roleService, passwordEncoder);

        String email = "nonexist@gmail.com";

        Optional<Player> result = playerService.getUserByEmail(email);

        Assertions.assertEquals(Optional.empty(), result);
    }

    @Test
    @Order(9)
    void PlayerServiceImpl_getUserByUsername_ReturnsPlayer() {
        RoleService roleService = new RoleServiceImpl(roleRepository);
        PlayerServiceImpl playerService = new PlayerServiceImpl(playerRepository, roleService, passwordEncoder);

        String username = "abc";
        Player player = new Player(username, "a@gmail,com", "password");

        playerRepository.save(player);

        Optional<Player> result = playerService.getUserByUsername(username);

        Assertions.assertEquals(player, result.orElse(null));
    }

    @Test
    @Order(10)
    void PlayerServiceImpl_getUserByUsername_ReturnsEmpty() {
        RoleService roleService = new RoleServiceImpl(roleRepository);
        PlayerServiceImpl playerService = new PlayerServiceImpl(playerRepository, roleService, passwordEncoder);

        String username = "nonexist";

        Optional<Player> result = playerService.getUserByUsername(username);

        Assertions.assertEquals(Optional.empty(), result);
    }

    @Test
    @Order(11)
    void AnswerServiceImpl_addNewAnswer_SavesAnswer() {
        AnswerServiceImpl answerService = new AnswerServiceImpl(answerRepository);

        Answer respuesta = new Answer("respuesta", true);

        answerService.addNewAnswer(respuesta);

        Optional<Answer> respuestaGuardada = answerRepository.findById(respuesta.getId());
        Assertions.assertEquals(respuesta, respuestaGuardada.orElse(null));
    }

    @Test
    @Order(12)
    void AnswerServiceImpl_getAnswersPerQuestion_QuestionExists() {
        AnswerServiceImpl answerService = new AnswerServiceImpl(answerRepository);

        String statement = "What is the capital of France?";
        List<Answer> options = new ArrayList<>();
        options.add(new Answer("Paris", true));
        options.add(new Answer("Madrid", false));
        options.add(new Answer("Rome", false));
        Answer correctAnswer = options.get(0);
        Category category = new Category("Geography", "Capitales mundiales");
        String language = "en";
        Question question = new Question(statement, options, correctAnswer, category, language);

        List<Answer> expectedAnswers = question.getOptions();

        // Act
        List<Answer> result = answerService.getAnswersPerQuestion(question);

        // Assert
        Assertions.assertEquals(expectedAnswers.size(), result.size());
        Assertions.assertEquals(expectedAnswers, result);
    }

    @Test
    @Order(13)
    void AnswerServiceImpl_getAnswer_ReturnsEmpty() {
        AnswerServiceImpl answerService = new AnswerServiceImpl(answerRepository);

        Long id = 999L;

        Optional<Answer> result = answerService.getAnswer(id);

        Assertions.assertEquals(Optional.empty(), result);
    }

    @Test
    @Order(14)
    void AnswerServiceImpl_getAnswer_ReturnsAnswer() {
        AnswerServiceImpl answerService = new AnswerServiceImpl(answerRepository);

        Answer answer = new Answer("Content", true);
        Long id = answerRepository.save(answer).getId();

        Optional<Answer> result = answerService.getAnswer(id);

        Assertions.assertEquals(answer, result.orElse(null));
    }

    @Test
    @Order(15)
    void CategoryServiceImpl_addNewCategory_SavesCategory() {
        CategoryServiceImpl categoryService = new CategoryServiceImpl(categoryRepository);

        Category category = new Category("Capitals", "Capitals from countries");

        categoryService.addNewCategory(category);

        Optional<Category> savedCategory = categoryRepository.findById(category.getId());
        Assertions.assertEquals(category, savedCategory.orElse(null));
    }

    @Test
    @Order(16)
    void CategoryServiceImpl_getAllCategories_ReturnsList() {
        CategoryServiceImpl categoryService = new CategoryServiceImpl(categoryRepository);

        List<Category> categories = new ArrayList<>();
        Category category = new Category("Capitals", "Capitals from countries");
        categories.add(category);

        categoryService.addNewCategory(category);

        List<Category> result = categoryService.getAllCategories();

        Assertions.assertEquals(categories.size(), result.size());
        Assertions.assertEquals(categories, result);
    }

    @Test
    @Order(17)
    void CategoryServiceImpl_getAllCategories_EmptyList() {
        CategoryServiceImpl categoryService = new CategoryServiceImpl(categoryRepository);

        List<Category> result = categoryService.getAllCategories();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(18)
    void CategoryServiceImpl_getCategory_ReturnsCategory() {
        CategoryServiceImpl categoryService = new CategoryServiceImpl(categoryRepository);

        Category category = new Category("Capitals", "Capitals from countries");

        categoryService.addNewCategory(category);

        Optional<Category> result = categoryService.getCategory(category.getId());

        Assertions.assertEquals(category, result.orElse(null));
    }

    @Test
    @Order(19)
    void CategoryServiceImpl_getCategory_ReturnsEmptyOpt() {
        CategoryServiceImpl categoryService = new CategoryServiceImpl(categoryRepository);

        Long id = 999L;
        Optional<Category> result = categoryService.getCategory(id);

        Assertions.assertEquals(Optional.empty(), result);
    }

    @Test
    @Order(20)
    void CategoryServiceImpl_getCategoryByName_ReturnsCategory() {
        CategoryServiceImpl categoryService = new CategoryServiceImpl(categoryRepository);

        String name = "Capitals";
        Category category = new Category(name, "Capitals from countries");

        categoryService.addNewCategory(category);

        Category result = categoryService.getCategoryByName(name);

        Assertions.assertEquals(category, result);
    }

    @Test
    @Order(21)
    void CategoryServiceImpl_getCategoryByName_ReturnsNull() {
        CategoryServiceImpl categoryService = new CategoryServiceImpl(categoryRepository);

        Category result = categoryService.getCategoryByName("abcd");

        Assertions.assertNull(result);
    }

}
