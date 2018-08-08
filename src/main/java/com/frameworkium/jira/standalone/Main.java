package com.frameworkium.jira.standalone;

public class Main {

    public static void main(String[] args) {
        //Parse csv file or feature file/top dir to look for features
        StandaloneTool uploader = new StandaloneTool(args[1]);

        //check we have correct amount of args being parsed to Jar
        uploader.checkArgs(args);

        //Check the relevant jira/Zephyr properties work
        uploader.checkProperties(args);

        //Take appropriate action: update current zephyr tests or create new zephyr tests from BDDs
        switch (args[0].toLowerCase()) {
            case "update":
                uploader.uploadResultsFromCsv();
                break;
            case "sync":
                uploader.syncBddsWithZephyr();
                break;
            default:
                throw new RuntimeException("Expected first arg keyword 'update' to update Zephyr tests or 'sync'" +
                        " to create new Zephyr tests from feature files but got " + args[0]);
        }

        //Check if there have been errors and end with exit 1 code to indicate there were errors
        if (StandaloneTool.isErrorsEncountered()) {
            System.exit(1);
        }

    }
}
