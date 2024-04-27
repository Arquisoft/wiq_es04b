package com.uniovi.steps;

import com.uniovi.Wiq_IntegrationTests;
import com.uniovi.services.InsertSampleDataService;
import com.uniovi.services.QuestionGeneratorService;
import com.uniovi.util.PropertiesExtractor;
import com.uniovi.util.SeleniumUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

public class GameStep extends Wiq_IntegrationTests {

    @Autowired
    private InsertSampleDataService dataService;
    @Autowired
    private QuestionGeneratorService questionGeneratorService;

    private Logger log = LoggerFactory.getLogger(GameStep.class);

    @When("I press Play")
    public void iPressPlay() throws IOException, InterruptedException {
        questionGeneratorService.generateTestQuestions();
        List<WebElement> elems = SeleniumUtils.waitLoadElementsBy(driver, "free", "//*[@href=\"/game\"]", 5);
        elems.get(0).click();
    }

    @Then("I should start playing until I see message {string}")
    public void iShouldStartPlaying(String messageKey) {
        boolean playing = true;
        String xpath = "//*[contains(text(),'" + p.getString(messageKey, PropertiesExtractor.getSPANISH()) + "')]";
        int i= 0;
        List<WebElement> finalMessage;
        while(playing) {
            //I obtain the buttons for the answers
            List<WebElement> elems = By.cssSelector(".container .row button").findElements(driver);
            log.info("Found " + elems.size() + " buttons");
            log.info("Iteration " + i + " of the game");
            //I click on the first button
            elems.get(0).click();

            try {
                finalMessage = SeleniumUtils.waitLoadElementsByXpath(driver, xpath, 10);
            } catch(Exception e) {
                continue;
            }

            if(!finalMessage.isEmpty()) {
                playing = false;
            }
            i++;
        }
    }

    @When("I press Play With Friends")
    public void iPressPlayWithFriends() throws IOException, InterruptedException {
        questionGeneratorService.generateTestQuestions();
        List<WebElement> elems = SeleniumUtils.waitLoadElementsBy(driver, "id", "multiplayerBtn", 5);
        elems.get(0).click();
    }

    @And("I create a code")
    public void iCreateACode() {
        List<WebElement> elems = SeleniumUtils.waitLoadElementsBy(driver, "id", "createBtn", 5);
        elems.get(0).click();
    }

    @And("I press start")
    public void iPressStart() {
        List<WebElement> elems = SeleniumUtils.waitLoadElementsBy(driver, "id", "startBtn", 5);
        elems.get(0).click();
    }
}
