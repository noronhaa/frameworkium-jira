package com.frameworkium.jira.properties;

import com.frameworkium.base.properties.Property;
import com.frameworkium.jira.JiraConfig;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Validation {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Check that all Properties required to integrate with Zephyr are present: resultVersion, zapiCycleName, jiraProjectKey
     * @return
     */
    public static boolean zephyrDetailsPresent(){
        boolean versionSpecified = Property.RESULT_VERSION.isSpecified();
        boolean cycleNameSpecified = Property.ZAPI_CYCLE_REGEX.isSpecified();
        boolean projectKeySpecified = Property.JIRA_PROJECT_KEY.isSpecified();

        if (!versionSpecified) {
            logger.error("Property missing for test version: resultVersion");
        }
        if (!cycleNameSpecified) {
            logger.error("Property missing for Zephyr Test Cycle: zapiCycleName");
        }
        if (!projectKeySpecified){
            logger.error("Property missing for Jira Project Key: jiraProjectKey");
        }

        return versionSpecified && cycleNameSpecified && projectKeySpecified;
    }

    /**
     * Check that the Jira/Zephyr properties are correct and we can successfully connect to Jira. Requires:
     * jiraUsername, jiraPassword, jiraURL
     */
    public static boolean authenticateJira(){
        boolean canAuthenticate = false;
        boolean usernameSpecified = Property.JIRA_USERNAME.isSpecified();
        boolean passwordSpecified = Property.JIRA_PASSWORD.isSpecified();
        boolean jiraUrlSpecified = Property.JIRA_URL.isSpecified();

        if (!usernameSpecified) {
            logger.error("Property missing for JIRA Username: jiraUsername");
        }
        if (!passwordSpecified) {
            logger.error("Property missing for JIRA Password: jiraPassword");
        }
        if (!jiraUrlSpecified){
            logger.error("Property missing for JIRA URL: jiraURL");
        }

        if (jiraUrlSpecified && usernameSpecified && passwordSpecified){
            Response response = JiraConfig.getJIRARequestSpec().get("/rest/auth/1/session");

            if (response.statusCode() != 200){
                logger.error("Could not authenticate to JIRA, expected 200 but got " + response.statusCode());
            } else {
                canAuthenticate = true;
            }
        }

        return canAuthenticate;

    }

    public static boolean allPropertiesPresent(){
        return zephyrDetailsPresent() && authenticateJira();
    }

}
