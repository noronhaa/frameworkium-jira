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

## Docs WIP
use the standalone version of this tool to update zephyr tests with results from a csv

csv format (eachline) `<Issue Key>, <Pass/Passed/Fail/Failed>, <optional comment>, <optional attachment path>`
csv example line: `CSCYBER-1,Pass,updated by frameworkium-zephyr tool,path/to/attachment`

Package jar: `mvn package` jar will be created in target dir
run jar: `java -jar <jarname> <csv file path> <jiraURL> <jiraUsername> <jiraPassword> <jira fix version> <zephyr test cycle name(regex)>`
