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
    private String status;
    private String comment;
    private String attachment;

    private final Logger logger = LogManager.getLogger();


    public ZephyrTestObject(String key, String status, String comment, String attachment) {
        this.key = key;
        this.status = status;
        this.comment = comment;
        this.attachment = attachment;
    }

    public String getKey() {
        return key;
    }

    public String getStatus() {
        return status;
    }

    public String getComment() {
        return comment;
    }

    public String getAttachment() {
        return attachment;
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
