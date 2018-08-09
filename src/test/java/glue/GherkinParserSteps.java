package glue;

import com.frameworkium.jira.FileUtils;
import com.frameworkium.jira.JiraConfig;
import com.frameworkium.jira.gherkin.FeatureParser;
import com.frameworkium.jira.gherkin.GherkinUtils;
import com.frameworkium.jira.zapi.Execution;
import com.frameworkium.jira.zapi.cycle.*;
import cucumber.api.java.en.*;
import gherkin.pickles.Pickle;
import org.testng.Assert;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GherkinParserSteps {

    private static final Path FEATURE_DIR =
            Paths.get("src/test/resources/gherkinParser/features/");

    private Path featurePath;
    private int cycleId;

    /**
     * Make copy of a base feature which will be used as the feature under test keeping original intact
     */
    private void setupTestFeature(Path baseFile) {
        Path featureUnderTest = FEATURE_DIR.resolve("featureUnderTest.feature");
        try {
            Files.write(featureUnderTest, FileUtils.readFile(baseFile).getBytes());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        featurePath = featureUnderTest;
    }

    @Given("^I have a scenario that does NOT contain a zephyr tag$")
    public void iHaveAScenarioThatDoesNOTContainAZephyrTag() {
        Path testFeature = Paths.get("src/test/resources/gherkinParser/features/editFeature.feature");
        setupTestFeature(testFeature);
    }

    @Given("^I have a multiple scenarios that do NOT contain a zephyr tag$")
    public void iHaveAMultipleScenariosThatDoNOTContainAZephyrTag() throws Throwable {
        Path baseFile = FEATURE_DIR.resolve("multipleScenarios1Zid.feature");
        Path featureUnderTest = FEATURE_DIR.resolve("featureUnderTest.feature");
        Files.write(featureUnderTest, FileUtils.readFile(baseFile).getBytes());

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
                .filter(pickle -> !GherkinUtils.pickleHasZephyrTag(pickle.getTags()))
                .forEach(pickle -> parser.addTagsToScenario(pickle, zId));
    }

    @Then("^a new zephyr test will be created$")
    public void aNewZephyrTestWillBeCreated() {
        // todo when zephyr functionality implemented
    }

    @And("^the feature will be successfully updated and match \"([^\"]*)\"$")
    public void theScenarioWillBeSuccessfullyUpdatedAndMatch(String relPath) {
        Path expectedPath = Paths.get("src/test/resources/" + relPath);

        String expectedFeature = FileUtils.readFile(expectedPath);
        String actualFeature = FileUtils.readFile(featurePath);
        //todo need to match regex? of get zID and parse that in to assert against?
        Assert.assertEquals(actualFeature, expectedFeature);
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
        versionId = cycle.getVersionIdByName(projectId, "ARGON");

        CycleEntity cycleEntity = new CycleEntity();
        cycleEntity.name = "E2E Automation Cycle";
        cycleEntity.projectId = projectId;
        cycleEntity.versionId = versionId;

        this.cycleId = cycle.createNewCycle(cycleEntity);
        System.out.println(String.format("[Z Cycle Id: %s]", cycleId));
        System.out.println(String.format("[projectId Id: %s]", projectId));
        System.out.println(String.format("[version Id: %s]", versionId));
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

        AddToCycleEntity addToCycleEntity = new AddToCycleEntity();
        addToCycleEntity.cycleId = String.valueOf(this.cycleId);
        addToCycleEntity.issues = zephyrIds;
        addToCycleEntity.projectId = "1";
        addToCycleEntity.projectId = projectId;
        addToCycleEntity.versionId = Integer.valueOf(versionId);

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
