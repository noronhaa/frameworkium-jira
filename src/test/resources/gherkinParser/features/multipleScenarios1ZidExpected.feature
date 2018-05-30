Feature:

  @TestCaseId:TP-12345
  Scenario: a
    Given x

    @TestCaseId:x
  Scenario: b
    Given y

    @q
  @TestCaseId:TP-12345
  Scenario: c
    Given z