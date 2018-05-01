import com.frameworkium.jira.standalone.StandaloneTool;
import com.frameworkium.jira.standalone.ZephyrTestObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class UnitTests {

//
//    @Test
//    public void csvEntryWithNoAttachmentWillNotGiveError(){
//        String csv = "src/test/resources/csv/noAttachment.csv";
//
//        StandaloneTool tool = new StandaloneTool(csv);
//        List<ZephyrTestObject> list = tool.uploadResultsFromCsv();
//
//        Assert.assertTrue(list.get(0).getAttachment()[0].isEmpty());
//        Assert.assertNotNull(list.get(0).getAttachment());
//        Assert.assertEquals(tool.uploadResultsFromCsv().size(), 1);
//    }
//
//    @Test
//    public void csvEntryWithAttachmentAndNoAttachmentEntriesWillNotGiveError(){
//        String csv = "src/test/resources/csv/attachmentsAndNoAttachments.csv";
//
//        StandaloneTool tool = new StandaloneTool(csv);
//        List<ZephyrTestObject> list = tool.uploadResultsFromCsv();
//
//
//        Assert.assertFalse(list.get(0).getAttachment()[0].isEmpty());
//
//        Assert.assertTrue(list.get(1).getAttachment()[0].isEmpty());
//        Assert.assertNotNull(list.get(1).getAttachment());
//
//        Assert.assertFalse(list.get(2).getAttachment()[0].isEmpty());
//
//        Assert.assertEquals(tool.uploadResultsFromCsv().size(), 3);
//    }
//
//    @Test
//    public void csvEntryWithBlankEntryForCommentWillNotGiveError() {
//        String csv = "src/test/resources/csv/noComment.csv";
//
//        StandaloneTool tool = new StandaloneTool(csv);
//        List<ZephyrTestObject> list = tool.uploadResultsFromCsv();
//
//        Assert.assertTrue(list.get(0).getComment().isEmpty());
//        Assert.assertNotNull(list.get(0).getComment());
//        Assert.assertEquals(tool.uploadResultsFromCsv().size(), 1);
//    }
//
//
//    //todo test this works with the api
//    @Test(groups = "zapi")
//    public void updatingTicketWithNoAttachmentWillNotGiveError(){
//        String csv = "src/test/resources/csv/noAttachment.csv";
//
//        StandaloneTool tool = new StandaloneTool(csv);
//        List<ZephyrTestObject> list = tool.uploadResultsFromCsv();
//        tool.updateTests(list);
//    }
//
//    @Test
//    public void updateTicketWithMultipleAttachmentsWillNotGIveError(){
//        String csv = "src/test/resources/csv/multipleAttachments.csv";
//        String[] expected = {"src/test/resources/attachment2.txt", "src/test/resources/attachment.txt"};
//
//        StandaloneTool tool = new StandaloneTool(csv);
//        List<ZephyrTestObject> list = tool.uploadResultsFromCsv();
//        String[] actual = list.get(0).getAttachment();
//
//        Assert.assertEquals(actual, expected);
//
//    }
//
//    @Test
//    public void executionWithNoIdListWillNotThrowNullPointerException(){
//        String csv = "src/test/resources/csv/simpledata.csv";
//
//        StandaloneTool tool = new StandaloneTool(csv);
//        List<ZephyrTestObject> list = tool.uploadResultsFromCsv();
//        tool.updateTests(list);
//    }
//


}
