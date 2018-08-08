package glue;

import com.frameworkium.jira.JiraConfig;
import com.frameworkium.jira.api.*;
import com.frameworkium.jira.zapi.Execution;
import com.frameworkium.jira.zapi.SearchExecutions;
import cucumber.api.java.After;
import cucumber.api.java.en.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseOptions;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class Steps {

    private Response response;
    private List<Response> responses;
    private String testCase = "CSCYBER-3072";
    private String ticket = "CSCYBER-3053";
    private String[] tickets = {"CSCYBER-3053", "CSCYBER-3054"};
    private static final String DESCRIPTION_TEXT = "Issue for testing Automation interaction with JIRA API ";

    private Issue issue = new Issue(tickets[1]);
    private List<String> attachmentIds = null;
    private SearchIssues searchIssues = null;
    private String summary = null;

    private static final String resultVersion = "MSS Automation";

//    @Before //for debugging
//    public void setupConnectionCredentials(){
//        System.out.println("before");
//        System.setProperty("jiraURL","");
//        System.setProperty("jiraUsername","");
//        System.setProperty("jiraPassword","");
//        System.setProperty("resultVersion",resultVersion);
//        System.setProperty("zapiCycleRegEx","dummy cycle");
//    }


    @After("@JiraTransition")
    public void resetJiraTransisiton() {
        JiraTest.transitionIssue(ticket, "Rejected");
    }


    @Given("^I have the details of '(\\d+)' JIRA ticket\\(s\\)$")
    public void iHaveTheDetailsOfJIRATicketS(int tickets) throws Throwable {
        Arrays.stream(this.tickets).forEach(t -> {
                    int status = JiraConfig.getJIRARequestSpec()
                            .get(JiraConfig.JIRA_REST_PATH + "issue/" + t)
                            .thenReturn()
                            .statusCode();

                    Assert.assertTrue(String.valueOf(status).startsWith("2"),
                            "ticket " + t + " not found - " + status);
                }
        );
    }

    @When("^I link the 2 tickets by '(.*)'$")
    public void iLinkTheTicketsByIsBlockedBy(String linkOption) throws Throwable {
        Issue issue = new Issue(tickets[0]);
        response = issue.linkIssues(linkOption, tickets[0], tickets[1]);
    }

    @Then("^I will get a 2XX response$")
    public void iWillGetA2XXResponse() throws Throwable {
        String statusCode = String.valueOf(response.statusCode());

        Assert.assertTrue(statusCode.startsWith("2"),
                response.statusLine() + "\n" + response.prettyPeek());
    }

    @Then("^all the responses be a 2XX$")
    public void allTheResponsesBeAXX() throws Throwable {
        this.responses.stream()
                .map(ResponseOptions::statusCode)
                .map(Object::toString)
                .forEach(r ->
                        Assert.assertTrue(r.startsWith("2"), r));
    }


    @When("^I add an attachment$")
    public void iAddAnAttachment() throws Throwable {
        response = issue.addAttachment(new File("src/test/resources/attachment.txt"));
    }

    @When("^I change the '(.*)' field$")
    public void iChangeTheDescriptionField(String field) throws Throwable {
        response = JiraTest.changeIssueFieldValue(ticket, field, DESCRIPTION_TEXT + RandomStringUtils.randomAscii(5));
    }

    @When("^I leave a comment on a Jira ticket$")
    public void iLeaveACommentOnAJiraTicket() throws Throwable {
        response = JiraTest.addComment(ticket, "test comment");
    }

    @When("^I change the ticket to '(.*)'$")
    public void iChangeTheTicketToReady(String transition) throws Throwable {
        response = JiraTest.transitionIssue(ticket, transition);
    }


    @And("^I can find the ticket by querying$")
    public void iCanFindTheTicketByQuerying() throws Throwable {
        searchIssues = new SearchIssues("key = " + ticket);
    }

    @Then("^I can get the keys for the Issue$")
    public void iCanGetTheKeysForTheIssue() throws Throwable {
        searchIssues.getKeys();
    }

    @And("^I can get the Summaries for the Issue$")
    public void iCanGetTheSummariesForTheIssue() throws Throwable {
        List<String> summaries = searchIssues.getSummaries();
        summary = summaries.get(0);
    }

    @And("^I can get the Key for a Summary$")
    public void iCanGetTheKeyForASummary() throws Throwable {
        searchIssues.getKeyForSummary(summary);
    }


    //ZAPHYR

    @Given("^I have zephyr details for a Test Case$")
    public void iHaveZephyrDetailsForATestCase() throws Throwable {
        int status = JiraConfig.getJIRARequestSpec()
                .get(JiraConfig.JIRA_REST_PATH + "issue/" + testCase)
                .thenReturn()
                .statusCode();

        Assert.assertTrue(String.valueOf(status).startsWith("2"),
                "testCase " + testCase + " not found - " + status);
    }

    @When("^I change the status to '(.*)'$")
    public void iChangeTheStatusToStatus(String status) throws Throwable {
        new Execution(testCase).update(parseStatus(status), "");

    }

    private int parseStatus(String status) {

        int status2 = 0;
        switch (status) {
            case "pass":
                status2 = JiraConfig.ZapiStatus.ZAPI_STATUS_PASS;
                break;
            case "fail":
                status2 = JiraConfig.ZapiStatus.ZAPI_STATUS_FAIL;
                break;
            case "blocked":
                status2 = JiraConfig.ZapiStatus.ZAPI_STATUS_BLOCKED;
                break;
            case "wip":
                status2 = JiraConfig.ZapiStatus.ZAPI_STATUS_WIP;
                break;
            default:
                throw new RuntimeException("status not found");

        }

        return status2;
    }


    @Then("^the status will be '(.*)'$")
    public void theStatusWillBeStatus(String status) throws Throwable {
        Assert.assertEquals(
                parseStatus(status),
                new Execution(testCase).getExecutionStatus()
        );
    }


    @When("^I add '(.*)' attachments$")
    public void iChangeTheStatusToFailAndLeaveAttachmentsAttachment(String attachments) throws Throwable {
        int attach = Integer.valueOf(attachments);
        String attachmentz[];
        if (attach == 1) {
            attachmentz = new String[]{"src/test/resources/attachment.txt"};
        } else if (attach == 2) {
            attachmentz = new String[]{"src/test/resources/attachment.txt", "src/test/resources/attachment2.txt"};
        } else {
            throw new RuntimeException("Haven't implemented functionality to add more that 2 attachments in this test");
        }

        Execution execution = new Execution(testCase);
        Field idListField = Execution.class.getDeclaredField("idList");
        idListField.setAccessible(true);
        List<Integer> idList = (List<Integer>) idListField.get(execution);

        Method method = Execution.class.getDeclaredMethod("addAttachments", Integer.class, String[].class);
        method.setAccessible(true);
        this.responses = (List<Response>) method.invoke(execution, idList.get(0), attachmentz);

    }


    @When("^I add delete attachments$")
    public void iAddDeleteAttachments() throws Throwable {
        Execution execution = new Execution(testCase);
        Field idListField = Execution.class.getDeclaredField("idList");
        idListField.setAccessible(true);
        List<Integer> idList = (List<Integer>) idListField.get(execution);

        Method method = Execution.class.getDeclaredMethod("deleteExistingAttachments", Integer.class);
        method.setAccessible(true);
        this.responses = (List<Response>) method.invoke(execution, idList.get(0));

    }

    private SearchExecutions searchExecutions;

    @When("^I search for a test execution cycle$")
    public void iSearchForAnTestExecutionCycle() throws Throwable {
        String query = String.format("issue='%s' and fixVersion='%s'",
                testCase, resultVersion);
        this.searchExecutions = new SearchExecutions(query);

        Field field = SearchExecutions.class.getDeclaredField("jsonPath");
        field.setAccessible(true);
        JsonPath jsonPath = (JsonPath) field.get(this.searchExecutions);
        if (jsonPath == null) {
            throw new RuntimeException("Query did not return a result");
        }

    }


    @Then("^I can get the '(.*)' for the execution$")
    public void iCanGetTheIdForTheExecution(String var) throws Throwable {
        List<Integer> list;

        if ("id".equals(var)) {
            list = this.searchExecutions.getExecutionIds();
        } else if ("status".equals(var)) {
            list = this.searchExecutions.getExecutionStatuses();
        } else {
            throw new RuntimeException("unknown option:" + var);
        }

        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
    }


    @When("^I 'run' the tests$")
    public void iRunTheTests() throws Throwable {
        System.out.println("Pretend tests are running...)");
    }
}
