package com.frameworkium.jira;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileUtils {

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

}
