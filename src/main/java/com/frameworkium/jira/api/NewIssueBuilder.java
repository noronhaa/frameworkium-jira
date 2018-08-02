package com.frameworkium.jira.api;

import com.frameworkium.jira.JiraConfig;

import java.util.HashMap;

public class NewIssueBuilder {
    private String key;
    private String summary;
    private String description;
    private NewIssue.IssueType issueType;
    private String bddField;
    private String[] labels;
    private JiraConfig.Priority priority = JiraConfig.Priority.UNCATEGORISED;
    private HashMap<String, Object> customFields;

    public NewIssueBuilder() {
        this.customFields = new HashMap<>();
    }

    public NewIssueBuilder setKey(String key) {
        this.key = key;
        return this;
    }

    public NewIssueBuilder setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public NewIssueBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public NewIssueBuilder setIssueType(NewIssue.IssueType issueType) {
        this.issueType = issueType;
        return this;
    }

    public NewIssueBuilder setBddField(String bddField) {
        this.bddField = bddField;
        return this;
    }

    public NewIssueBuilder setPriority(JiraConfig.Priority priority) {
        this.priority = priority;
        return this;
    }

    public NewIssueBuilder setCustomField(String fieldName, Object fieldValue){
        //need the ID so query the api for all fields and look for the name and find the corresponding ID
        String fieldID = JiraTest.getFieldId(fieldName);
        customFields.put(fieldID, fieldValue);
        return this;
    }

    public NewIssueBuilder setLabels(String[] labels) {
        this.labels = labels;
        return this;
    }

    public NewIssue createNewIssue() {
        return new NewIssue(key, summary, description, issueType, bddField, priority, labels, customFields);
    }

}