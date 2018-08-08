import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;
import org.testng.annotations.Test;

@Test
@CucumberOptions(
        features = {"src/test/resources/features"},
        plugin = {"pretty"},
        monochrome = true,
        glue = {"glue"},
        tags = {"~@wip"})
//        tags = "@bug")
public class JiraZapiApiUnitTests extends AbstractTestNGCucumberTests {

}


