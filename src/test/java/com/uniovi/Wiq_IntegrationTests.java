package com.uniovi;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Tag("integration")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class Wiq_IntegrationTests {
    static final String URL = "http://localhost:3000/";

    static WebDriver driver;

    @Autowired
    Environment env;

    @BeforeEach
    public void begin() {
        WebDriverManager.firefoxdriver().setup();
        if (env.getProperty("headless") != null && env.getProperty("headless").equals("true")) {
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--headless");
            driver = new FirefoxDriver(options);
        } else {
            driver = new FirefoxDriver();
        }
        driver.navigate().to(URL);
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
        fail();
    }
}
