package com.frameworkium.jira.standalone;

import com.frameworkium.jira.JiraConfig;
import com.frameworkium.jira.zapi.Execution;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Optional;

public class ZephyrTestObject {



    private String key;
    private int status;
    private String comment;
    private String[] attachment;

    private final Logger logger = LogManager.getLogger();



    public ZephyrTestObject(String key, String status, String comment, String attachment) {
        this.key = key;
        this.status = convertStatus(status);
        this.comment = comment;
        this.attachment = handleMultipleAttachments(attachment);
    }

    public String getKey() {
        return key;
    }

    public int getStatus() {
        return status;
    }

    public String getComment() {
        return comment;
    }

    public String[] getAttachment() {
        return attachment;
    }

    private int convertStatus(String status){
        int intStatus = 0;
        switch (status.toLowerCase()){
            case "pass" :
            case "passed" :
            case "passes" : intStatus = JiraConfig.ZapiStatus.ZAPI_STATUS_PASS;
                break;
            case "failed" :
            case "fails" :
            case "fail" : intStatus = JiraConfig.ZapiStatus.ZAPI_STATUS_FAIL;
                break;
            case "skipped" :
            case "blocked" : intStatus = JiraConfig.ZapiStatus.ZAPI_STATUS_BLOCKED;
                break;
        }

        if (intStatus == 0){
            throw new RuntimeException("Could not find status, accepted value are pass, fail and blocked but got:" + status);
        }

        return intStatus;
    }

    private String[] handleMultipleAttachments(String csvAttachmentField){
        if (csvAttachmentField.isEmpty()){
            return new String[]{""};
        } else {
            return StringUtils.split(csvAttachmentField);
        }
    }

    @Override
    public String toString() {
        return "ZephyrTestObject{" +
                "key='" + key + '\'' +
                ", status=" + status +
                ", comment='" + comment + '\'' +
                ", attachment='" + attachment + '\'' +
                '}';
    }



}
