package com.frameworkium.jira.zapi.cycle;

/**
 * Object for creating a test cycle, required fields are issueType, projectId, versionId. These are the minimum required to
 * create a test cycle. All other fields are optional.
 */
public class CycleEntity {

    public String clonedCycleId;
    public String name;
    public String build;
    public String environment;
    public String description;
    public String startDate;
    public String endDate;
    public String projectId;
    public String versionId;
    public Object sprintId;

}
