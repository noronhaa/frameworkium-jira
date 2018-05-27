package com.frameworkium.jira.zapi.entities;

import lombok.Builder;
import lombok.Data;

@Data
public class MoveExecutionsToCycleEntitiy {

    private String[] executions;
    @Builder.Default private String projectId = "10000";
    @Builder.Default private String versionId = "10000";
    @Builder.Default private boolean clearStatusFlag = false;
    @Builder.Default private boolean clearDefectMappingFlag = false;
    @Builder.Default private int folderId = 123; //WHAT IS FOLDER ID

}
