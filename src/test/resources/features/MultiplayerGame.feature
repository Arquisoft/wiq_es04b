Feature: I play a multiplayer game
    Scenario: I start a multiplayer game alone
      Given I am not registered or logged in
      And I am on the register page
      When I fill in the form with valid data username: "userGame" email: "userGame@gmail.com" password: "password" password_confirmation: "password"
      And I press the register button
      And I go to the home page
      When I press Play With Friends
      And I create a code
      And I press start
      Then I should start playing until I see message "multi.finished"