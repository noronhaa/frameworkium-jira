package com.frameworkium.jira.listeners;

import com.frameworkium.base.properties.Property;
import com.frameworkium.jira.JiraConfig;
import com.frameworkium.jira.api.NewIssue;
import com.frameworkium.jira.api.NewIssueBuilder;
import com.frameworkium.jira.gherkin.GherkinUtils;
import com.frameworkium.jira.properties.Validation;
import com.frameworkium.jira.zapi.Execution;
import com.frameworkium.jira.zapi.cycle.*;
import com.google.common.collect.ImmutableList;
import cucumber.api.Result;
import cucumber.api.TestStep;
import cucumber.api.event.*;
import cucumber.api.formatter.Formatter;
import gherkin.pickles.PickleStep;
import gherkin.pickles.PickleTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ZephyrCucumber3Jvm implements Formatter {

    private static final Logger logger = LogManager.getLogger();

    private int zephyrCycleId;
    private Cycle zephyrCycle;
    private String projectId;
    private String versionId;
    private String zephyrId;

    private static final String UPDATE_COMMENT = "Updated by frameworkium-jira";

    private EventHandler<TestCaseStarted> caseStartedHandler = this::handleTestCaseStarted;
    private EventHandler<TestCaseFinished> caseFinishedEventHandler = this::handleTestCaseFinished;
    private EventHandler<TestRunStarted> testRunStartedHandler = this::handleTestRunStarted;

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        //If there are properties missing don't bother registering the handlers - basically switch off listener
        if (Validation.allPropertiesPresent()) {
            publisher.registerHandlerFor(TestCaseStarted.class, caseStartedHandler);
            publisher.registerHandlerFor(TestCaseFinished.class, caseFinishedEventHandler);
            publisher.registerHandlerFor(TestRunStarted.class, testRunStartedHandler);
        }
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

        //Get tags from the scenario and see if there are any that match '@TestCaseId:' indicating a Zephyr test
        Optional<String> zephyrTag = GherkinUtils.getZephyrIdFromTags(tags);

        //todo DO WE WANT TO CREATE Z TESTS FROM LISTENER???? The commented out code will automatically create a Zephyr test for a test that is not tagged with a Z tag
//        if (!zephyrTag.isPresent()) {
//            //If there is no zephyr tag then there is no test in zephyr so we create one returning the Jira/Zephyr id
//            zephyrTag = Optional.of(createZephyrTest(event));
//
//            //find original scenario and update with new Zephyr Id in format @TestCaseId:<Zephyr Id>
//            String uri = event.testCase.getUri();
//            String scenarioName = event.testCase.getName();
//            new FeatureParser(uri).addTagsToScenario(scenarioName, zephyrTag.get());
//        }

        if (zephyrTag.isPresent()){
            //Add the current Zephyr test to the Zephyr Test Cycle
            addTestToZephyrCycle(zephyrTag.get());

            //Update the Zephyr Test Status to WIP as we are about to execute the test
            new Execution(zephyrTag.get()).update(
                    JiraConfig.ZapiStatus.ZAPI_STATUS_WIP, UPDATE_COMMENT);

            //set zephyr ID externally so we can hook onto it after test has run
            this.zephyrId = zephyrTag.get();
        }

    }


    /**
     * On TestCaseFinished:
     * - Get Zephyr Tag from Scenario
     * - Update result status
     * @param event
     */
    private void handleTestCaseFinished(TestCaseFinished event) {
        logger.info("TestCaseFinished event");
        String comment = UPDATE_COMMENT;

        if (this.zephyrId != null){
            //Add stack trace of test if status not pass
            if (!event.result.is(Result.Type.PASSED)){
                comment = comment + "\n" + event.result.getErrorMessage();
            }

            //Update Zephyr test case with result status
            new Execution(this.zephyrId).update(
                    getUpdateStatus(event), comment);
        }

        //reset the zephyr tag for next test run
        this.zephyrId = null;
    }




    //----Utility Methods----


    private void addTestToZephyrCycle(String zephyrTag) {
        AddToCycleEntity addToCycleEntity = new AddToCycleEntity();
        addToCycleEntity.cycleId = String.valueOf(zephyrCycleId);
        addToCycleEntity.issues = ImmutableList.of(zephyrTag);
        addToCycleEntity.cycleId = "1";
        addToCycleEntity.projectId = projectId;
        addToCycleEntity.versionId = Integer.valueOf(versionId);
        zephyrCycle.addTestsToCycle(addToCycleEntity);
    }

    private void createZephyrTestCycle(){
        zephyrCycle = new Cycle();

        String projectKey = Property.JIRA_PROJECT_KEY.getValue();
        String version = Property.RESULT_VERSION.getValue();
        String cycleName = Property.ZAPI_CYCLE_REGEX.getValue();

        projectId = zephyrCycle.getProjectIdByKey(projectKey);
        versionId = zephyrCycle.getVersionIdByName(projectId, version);

        //get cycle id if the cycle exists, return -2 if it does not exist
        int cycleExist = zephyrCycle.cycleExists(projectKey, version, cycleName);

        //if the cycle does not exist (-2) then create the Zephyr Test Cycle
        if (cycleExist == -2) {

            //create new cycle details object
            CycleEntity cycleEntity = new CycleEntity();
            cycleEntity.name = cycleName;
            cycleEntity.projectId = projectId;
            cycleEntity.versionId = versionId;

            //create new cycle returning cycle Id
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

        return new NewIssueBuilder()
                .setKey(projectKey)
                .setSummary(name)
                .setIssueType(NewIssue.IssueType.TEST)
                .setBddField(steps)
                .createNewIssue()
                .create();
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
