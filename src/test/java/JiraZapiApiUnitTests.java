import cucumber.api.CucumberOptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import cucumber.api.testng.AbstractTestNGCucumberTests;

@Test
@CucumberOptions(
        features = {"src/test/resources/"},
        plugin = {"pretty"},
        monochrome = true,
        glue = {"glue"},
        tags = {"@attachment", "~@bug"})
//        tags = "@bug")
public class JiraZapiApiUnitTests extends AbstractTestNGCucumberTests {

}


