package com.uniovi.steps;

import com.uniovi.Wiq_IntegrationTests;
import io.cucumber.java.en.And;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class LogInStep extends Wiq_IntegrationTests {
    @And("I press the login button")
    public void iPressTheLoginButton() {
        By button = By.className("btn");
        driver.findElement(button).click();
    }

    @And("I fill in the form with valid data email: {string} password: {string}")
    public void iFillInTheFormWithValidDataEmailPassword(String username, String pass) {
        WebElement mail = driver.findElement(By.name("username"));
        mail.click();
        mail.clear();
        mail.sendKeys(username);
        WebElement password = driver.findElement(By.name("password"));
        password.click();
        password.clear();
        password.sendKeys(pass);
    }
}
