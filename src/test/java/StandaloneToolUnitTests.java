import com.frameworkium.jira.properties.Property;
import com.frameworkium.jira.JiraConfig;
import com.frameworkium.jira.standalone.StandaloneTool;
import com.frameworkium.jira.standalone.ZephyrTestObject;
import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class StandaloneToolUnitTests {

    @Test(groups = "zapi")
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

    @Test
    public void canReadACsvWithAcommentContainingACommaInDoubleQuotes(){
        String csv ="src/test/resources/csv/commentWithCommaAndDoubleQuotes.csv";
        String expectedComment = "comment with comma, and double quotes";

        StandaloneTool tool = new StandaloneTool(csv);
        List<ZephyrTestObject> list = tool.collectTests();

        System.out.println(list.get(0).getAttachment()[0]);
        Assert.assertEquals(list.size(),1);
        Assert.assertEquals(list.get(0).getComment(),expectedComment);

    }

    @Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
    public void CsvWithNoAttachmentAndLastCommaMissingWillGiveArrayIndexOutOfBoundsException(){

        String csv ="src/test/resources/csv/missingLastCommaNoAttachment.csv";

        StandaloneTool tool = new StandaloneTool(csv);
        tool.collectTests();

    }

    @Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
    public void lineWithCommentWithMultipleCommaInDoubleQuotesWorksAndMissingTrailingCommaBeforeBlankAttachmentWillGiveArrayIndexOutOfBoundsException(){
        String csv ="src/test/resources/csv/commentWithMultipleCommaDoubleQuotesAndMissingAttachmentComma.csv";

        StandaloneTool tool = new StandaloneTool(csv);
        tool.collectTests();
    }

    String doubleQuote = "\"";

    private String[] runHandleCommaInCommentMethod(String line){
        StandaloneTool standaloneTool = new StandaloneTool("");
        Method method = null;
        try {
            method = StandaloneTool.class.getDeclaredMethod("handleCommaInComment",String.class);
            method.setAccessible(true);
            return (String[]) method.invoke(standaloneTool, line);


        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Test
    public void lineWithCommentWithSingleCommaInDoubleQuotesWorks(){
        String line = String.format("T-1,pass,%scomment with, comma%s,attach", doubleQuote,doubleQuote);

        try {
            String[] result = runHandleCommaInCommentMethod(line);
            Assert.assertEquals(result[0],"comment with, comma");
            Assert.assertEquals(result[1],"attach");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Test
    public void lineWithCommentWithMultipleCommaInDoubleQuotesWorks(){
        String line = String.format("T-1,pass,%scomment, with, multiple comma%s,attach", doubleQuote,doubleQuote);

        try {
            String[] result = runHandleCommaInCommentMethod(line);
            Assert.assertEquals(result[0],"comment, with, multiple comma");
            Assert.assertEquals(result[1],"attach");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Test
    public void lineWithCommentWithMultipleCommaInDoubleQuotesWorksAndBlankAttachmentWorks(){
        String line = String.format("T-1,pass,%scomment, with, multiple comma%s,", doubleQuote,doubleQuote);

        try {
            String[] result = runHandleCommaInCommentMethod(line);
            Assert.assertEquals(result[0],"comment, with, multiple comma");
            Assert.assertEquals(result[1],"");
            Assert.assertNotNull(result[1]);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Test
    public void lineWithCommentWithNoCommaInDoubleQuotesWorksAndBlankAttachmentWorks(){
        String line = String.format("T-1,pass,%scomment, with, multiple comma%s,", doubleQuote,doubleQuote);

        try {
            String[] result = runHandleCommaInCommentMethod(line);
            Assert.assertEquals(result[0],"comment, with, multiple comma");
            Assert.assertEquals(result[1],"");
            Assert.assertNotNull(result[1]);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Test
    public void lineWithMultipleCommasGetsCorrectValuesForAllFieldsInCSV(){
        String csv = "src/test/resources/csv/fullLineMultipleCommasInComment.csv";

        StandaloneTool tool = new StandaloneTool(csv);
        List<ZephyrTestObject> list = tool.collectTests();

        Assert.assertEquals(list.get(0).getKey(),"TP-1");
        Assert.assertEquals(list.get(0).getStatus(),1);
        Assert.assertEquals(list.get(0).getComment(),"comma, in comment");
        Assert.assertEquals(list.get(0).getAttachment()[0],"path/to/attach.txt");

    }


    @Test
    public void lineWithNoCommasInCommentGetsCorrectValuesForAllFieldsInCSV(){
        String csv = "src/test/resources/csv/fullLineNoCommasInComment.csv";
        StandaloneTool tool = new StandaloneTool(csv);
        List<ZephyrTestObject> list = tool.collectTests();

        Assert.assertEquals(list.get(0).getKey(),"TP-1");
        Assert.assertEquals(list.get(0).getStatus(),1);
        Assert.assertEquals(list.get(0).getComment(),"\"no comma in comment\"");
        Assert.assertEquals(list.get(0).getAttachment()[0],"path/to/attach.txt");

    }

//todo test for single double quote in comment returns csv error
    //todo test no double quotes with commas fails

}
