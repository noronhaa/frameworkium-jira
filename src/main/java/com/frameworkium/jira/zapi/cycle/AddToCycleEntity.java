package com.frameworkium.jira.zapi.cycle;

import java.util.List;

/**
 * This API will execute based on following conditions:
 * 1	From individual test required following params:
 * (assigneeType, cycleId, issues, method = 1, projectId, versionId)
 * 2	From search filter required following params:
 * (assigneeType, cycleId, issues, method = 2, projectId, versionId, searchId)
 * 3	From another cycle required following params:
 * (assigneeType, cycleId, issues, method = 3, projectId, versionId, components, fromCycleId, fromVersionId, hasDefects, labels, priorities, statuses)
 */
public class AddToCycleEntity {

    public String cycleId;
    public List<String> issues;
    public String method = "1";
    public String projectId;
    public int versionId;

}
