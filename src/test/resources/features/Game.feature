Feature: I try to play a normal game
  Scenario: I play a game
    Given I am not registered or logged in
    And I am on the register page
    When I fill in the form with valid data username: "user1" email: "user1@gmail.com" password: "password" password_confirmation: "password"
    And I press the register button
    And I go to the home page
    When I press Play
    Then I should start playing