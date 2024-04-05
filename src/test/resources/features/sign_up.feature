Feature: I try to register

    Scenario: I Register with valid data and I see the profile page
        Given I am not registered or logged in
        And I am on the register page
        When I fill in the form with valid data username: "user1" email: "user1@gmail.com" password: "password" password_confirmation: "password"
        And I press the register button
        Then I should see the profile page

    Scenario: I Register with invalid data and I see the error message
        Given I am not registered or logged in
        And I am on the register page
        When I fill in the form with invalid data username: "user1" email: "test1test.com" password: "password" password_confirmation: "password"
        And I press the register button
        Then I should see the error message "signup.error.email.valid"