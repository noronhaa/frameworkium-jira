package com.frameworkium.jira.listeners;

import com.frameworkium.jira.api.SearchIssues;
import com.frameworkium.reporting.allure.TestIdUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;

import java.util.List;

import static com.frameworkium.base.properties.Property.JIRA_URL;
import static com.frameworkium.base.properties.Property.JQL_QUERY;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class MethodInterceptor implements IMethodInterceptor {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public List<IMethodInstance> intercept(
            List<IMethodInstance> methods, ITestContext context) {

        return filterTestsToRunByJQL(methods);
    }

    private List<IMethodInstance> filterTestsToRunByJQL(
            List<IMethodInstance> methodsToFilter) {

        // Can't run JQL without JIRA_URL or JQL_QUERY
        if (!JQL_QUERY.isSpecified() || !JIRA_URL.isSpecified()) {
            return methodsToFilter;
        }

        logger.info("Filtering specified tests to run with JQL query results");

        List<String> testIDsFromJQL =
                new SearchIssues(JQL_QUERY.getValue()).getKeys();

        List<IMethodInstance> methodsToRun = methodsToFilter.stream()
                .filter(m -> testIDsFromJQL.contains(
                        TestIdUtils.getIssueOrTmsLinkValue(m).orElse("")))
                .collect(toList());

        logTestMethodInformation(methodsToRun);

        return methodsToRun;
    }

    private void logTestMethodInformation(
            List<IMethodInstance> methodsPostFiltering) {

        logger.debug("Running the following test methods:\n{}", () ->
                methodsPostFiltering.stream()
                        .map(this::getMethodNameFromIMethod)
                        .collect(joining("\n")));

        logger.info("Running {} tests specified by JQL query", methodsPostFiltering.size());
    }

    private String getMethodNameFromIMethod(IMethodInstance iMethod) {
        return iMethod.getMethod().getConstructorOrMethod().getMethod().getName();
    }
}
