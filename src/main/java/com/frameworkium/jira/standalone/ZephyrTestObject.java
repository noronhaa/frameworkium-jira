package com.frameworkium.jira.standalone;

import com.frameworkium.jira.JiraConfig;
import com.frameworkium.jira.zapi.Execution;
import io.restassured.response.Response;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Optional;

public class ZephyrTestObject {


    /**
     * Zephyr Test ID Key
     */
    @Getter
    private String key;

    /**
     * Execution status ID eg Pass, WIP, Fail
     */
    @Getter
    private int status;

    /**
     * Comment to leave on Zephyr Test Execution
     */
    @Getter
    private String comment;

    /**
     * Attachments to leave on Execution
     */
    @Getter
    private String[] attachment;

    private final Logger logger = LogManager.getLogger();



    public ZephyrTestObject(String key, String status, String comment, String attachment) {
        this.key = key;
        this.status = convertStatus(status);
        this.comment = comment;
        this.attachment = handleMultipleAttachments(attachment);
    }

    /**
     * Return the ID of the test execution status
     * @param status status string value to convert
     * @return integer ID required to change the status
     */
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

    /**
     * Convert the attachment csv field which is a space delimited string to an array
     * @param csvAttachmentField field for the attachment in the csv, can be multiple attachments delimited by a whitespace
     * @return Array of attachments
     */
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
