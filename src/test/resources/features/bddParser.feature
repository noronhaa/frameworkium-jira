
Feature: checking if a bdd had a zephyr tag and adding one if needed after creating a new test in zephyr

#  @gherkin
  Scenario: update single scenario with tags tha are not in zephyr
    Given I have a scenario that does NOT contain a zephyr tag
    When I parse the feature file
    Then a new zephyr test will be created
    And the scenario will be successfully updated and match "gherkinParser/features/editFeatureUpdatedExpected.feature"

  @gherkin
  Scenario: update multiple scenario with tags tha are not in zephyr
    Given I have a multiple scenarios that do NOT contain a zephyr tag
    When I parse the feature file
    Then a new zephyr test will be created
    And the scenario will be successfully updated and match "gherkinParser/features/multipleScenarios1ZidExpected.feature"



