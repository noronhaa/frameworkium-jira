
Feature: checking if a bdd had a zephyr tag and adding one if needed after creating a new test in zephyr

  @gherkin
  Scenario: update single scenario with tags
    Given I have a scenario that does NOT contain a zephyr tag
#    When I parse the feature file
    When I parse the feature file without connecting to zephyr
    And the feature will be successfully updated and match "gherkinParser/features/editFeatureUpdatedExpected.feature"

  @gherkin
  Scenario: update multiple scenario with tags tha are not in zephyr
    Given I have a multiple scenarios that do NOT contain a zephyr tag
    When I parse the feature file without connecting to zephyr
    And the feature will be successfully updated and match "gherkinParser/features/multipleScenarios1ZidExpected.feature"

    @e2e
  Scenario: End to End run of tests using zephyt integrations and test creation
    Given I have a feature file with a mix of zephyr and non zephyr scenarios
    And I do not have a zephyr test cycle setup
    When I 'run' the tests
    Then a new test in zephyr will be created for the test not in zephyr
    And a new zephyr cycle will be created
    And the tests will be added to the new cycle
    And the tests will be updated for the execution
    And the feature will be successfully updated and match ""




