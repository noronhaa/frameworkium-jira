package com.frameworkium.jira.standalone;

import com.frameworkium.jira.zapi.Execution;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.frameworkium.jira.JiraConfig;

import static com.frameworkium.jira.JiraConfig.JIRA_REST_PATH;


public class StandaloneTool {

    private String csvFile = "src/test/resources/realdata.csv";
    private static String staticCsvFile = "src/test/resources/realdata.csv";
    private String splitBy = ",";

    public StandaloneTool(String csvFile) {
        this.csvFile = csvFile;
    }

    private final Logger logger = LogManager.getLogger();


    public static void main(String[] args) {
        String[] testArgs = {"src/test/resources/realdata.csv",
                "",
                "",
                "",
                "MSS Automation",
                "dummy cycle"};

        entryPoint(testArgs);
    }

    private static void entryPoint(String[] args){
        StandaloneTool uploader = new StandaloneTool(args[0]);
        uploader.checkArgs(args);
        uploader.checkProperties(args);

        System.out.println("Starting upload");
        ArrayList<ZephyrTestObject> tests = (ArrayList<ZephyrTestObject>) uploader.mapCsv();
        uploader.updateTests(tests);
    }


    /**
     * Take the Jira & Zephyr parameters from the CLI and set as system properties
     * @param jiraURL
     * @param jiraUsername
     * @param jiraPassword
     * @param resultVersion
     * @param zapiCycleRegEx
     */
    private static void setProperties(String jiraURL, String jiraUsername, String jiraPassword, String resultVersion,
                                      String zapiCycleRegEx){
        System.setProperty("jiraURL",jiraURL);
        System.setProperty("jiraUsername",jiraUsername);
        System.setProperty("jiraPassword",jiraPassword);
        System.setProperty("resultVersion",resultVersion);
        System.setProperty("zapiCycleRegEx",zapiCycleRegEx);
    }

    /**
     * Take the csv file and return a list of ZephyrTestObject for each line which represents a single test
     * @return
     */
    public List<ZephyrTestObject> mapCsv(){

        List<ZephyrTestObject> zephyrTests = new ArrayList<ZephyrTestObject>();
        File csvFile = new File(this.csvFile);

        try {

            InputStream inputStream = new FileInputStream(csvFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            zephyrTests = br.lines().map(mapToZephyrTestObject).collect(Collectors.toList());
            br.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

        return zephyrTests;

    }

    /**
     * function to take a line of the csv and map it to a ZephyrTestObject
     */
    private Function<String, ZephyrTestObject> mapToZephyrTestObject = (line) -> {
        String[] args = line.split(splitBy);
        return new ZephyrTestObject(args[0],args[1],args[2],args[3]);
    };

    void updateTests(List<ZephyrTestObject> tests){
        tests.forEach(t -> update(t.getKey(),t.getStatus(),t.getComment(),t.getAttachment()));
//        new Execution(key).update(status,comment,attachment);
    }

    private void update(String k, int st, String comment, String attch){
        System.out.println(String.format("updating: %s %s %s %s",k,st,comment,attch));
        new Execution(k).update(st,comment,attch);

    }


    /**
     * Check that the Jira/Zephyr properties are correct and we can successfully connect to Jira
     * @param args
     */
    private void checkProperties(String[] args){
        System.out.print("Checking args..");
        setProperties(args[1],args[2],args[3],args[4],args[5]);

        Response response = JiraConfig.getJIRARequestSpec().get("/rest/auth/1/session");

        if (response.statusCode() != 200){
            throw new RuntimeException("Could not authenticate, expected 200 but got " + response.statusCode());
        } else {
            System.out.println("Done");
        }
    }

    /**
     * Check there are the correct amount of arguments parsed into the Jar
     * @param args
     */
    void checkArgs(String[] args){

        String expected = "expected args are:" +
                "\n1) csv file path" +
                "\n2) jiraURL" +
                "\n3) jiraUsername" +
                "\n4) jiraPassword" +
                "\n5) resultVersion" +
                "\n6) zapiCycleRegEx (optional)";

        String errorMessage = "Incorrect amount of args, expected at least 5 but got " + args.length + "\n" + expected;

        System.out.print("Checking args..");
        if (args.length < 5) { throw new RuntimeException(errorMessage); }
        System.out.println("Done");
    }


}
