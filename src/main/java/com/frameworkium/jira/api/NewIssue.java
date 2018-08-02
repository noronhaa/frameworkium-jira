package com.frameworkium.jira.api;

import com.frameworkium.jira.JiraConfig;
import gherkin.deps.com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.testng.util.Strings;

import java.util.HashMap;

import static com.frameworkium.jira.JiraConfig.JIRA_REST_PATH;

/**
 * Class for creating a new Jira Issue
 */
@Data @AllArgsConstructor
public class NewIssue {

    private static final Logger logger = LogManager.getLogger();

//    public static final String CUSTOM_BDD_FIELD_ID = "customfield_15942";
//    public static final String CUSTOM_ORIGIN_FIELD_ID = "customfield_11841"; // where the isssue was found   (for a bug)
//    public static final String CUSTOM_ORIGIN_FIELD_CLIENT_VALUE = "23212";

    private String key; //Project Key eg TP from TP-12345
    private String summary; //summary or title field of the ticket
    private String description; // description field
    private IssueType issueType; // Type of issue eg Test, Bug, Task
    private String bddField;
    private JiraConfig.Priority priority; //priority of issue
    private String[] labels; // Jira labels to add if any
    private HashMap<String, Object> customfields; // Many fields are not default jira fields, we can also add these

    /**
     * Generated the Json payload to create a new Issue
     * @return
     */
    private String generateJson(){

        JSONObject object = new JSONObject();
        JSONObject fields = new JSONObject();

        JSONObject project = new JSONObject();
        project.put("key",this.key);

        JSONObject issueType = new JSONObject();
        issueType.put("name", this.issueType.name);

        fields.put("project",project);
        fields.put("summary",this.summary);
        fields.put("issuetype",issueType);

//        JSONObject originEntity = new JSONObject();
//        originEntity.put("value","Test");
//        fields.put(CUSTOM_ORIGIN_FIELD_ID,originEntity);

        if (description != null){
            fields.put("description",this.description);
        }

        if (bddField != null){
            fields.put(JiraConfig.getBddFieldKey(), this.bddField);
        }

        if (labels != null){
            fields.put("labels", this.labels);
        }

        customfields.forEach(fields::put);

        if (priority !=null){
            JSONObject priority = new JSONObject();
            priority.put("id", this.priority.value);
            fields.put("priority",priority);
        }

        object.put("fields", fields);

        logger.debug(object.toString());
        logger.info(object.toString());
        return object.toString();
    }


    /**
     * Send POST request to create new Jira Issue
     * @return
     */
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
