package com.frameworkium.jira;

import com.frameworkium.base.properties.Property;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class JiraConfig {

    public static final String JIRA_REST_PATH = "/rest/api/latest/";
    public static final String REST_ZAPI_PATH = "/rest/zapi/latest/";
    public static final String ZEPHYR_TAG_PREFIX = "@TestCaseId:";
    public static final String NO_UPLOAD_TO_ZEPHYR = "@NoZephyr";
    private static final String DEFAULT_JIRA_BDD_FIELD_KEY = "description";


    private JiraConfig() {
        // hide default constructor for this util class
    }

    /**
     * Basic request to send to JIRA and authenticate successfully.
     */
    public static RequestSpecification getJIRARequestSpec() {
        return given()
                .baseUri(Property.JIRA_URL.getValue())
                .relaxedHTTPSValidation()
                .auth().preemptive().basic(
                        Property.JIRA_USERNAME.getValue(),
                        Property.JIRA_PASSWORD.getValue());
    }

    /**
     * These should correspond to your ZAPI result IDs and
     * are only used if logging to Zephyr for JIRA.
     */
    public static class ZapiStatus {

        public static final int ZAPI_STATUS_PASS = 1;
        public static final int ZAPI_STATUS_FAIL = 2;
        public static final int ZAPI_STATUS_WIP = 3;
        public static final int ZAPI_STATUS_BLOCKED = 4;
    }

    public enum Priority{
        _1_CRITICAL("5"),
        _2_HIGH("1"),
        _3_MEDIUM("2"),
        _4_LOW("3"),
        UNCATEGORISED("6");
        public String value;

        Priority(String priority) {
            this.value = priority;
        }

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

    /**
     * These should correspond to your field options
     * if logging a test result to a field.
     */
    public static class JiraFieldStatus {
        public static final String JIRA_STATUS_PASS = "Pass";
        public static final String JIRA_STATUS_FAIL = "Fail";
        public static final String JIRA_STATUS_WIP = "WIP";
        public static final String JIRA_STATUS_BLOCKED = "Blocked";
    }

    /**
     * These should correspond to the workflow transition names required to mark
     * the result if using a customised com.frameworkium.jira.jira issue type & workflow to manage
     * tests NB - put all required transitions to get between statuses
     * (e.g. restart, then mark result) - each will be tried & ignored if not possible
     */
    public static class JiraTransition {
        public static final String[] JIRA_TRANSITION_PASS = {"Done"};
        public static final String[] JIRA_TRANSITION_FAIL = {"Done"};
        public static final String[] JIRA_TRANSITION_WIP = {"Reopen", "Start Progress"};
        public static final String[] JIRA_TRANSITION_BLOCKED = {"Done"};
    }

    //todo what should default value be?? custom bddfield or description field?
    /**
     * Use a default field for putting the BDD in a Zephyr field however we can allow this to be overridden by supplying
     * a system property
     * @return key for the Jira Field to place the BDD given,when,then
     */
    public static String getBddFieldKey(){
        if(Property.JIRA_BDD_FIELD_KEY.isSpecified()){
            return Property.JIRA_BDD_FIELD_KEY.getValue();
        } else {
            return DEFAULT_JIRA_BDD_FIELD_KEY;
        }
    }
}
