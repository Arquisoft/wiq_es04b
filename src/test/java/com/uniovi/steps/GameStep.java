package com.uniovi.steps;

import com.uniovi.Wiq_IntegrationTests;
import com.uniovi.services.InsertSampleDataService;
import com.uniovi.util.PropertiesExtractor;
import com.uniovi.util.SeleniumUtils;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class GameStep extends Wiq_IntegrationTests {

    @Autowired
    private InsertSampleDataService dataService;

    @When("I press Play")
    public void iPressPlay() {
        List<WebElement> elems = SeleniumUtils.waitLoadElementsBy(driver, "free", "//*[@href=\"/game\"]", 5);
        elems.get(0).click();
    }

    @Then("I should start playing")
    public void iShouldStartPlaying() throws InterruptedException {
        boolean playing = true;
        String xpath = "//*[contains(text(),'" + p.getString("game.finish", PropertiesExtractor.getSPANISH()) + "')]";
        List<WebElement> finalMessage;
        while(playing){
            //I obtain the buttons for the answers
            List<WebElement> elems = SeleniumUtils.waitLoadElementsBy(driver, "text", "Test answer 0", 5);
            //I click on the first button
            elems.get(0).click();

            try{
                finalMessage=  SeleniumUtils.waitLoadElementsByXpath(driver, xpath, 10);
            }catch(Exception e){
                continue;
            }
            if(finalMessage.size()>0){
                playing = false;
            }
        }
    }
}
