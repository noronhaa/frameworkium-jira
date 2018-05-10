@wip
Feature: set of scenarios covering required new functionality of the frameworkium-jira

  @standaloneTool
  Scenario: User can update tests results in an EXISTING test cycle using the standalone tool
    Given I have an exiting test cycle containing my tests
    And I have a file containing test results
    When I use the standalone tool
    Then my existing tests results will be updated

  @standaloneTool
  Scenario: User can update tests results in a different test cycle that exists using the standalone tool
    Given I have a existing test cycle I want to add my tests to
    And I have a file containing test results
    When I use the standalone tool
    Then my tests will be added to the new cycle
    And the tests results within the new cycle will be updated

  @standaloneTool
  Scenario: User can add existing tests to a new test cycle and update test results
    Given I have a NEW test cycle I would like to add
    And I have a file containing test results
    When I use the standalone tool
    Then a new test cycle will be created
    And my tests will be added to the new cycle
    And the tests results within the new cycle will be updated



  Scenario: User can update the BDD Gherkin tests in Zephyr in an EXISTING test cycle
    Given I have an exiting test cycle containing my tests
    And I have made a change to my BDD tests in my automation framework
    When I run my automated tests
    Then my existing tests results will be updated
    And my Gherkin BDDs will be updated in the corresponding zephyr test

  Scenario: User can update the BDD Gherkin tests in Zephyr in a NEW test cycle
    Given I have a NEW test cycle I would like to add
    And I have made a change to my BDD tests in my automation framework
    When I run my automated tests
    Then a new test cycle will be created
    And my tests will be added to the new cycle
    And the tests within the new cycle will be updated
    And my Gherkin BDDs will be updated in the corresponding zephyr test

  Scenario: User can create a NEW zephyr test and upload a BDD Gherkin test, adding to an EXISTING cycle
    Given I have an exiting test cycle
    And I have added a new BDD test in my automation framework
    When I run my automated tests
    Then a new zephyr test will be added
    And the new test will be added to the existing cycle
    And existing tests in the cycle will not be re-added
    And the test results for the new test will be updated
    And the BDD Sceanrio in my automation framework will be updated with the Zephyr Test ID

  Scenario: User can create a NEW zephyr test and upload a BDD Gherkin test, adding to a NEW cycle
    Given I have a NEW test cycle I would like to add
    And I have added a new BDD test in my automation framework
    When I run my automated tests
    Then a new zephyr test will be added
    And a new test cycle will be created
    And the new test will be added to the new cycle
    And the test results for the new test will be updated
    And the BDD Sceanrio in my automation framework will be updated with the Jira ID