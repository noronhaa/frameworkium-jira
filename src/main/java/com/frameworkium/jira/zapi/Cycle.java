package com.frameworkium.jira.zapi;

import com.frameworkium.jira.JiraConfig;
import com.frameworkium.jira.zapi.entities.CycleEntity;
import com.frameworkium.jira.zapi.entities.MoveExecutionsToCycleEntitiy;

public class Cycle {

    private final static String CYCLE_ENDPOINT = "cycle";


    public int createCycle(CycleEntity cycleEntity){
        return JiraConfig.getJIRARequestSpec()
                .given()
                    .body(cycleEntity)
                .expect()
                    .statusCode(200)
                .when()
                    .post(JiraConfig.REST_ZAPI_PATH + CYCLE_ENDPOINT)
                .thenReturn()
                .jsonPath().getInt("id");
    }

    /**
     * add Executions to a zephyr cycle.
     * @param cycyleID of the created or existing zephyr cycle
     * @param executions list of ids of the tests you want to add to the cycle
     * @return jobProgressToken to track progress of the job
     */
    public String addTestsToCycle(int cycyleID, String[] executions){
        String endpoint = CYCLE_ENDPOINT + "/" + cycyleID + "/move";
        MoveExecutionsToCycleEntitiy body = new MoveExecutionsToCycleEntitiy();
        body.setExecutions(executions);

        return JiraConfig.getJIRARequestSpec()
                .given()
                    .body(body)
                .expect()
                    .statusCode(200)
                .when()
                    .post(endpoint)
                .thenReturn()
                .jsonPath().getString("jobProgressToken");

    }

    public void pollJobStatus(String jobProgressToken){
        String endpoint  = "/execution/jobProgress/:jobProgressToken?type=reindex_job_progress)";
    }


}
