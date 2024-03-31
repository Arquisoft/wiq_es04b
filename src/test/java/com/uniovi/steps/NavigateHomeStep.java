package com.uniovi.steps;

import com.uniovi.*;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.junit.jupiter.api.Assertions;

public class NavigateHomeStep extends Wiq_IntegrationTests {

    @Given("I am in the home page")
    public void i_am_in_the_home_page() {
        driver.navigate().to(URL);
    }

    @Then("I should see the title {string}")
    public void i_should_see_the_title(String title) {
        Assertions.assertEquals(title, driver.getTitle());
    }

    @After
    public void cleanUpAfterScenario() {
        driver.manage().deleteAllCookies();
    }
}
