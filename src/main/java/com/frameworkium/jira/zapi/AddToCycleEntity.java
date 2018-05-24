package com.frameworkium.jira.zapi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
@Data @AllArgsConstructor
public class AddToCycleEntity {

    private String cycleId;

    private List<String> issues;

    @Builder.Default
    private String method = "1";

    private String projectId;

    private int versionId;



}
