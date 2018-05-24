package com.frameworkium.jira.zapi;

import com.frameworkium.jira.JiraConfig;

public class Cycle {

    private static final String CYCLE_ENDPOINT = "cycle";
    private static final String APPLICATION_JSON = "application/json";


    /**
     * create a new zephyr test cycle
     * @param cycleEntity build request body using CycleEntity object, required fields are versionId, sprintId, projectId, name
     * @return id of newly created cycle
     */
    public int createNewCycle(CycleEntity cycleEntity){
        return JiraConfig.getJIRARequestSpec()
                .given()
                    .contentType(APPLICATION_JSON)
                    .body(cycleEntity)
                .expect()
                    .statusCode(200)
                .when()
                    .post(JiraConfig.REST_ZAPI_PATH + CYCLE_ENDPOINT)
                .thenReturn().jsonPath().getInt("id");
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
    public int getProjectIdByKey(String projectKey){
        String endpoint = JiraConfig.JIRA_REST_PATH + "project/" + projectKey;

        return JiraConfig.getJIRARequestSpec()
                .given()
                    .contentType(APPLICATION_JSON)
                .expect()
                    .statusCode(200)
                .when()
                    .get(endpoint)
                .andReturn().jsonPath().getInt("id");
    }



    /**
     *
     * @param projectID id of your project, returned by getProjectIdByKey()
     * @param versionName you wish to find the key of
     * return the key of a build/fix version that we may execute a test/cycle against
     */
    public String getVersionIdByName(int projectID, String versionName){
        String endpoint = String.format(JiraConfig.JIRA_REST_PATH  + "project/%s/versions", String.valueOf(projectID));
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
     *
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
    }


}
