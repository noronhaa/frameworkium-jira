# frameworkium-jira

use the standalone version of this tool to update zephyr tests with results from a csv

csv format (eachline) `<Issue Key>, <Pass/Passed/Fail/Failed>, <optional comment>, <optional attachment path>`
csv example line: `CSCYBER-1,Pass,updated by frameworkium-zephyr tool,path/to/attachment`

Package jar: `mvn package` jar will be created in target dir
run jar: `java -jar <jarname> <csv file path> <jiraURL> <jiraUsername> <jiraPassword> <jira fix version> <zephyr test cycle name(regex)>`
