package com.frameworkium.jira.zapi;

import com.frameworkium.jira.standalone.StandaloneTool;
import io.restassured.response.Response;
import com.frameworkium.jira.properties.Property;
import com.frameworkium.jira.JiraConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.ITestResult;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.frameworkium.jira.JiraConfig.REST_ZAPI_PATH;
import static com.frameworkium.jira.JiraConfig.getJIRARequestSpec;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class Execution {

    private static final Logger logger = LogManager.getLogger();

    private final String version;
    private final String issue;
    private List<Integer> idList;
    private int currentStatus;

    /**
     * Constructor that links an execution to an issue.
     */
    public Execution(String issue) {
        this.version = Property.RESULT_VERSION.getValue();
        this.issue = issue;
        initExecutionIdsAndCurrentStatus();
    }

    private void initExecutionIdsAndCurrentStatus() {
        if (isBlank(version) || isBlank(issue)) {
            return;
        }
        String query = String.format(
                "issue='%s' and fixVersion='%s'", issue, version);

        SearchExecutions search = new SearchExecutions(query);
        idList = search.getExecutionIds();

        List<Integer> statusList = search.getExecutionStatuses();
        if (!statusList.isEmpty()) {
            currentStatus = statusList.get(0);
        }
    }

    /**
     * Gets the status of the status.
     *
     * @return ZAPI execution status from the ITestResult status
     */
    public static int getZAPIStatus(int status) {
        switch (status) {
            case ITestResult.SUCCESS:
                return JiraConfig.ZapiStatus.ZAPI_STATUS_PASS;
            case ITestResult.FAILURE:
                return JiraConfig.ZapiStatus.ZAPI_STATUS_FAIL;
            case ITestResult.SKIP:
                return JiraConfig.ZapiStatus.ZAPI_STATUS_BLOCKED;
            default:
                return JiraConfig.ZapiStatus.ZAPI_STATUS_FAIL;
        }
    }

    public int getExecutionStatus() {
        return currentStatus;
    }

    /**
     * Update issue with a comment and attachments.
     */
    public void update(int status, String comment, String... attachments) {

        if (idList.size() == 0) {
            logger.info("FAILED [{}] - Issue not found", issue);
            StandaloneTool.setErrorsEncountered(true);
            return ;
        }

        for (Integer executionId : idList) {
            updateStatusAndComment(executionId, status, comment);
            replaceExistingAttachments(executionId, attachments);

//            logger.debug("ZAPI Updater - Updated {} to status {}", issue, status);
        }
    }

    private Response updateStatusAndComment(Integer executionId, int status, String comment) {

        Response response = null;

        try {
            JSONObject obj = new JSONObject();
            obj.put("status", String.valueOf(status));
            int commentMaxLen = 750;
            obj.put("comment", StringUtils.abbreviate(comment, commentMaxLen));

            response = getJIRARequestSpec()
                    .contentType("application/json")
                    .body(obj.toString())
                    .when()
                    .put(REST_ZAPI_PATH + "execution/" + executionId + "/execute");

        } catch (JSONException e) {
            logger.error("Update status and comment failed", e);
            StandaloneTool.setErrorsEncountered(true);
        }

        if (response !=null && response.statusCode() == 200){
            logger.info("SUCCESS [{}] - updated status",issue);
        } else {
            logger.info("FAILED [{}] failed to update status",issue);
            StandaloneTool.setErrorsEncountered(true);
        }

        return response;
    }

    private void replaceExistingAttachments(Integer executionId, String... attachments) {
        if (attachments != null) {
            deleteExistingAttachments(executionId);
            addAttachments(executionId, attachments);
        }
    }

    private List<Response> deleteExistingAttachments(Integer executionId) {

        String path = "attachment/attachmentsByEntity?entityType=EXECUTION&entityId=" + executionId;

        ArrayList<Response> responses = new ArrayList<>();

        getJIRARequestSpec()
                .get(REST_ZAPI_PATH + path).thenReturn().jsonPath()
                .getList("data.fileId", String.class)
                .stream()
                .map(fileId -> REST_ZAPI_PATH + "attachment/" + fileId)
                .forEach(attachment -> responses.add(
                        getJIRARequestSpec()
                                .delete(attachment)));

        return responses;
    }

    private List<Response> addAttachments(Integer executionId, String... attachments) {

        String path = REST_ZAPI_PATH
                + "attachment?entityType=EXECUTION&entityId=" + executionId;

        Map<String,Response> uploadResult = new HashMap<>();

        Arrays.stream(attachments)
                .filter(a -> !a.isEmpty())
                .map(File::new)
                .map(f -> {
                    if (!f.exists()){
                        logger.info("FAILED [{}] attachment file not found: {}",issue,f.getPath());
                        StandaloneTool.setErrorsEncountered(true);
                        return null;
                    } else{
                        return f;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(attachment -> uploadResult.put(attachment.getPath(),
                    getJIRARequestSpec()
                            .header("X-Atlassian-Token", "nocheck")
                            .multiPart(attachment)
                            .when()
                            .post(path)
        ));

        uploadResult.forEach( (attachment, response) -> {
            if (response.getStatusCode() == 200) {
                logger.info("SUCCESS [{}] Successfully added attachment: {}",issue,attachment);
            } else {
                logger.info("FAILED [{}] Failed to added attachment: {}",issue, attachment);
                StandaloneTool.setErrorsEncountered(true);
            }
        });

        return new ArrayList<>(uploadResult.values());

    }
}
