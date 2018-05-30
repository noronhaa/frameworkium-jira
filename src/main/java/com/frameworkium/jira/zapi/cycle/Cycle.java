package com.frameworkium.jira.zapi.cycle;

import com.frameworkium.jira.JiraConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Cycle {

    private static final Logger logger = LogManager.getLogger();

    private static final String CYCLE_ENDPOINT = "cycle";
    private static final String APPLICATION_JSON = "application/json";


    /**
     * create a new zephyr test cycle
     * @param cycleEntity build request body using CycleEntity object, required fields are versionId, sprintId, projectId, name
     * @return id of newly created cycle
     */
    //todo create new cycle IF cycle doesn't exist??
    public int createNewCycle(CycleEntity cycleEntity){
        int cycleId =  JiraConfig.getJIRARequestSpec()
                                .given()
                                    .contentType(APPLICATION_JSON)
                                    .body(cycleEntity)
                                .expect()
                                    .statusCode(200)
                                .when()
                                    .post(JiraConfig.REST_ZAPI_PATH + CYCLE_ENDPOINT)
                                .thenReturn().jsonPath().getInt("id");

        logger.info(String.format("Zephyr Cycle created: %s [ID:%s]",cycleEntity.getName(),cycleId));

        return cycleId;
    }


    /**
     * Delete a cycle by its cycle ID
     * @param cycleId
     */
    public void deleteCycle(String cycleId){
        String endpoint = JiraConfig.REST_ZAPI_PATH + CYCLE_ENDPOINT + "/" + cycleId;

        JiraConfig.getJIRARequestSpec()
                .given()
                    .contentType(APPLICATION_JSON)
                .expect()
                    .statusCode(200)
                .when()
                    .delete(endpoint);
    }

    /**
     * Using a project key (eg 'TP' from TP-12345), find the ID of this project
     * @param projectKey key such as TP, CSCYBER
     * @return project key
     */
    public String getProjectIdByKey(String projectKey){
        String endpoint = JiraConfig.JIRA_REST_PATH + "project/" + projectKey;

        int projectId = JiraConfig.getJIRARequestSpec()
                .given()
                    .contentType(APPLICATION_JSON)
                .expect()
                    .statusCode(200)
                .when()
                    .get(endpoint)
                .andReturn().jsonPath().getInt("id");

        return String.valueOf(projectId);
    }



    /**
     *
     * Get the id of a 'version' stored in zephyr api
     * @param projectID id of your project, returned by getProjectIdByKey()
     * @param versionName you wish to find the key if of
     * return the key of a build/fix version that we may execute a test/cycle against
     */
    public String getVersionIdByName(String projectID, String versionName){
        String endpoint = String.format(JiraConfig.JIRA_REST_PATH  + "project/%s/versions", projectID);
        String jsonPathMatch = String.format("findAll { it.name == '%s' }.id[0]",versionName);

        return JiraConfig.getJIRARequestSpec()
                .given()
                    .contentType(APPLICATION_JSON)
                .expect()
                    .statusCode(200)
                .when()
                    .get(endpoint)
                .thenReturn().jsonPath().getString(jsonPathMatch);
    }

    /**
     * Add tests to a zephyr test cycle
     * @param addToCycleEntity
     */
    public void addTestsToCycle(AddToCycleEntity addToCycleEntity){
        String endpoint = JiraConfig.REST_ZAPI_PATH + "execution/addTestsToCycle/";

        JiraConfig.getJIRARequestSpec()
                .given()
                    .contentType(APPLICATION_JSON)
                    .body(addToCycleEntity)
                .expect()
                    .statusCode(200)
                .when()
                    .post(endpoint);

        logger.info(String.format("Zephyr Tests added to Cycle: %s -> %s",
                addToCycleEntity.getIssues().toString(),
                addToCycleEntity.getCycleId()));
    }


}
