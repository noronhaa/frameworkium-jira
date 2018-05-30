package com.frameworkium.jira.api;

import io.restassured.response.Response;
import com.frameworkium.jira.JiraConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;

public class Issue {

    private static final Logger logger = LogManager.getLogger();
    private final String issueKey; // Jira Key e.g. KT-123

    public Issue(String issue) {
        this.issueKey = issue;
    }

    public boolean found(){
        String endpoint = "search?jql=issue=" + this.issueKey;

        int statuscode = JiraConfig.getJIRARequestSpec()
                            .get(endpoint)
                            .statusCode();

        if (statuscode == 200){
            return true;
        } else if (statuscode == 400){
            return false;
        } else {
            throw new RuntimeException(
                    "unexpected status code, expected 200 (found) or 400 (not found) but got " + statuscode);
        }
    }


    /**
     * Create and post a JSON request to JIRA to get issues.
     *
     * @param type         name of issue
     * @param inwardIssue  inward issue key
     * @param outwardIssue outward issue key
     */
    public Response linkIssues(String type, String inwardIssue, String outwardIssue) {
        JSONObject obj = new JSONObject();
        JSONObject typeObj = new JSONObject();
        JSONObject inwardIssueObj = new JSONObject();
        JSONObject outwardIssueObj = new JSONObject();

        try {
            typeObj.put("name", type);
            inwardIssueObj.put("key", inwardIssue);
            outwardIssueObj.put("key", outwardIssue);

            obj.put("type", typeObj);
            obj.put("inwardIssue", inwardIssueObj);
            obj.put("outwardIssue", outwardIssueObj);
        } catch (JSONException e) {
            logger.error("Can't create JSON Object for linkIssues", e);
        }

        return JiraConfig.getJIRARequestSpec()
                .contentType("application/json")
                .body(obj.toString())
                .when()
                .post(JiraConfig.JIRA_REST_PATH + "issueLink");
    }


    /** Adds the file attachment to the JIRA issue. */
    public Response addAttachment(File attachment) {
        String attachmentPath = String.format("issue/%s/attachments", issueKey);

        return JiraConfig.getJIRARequestSpec()
                .header("X-Atlassian-Token", "nocheck")
                .multiPart(attachment).and()
                .when()
                .post(JiraConfig.JIRA_REST_PATH + attachmentPath);
    }
}
