package glue;

import com.frameworkium.jira.FileUtils;
import com.frameworkium.jira.gherkin.FeatureParser;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.testng.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class GherkinParserSteps {

    public static final String FEATURE_DIR = "src/test/resources/gherkinParser/features/";

    private String featurePath;

    public static void main(String[] args) {
        Optional<String> s = Optional.empty();
        System.out.println(s.isPresent());
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

    @Then("^a new zephyr test will be created$")
    public void aNewZephyrTestWillBeCreated() throws Throwable {
        // todo when zephyr functionality implemented
    }


    @And("^the scenario will be successfully updated and match \"([^\"]*)\"$")
    public void theScenarioWillBeSuccessfullyUpdatedAndMatch(String relPath) throws Throwable {
        String expectedPath = "src/test/resources/" + relPath;


        String expectedFeature = FileUtils.readFile(expectedPath);
        String actualFeature =  FileUtils.readFile(featurePath);
    //todo need to match regex? of get zID and parse that in to assert against?
        Assert.assertEquals(actualFeature,expectedFeature);
    }


}
