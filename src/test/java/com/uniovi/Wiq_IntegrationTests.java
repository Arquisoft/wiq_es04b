package com.uniovi;

import com.uniovi.util.FirefoxWebDriver;
import io.cucumber.java.After;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Tag("integration")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@CucumberContextConfiguration
@ContextConfiguration(classes = CucumberWiqContext.class)
public class Wiq_IntegrationTests {
    protected static final String URL = "http://localhost:3000/";

    @Autowired
    protected WebDriver driver;
}
