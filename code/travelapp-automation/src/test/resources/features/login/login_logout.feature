Feature: As a Amazon user I should be able to login and logout with valid credentials

  Scenario: Login into the application with valid credentials
  	Given I am on the Login page URL "https://www.google.com/"
    Then I click on sign in button and wait for sign in page
    Then I should see Sign In Page
    When I enter username as "automation20201@gmail.com"
    And I Click on Continue button
    And I enter password as "auto20201"
    And click on login button
    Then I am logged in
	Then I got log out from the application and land on sign in page
  
   
