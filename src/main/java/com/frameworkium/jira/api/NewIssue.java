package com.frameworkium.jira.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.JSONObject;

import javax.naming.ldap.PagedResultsControl;

@Data @AllArgsConstructor
public class NewIssue {

    public static final String CUSTOM_BDD_FIELD_ID = "customfield_15942";

    private String key;
    private String summary;
    private String description;
    private String name;
    private String bddField;

    public String generateJson(){
        JSONObject object = new JSONObject();
        JSONObject fields = new JSONObject();

        JSONObject project = new JSONObject();
        project.put("key",this.key);

        JSONObject issueType = new JSONObject();
        issueType.put("name", this.name);

        fields.put("project",project);
        fields.put("summary",this.summary);
        fields.put("description",this.description);
        fields.put("issuetype",issueType);
        fields.put(CUSTOM_BDD_FIELD_ID, bddField);

        object.put("fields", fields);
        System.out.println(object.toString(2));

        return object.toString();
    }

}
