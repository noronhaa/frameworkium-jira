package com.frameworkium.jira.standalone;

import com.frameworkium.jira.JiraConfig;
import com.frameworkium.jira.zapi.Execution;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Optional;

public class ZephyrTestObject {



    private String key;
    private int status;
    private String comment;
    private String attachment;

    private final Logger logger = LogManager.getLogger();



    public ZephyrTestObject(String key, String status, String comment, String attachment) {
        this.key = key;
        this.status = convertStatus(status);
        this.comment = comment;
        this.attachment = attachment;
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

    public String getAttachment() {
        return attachment;
    }

    private int convertStatus(String status){
        int intStatus = 0;
        switch (status.toLowerCase()){
            case "pass" : intStatus = JiraConfig.ZapiStatus.ZAPI_STATUS_PASS;
                break;
            case "fail" : intStatus = JiraConfig.ZapiStatus.ZAPI_STATUS_FAIL;
                break;
            case "blocked" : intStatus = JiraConfig.ZapiStatus.ZAPI_STATUS_BLOCKED;
                break;
        }

        if (intStatus == 0){
            throw new RuntimeException("Could not find status, accepted value are pass, fail and blocked but got:" + status);
        }

        return intStatus;
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
