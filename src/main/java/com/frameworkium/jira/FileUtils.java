package com.frameworkium.jira;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class FileUtils {

    public static String readFile(Path filePath) {
        try {
            return new String(Files.readAllBytes(filePath));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    /**
     * Read contents of file and return as a String
     *
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
     * Either take a single file or recursively searches a directory collecting
     * the paths for all feature files
     *
     * @param directoryPath top directory to start looking through
     * @return list of feature file paths
     */
    public static List<Path> findFeatures(Path directoryPath) {

        if (Files.notExists(directoryPath)) {
            throw new IllegalArgumentException(
                    "Could not find top level file: " + directoryPath);
        }
        try {
            return Files
                    .walk(directoryPath)
                    .filter(path -> Files.isRegularFile(path))
                    .filter(path -> path.getFileName().endsWith(".feature"))
                    .collect(Collectors.toList());
        } catch (IOException ioex) {
            throw new IllegalArgumentException(ioex);
        }
    }
}
