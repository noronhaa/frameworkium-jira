package com.frameworkium.jira.standalone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StandaloneTool {

    private String csvFile = "src/test/resources/simpledata.csv";
    private String splitBy = ",";

    public StandaloneTool(String csvFile) {
        this.csvFile = csvFile;
    }

    private final Logger logger = LogManager.getLogger();



    private static void setProperties(String jiraURL, String jiraUsername, String jiraPassword, String resultVersion, String zapiCycleRegEx){
        System.setProperty("jiraURL",jiraURL);
        System.setProperty("jiraUsername",jiraUsername);
        System.setProperty("jiraPassword",jiraPassword);
        System.setProperty("resultVersion",resultVersion);
        System.setProperty("zapiCycleRegEx",zapiCycleRegEx);
    }

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

    private Function<String, ZephyrTestObject> mapToZephyrTestObject = (line) -> {
        String[] args = line.split(splitBy);
        return new ZephyrTestObject(args[0],args[1],args[2],args[3]);
    };

    private void updateTests(List<ZephyrTestObject> tests){
        tests.forEach(t -> update(t.getKey(),t.getStatus(),t.getComment(),t.getAttachment()));
//        new Execution(key).update(status,comment,attachment);
    }

    private void update(String k, String st, String comment, String attch){
        System.out.println(String.format("updating: %s %s %s %s",k,st,comment,attch));
    }

    public static void main(String[] args) {
        StandaloneTool uploader = new StandaloneTool(args[0]);
        uploader.checkArgs(args);

        System.out.println("Starting upload");
        ArrayList<ZephyrTestObject> tests = (ArrayList<ZephyrTestObject>) uploader.mapCsv();
        uploader.updateTests(tests);
    }

    private void checkProperties(String[] args){
        System.out.print("Checking args..");
        setProperties(args[1],args[2],args[3],args[4],args[5]);
        //todo check we can hit an endpoint and get a 2XX

    }

    private void checkArgs(String[] args){
        System.out.print("Checking args..");
        if (args.length < 6) { throw new RuntimeException("Incorrect amount of args"); }
        System.out.println("Done");
    }


}
