package com.frameworkium.jira.listeners;

import com.frameworkium.jira.JiraConfig;
import com.frameworkium.jira.gherkin.FeatureParser;
import com.frameworkium.jira.gherkin.GherkinUtils;
import com.frameworkium.jira.properties.Property;
import com.frameworkium.jira.zapi.Execution;
import com.frameworkium.jira.zapi.cycle.AddToCycleEntity;
import com.frameworkium.jira.zapi.cycle.Cycle;
import com.frameworkium.jira.zapi.cycle.CycleEntity;
import com.google.common.collect.ImmutableList;
import cucumber.api.*;
import cucumber.api.event.*;
import cucumber.api.formatter.Formatter;
import gherkin.pickles.PickleStep;
import gherkin.pickles.PickleTag;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

public class CukesListenerV2 implements Formatter {

    private static final String ZEPHYR_TAG_PREFIX = "@TestCaseId:";
    private GherkinUtils gherkinUtils = new GherkinUtils();
    private int zephyrCycleId;
    private Cycle zephyrCycle;
    private String projectId;
    private String versionId;


    public static final String UPDATE_COMMENT = "Updated by frameworkium-jira";



    private EventHandler<TestCaseStarted> caseStartedHandler = new EventHandler<TestCaseStarted>() {
        @Override
        public void receive(TestCaseStarted event) {
            handleTestCaseStarted(event);
        }
    };


    private EventHandler<TestCaseFinished> caseFinishedEventHandler = new EventHandler<TestCaseFinished>() {
        @Override
        public void receive(TestCaseFinished event) {
            handleTestCaseFinished(event);
        }
    };


    private EventHandler<TestRunStarted> testRunStartedHandler = new EventHandler<TestRunStarted>() {
        @Override
        public void receive(TestRunStarted event) {
            handleTestRunStarted(event);
        }
    };

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseStarted.class, caseStartedHandler);
        publisher.registerHandlerFor(TestCaseFinished.class, caseFinishedEventHandler);

    }

    private void handleTestRunStarted(TestRunStarted event) {
        createZephyrTestCycle();
    }


    private void handleTestCaseStarted(TestCaseStarted event) {
        List<PickleTag> tags = event.testCase.getTags();
        Optional<String> zephyrTag = gherkinUtils.getZephyrId(tags);

        if (!zephyrTag.isPresent()){
            zephyrTag = Optional.of(addTestToZephyr(event));
        }

        addTestToZephyrCycle(zephyrTag.get());

        new Execution(zephyrTag.get()).update(
                JiraConfig.ZapiStatus.ZAPI_STATUS_WIP, UPDATE_COMMENT);

    }


    private void handleTestCaseFinished(TestCaseFinished event) {
        List<PickleTag> tags = event.testCase.getTags();

        gherkinUtils.getZephyrId(tags)
                .ifPresent(issueId -> new Execution(issueId).update(
                        getUpdateStatus(event), UPDATE_COMMENT)
                );

    }




    private void addTestToZephyrCycle(String zephyrTag){
        AddToCycleEntity addToCycleEntity = new AddToCycleEntity(
                String.valueOf(zephyrCycleId),
                ImmutableList.of(zephyrTag),
                "1",
                projectId,
                Integer.valueOf(versionId)
        );

        zephyrCycle.addTestsToCycle(addToCycleEntity);
    }

    private void createZephyrTestCycle(){
        zephyrCycle = new Cycle();

            String projectKey = System.getProperty("PROJECTKEY PROPERTY"); //TODO add sys property enum for project key
            String version = Property.RESULT_VERSION.getValue();
            String cycleName = Property.ZAPI_CYCLE_REGEX.getValue();

            projectId = zephyrCycle.getProjectIdByKey(projectKey);
            versionId = zephyrCycle.getVersionIdByName(projectId, version);

            int cycleExist = zephyrCycle.cycleExists(projectKey,version);

            //get cycle id, -1 mean no cycle exists
            if (cycleExist != -1){
                //create new cycle
                CycleEntity cycleEntity = new CycleEntity(
                        cycleName,
                        projectId,
                        versionId);

                zephyrCycleId = zephyrCycle.createNewCycle(cycleEntity);
            } else {
                //assign id of existing cycle
                zephyrCycleId = cycleExist;
            }

    }


    private String addTestToZephyr(TestCaseStarted event){
        String name= event.testCase.getName();
        String steps = event.testCase.getTestSteps()
                .stream()
                .map(TestStep::getPickleStep)
                .map(PickleStep::getText)
                .map(step -> step + "\n")
                .collect(Collectors.joining(","))
                .replace(",","");

        return new FeatureParser().addTestToZephyr(name, steps);
    }


    private int getUpdateStatus(TestCaseFinished event){
        Result.Type resultStatus = event.result.getStatus();
        int updateStatus = 0;
        switch (resultStatus){
            case PASSED : updateStatus = JiraConfig.ZapiStatus.ZAPI_STATUS_PASS;
                break;
            case SKIPPED: updateStatus = JiraConfig.ZapiStatus.ZAPI_STATUS_BLOCKED;
                break;
            case PENDING: updateStatus = JiraConfig.ZapiStatus.ZAPI_STATUS_WIP;
                break;
            case UNDEFINED: updateStatus = JiraConfig.ZapiStatus.ZAPI_STATUS_BLOCKED;
                break;
            case AMBIGUOUS: updateStatus = JiraConfig.ZapiStatus.ZAPI_STATUS_BLOCKED;
                break;
            case FAILED: updateStatus = JiraConfig.ZapiStatus.ZAPI_STATUS_FAIL;
                break;

        }

        return updateStatus;

    }

}
