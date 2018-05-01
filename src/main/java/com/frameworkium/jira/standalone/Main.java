package com.frameworkium.jira.standalone;

public class Main {

    public static void main(String[] args) {
        StandaloneTool uploader = new StandaloneTool(args[0]);
        uploader.checkArgs(args);
        uploader.checkProperties(args);
        uploader.uploadResultsFromCsv();
        if (StandaloneTool.isErrorsEncountered()){
            System.exit(1);
        }

    }
}
