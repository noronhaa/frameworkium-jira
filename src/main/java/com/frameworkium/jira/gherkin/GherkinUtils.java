package com.frameworkium.jira.gherkin;

import com.frameworkium.jira.JiraConfig;
import com.frameworkium.jira.api.Issue;
import gherkin.pickles.PickleStep;
import gherkin.pickles.PickleTag;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GherkinUtils {

    /**
     * remove @TestCaseId: if it is present from the tag
     */
    private static String stripZephyrTag(String zephyrTag) {
        return zephyrTag.replace(JiraConfig.ZEPHYR_TAG_PREFIX, "");
    }


    /**
     * Check through tags to see if there is one that contains a specific tag. Uses String.contains()
     *
     * @param tags        list of tags to look through
     * @param expectedTag tag we are looking to find
     * @return true if there is a tag that contains the tag we are looking for
     */
    public static boolean pickleContainsTag(List<PickleTag> tags, String expectedTag) {
        return tags.stream()
                .map(PickleTag::getName)
                .anyMatch(tag -> tag.contains(expectedTag));
    }


    /**
     * Check each tag for a zephyr tag checking it contains @TestCaseId:<zephyr tag> then query zephyr to check tag exists
     */
    public static boolean pickleHasZephyrTag(List<PickleTag> tags) {
        return getZephyrIdFromTags(tags).isPresent();
    }

    /**
     * Find any zephyr test id tags on the scenario and check zephyr for valid test
     *
     * @return the stripped zephyr id if present or Optional.empty if no zephyr id was found
     */
    //todo unit tests
    public static Optional<String> getZephyrIdFromTags(List<PickleTag> tags) {
        return tags
                .stream()
                .map(PickleTag::getName)
                .filter(pickleTag -> pickleTag.startsWith(JiraConfig.ZEPHYR_TAG_PREFIX))
                .map(GherkinUtils::stripZephyrTag)
                .filter(tag -> new Issue(tag).found())
                .findFirst();
    }

    public static String generateBddFromSteps(List<PickleStep> steps) {
        return steps.stream()
                .map(PickleStep::getText)
                .map(step -> step + "\n")
                .collect(Collectors.joining(","))
                .replace(",", "");
    }

}
