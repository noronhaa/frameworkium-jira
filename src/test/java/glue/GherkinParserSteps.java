package glue;

import com.frameworkium.jira.FileUtils;
import com.frameworkium.jira.JiraConfig;
import com.frameworkium.jira.gherkin.FeatureParser;
import com.frameworkium.jira.gherkin.GherkinUtils;
import com.frameworkium.jira.zapi.Execution;
import com.frameworkium.jira.zapi.cycle.AddToCycleEntity;
import com.frameworkium.jira.zapi.cycle.Cycle;
import com.frameworkium.jira.zapi.cycle.CycleEntity;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import gherkin.pickles.Pickle;
import org.testng.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GherkinParserSteps {

    public static final String FEATURE_DIR = "src/test/resources/gherkinParser/features/";
    GherkinUtils gherkinUtils = new GherkinUtils();

    private String featurePath;
    private int cycleId;

    /**
     * Make copy of a base feature which will be used as the feature under test keeping original intact
     * @param baseFile
     */
    private void setupTestFeature(String baseFile){
        String featureUnderTest = FEATURE_DIR + "featureUnderTest.feature";
        try {
            Files.write(Paths.get(featureUnderTest),FileUtils.readFile(baseFile).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        featurePath = featureUnderTest;

    }

    @Given("^I have a scenario that does NOT contain a zephyr tag$")
    public void iHaveAScenarioThatDoesNOTContainAZephyrTag() {
        String testFeature = "src/test/resources/gherkinParser/features/editFeature.feature";
        setupTestFeature(testFeature);
    }

    @Given("^I have a multiple scenarios that do NOT contain a zephyr tag$")
    public void iHaveAMultipleScenariosThatDoNOTContainAZephyrTag() throws Throwable {
        String baseFile = FEATURE_DIR + "multipleScenarios1Zid.feature";
        String featureUnderTest = FEATURE_DIR + "featureUnderTest.feature";
        Files.write(Paths.get(featureUnderTest),FileUtils.readFile(baseFile).getBytes());

        featurePath = featureUnderTest;
    }

    @When("^I parse the feature file$")
    public void iParseTheFeatureFile() {
        new FeatureParser(featurePath).syncTestsWithZephyr();
    }

    @When("^I parse the feature file without connecting to zephyr$")
    public void iParseTheFeatureFileWithoutConnectingToZephyr() {
        String zId = "TP-12345";
        FeatureParser parser = new FeatureParser(featurePath);

        parser.getPickles().stream()
                .filter(pickle1 -> !GherkinUtils.pickleHasZephyrTag(pickle1.getTags()))
                .forEach(pickle -> {
                    parser.addTagsToScenario(pickle, zId);
                });

    }

    @Then("^a new zephyr test will be created$")
    public void aNewZephyrTestWillBeCreated() {
        // todo when zephyr functionality implemented
    }


    @And("^the feature will be successfully updated and match \"([^\"]*)\"$")
    public void theScenarioWillBeSuccessfullyUpdatedAndMatch(String relPath) {
        String expectedPath = "src/test/resources/" + relPath;


        String expectedFeature = FileUtils.readFile(expectedPath);
        String actualFeature =  FileUtils.readFile(featurePath);
    //todo need to match regex? of get zID and parse that in to assert against?
        Assert.assertEquals(actualFeature,expectedFeature);
    }


    @Given("^I have a feature file with a mix of zephyr and non zephyr scenarios$")
    public void iHaveAFeatureFileWithAMixOfZephyrAndNonZephyrScenarios() throws Throwable {
        iHaveAMultipleScenariosThatDoNOTContainAZephyrTag();
    }

    @And("^I do not have a zephyr test cycle setup$")
    public void iDoNotHaveAZephyrTestCycleSetup() {
        //here for readability
    }

    @Then("^a new test in zephyr will be created for the test not in zephyr$")
    public void aNewTestInZephyrWillBeCreatedForTheTestNotInZephyr() {
       FeatureParser featureParser = new FeatureParser(featurePath);
       featureParser.syncTestsWithZephyr();
    }

    private String projectId;
    private String versionId;

    @And("^a new zephyr cycle will be created$")
    public void aNewZephyrCycleWillBeCreated() {
        Cycle cycle = new Cycle();

        projectId = cycle.getProjectIdByKey("TP");
        versionId = cycle.getVersionIdByName(projectId,"ARGON");

        CycleEntity cycleEntity = new CycleEntity("E2E Automation Cycle",projectId,versionId);

        this.cycleId = cycle.createNewCycle(cycleEntity);
        System.out.println(String.format("[Z Cycle Id: %s]",cycleId));
        System.out.println(String.format("[projectId Id: %s]",projectId));
        System.out.println(String.format("[version Id: %s]",versionId));
    }

    @And("^the tests will be added to the new cycle$")
    public void theTestsWillBeAddedToTheNewCycle() {
        FeatureParser parser = new FeatureParser(featurePath);
        List<String> zephyrIds = parser.getPickles().stream()
                            .map(Pickle::getTags)
                            .filter(GherkinUtils::pickleHasZephyrTag)
                            .map(GherkinUtils::getZephyrIdFromTags)
                            .map(Optional::get)
                            .collect(Collectors.toList());

        AddToCycleEntity addToCycleEntity = new AddToCycleEntity(
                String.valueOf(this.cycleId),
                zephyrIds,
                "1",
                projectId,
                Integer.valueOf(versionId));

        new Cycle().addTestsToCycle(addToCycleEntity);
    }

    @And("^the tests will be updated for the execution$")
    public void theTestsWillBeUpdatedForTheExecution() {
        FeatureParser parser = new FeatureParser(featurePath);
        parser.getPickles()
                .stream()
                .map(Pickle::getTags)
                .filter(GherkinUtils::pickleHasZephyrTag)
                .map(GherkinUtils::getZephyrIdFromTags)
                .filter(Optional::isPresent)
                .forEach(zephyrTest -> {
                                        System.out.println("trying to update zephyr test: " + zephyrTest);
                                        new Execution(zephyrTest.get())
                                                .update(JiraConfig.ZapiStatus.ZAPI_STATUS_PASS, "update by automation");
                        });
    }

//    @After
//    public void cleanup(){
//        new Cycle().deleteCycle(String.valueOf(cycleId));
//
//    }

}
