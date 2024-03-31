package com.uniovi;

import com.uniovi.util.FirefoxWebDriver;
import io.cucumber.java.After;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

public class CucumberWiqContext {
    WebDriver driver;

    @Bean(name="webdriver")
    public WebDriver webDriver() {
        driver = new FirefoxWebDriver();
        return driver;
    }
}
