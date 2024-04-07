package com.uniovi.steps;

import com.uniovi.*;
import com.uniovi.dto.RoleDto;
import com.uniovi.services.RoleService;
import com.uniovi.util.SeleniumUtils;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class NavigateHomeStep extends Wiq_IntegrationTests {

    @Autowired
    private RoleService roleService;

    @Given("I am in the home page")
    public void i_am_in_the_home_page() {
        driver.navigate().to(URL);
    }

    @Then("I should see the title {string}")
    public void i_should_see_the_title(String title) {
        Assertions.assertEquals(title, driver.getTitle());
    }

    @When("I click the register button")
    public void i_click_the_register_button() {
        List<WebElement> elems = SeleniumUtils.waitLoadElementsBy(driver, "@href", "signup", 5);
        elems.get(0).click();
    }

    @Then("I should see the register page")
    public void i_should_see_the_register_page() {
        SeleniumUtils.waitLoadElementsBy(driver, "h2", "Regístrate", 5);
        SeleniumUtils.textIsPresentOnPage(driver, "Regístrate");
    }
}
