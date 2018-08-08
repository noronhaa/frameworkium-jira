package com.frameworkium.jira.standalone;

import com.frameworkium.jira.JiraConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ZephyrTestObject {

    private static final Logger logger = LogManager.getLogger();

    /** Zephyr Test ID Key */
    private String key;

    /** Execution status ID eg Pass, WIP, Fail */
    private int status;

    /** Comment to leave on Zephyr Test Execution */
    private String comment;

    /** Attachments to leave on Execution */
    private String[] attachment;


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

    @Override
    public String toString() {
        return "ZephyrTestObject{" +
                "key='" + key + '\'' +
                ", status=" + status +
                ", comment='" + comment + '\'' +
                ", attachment='" + attachment + '\'' +
                '}';
    }

    /**
     * Return the ID of the test execution status
     *
     * @param status status string value to convert
     * @return integer ID required to change the status
     */
    private int convertStatus(String status) {
        int intStatus = 0;
        switch (status.toLowerCase()) {
            case "pass":
            case "passed":
            case "passes":
                intStatus = JiraConfig.ZapiStatus.ZAPI_STATUS_PASS;
                break;
            case "failed":
            case "fails":
            case "fail":
                intStatus = JiraConfig.ZapiStatus.ZAPI_STATUS_FAIL;
                break;
            case "skipped":
            case "blocked":
                intStatus = JiraConfig.ZapiStatus.ZAPI_STATUS_BLOCKED;
                break;
        }

        if (intStatus == 0) {
            throw new RuntimeException("Could not find status, accepted value are pass, fail and blocked but got:" + status);
        }

        return intStatus;
    }

    /**
     * Convert the attachment csv field which is a space delimited string to an array
     *
     * @param csvAttachmentField field for the attachment in the csv, can be multiple attachments delimited by a whitespace
     * @return Array of attachments
     */
    private String[] handleMultipleAttachments(String csvAttachmentField) {
        if (csvAttachmentField.isEmpty()) {
            return new String[]{""};
        } else {
            return StringUtils.split(csvAttachmentField);
        }
    }

}
