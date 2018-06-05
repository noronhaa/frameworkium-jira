package com.frameworkium.jira.gherkin;

import com.frameworkium.jira.api.Issue;
import gherkin.pickles.Pickle;
import gherkin.pickles.PickleTag;

import java.util.List;
import java.util.Optional;

public class GherkinUtils {

    private static final String ZEPHYR_TAG_PREFIX = "@TestCaseId:";

    /**
     * remove @TestCaseId: if it is present from the tag
     * @param zephyrTag
     * @return
     */
    private String stripZephyrTag(String zephyrTag){
        return zephyrTag.replace(ZEPHYR_TAG_PREFIX, "");
    }

    /**
     * Check each tag for a zephyr tag checking it contains @TestCaseId:<zephyr tag> then query zephyr to check tag exists
     * @param pickle
     * @return
     */
    public boolean pickleHasZephyrTag(List<PickleTag> tags){
        return tags.stream()
                .map(PickleTag::getName)
                .filter(tag -> tag.contains(ZEPHYR_TAG_PREFIX))
                .map(this::stripZephyrTag)
                .anyMatch(tag -> new Issue(tag).found());
    }

    /**
     * Find any zephyr test id tags on the scenario
     * @param pickle
     * @return the stripped zephyr id if present or Optional.empty if no zephyr id was found
     */
    //todo unit tests
    public Optional<String> getZephyrIdFromTags(List<PickleTag> tags){
        Optional<String> zephyrId = tags
                .stream()
                .map(PickleTag::getName)
                .filter(pickleTag -> pickleTag.startsWith(ZEPHYR_TAG_PREFIX))
                .map(this::stripZephyrTag)
                .findFirst();

        return zephyrId;
    }

}
