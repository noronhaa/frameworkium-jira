package com.frameworkium.jira;


import io.restassured.RestAssured;
import io.restassured.config.RedirectConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FileUtils {

    /**
     * Read contents of file and return as a String
     * @param pathname path to file to read
     * @return file as String
     */
    public static String readFile(String pathname) {
        File file = new File(pathname);
        StringBuilder fileContents = new StringBuilder((int) file.length());
        Scanner scanner = null;

        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String lineSeparator = System.getProperty("line.separator");

        try {
            while (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine());
                fileContents.append(lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }

    /**
     * Either take a single file or recursively searches a directory collecting the paths for all feature files
     * @param directoryPath top directory to start looking through
     * @return list of feature file paths
     */
    public static List<String> findFeatures(String directoryPath){

            File topDir = new File(directoryPath);
            ArrayList<String> featurePaths = new ArrayList<>();

           try {
               scanFilesForFeatures(topDir, featurePaths);
           } catch (FileNotFoundException e) {
               e.printStackTrace();
           }


        return featurePaths;
    }

    private static void scanFilesForFeatures(File topFile, ArrayList featureList) throws FileNotFoundException {

        if (! topFile.exists()) { // It's not there!
            throw new FileNotFoundException("Could not find top level file: " + topFile);
        }

        //if (feature) file given add this to the list
        if (topFile.isFile() && isFeatureFile(topFile)){
            featureList.add(topFile.getPath());
        } else {

            //if directory given recursively search for feature files
            for (File file : Objects.requireNonNull(topFile.listFiles())){
                if (isFeatureFile(file)){
                    featureList.add(file.getPath());
                }

                if (file.isDirectory()){
                    scanFilesForFeatures(file, featureList);
                }
            }
        }

    }

    private static boolean isFeatureFile(File file){
        return file.getName().endsWith(".feature");
    }

}
