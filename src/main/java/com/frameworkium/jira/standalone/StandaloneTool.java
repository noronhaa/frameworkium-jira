package com.frameworkium.jira.standalone;

import com.frameworkium.jira.zapi.Execution;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.frameworkium.jira.JiraConfig;


public class StandaloneTool {

    private String csvFile;
    private static final String COMMA = ",";
    private static boolean errorsEncountered = false;

    private final Logger logger = LogManager.getLogger();

    public StandaloneTool(String csvFile) {
        this.csvFile = csvFile;
    }


    public static void main(String[] args) {
//        String[] testArgs = {"src/test/resources/csv/realdata.csv",
//                "https://engineering/jira",
//                "automationexecution",
//                "po12PO!",
//                "MSS Automation",
//                "dummy cycle"};

        entryPointForDebugging(args);
    }

    private static void entryPointForDebugging(String[] args){
        StandaloneTool uploader = new StandaloneTool(args[0]);
        uploader.checkArgs(args);
        uploader.checkProperties(args);
        uploader.uploadResultsFromCsv();
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

//        System.out.println(System.getProperty("resultVersion"));
//        System.out.println(System.getProperty(Property.RESULT_VERSION.getValue()));
    }

    /**
     * Take the csv file and return a list of ZephyrTestObject for each line which represents a single test
     */
    void uploadResultsFromCsv(){

        logger.info("Starting Zephyr update");

        File csvFile = new File(this.csvFile);

        try (InputStream inputStream = new FileInputStream(csvFile);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));){

            br.lines().map(mapToZephyrTestObject)
                    .collect(Collectors.toList())
                    .forEach(test ->
                        new Execution(test.getKey()).update(test.getStatus(), test.getComment(), test.getAttachment()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("Zephyr update complete");

        if (errorsEncountered){
            logger.info("Errors found during update");
        }
    }

    /**
     * function to take a line of the csv and map it to a ZephyrTestObject
     */
    private Function<String, ZephyrTestObject> mapToZephyrTestObject = (line) -> {
        String[] args = line.split(COMMA,-1);
        return new ZephyrTestObject(args[0],args[1],args[2],args[3]);
    };


    /**
     * Check that the Jira/Zephyr properties are correct and we can successfully connect to Jira
     * @param args
     */
    void checkProperties(String[] args){
        System.out.print("Checking properties..");
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
                "\n6) zapiCycleRegEx ";

        String errorMessage = "Incorrect amount of args, expected at least 5 but got " + args.length + "\n" + expected;

        System.out.print("Checking args..");
        if (args.length != 6) { throw new RuntimeException(errorMessage); }
        System.out.println("Done");
    }

    public static void setErrorsEncountered(Boolean errors){
        errorsEncountered = errors;
    }

    static boolean isErrorsEncountered() {
        return errorsEncountered;
    }

}
