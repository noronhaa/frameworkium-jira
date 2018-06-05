package com.frameworkium.jira.listeners;

import com.frameworkium.base.properties.Property;
import com.frameworkium.jira.JiraConfig;
import com.frameworkium.jira.api.NewIssue;
import com.frameworkium.jira.gherkin.FeatureParser;
import com.frameworkium.jira.gherkin.GherkinUtils;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class CukesListenerV2 implements Formatter {

    private static final Logger logger = LogManager.getLogger();

    private static final String ZEPHYR_TAG_PREFIX = "@TestCaseId:";
    private GherkinUtils gherkinUtils = new GherkinUtils();
    private int zephyrCycleId;
    private Cycle zephyrCycle;
    private String projectId;
    private String versionId;


    public static final String UPDATE_COMMENT = "Updated by frameworkium-jira";
    public static final String CREATE_COMMENT = "Created by frameworkium-jira";

//todo properties validation?

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

    private EventHandler<TestSourceRead> testSourceReadHandler = new EventHandler<TestSourceRead>() {
        @Override
        public void receive(TestSourceRead event) {
            handleTestSourceRead(event);
        }
    };



    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseStarted.class, caseStartedHandler);
        publisher.registerHandlerFor(TestCaseFinished.class, caseFinishedEventHandler);
        publisher.registerHandlerFor(TestRunStarted.class, testRunStartedHandler);
        publisher.registerHandlerFor(TestSourceRead.class, testSourceReadHandler);
    }


    private void handleTestSourceRead(TestSourceRead event) {
        logger.info("TestSourceRead event");
        new FeatureParser(event.uri).syncTestsWithZephyr();
    }

    /**
     * When test run starts, check Zephyr cycle exists, if it doesn't, create a new cycle
     * @param event
     */
    private void handleTestRunStarted(TestRunStarted event) {
        createZephyrTestCycle();
    }


    /**
     * On TestCaseStarted:
     * - Look to see Scenario has a Zephyr tag
     * - If no Zephyr tag, create a new Zephyr Test
     * - Add (Zephyr) Test to Zephyr Test Cycle
     * - Update Test Execution Status to WIP
     * @param event
     */
    private void handleTestCaseStarted(TestCaseStarted event) {
        logger.info("TestCaseStarted event");
        List<PickleTag> tags = event.testCase.getTags();
        Optional<String> zephyrTag = GherkinUtils.getZephyrIdFromTags(tags);

        if (!zephyrTag.isPresent()) {
            zephyrTag = Optional.of(createZephyrTest(event));
        }

        addTestToZephyrCycle(zephyrTag.get());

        new Execution(zephyrTag.get()).update(
                JiraConfig.ZapiStatus.ZAPI_STATUS_WIP, UPDATE_COMMENT);

    }


    /**
     * On TestCaseFinished:
     * - Get Zephyr Tag from Scenario
     * - Update result status
     * @param event
     */
    private void handleTestCaseFinished(TestCaseFinished event) {
        logger.info("TestCaseFinished event");

        List<PickleTag> tags = event.testCase.getTags();

        String comment = UPDATE_COMMENT + "\n" + event.result.getErrorMessage();

        GherkinUtils.getZephyrIdFromTags(tags)
                .ifPresent(issueId -> new Execution(issueId).update(
                        getUpdateStatus(event), comment)
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

            String projectKey = Property.JIRA_PROJECT_KEY.getValue();
            String version = Property.RESULT_VERSION.getValue();
            String cycleName = Property.ZAPI_CYCLE_REGEX.getValue();

            projectId = zephyrCycle.getProjectIdByKey(projectKey);
            versionId = zephyrCycle.getVersionIdByName(projectId, version);

            int cycleExist = zephyrCycle.cycleExists(projectKey,version,cycleName);

            //get cycle id, -2 mean no cycle exists
            if (cycleExist == -2){

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

    private String createZephyrTest(TestCaseStarted event){
        String projectKey = Property.JIRA_PROJECT_KEY.getValue();
        String name= event.testCase.getName();
        String steps = event.testCase.getTestSteps()
                .stream()
                .map(TestStep::getPickleStep)
                .map(PickleStep::getText)
                .map(step -> step + "\n")
                .collect(Collectors.joining(","))
                .replace(",","");
        return new NewIssue(projectKey,name,CREATE_COMMENT,NewIssue.IssueType.TEST,steps).create();
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
