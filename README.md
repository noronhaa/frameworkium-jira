# frameworkium-jira

### Frameworkium 3

This is one of the Frameworkium 3 libraries. Frameworkium 3 is a new release of [frameworkium-core](https://github.com/Frameworkium/frameworkium-core) aka Frameworkium 2.
 In Frameworkium 3 we have split the project up into their own logical modules that can be used independently of the other modules. 
 
 Original Framewokium 2 docs [here]()
 
 Frameworkium 3 libraries:
 1. [frameworkium-ui](https://github.com/Frameworkium/frameworkium-ui)
 2. [frameworkium-api](https://github.com/Frameworkium/frameworkium-api)
 3. [frameworkium-reporting](https://github.com/Frameworkium/frameworkium-reporting)
 4. [frameworkium-jira](https://github.com/Frameworkium/frameworkium-jira)  
 
 Example Projects implementing Frameworkium 3:
 - [frameworkium-examples](https://github.com/Frameworkium/frameworkium-examples/tree/frameworkium3)
 - [frameworkium-bdd](https://github.com/Frameworkium/frameworkium-bdd/tree/frameworkium3)

***

### Summary
frameworkium-jira is a library for updating test results in Jira or Zephyr (A Test Case management plugin for Jira). 
By linking your automated tests to Jira/Zephyr
by the use of Tags/annotations, frameworkium-jira can automatically change the status after a test is executed

frameworkium-jira can also filter tests due to be executed so that only tests matching a given JQL Query are run

There are 2 ways you can use frameworkium-jira:
- As a dependency in your Java project
- As a standalone tool, independent of your project (jump to [docs](#standalone-tool))

#### Technologies used / supported
frameworkium-reporting is built using the following technologies
- Java
- TestNG
- Cucumber-JVM

If you have a Java project with TestNG and/or Cucumber-JVM which has access to Jira/Zephyr then use frameworkium-jir
 as a dependency in your project, otherwise use the [standalone tool](#standalone-tool)

### How does frameworkium-jira work?
We interact with Jira and Zephyr by usijng the Jira and Zephyr APIs, we build up functionality to:

##### Jira

- Leave a comment on a Ticket
- Add an attachment to a ticket
- Transition the state of a ticket 
- Link 2 Issues together
##### Zephyr
- Update Status of a Test
- Leave a comment on the execution of a Test
- Remove and add attachments on the execution of a Test

We then use either a TestNG or Cucumber listener to track your test execution and when a test fails/passes we will then change the 
state of the corresponding ticket/test with the result uploading a failure screenshot of a webpage if
relevant too

In order to interact with Jira/Zephyr connection details need to be parsed at runtime, Details of parameters are in the 
[Command Line Options](#command-line-options) section

### Getting Started
1. Add frameworkium-jira dependency to your pom
```xml 
 <dependencies>
     <dependency>
       <groupId>com.github.frameworkium</groupId>
       <artifactId>frameworkium-jira</artifactId>
       <version>3.0-BETA1</version>
     </dependency>
 </dependencies>
 ```
 2. (**Cucumber Only**) add the `com.frameworkium.jira.listeners.CucumberV1ZephyrListener` listener as a plugin to 
 your run configuration, either to your runner or to the _cucumber-jvm-parallel-plugin_ in your `pom.xml`:
```xml
  <plugins>
    <plugin>
      <name>com.frameworkium.jira.listeners.CucumberV1ZephyrListener</name>
      <noOutput>true</noOutput>
    </plugin>
  </plugins>
```
3. Run your tests, parsing in the required Jira/Zephyr CLI parameters eg `mvn clean test -DjiraURL=http://jira:8080 -DjiraUsername=abc
 -DjiraPassword=xyz -DresultVersion=1.0 -DzapiCycleRegEx="Regression run"`

### Command Line Options

No defaults

Property | Description | Values
-------- | ----------- | ------
`jiraURL`| The base URL of the JIRA instance you want to use | e.g. `http://jira:8080`
`jiraUsername`| The JIRA user you want to use | e.g. `JBloggs`
`jiraPassword`| The JIRA user's password | e.g. `password`
`jqlQuery`| the JQL query to use to look up the JIRA tests to run (the results of the query will be looked up against the `@Issue` annotations on tests | e.g. `(priority=1 and component=Admin) or issueKey=JIRA-123`
`jiraResultFieldName`| The Jira field name to attempt to log results to for the specified `@Issue`. The values to change the field to are specified in the Jira config file. Useful if you're using a Jira field to mark the test result | e.g. `Test Result`
`jiraResultTransition`| If specified, will attempt to transition the `@Issue` specified through the transitions specified in the Jira config. Useful if using a customised Jira workflow for managing test results. | any value
`resultVersion`| The 'Version' to mark the test execution against in Zephyr for JIRA (requires ZAPI) | e.g. `App v1.1.2`
`zapiCycleRegEx`| If the Zephyr test cycle name contains this string test results will be logged against the matching cycles. | e.g. `firefox` or `my-special-cycle`

The most common usecase is updating Zephyr test cases in which the following are required `jiraURL`, `jiraUsername`, `jiraPassword`
`resultVersion` and `zapiCycleRegEx`


### Standalone tool
//todo


## Docs WIP
use the standalone version of this tool to update zephyr tests with results from a csv

csv format (eachline) `<Issue Key>, <Pass/Passed/Fail/Failed>, <optional comment>, <optional attachment path>`
csv example line: `CSCYBER-1,Pass,updated by frameworkium-zephyr tool,path/to/attachment`

Package jar: `mvn package` jar will be created in target dir
run jar: `java -jar <jarname> <csv file path> <jiraURL> <jiraUsername> <jiraPassword> <jira fix version> <zephyr test cycle name(regex)>`
