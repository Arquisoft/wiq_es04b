Feature: I enter the webpage

    Scenario: I see the title
        Given I am in the home page
        Then I should see the title "Wikigamez"

    Scenario: I click register
        Given I am in the home page
        When I click the register button
        Then I should see the register page