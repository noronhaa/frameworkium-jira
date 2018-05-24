package com.frameworkium.jira.zapi;

import lombok.NonNull;
import lombok.Builder;
import lombok.Data;

/**
 * Object for creating a test cycle, required fields are name, projectId, versionId. These are the minimum required to
 * create a test cycle. All other fields are optional.
 */
@Data
public class CycleEntity {

    private String clonedCycleId;
    @NonNull private String name;
    private String build;
    private String environment;
    private String description;
    private String startDate;
    private String endDate;
    @NonNull private String projectId;
    @NonNull private String versionId;
    @Builder.Default private Object sprintId = null;

}
