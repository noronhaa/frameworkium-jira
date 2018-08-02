package com.frameworkium.jira.api;

import io.restassured.response.Response;
import com.frameworkium.jira.JiraConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.*;

import static com.frameworkium.jira.JiraConfig.JIRA_REST_PATH;

/**
 * This class is modeled for using a normal Jira Issue as a test case. This is NOT the same as a Zephyr Test
 */
public class JiraTest {

    private static final Logger logger = LogManager.getLogger();

    private static final String ISSUE_PATH = "issue/";

    private JiraTest() {}

    //todo this is flaky, depending on the field you may need a different json object to be parsed in or you get 400 bad request
    // however reluctant to change as it is existing functionality and could break existing usage
    /** Create and send a PUT request to JIRA to change the value of a field. */
    public static Response changeIssueFieldValue(
            String issueKey, String fieldToUpdate, String resultValue) {

        Response response = null;
        JSONObject obj = new JSONObject();
        JSONObject fieldObj = new JSONObject();
        JSONArray setArr = new JSONArray();
        JSONObject setObj = new JSONObject();

        try {
            obj.put("update", fieldObj);
            fieldObj.put(getFieldId(fieldToUpdate), setArr);
            setArr.put(setObj);
            setObj.put("set", resultValue);

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
     * Query list of all fields and then find the ID of a field by issueType
     * @param fieldName
     * @return field id
     */
    static String getFieldId(String fieldName) {

        String fieldId =  JiraConfig.getJIRARequestSpec()
                .expect()
                    .statusCode(200).log().ifError()
                .when()
                    .get(JIRA_REST_PATH + "field")
                .thenReturn().jsonPath()
                .getString(String.format("find {it.name == '%s'}.id", fieldName));

        if(fieldId == null){
            String message = String.format("could not find an ID for field '%s' check field name spelt " +
                    "right with correct capitalisation", fieldName);
            logger.error(message);
        }

        return  fieldId;
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

        logger.debug("Transition issueType: " + transitionName);
        return transitionIssue(issueKey, getTransitionId(issueKey, transitionName));
    }

    /**
     * Transition the status of an issue eg open -> In progress
     * @param issueKey key of the issue to transition to a new state
     * @param transitionId the ID of the new state
     * @return response of the request
     */
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


    /**
     * use the name of the a transition to find its ID
     * @param issueKey issue you wish to transition so find available transitions from Issues current state
     * @param transitionName name of the transition you want to transition the issue state to
     * @return
     */
    private static int getTransitionId(String issueKey, String transitionName) {

        return JiraConfig.getJIRARequestSpec()
                .get(JIRA_REST_PATH + ISSUE_PATH + issueKey + "?expand=transitions.fields")
                .thenReturn().jsonPath()
                .getInt(String.format(
                        "transitions.find {it -> it.name == '%s'}.id", transitionName));
    }

}
