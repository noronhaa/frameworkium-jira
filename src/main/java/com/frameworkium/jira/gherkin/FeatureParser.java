package com.frameworkium.jira.gherkin;

//import com.frameworkium.jira.FileUtils;
import gherkin.AstBuilder;
import gherkin.Parser;
import gherkin.ast.GherkinDocument;
import gherkin.pickles.Pickle;
import gherkin.pickles.Compiler;
import gherkin.pickles.PickleStep;
import gherkin.pickles.PickleTag;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FeatureParser {

    private static final String ZEPHYR_TAG_PREFIX = "@TestCaseId:";
    private static final String SCENARIO_KEYWORD = "Scenario:";
    private static final String NEW_LINE = "\n";
    private static final String INDENTATION = "  ";

    private String featurePath;
    private List<Pickle> pickles;

    public FeatureParser(String featurePath) {
        this.featurePath = featurePath;
        pickles = parse();
    }

    public static void main(String[] args) {
//        FeatureParser featureParser = new FeatureParser();
//        List<Pickle> pickles = new FeatureParser().parse(
//                "src/test/resources/gherkinParser/features/test.feature");
//
//        pickles.forEach(pickle -> {
//            System.out.println(pickle.getName());
//            pickle.getSteps().forEach(pickleStep -> System.out.println(pickleStep.getText()));
//            pickle.getTags().forEach(pickleTag -> System.out.println(pickleTag.getName()));
//
//            String s = String.valueOf(featureParser.getZephyrId(pickle));
//            System.out.println(s);
//        });


    }

    private List<Pickle> parse(){
        String feature = com.frameworkium.jira.FileUtils.readFile(this.featurePath);
        Parser<GherkinDocument> parser = new Parser<>(new AstBuilder());
        GherkinDocument gherkinDocument = parser.parse(feature);
        List<Pickle> pickles = new Compiler().compile(gherkinDocument);
        return pickles;
    }

    public void syncWithZephyr(){
        this.pickles.stream()
                .filter(pickle1 -> !pickleHasZephyrTag(pickle1))
                .forEach(pickle -> {
                    String zId = addTestToZephyr(pickle);
                    addTags(pickle.getName(), zId);
                });

    }


    private boolean pickleHasZephyrTag(Pickle pickle){
        return pickle.getTags().stream()
                            .map(PickleTag::getName)
                            .anyMatch(tag -> tag.contains(ZEPHYR_TAG_PREFIX));
    }

    /**
     * Find any zephyr test id tags on the scenario
     * @param pickle
     * @return the stripped zephyr id if present or Optional.empty if no zephyr id was found
     */
    private Optional<String> getZephyrId(Pickle pickle){

        if (pickle.getTags().size() > 0){

            boolean zephyrID = pickle.getTags()
                    .stream()
                    .map(PickleTag::getName)
                    .anyMatch(pickleTag -> pickleTag.startsWith(ZEPHYR_TAG_PREFIX));

            if (zephyrID){
                return pickle.getTags()
                     .stream()
                     .map(PickleTag::getName)
                     .filter(pickleTag -> pickleTag.startsWith(ZEPHYR_TAG_PREFIX))
                     .map(pickleTag -> pickleTag.substring(ZEPHYR_TAG_PREFIX.length()))
                     .findFirst();

            } else {
                return Optional.empty();
            }

        } else {
            return Optional.empty();
        }
    }


    public boolean testPresentInZephyr(String ZephyrTestId){
        return RandomUtils.nextBoolean();
    }

    public String addTestToZephyr(Pickle pickle){
        String zTestDescription = pickle.getName();
        String zTestBdd = pickle.getSteps().stream()
                                    .map(PickleStep::getText)
                                    .map(step -> step + "\n")
                                    .collect(Collectors.joining(","));

        System.out.println(zTestBdd);


        return "12345";
    }

    /**
     * Adds a tag to a Scenario in a feature file
     * 1 - Reads feature file as a stream
     * 2 - for each line will look to match with the name of the scenario
     * 3 - replace that line with a line with the tag followed by original scenario line
     * 4 - transform stream of strings to bytes
     * 5 - write bytes to original file (overwrite)
     * @param scenarioNameToUpdate name of scenario on its own (do not add 'Scenario:')
     * @param zephyrId ID of Zephyr test
     */
    public void addTags(String scenarioNameToUpdate, String zephyrId){
        String tag = ZEPHYR_TAG_PREFIX + zephyrId;

        //regex to match: (any number of white space)Scenario:(0 or 1 whitespace)(name of scenario)
        String scenarioTitle = String.format("( *)%s( ?)%s",SCENARIO_KEYWORD, scenarioNameToUpdate);
        String scenarioLine = INDENTATION + SCENARIO_KEYWORD + " " + scenarioNameToUpdate;
        File file = new File(this.featurePath);
        String fileContext = null;
        try {
            fileContext = FileUtils.readFileToString(file);
            fileContext = fileContext.replaceAll(scenarioTitle,
                    INDENTATION + INDENTATION + tag + NEW_LINE + scenarioLine);
            FileUtils.write(file, fileContext);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
