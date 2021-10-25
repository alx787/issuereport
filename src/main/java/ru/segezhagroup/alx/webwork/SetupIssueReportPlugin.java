package ru.segezhagroup.alx.webwork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.jira.web.action.JiraWebActionSupport;

public class SetupIssueReportPlugin extends JiraWebActionSupport
{
    private static final Logger log = LoggerFactory.getLogger(SetupIssueReportPlugin.class);

    @Override
    public String execute() throws Exception {
        return super.execute(); //returns SUCCESS
    }

    public String doDefault() throws Exception {
        return SUCCESS;
    }



}
