package com.frameworkium.jira.api;

import com.frameworkium.jira.JiraConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

@Data @AllArgsConstructor
public class NewIssue {

    private static final Logger logger = LogManager.getLogger();

    public static final String CUSTOM_BDD_FIELD_ID = "customfield_15942";

    private String key;
    private String summary;
    private String description;
    private IssueType issueType;
    private String bddField;

    private String generateJson(){
        JSONObject object = new JSONObject();
        JSONObject fields = new JSONObject();

        JSONObject project = new JSONObject();
        project.put("key",this.key);

        JSONObject issueType = new JSONObject();
        issueType.put("name", this.issueType.name);

        fields.put("project",project);
        fields.put("summary",this.summary);
        fields.put("description",this.description);
        fields.put("issuetype",issueType);
        fields.put(CUSTOM_BDD_FIELD_ID, bddField);

        object.put("fields", fields);
//        System.out.println(object.toString(2));

        return object.toString();
    }

    public String create(){
        String endpoint = JiraConfig.JIRA_REST_PATH + "issue";

        //TODO get the bdd out to display properly
        String body = this.generateJson();

        logger.debug("Payload: " + body);

        String zephyrId = JiraConfig.getJIRARequestSpec()
                .given()
                .contentType("application/json")
                .body(body)
                .expect()
                .statusCode(201).log().ifError()
                .when()
                .post(endpoint)
                .thenReturn()
                .jsonPath().getString("key");

        logger.info("Zephyr Test Created: " + zephyrId);

        return zephyrId;

    }

    public enum IssueType{
        BUG("Bug"),
        EPIC("Epic"),
        STORY("Story"),
        TEST("Test"),
        TASK("Task");

        private String name;

        IssueType(String task) {
            name = task;
        }
    }

}
