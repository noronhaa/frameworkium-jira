@zephyr @all
Feature: Set of tests checking the functionality of the zephyr API

    @Execution
  Scenario Outline: I can successfully update the status of a zephyr test with a comment
    Given I have zephyr details for a Test Case
    When I change the status to '<status>'
    Then the status will be '<status>'

    Examples:
    | status |
    | pass   |
    | fail   |
    | blocked|
    | wip    |

    @Execution @attachment
  Scenario Outline: I can successfully add an attachment to a zephyr test
    Given I have zephyr details for a Test Case
    When I add '<attachments>' attachments
    Then all the responses be a 2XX


    Examples:
     | attachments |
     | 1           |
     | 2           |


    @Execution
  Scenario: I can successfully delete all attachments on a zephyr test
    Given I have zephyr details for a Test Case
    When I add delete attachments
    Then all the responses be a 2XX

# note test requires a fix version to find the execution - this is NOT version in the Execution cycle
    @SearchExecution
  Scenario: I can query an execution IDs and statuses for a test case
    Given I have zephyr details for a Test Case
    When I search for a test execution cycle
    Then I can get the 'id' for the execution
    And I can get the 'status' for the execution