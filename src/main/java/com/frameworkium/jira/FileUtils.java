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
