package com.frameworkium.jira.standalone;

import com.frameworkium.jira.FileUtils;
import com.frameworkium.jira.exceptions.CsvException;
import com.frameworkium.jira.gherkin.FeatureParser;
import com.frameworkium.jira.properties.Validation;
import com.frameworkium.jira.zapi.Execution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StandaloneTool {

    private Path filePath;
    private static final String COMMA = ",";
    private static boolean errorsEncountered = false;

    private final Logger logger = LogManager.getLogger();

    public StandaloneTool(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    public StandaloneTool(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Take the Jira & Zephyr parameters from the CLI and set as system properties
     */
    private static void setProperties(
            String jiraURL, String jiraUsername, String jiraPassword,
            String resultVersion, String zapiCycleRegEx) {

        System.setProperty("jiraURL", jiraURL);
        System.setProperty("jiraUsername", jiraUsername);
        System.setProperty("jiraPassword", jiraPassword);
        System.setProperty("resultVersion", resultVersion);
        System.setProperty("zapiCycleRegEx", zapiCycleRegEx);
    }

    /**
     * Take the csv file and return a list of ZephyrTestObject for each line which represents a single test
     */
    public void uploadResultsFromCsv() {

        logger.info("Starting Zephyr update");

        collectTests().forEach(test ->
                new Execution(test.getKey())
                        .update(test.getStatus(), test.getComment(), test.getAttachment()));

        logger.info("Zephyr update complete");

        if (errorsEncountered) {
            logger.info("Errors found during update");
        }
    }

    public List<ZephyrTestObject> collectTests() {
        try {
            return Files.lines(filePath)
                    .map(mapToZephyrTestObject)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new CsvException(e);
        }
    }

    /**
     * function to take a line of the csv and map it to a ZephyrTestObject
     */
    private Function<String, ZephyrTestObject> mapToZephyrTestObject = (line) -> {
        String[] args = line.split(COMMA, -1);
        long commas = (int) line.chars().filter(ch -> ch == ',').count();
        if (commas < 2) {
            throw new CsvException("Not enough columns");
        }

        String id = args[0];
        String passStatus = args[1];
        if (commas == 3) {
            return new ZephyrTestObject(id, passStatus, args[2], args[3]);
        } else {
            //contains a comma in the comment section so we need to handle this
            String lineRemainder = line.substring(id.length() + passStatus.length() + 1);
            String[] commentAndAttachment = handleCommaInComment(lineRemainder);
            return new ZephyrTestObject(
                    id, passStatus, commentAndAttachment[0], commentAndAttachment[1]);
        }
    };

    /**
     * in the comment there may be comma which would mess wit the csv so we need to handle a comment having a comma.
     *
     * @param line in CSV file
     * @return An array of 2 elements, first element is the comment, second element is the attachments
     */
    private String[] handleCommaInComment(String line) {
        char doubleQuote = '\"';
        char comma = ',';

        int startQuoteIndex = -1;
        int endQuoteIndex = -1;
        int doubleQuoteCount = 0;
        int lastComma = -1;

        char[] charArray = line.toCharArray();

        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == doubleQuote) {
                if (startQuoteIndex == -1) {
                    startQuoteIndex = i;
                } else if (endQuoteIndex == -1) {
                    endQuoteIndex = i;
                }
                doubleQuoteCount++;
            } else if (charArray[i] == comma) {
                lastComma = i;
            }

        }

        if (doubleQuoteCount != 2) {
            logger.info("Invalid Comment, should contain 0 or 2 double quotes, skipping csv row");
            throw new CsvException(String.format("Found %s double quotes. Should be 0 or 2 double quotes and only in the " +
                    "comment section of the CSV", doubleQuoteCount));
        }

        String comment = line.substring(startQuoteIndex + 1, endQuoteIndex);
        String attachments = line.substring(lastComma + 1);

        // check we are not missing last actual comma before attachments in CSV
        if (lastComma < endQuoteIndex) {
            logger.info("Missing comma after comment in csv");
            throw new ArrayIndexOutOfBoundsException();
        }

        return new String[]{comment, attachments};

    }

    /**
     * Check that the Jira/Zephyr properties are correct and we can successfully connect to Jira
     */
    void checkProperties(String[] args) {
        System.out.print("Checking properties..");
        setProperties(args[2], args[3], args[4], args[5], args[6]);

        if (!Validation.authenticateJira()) {
            throw new RuntimeException("Could not authenticate to Jira");
        } else {
            System.out.println("Done");
        }
    }

    /**
     * Check there are the correct amount of arguments parsed into the Jar
     */
    void checkArgs(String[] args) {

        String expected = "expected args are:" +
                "\n1) keyword: 'update' or 'sync'" +
                "\n2) csv file path / featureFile or top directory path" +
                "\n3) jiraURL" +
                "\n4) jiraUsername" +
                "\n5) jiraPassword" +
                "\n6) resultVersion" +
                "\n7) zapiCycleRegEx ";

        String errorMessage = "Incorrect amount of args, expected at least 5 but got " + args.length + "\n" + expected;

        System.out.print("Checking args..");
        if (args.length != 7) {
            throw new RuntimeException(errorMessage);
        }
        System.out.println("Done");
    }


    /**
     * Looks for all feature files from this top level directory or will just
     * take a single feature file as arg. Then create a new zephyr test for
     * every BDD without a zephyr test. For existing Zephyr Tests update the
     * Zephyr test with any local changes to the BDD.
     *
     * @param featureDir either a feature file or a directory to recursively look through for feature files
     */
    public void syncBddsWithZephyr(Path featureDir) {
        FileUtils.findFeatures(featureDir).forEach(feature -> new FeatureParser(feature).syncTestsWithZephyr());
    }

    public void syncBddsWithZephyr() {
        FileUtils.findFeatures(filePath).forEach(feature -> new FeatureParser(feature).syncTestsWithZephyr());
    }

    public static void setErrorsEncountered(Boolean errors) {
        errorsEncountered = errors;
    }

    static boolean isErrorsEncountered() {
        return errorsEncountered;
    }

}
