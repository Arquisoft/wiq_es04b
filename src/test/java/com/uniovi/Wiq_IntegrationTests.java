package com.uniovi;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Tag("integration")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class Wiq_IntegrationTests {
    static final String URL = "http://localhost:3000/";

    static WebDriver driver;
    static WebDriverManager manager;

    @BeforeEach
    public void begin() {
        manager = WebDriverManager.firefoxdriver();
        driver = manager.create();
        driver.navigate().to(URL);
    }

    @BeforeEach
    void setup() {

    }

    @AfterEach
    void tearDown() {
        driver.manage().deleteAllCookies();
        driver.quit();
    }

    @Test
    @Order(1)
    void testHome() {
        // Check the title
        Assertions.assertEquals("Wikigame", driver.getTitle());
    }
}
