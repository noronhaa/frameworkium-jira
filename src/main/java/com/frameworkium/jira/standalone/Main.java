package com.frameworkium.jira.standalone;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        StandaloneTool uploader = new StandaloneTool(args[0]);
        uploader.checkArgs(args);

        System.out.println("Starting upload");
        ArrayList<ZephyrTestObject> tests = (ArrayList<ZephyrTestObject>) uploader.mapCsv();
        uploader.updateTests(tests);
    }
}
