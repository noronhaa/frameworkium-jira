import com.frameworkium.jira.properties.Property;
import com.frameworkium.jira.JiraConfig;
import com.frameworkium.jira.standalone.StandaloneTool;
import com.frameworkium.jira.standalone.ZephyrTestObject;
import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class StandaloneToolUnitTests {

    @Test
    public void testJiraConnection(){
        int baeactual3 = RestAssured.given()
                .baseUri(Property.JIRA_URL.getValue())
                .relaxedHTTPSValidation()
                .auth()
                .preemptive()
                .basic(
                        Property.JIRA_USERNAME.getValue(),
                        Property.JIRA_PASSWORD.getValue())
                .get("/rest/api/latest/issue/CSCYBER-3053")
                .thenReturn()
                .statusCode();

        Assert.assertEquals(baeactual3,200);


        int baeactual4 = JiraConfig.getJIRARequestSpec()
                .get("/rest/api/latest/issue/CSCYBER-3053")
                .thenReturn()
                .statusCode();

        Assert.assertEquals(baeactual4,200);





        int status = JiraConfig.getJIRARequestSpec()
                .get(JiraConfig.JIRA_REST_PATH + "issue/" + "CSCYBER-3053")
                .thenReturn()
                .statusCode();

        Assert.assertTrue(String.valueOf(status).startsWith("2"),
                "ticket " + "CSCYBER-3053" + " not found - " + status);
    }



    @Test
    public void csvEntryWithNoAttachmentWillNotGiveError(){
        String csv = "src/test/resources/csv/noAttachment.csv";

        StandaloneTool tool = new StandaloneTool(csv);
        List<ZephyrTestObject> list = tool.collectTests();

        Assert.assertTrue(list.get(0).getAttachment()[0].isEmpty());
        Assert.assertNotNull(list.get(0).getAttachment());
        Assert.assertEquals(list.size(), 1);
    }

    @Test
    public void csvEntryWithAttachmentAndNoAttachmentEntriesWillNotGiveError(){
        String csv = "src/test/resources/csv/attachmentsAndNoAttachments.csv";

        StandaloneTool tool = new StandaloneTool(csv);
        List<ZephyrTestObject> list = tool.collectTests();


        Assert.assertFalse(list.get(0).getAttachment()[0].isEmpty());

        Assert.assertTrue(list.get(1).getAttachment()[0].isEmpty());
        Assert.assertNotNull(list.get(1).getAttachment());

        Assert.assertFalse(list.get(2).getAttachment()[0].isEmpty());

        Assert.assertEquals(list.size(), 3);
    }

    @Test
    public void csvEntryWithBlankEntryForCommentWillNotGiveError() {
        String csv = "src/test/resources/csv/noComment.csv";

        StandaloneTool tool = new StandaloneTool(csv);
        List<ZephyrTestObject> list = tool.collectTests();

        Assert.assertTrue(list.get(0).getComment().isEmpty());
        Assert.assertNotNull(list.get(0).getComment());
        Assert.assertEquals(list.size(), 1);
    }


    //todo test this works with the api
    @Test(groups = "zapi")
    public void updatingTicketWithNoAttachmentWillNotGiveError(){
        String csv = "src/test/resources/csv/noAttachment.csv";

        StandaloneTool tool = new StandaloneTool(csv);
        tool.uploadResultsFromCsv();
    }

    @Test
    public void updateTicketWithMultipleAttachmentsWillNotGIveError(){
        String csv = "src/test/resources/csv/multipleAttachments.csv";
        String[] expected = {"src/test/resources/attachment2.txt", "src/test/resources/attachment.txt"};

        StandaloneTool tool = new StandaloneTool(csv);
        List<ZephyrTestObject> list = tool.collectTests();
        String[] actual = list.get(0).getAttachment();

        Assert.assertEquals(actual, expected);

    }

    @Test
    public void executionWithNoIdListWillNotThrowNullPointerException(){
        String csv = "src/test/resources/csv/simpledata.csv";

        StandaloneTool tool = new StandaloneTool(csv);
        tool.uploadResultsFromCsv();
    }

}
