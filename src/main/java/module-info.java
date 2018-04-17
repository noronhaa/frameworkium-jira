module com.frameworkium.jira {
    requires rest.assured;
    requires org.apache.logging.log4j;
    requires org.apache.commons.lang3;
    requires cucumber.core;
    requires gherkin;
    requires cucumber.testng;
    requires testng;
    requires json;
    requires json.path;
    requires allure.java.annotations;
    requires snakeyaml;


    exports com.frameworkium.jira;
    exports com.frameworkium.jira.api;
    exports com.frameworkium.jira.listners;
    exports com.frameworkium.jira.zapi;

}