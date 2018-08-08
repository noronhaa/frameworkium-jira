package com.frameworkium.jira.zapi.cycle;

import com.frameworkium.base.properties.Property;
import com.frameworkium.jira.JiraConfig;
import io.restassured.path.json.JsonPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.frameworkium.jira.JiraConfig.REST_ZAPI_PATH;

public class Cycle {

    private static final Logger logger = LogManager.getLogger();

    private static final String CYCLE_ENDPOINT = "cycle";
    private static final String APPLICATION_JSON = "application/json";


    /**
     * create a new zephyr test cycle
     * @param cycleEntity build request body using CycleEntity object, required fields are versionId, sprintId, projectId, issueType
     * @return id of newly created cycle
     */
    public int createNewCycle(CycleEntity cycleEntity){
        int cycleId =  JiraConfig.getJIRARequestSpec()
                                .given()
                                    .contentType(APPLICATION_JSON)
                                    .body(cycleEntity)
                                .expect()
                                    .statusCode(200)
                                .when()
                                    .post(REST_ZAPI_PATH + CYCLE_ENDPOINT)
                                .thenReturn().jsonPath().getInt("id");

        logger.info(String.format("Zephyr Cycle created: %s [ID:%s]", cycleEntity.name, cycleId));

        return cycleId;
    }


    /**
     * Query ZAPI for list of cycles using projectId and version Id. Check results to find a match for cycleName, if no
     * result found return value of -2 to be handles elsewhere, otherwise return cycleId of the first match found
     * @param projectKey key of your project eg 'TP' from TP-12345
     * @param versionName version cycle is testing against
     * @param cycleName name of your cycle
     * @return cycleId of first found cycle or -2 for no matches
     */
    //todo unit tests
    public int cycleExists(String projectKey, String versionName, String cycleName){

        String projectId = getProjectIdByKey(projectKey);
        String versionId = getVersionIdByName(projectId, versionName);
        int cycleId = -2; // -1 reserved for adhoc cycleId

        String endpoint = String.format("%s%s?projectId=%s&versionId=%s",
                REST_ZAPI_PATH,CYCLE_ENDPOINT,projectId,versionId);

        logger.debug("endpoint: "+ Property.JIRA_URL.getValue() + endpoint);

        // TODO use DTOs not Object
        HashMap<String, Object> result =
                JiraConfig.getJIRARequestSpec()
                    .given()
                        .contentType(APPLICATION_JSON)
                    .expect()
                        .statusCode(200).log().ifError()
                    .when()
                        .get(endpoint)
                    .thenReturn()
                        .jsonPath().get();

        Optional<String> cycleIdIfFound = result.entrySet()
                .stream()
                .filter(e -> !e.getKey().equals("recordsCount"))
                .filter(e -> ((HashMap<String,String>) e.getValue()).get("name").equals(cycleName))
                .map(Map.Entry::getKey)
                .findFirst();

        if (cycleIdIfFound.isPresent()){
            cycleId = Integer.valueOf(cycleIdIfFound.get());
            logger.info(String.format("Cycle Found [ID:%s] for Project=%s Version=%s Name=%s",
                    cycleId,projectKey,versionName,cycleName));
        } else {
            logger.info(String.format("Cycle NOT Found for Project=%s Version=%s Name=%s",
                    projectKey,versionName,cycleName));
        }

        return cycleId;
    }

    /**
     * Delete a cycle by its cycle ID
     * @param cycleId
     */
    public void deleteCycle(String cycleId){
        String endpoint = REST_ZAPI_PATH + CYCLE_ENDPOINT + "/" + cycleId;

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
        String endpoint = REST_ZAPI_PATH + "execution/addTestsToCycle/";

        JiraConfig.getJIRARequestSpec()
                .given()
                    .contentType(APPLICATION_JSON)
                    .body(addToCycleEntity)
                .expect()
                    .statusCode(200)
                .when()
                    .post(endpoint);

        logger.info(String.format("Zephyr Tests added to Cycle: %s -> %s",
                addToCycleEntity.issues.toString(),
                addToCycleEntity.cycleId));
    }


}
