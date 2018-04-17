package com.frameworkium.jira.api;

import io.restassured.response.Response;
import com.frameworkium.jira.JiraConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.*;

import static com.frameworkium.jira.JiraConfig.JIRA_REST_PATH;

public class JiraTest {

    private static final Logger logger = LogManager.getLogger();

    private static final String ISSUE_PATH = "issue/";

    private JiraTest() {}

    public static void main(String[] args) {
        System.setProperty("jiraURL","https://engineering/com.frameworkium.jira.jira");
        System.setProperty("jiraUsername","automationexecution");
        System.setProperty("jiraPassword","po12PO!");
        System.setProperty("resultVersion","MSS Automation");
        System.setProperty("zapiCycleRegEx","dummy cycle");

        System.out.println("----");
        System.out.println(getFieldId("Description"));
        Response r = changeIssueFieldValue("CSCYBER-3053", "Labels", "Ready");
        System.out.println(r.getStatusCode());

    }


    //todo this is flaky, depending on the field you may need a different json object to be parsed in or you get 400 bad request
    /** Create and send a PUT request to JIRA to change the value of a field. */
    public static Response changeIssueFieldValue(
            String issueKey, String fieldToUpdate, String resultValue) {

        Response response = null;
        JSONObject obj = new JSONObject();
        JSONObject fieldObj = new JSONObject();
        JSONArray setArr = new JSONArray();
        JSONObject setObj = new JSONObject();
        JSONObject valueObj = new JSONObject();

        try {
            obj.put("update", fieldObj);
            fieldObj.put(getFieldId(fieldToUpdate), setArr);
            setArr.put(setObj);
            setObj.put("set", resultValue);
//            setObj.put("set", valueObj);
//            valueObj.put("value", resultValue);

            response = JiraConfig.getJIRARequestSpec()
                    .contentType("application/json").and()
                    .body(obj.toString())
                    .when()
                    .put(JIRA_REST_PATH + ISSUE_PATH + issueKey);
        } catch (JSONException e) {
            logger.error("Can't create JSON Object for test case result update", e);
        }

        return response;
    }

    /**
     * Generic method to change a field as different field will require different json bodies,
     * create your own json object to update a field and parse into this wrapper method
     * @param issueKey key of the JIRA ticket you want to update
     * @param jsonObject the json object representing the field you wish to change and new value
     * @return response object from request
     */
    public static Response changeIssueFieldValue(
            String issueKey, JSONObject jsonObject) {

        Response response = null;

        try {
            response = JiraConfig.getJIRARequestSpec()
                    .contentType("application/json").and()
                    .body(jsonObject.toString())
                    .when()
                    .put(JIRA_REST_PATH + ISSUE_PATH + issueKey);
        } catch (JSONException e) {
            logger.error("Can't create JSON Object for test case result update", e);
        }

        return response;
    }


    /**
     * Query the field id using the name of the field
     * @param fieldName
     * @return field id
     */
    private static String getFieldId(String fieldName) {

        return JiraConfig.getJIRARequestSpec()
                .when()
                .get(JIRA_REST_PATH + "field")
                .thenReturn().jsonPath()
                .getString(String.format("find {it.name == '%s'}.id", fieldName));
    }


    /**
     * Create and post a JSON request for a comment update in JIRA.
     */
    public static Response addComment(String issueKey, String commentToAdd) {

        Response response = null;
        JSONObject obj = new JSONObject();

        try {
            obj.put("body", commentToAdd);
            response = JiraConfig.getJIRARequestSpec()
                    .contentType("application/json")
                    .body(obj.toString())
                    .when()
                    .post(JIRA_REST_PATH + ISSUE_PATH + issueKey + "/comment");
        } catch (JSONException e) {
            logger.error("Can't create JSON Object for comment update", e);
        }

        return response;
    }

    /**
     * Create and post a JSON request for a transition change in JIRA.
     */
    public static Response transitionIssue(String issueKey, String transitionName) {

        logger.debug("Transition name: " + transitionName);
        return transitionIssue(issueKey, getTransitionId(issueKey, transitionName));
    }

    private static Response transitionIssue(String issueKey, int transitionId) {

        Response response = null;

        logger.debug("Transition id: " + transitionId);
        JSONObject obj = new JSONObject();
        JSONObject idObj = new JSONObject();

        try {
            obj.put("transition", idObj);
            idObj.put("id", transitionId);
            response = JiraConfig.getJIRARequestSpec()
                    .contentType("application/json").and()
                    .body(obj.toString())
                    .when()
                    .post(JIRA_REST_PATH + ISSUE_PATH + issueKey + "/transitions");
        } catch (JSONException e) {
            logger.error("Can't create JSON Object for transition change", e);
        }

        return response;
    }

    private static int getTransitionId(String issueKey, String transitionName) {

        return JiraConfig.getJIRARequestSpec()
                .get(JIRA_REST_PATH + ISSUE_PATH + issueKey + "?expand=transitions.fields")
                .thenReturn().jsonPath()
                .getInt(String.format(
                        "transitions.find {it -> it.name == '%s'}.id", transitionName));
    }

}
