@jira @all
Feature: Set of sets to check interaction with Jira API works as expected

  @Issue
  Scenario: Check we can link 2 JIRA tickets
    Given I have the details of '2' JIRA ticket(s)
    When I link the 2 tickets by 'Duplicate'
    Then I will get a 2XX response

  @Issue
  Scenario: Check we can add an attachment to a ticket
    Given I have the details of '1' JIRA ticket(s)
    When I add an attachment
    Then I will get a 2XX response

    #doesn't work for all fields, completely depends on the structure of the json
  @JiraTest @flaky
  Scenario: Check we can update a field value
    Given I have the details of '1' JIRA ticket(s)
    When I change the 'Description' field
    Then I will get a 2XX response

  @JiraTest
  Scenario: Check we can leave a comment on a ticket
    Given I have the details of '1' JIRA ticket(s)
    When I leave a comment on a Jira ticket
    Then I will get a 2XX response

#    CANNOT TRANSITION TO CURRENT TRANSITION
  @JiraTest @JiraTransition
  Scenario: Check we can change a ticket to a different status
    Given I have the details of '1' JIRA ticket(s)
    When I change the ticket to 'Blocked'
    Then I will get a 2XX response

  @SearchIssues
  Scenario: Check we can query a Jira Issue
    Given I have the details of '1' JIRA ticket(s)
    And I can find the ticket by querying
    Then I can get the keys for the Issue
    And I can get the Summaries for the Issue
    And I can get the Key for a Summary








