Feature: I enter the webpage without being logged in

    Scenario: I see the title
        Given I am in the home page
        Given I am not logged in
        Then I should see the title "Wikigame"

    Scenario: I click register
        Given I am in the home page
        Given I am not logged in
        When I click the register button
        Then I should see the register page

    Scenario: I click login
        Given I am in the home page
        Given I am not logged in
        When I click the login button
        Then I should see the login page

    Scenario: I click play
        Given I am in the home page
        Given I am not logged in
        When I click the play button
        Then I should see the login page

    Scenario: I click global ranking
        Given I am in the home page
        Given I am not logged in
        When I click the global ranking button
        Then I should see the global ranking page

    Scenario: I cant see the logout button
        Given I am in the home page
        Given I am not logged in
        Then I should not see the logout button

    Scenario: I cant see the profile button
        Given I am in the home page
        Given I am not logged in
        Then I should not see the profile button

    Scenario: I cant see the personal ranking button
        Given I am in the home page
        Given I am not logged in
        Then I should not see the personal ranking button
