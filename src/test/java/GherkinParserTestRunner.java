import cucumber.api.CucumberOptions;
import org.testng.annotations.Test;

@Test
@CucumberOptions(
        features = {"src/test/resources/features"},
        plugin = {"pretty"},
        monochrome = true,
        glue = {"glue"},
        tags = {"@gherkin"})
public class GherkinParserTestRunner {
}
