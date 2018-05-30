package glue;

import com.frameworkium.jira.FileUtils;
import com.frameworkium.jira.JiraConfig;
import com.frameworkium.jira.gherkin.FeatureParser;
import com.frameworkium.jira.zapi.Execution;
import com.frameworkium.jira.zapi.cycle.AddToCycleEntity;
import com.frameworkium.jira.zapi.cycle.Cycle;
import com.frameworkium.jira.zapi.cycle.CycleEntity;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.testng.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GherkinParserSteps {

    public static final String FEATURE_DIR = "src/test/resources/gherkinParser/features/";

    private String featurePath;
    private int cycleId;

    public static void main(String[] args) {
        Optional<String> s = Optional.of("aaa");
        System.out.println(s.toString());
        System.out.println(String.valueOf(s));
        System.out.println(s.get());
    }

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
    public void iHaveAScenarioThatDoesNOTContainAZephyrTag() throws Throwable {
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
    public void iParseTheFeatureFile() throws Throwable {
        new FeatureParser(featurePath).syncWithZephyr();
    }

    @When("^I parse the feature file without connecting to zephyr$")
    public void iParseTheFeatureFileWithoutConnectingToZephyr() throws Throwable {
        String zId = "TP-12345";
        FeatureParser parser = new FeatureParser(featurePath);

        parser.getPickles().stream()
                .filter(pickle1 -> !parser.pickleHasZephyrTag(pickle1))
                .forEach(pickle -> {
                    parser.addTagsToScenario(pickle, zId);
                });

    }

    @Then("^a new zephyr test will be created$")
    public void aNewZephyrTestWillBeCreated() throws Throwable {
        // todo when zephyr functionality implemented
    }


    @And("^the feature will be successfully updated and match \"([^\"]*)\"$")
    public void theScenarioWillBeSuccessfullyUpdatedAndMatch(String relPath) throws Throwable {
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
    public void iDoNotHaveAZephyrTestCycleSetup() throws Throwable {
        //here for readability
    }

    @Then("^a new test in zephyr will be created for the test not in zephyr$")
    public void aNewTestInZephyrWillBeCreatedForTheTestNotInZephyr() throws Throwable {
       FeatureParser featureParser = new FeatureParser(featurePath);
       featureParser.syncWithZephyr();
    }

    private String projectId;
    private String versionId;

    @And("^a new zephyr cycle will be created$")
    public void aNewZephyrCycleWillBeCreated() throws Throwable {
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
    public void theTestsWillBeAddedToTheNewCycle() throws Throwable {
        FeatureParser parser = new FeatureParser(featurePath);
        List<String> zephyrIds = parser.getPickles().stream()
                            .filter(parser::pickleHasZephyrTag)
                            .map(parser::getZephyrId)
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
    public void theTestsWillBeUpdatedForTheExecution() throws Throwable {
        FeatureParser parser = new FeatureParser(featurePath);
        parser.getPickles()
                .stream()
                .filter(parser::pickleHasZephyrTag)
                .map(parser::getZephyrId)
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