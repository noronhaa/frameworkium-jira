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
    //todo create new cycle IF cycle doesn't exist??
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

        logger.info(String.format("Zephyr Cycle created: %s [ID:%s]",cycleEntity.getName(),cycleId));

        return cycleId;
    }

    public static void main(String[] args) {
//        String project = "16506";
//        System.out.println(new Cycle().cycleExists(project,"ARGON"));

        String s = "{\n" +
                "    \"-1\": {\n" +
                "        \"totalExecutions\": 0,\n" +
                "        \"endDate\": \"\",\n" +
                "        \"description\": \"\",\n" +
                "        \"totalExecuted\": 0,\n" +
                "        \"started\": \"\",\n" +
                "        \"versionName\": \"ARGON\",\n" +
                "        \"expand\": \"executionSummaries\",\n" +
                "        \"projectKey\": \"TP\",\n" +
                "        \"versionId\": 83930,\n" +
                "        \"environment\": \"\",\n" +
                "        \"build\": \"\",\n" +
                "        \"ended\": \"\",\n" +
                "        \"name\": \"Ad hoc\",\n" +
                "        \"modifiedBy\": \"\",\n" +
                "        \"projectId\": 16506,\n" +
                "        \"startDate\": \"\",\n" +
                "        \"executionSummaries\": {\n" +
                "            \"executionSummary\": [\n" +
                "                \n" +
                "            ]\n" +
                "        }\n" +
                "    },\n" +
                "    \"237\": {\n" +
                "        \"totalExecutions\": 1,\n" +
                "        \"endDate\": \"\",\n" +
                "        \"description\": \"dummy cycle to test jira + zephyr  + automation framework integration\",\n" +
                "        \"totalExecuted\": 1,\n" +
                "        \"started\": \"\",\n" +
                "        \"versionName\": \"ARGON\",\n" +
                "        \"expand\": \"executionSummaries\",\n" +
                "        \"projectKey\": \"TP\",\n" +
                "        \"versionId\": 83930,\n" +
                "        \"environment\": \"\",\n" +
                "        \"build\": \"\",\n" +
                "        \"createdBy\": \"ANoronha\",\n" +
                "        \"ended\": \"\",\n" +
                "        \"name\": \"dummy cycle\",\n" +
                "        \"modifiedBy\": \"anoronha\",\n" +
                "        \"projectId\": 16506,\n" +
                "        \"createdByDisplay\": \"Noronha, Ashley (UK Guildford)\",\n" +
                "        \"startDate\": \"\",\n" +
                "        \"executionSummaries\": {\n" +
                "            \"executionSummary\": [\n" +
                "                \n" +
                "            ]\n" +
                "        }\n" +
                "    },\n" +
                "    \"311\": {\n" +
                "        \"totalExecutions\": 314,\n" +
                "        \"endDate\": \"\",\n" +
                "        \"description\": \"Story tests\",\n" +
                "        \"totalExecuted\": 0,\n" +
                "        \"started\": \"\",\n" +
                "        \"versionName\": \"ARGON\",\n" +
                "        \"expand\": \"executionSummaries\",\n" +
                "        \"projectKey\": \"TP\",\n" +
                "        \"versionId\": 83930,\n" +
                "        \"environment\": \"\",\n" +
                "        \"build\": \"\",\n" +
                "        \"createdBy\": \"jabiraham\",\n" +
                "        \"ended\": \"\",\n" +
                "        \"name\": \"Story Tests (excluding Admin App)\",\n" +
                "        \"modifiedBy\": \"jabiraham\",\n" +
                "        \"projectId\": 16506,\n" +
                "        \"createdByDisplay\": \"jabiraham\",\n" +
                "        \"startDate\": \"\",\n" +
                "        \"executionSummaries\": {\n" +
                "            \"executionSummary\": [\n" +
                "                \n" +
                "            ]\n" +
                "        }\n" +
                "    },\n" +
                "    \"3006\": {\n" +
                "        \"totalExecutions\": 0,\n" +
                "        \"endDate\": \"\",\n" +
                "        \"description\": \"\",\n" +
                "        \"totalExecuted\": 0,\n" +
                "        \"started\": \"\",\n" +
                "        \"versionName\": \"ARGON\",\n" +
                "        \"expand\": \"executionSummaries\",\n" +
                "        \"projectKey\": \"TP\",\n" +
                "        \"versionId\": 83930,\n" +
                "        \"environment\": \"\",\n" +
                "        \"build\": \"\",\n" +
                "        \"createdBy\": \"ANoronha\",\n" +
                "        \"ended\": \"\",\n" +
                "        \"name\": \"E2E Automation Cycle\",\n" +
                "        \"modifiedBy\": \"anoronha\",\n" +
                "        \"projectId\": 16506,\n" +
                "        \"createdByDisplay\": \"Noronha, Ashley (UK Guildford)\",\n" +
                "        \"startDate\": \"\",\n" +
                "        \"executionSummaries\": {\n" +
                "            \"executionSummary\": [\n" +
                "                \n" +
                "            ]\n" +
                "        }\n" +
                "    },\n" +
                "    \"3007\": {\n" +
                "        \"totalExecutions\": 0,\n" +
                "        \"endDate\": \"\",\n" +
                "        \"description\": \"\",\n" +
                "        \"totalExecuted\": 0,\n" +
                "        \"started\": \"\",\n" +
                "        \"versionName\": \"ARGON\",\n" +
                "        \"expand\": \"executionSummaries\",\n" +
                "        \"projectKey\": \"TP\",\n" +
                "        \"versionId\": 83930,\n" +
                "        \"environment\": \"\",\n" +
                "        \"build\": \"\",\n" +
                "        \"createdBy\": \"ANoronha\",\n" +
                "        \"ended\": \"\",\n" +
                "        \"name\": \"E2E Automation Cycle\",\n" +
                "        \"modifiedBy\": \"anoronha\",\n" +
                "        \"projectId\": 16506,\n" +
                "        \"createdByDisplay\": \"Noronha, Ashley (UK Guildford)\",\n" +
                "        \"startDate\": \"\",\n" +
                "        \"executionSummaries\": {\n" +
                "            \"executionSummary\": [\n" +
                "                \n" +
                "            ]\n" +
                "        }\n" +
                "    },\n" +
                "    \"3008\": {\n" +
                "        \"totalExecutions\": 0,\n" +
                "        \"endDate\": \"\",\n" +
                "        \"description\": \"\",\n" +
                "        \"totalExecuted\": 0,\n" +
                "        \"started\": \"\",\n" +
                "        \"versionName\": \"ARGON\",\n" +
                "        \"expand\": \"executionSummaries\",\n" +
                "        \"projectKey\": \"TP\",\n" +
                "        \"versionId\": 83930,\n" +
                "        \"environment\": \"\",\n" +
                "        \"build\": \"\",\n" +
                "        \"createdBy\": \"ANoronha\",\n" +
                "        \"ended\": \"\",\n" +
                "        \"name\": \"E2E Automation Cycle\",\n" +
                "        \"modifiedBy\": \"anoronha\",\n" +
                "        \"projectId\": 16506,\n" +
                "        \"createdByDisplay\": \"Noronha, Ashley (UK Guildford)\",\n" +
                "        \"startDate\": \"\",\n" +
                "        \"executionSummaries\": {\n" +
                "            \"executionSummary\": [\n" +
                "                \n" +
                "            ]\n" +
                "        }\n" +
                "    },\n" +
                "    \"3009\": {\n" +
                "        \"totalExecutions\": 0,\n" +
                "        \"endDate\": \"\",\n" +
                "        \"description\": \"\",\n" +
                "        \"totalExecuted\": 0,\n" +
                "        \"started\": \"\",\n" +
                "        \"versionName\": \"ARGON\",\n" +
                "        \"expand\": \"executionSummaries\",\n" +
                "        \"projectKey\": \"TP\",\n" +
                "        \"versionId\": 83930,\n" +
                "        \"environment\": \"\",\n" +
                "        \"build\": \"\",\n" +
                "        \"createdBy\": \"ANoronha\",\n" +
                "        \"ended\": \"\",\n" +
                "        \"name\": \"E2E Automation Cycle\",\n" +
                "        \"modifiedBy\": \"anoronha\",\n" +
                "        \"projectId\": 16506,\n" +
                "        \"createdByDisplay\": \"Noronha, Ashley (UK Guildford)\",\n" +
                "        \"startDate\": \"\",\n" +
                "        \"executionSummaries\": {\n" +
                "            \"executionSummary\": [\n" +
                "                \n" +
                "            ]\n" +
                "        }\n" +
                "    },\n" +
                "    \"3010\": {\n" +
                "        \"totalExecutions\": 0,\n" +
                "        \"endDate\": \"\",\n" +
                "        \"description\": \"\",\n" +
                "        \"totalExecuted\": 0,\n" +
                "        \"started\": \"\",\n" +
                "        \"versionName\": \"ARGON\",\n" +
                "        \"expand\": \"executionSummaries\",\n" +
                "        \"projectKey\": \"TP\",\n" +
                "        \"versionId\": 83930,\n" +
                "        \"environment\": \"\",\n" +
                "        \"build\": \"\",\n" +
                "        \"createdBy\": \"ANoronha\",\n" +
                "        \"ended\": \"\",\n" +
                "        \"name\": \"E2E Automation Cycle\",\n" +
                "        \"modifiedBy\": \"anoronha\",\n" +
                "        \"projectId\": 16506,\n" +
                "        \"createdByDisplay\": \"Noronha, Ashley (UK Guildford)\",\n" +
                "        \"startDate\": \"\",\n" +
                "        \"executionSummaries\": {\n" +
                "            \"executionSummary\": [\n" +
                "                \n" +
                "            ]\n" +
                "        }\n" +
                "    },\n" +
                "    \"3012\": {\n" +
                "        \"totalExecutions\": 3,\n" +
                "        \"endDate\": \"\",\n" +
                "        \"description\": \"\",\n" +
                "        \"totalExecuted\": 1,\n" +
                "        \"started\": \"\",\n" +
                "        \"versionName\": \"ARGON\",\n" +
                "        \"expand\": \"executionSummaries\",\n" +
                "        \"projectKey\": \"TP\",\n" +
                "        \"versionId\": 83930,\n" +
                "        \"environment\": \"\",\n" +
                "        \"build\": \"\",\n" +
                "        \"createdBy\": \"ANoronha\",\n" +
                "        \"ended\": \"\",\n" +
                "        \"name\": \"E2E Automation Cycle\",\n" +
                "        \"modifiedBy\": \"anoronha\",\n" +
                "        \"projectId\": 16506,\n" +
                "        \"createdByDisplay\": \"Noronha, Ashley (UK Guildford)\",\n" +
                "        \"startDate\": \"\",\n" +
                "        \"executionSummaries\": {\n" +
                "            \"executionSummary\": [\n" +
                "                \n" +
                "            ]\n" +
                "        }\n" +
                "    },\n" +
                "    \"3013\": {\n" +
                "        \"totalExecutions\": 3,\n" +
                "        \"endDate\": \"\",\n" +
                "        \"description\": \"\",\n" +
                "        \"totalExecuted\": 3,\n" +
                "        \"started\": \"\",\n" +
                "        \"versionName\": \"ARGON\",\n" +
                "        \"expand\": \"executionSummaries\",\n" +
                "        \"projectKey\": \"TP\",\n" +
                "        \"versionId\": 83930,\n" +
                "        \"environment\": \"\",\n" +
                "        \"build\": \"\",\n" +
                "        \"createdBy\": \"ANoronha\",\n" +
                "        \"ended\": \"\",\n" +
                "        \"name\": \"E2E Automation Cycle\",\n" +
                "        \"modifiedBy\": \"anoronha\",\n" +
                "        \"projectId\": 16506,\n" +
                "        \"createdByDisplay\": \"Noronha, Ashley (UK Guildford)\",\n" +
                "        \"startDate\": \"\",\n" +
                "        \"executionSummaries\": {\n" +
                "            \"executionSummary\": [\n" +
                "                \n" +
                "            ]\n" +
                "        }\n" +
                "    },\n" +
                "    \"3017\": {\n" +
                "        \"totalExecutions\": 3,\n" +
                "        \"endDate\": \"\",\n" +
                "        \"description\": \"\",\n" +
                "        \"totalExecuted\": 0,\n" +
                "        \"started\": \"\",\n" +
                "        \"versionName\": \"ARGON\",\n" +
                "        \"expand\": \"executionSummaries\",\n" +
                "        \"projectKey\": \"TP\",\n" +
                "        \"versionId\": 83930,\n" +
                "        \"environment\": \"\",\n" +
                "        \"build\": \"\",\n" +
                "        \"createdBy\": \"automationexecution\",\n" +
                "        \"ended\": \"\",\n" +
                "        \"name\": \"E2E Automation Cycle\",\n" +
                "        \"modifiedBy\": \"automationexecution\",\n" +
                "        \"projectId\": 16506,\n" +
                "        \"createdByDisplay\": \"Cyberreveal Automation\",\n" +
                "        \"startDate\": \"\",\n" +
                "        \"executionSummaries\": {\n" +
                "            \"executionSummary\": [\n" +
                "                \n" +
                "            ]\n" +
                "        }\n" +
                "    },\n" +
                "    \"3081\": {\n" +
                "        \"totalExecutions\": 0,\n" +
                "        \"endDate\": \"\",\n" +
                "        \"description\": \"\",\n" +
                "        \"totalExecuted\": 0,\n" +
                "        \"started\": \"\",\n" +
                "        \"versionName\": \"ARGON\",\n" +
                "        \"expand\": \"executionSummaries\",\n" +
                "        \"projectKey\": \"TP\",\n" +
                "        \"versionId\": 83930,\n" +
                "        \"environment\": \"\",\n" +
                "        \"build\": \"\",\n" +
                "        \"createdBy\": \"automationexecution\",\n" +
                "        \"ended\": \"\",\n" +
                "        \"name\": \"Auto Test Cycle 1\",\n" +
                "        \"modifiedBy\": \"automationexecution\",\n" +
                "        \"projectId\": 16506,\n" +
                "        \"createdByDisplay\": \"Cyberreveal Automation\",\n" +
                "        \"startDate\": \"\",\n" +
                "        \"executionSummaries\": {\n" +
                "            \"executionSummary\": [\n" +
                "                \n" +
                "            ]\n" +
                "        }\n" +
                "    },\n" +
                "    \"3078\": {\n" +
                "        \"totalExecutions\": 0,\n" +
                "        \"endDate\": \"\",\n" +
                "        \"description\": \"\",\n" +
                "        \"totalExecuted\": 0,\n" +
                "        \"started\": \"\",\n" +
                "        \"versionName\": \"ARGON\",\n" +
                "        \"expand\": \"executionSummaries\",\n" +
                "        \"projectKey\": \"TP\",\n" +
                "        \"versionId\": 83930,\n" +
                "        \"environment\": \"\",\n" +
                "        \"build\": \"\",\n" +
                "        \"createdBy\": \"automationexecution\",\n" +
                "        \"ended\": \"\",\n" +
                "        \"name\": \"Auto Test Cycle 1\",\n" +
                "        \"modifiedBy\": \"automationexecution\",\n" +
                "        \"projectId\": 16506,\n" +
                "        \"createdByDisplay\": \"Cyberreveal Automation\",\n" +
                "        \"startDate\": \"\",\n" +
                "        \"executionSummaries\": {\n" +
                "            \"executionSummary\": [\n" +
                "                \n" +
                "            ]\n" +
                "        }\n" +
                "    },\n" +
                "    \"recordsCount\": 13\n" +
                "}";

                String s2 = "{\n" +
                        "    \"-1\": {\n" +
                        "        \"totalExecutions\": 0,\n" +
                        "        \"endDate\": \"\",\n" +
                        "        \"description\": \"\",\n" +
                        "        \"totalExecuted\": 0,\n" +
                        "        \"started\": \"\",\n" +
                        "        \"versionName\": \"ARGON\",\n" +
                        "        \"expand\": \"executionSummaries\",\n" +
                        "        \"projectKey\": \"AMP\",\n" +
                        "        \"versionId\": 83930,\n" +
                        "        \"environment\": \"\",\n" +
                        "        \"build\": \"\",\n" +
                        "        \"ended\": \"\",\n" +
                        "        \"name\": \"Ad hoc\",\n" +
                        "        \"modifiedBy\": \"\",\n" +
                        "        \"projectId\": 27392,\n" +
                        "        \"startDate\": \"\",\n" +
                        "        \"executionSummaries\": {\n" +
                        "            \"executionSummary\": []\n" +
                        "        }\n" +
                        "    },\n" +
                        "    \"recordsCount\": 1\n" +
                        "}";


                String match = "E2E Automation Cycle";

                HashMap<String, Object> result = JsonPath.from(s2).get();
                int cycleId = -2; // -1 reserved for adhock cycleId

                Optional<String> cycleIdIfFound = result.entrySet()
                                    .stream()
                        .filter(e -> !e.getKey().equals("recordsCount"))
                        .filter(e -> ((HashMap<String,String>) e.getValue()).get("name").equals(match))
                        .map(Map.Entry::getKey)
                        .findFirst();

                if (cycleIdIfFound.isPresent()){
                    cycleId = Integer.valueOf(cycleIdIfFound.get());
                }

                System.out.println(cycleId);



//        List<String> result = jsonPath.getList("findAll { it.name == 'Auto Test Cycle 1'}.versionName");
//        Boolean result = jsonPath.get("$..['name']");
//        String result = jsonPath.getString("237.name");
//        System.out.println(result.size());

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
        int cycleId = -2; // -1 reserved for adhock cycleId

        String endpoint = String.format("%s%s?projectId=%s&versionId=%s",
                REST_ZAPI_PATH,CYCLE_ENDPOINT,projectId,versionId);

        logger.debug("endpoint: "+ Property.JIRA_URL.getValue() + endpoint);

        HashMap<String, Object> result =
                JiraConfig.getJIRARequestSpec()
                    .given()
                        .contentType(APPLICATION_JSON)
                    .expect()
                        .statusCode(200)
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



//        String endpoint = JiraConfig.REST_ZAPI_PATH + CYCLE_ENDPOINT + "/cyclesByVersionsAndSprint";


//        int projectIdInt = Integer.valueOf(projectId);
//
//        JSONObject object = new JSONObject();
//        object.put("expand","executionSummaries");
//        object.put("offset",0);
//        object.put("projectId",projectIdInt);
//        object.put("versionId",versionId);
//        object.put("sprintId",-1);
//
//        System.out.println(object.toString());
//
//        JsonPath path = JiraConfig.getJIRARequestSpec()
//                .given()
//                    .contentType(APPLICATION_JSON)
//                    .body(object.toString())
////                .expect()
////                    .statusCode(200)
//                .when()
//                    .post(endpoint)
//                .prettyPeek()
//                .thenReturn().jsonPath();

        //todo do something cleve with jsonpath to find cycle
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
                addToCycleEntity.getIssues().toString(),
                addToCycleEntity.getCycleId()));
    }


}
