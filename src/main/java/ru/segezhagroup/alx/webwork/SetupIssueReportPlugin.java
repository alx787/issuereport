package ru.segezhagroup.alx.webwork;

import com.atlassian.jira.security.request.RequestMethod;
import com.atlassian.jira.security.request.SupportedMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.jira.web.action.JiraWebActionSupport;

@SupportedMethods(RequestMethod.GET)
public class SetupIssueReportPlugin extends JiraWebActionSupport
{
    private static final Logger log = LoggerFactory.getLogger(SetupIssueReportPlugin.class);

    @Override
    public String execute() throws Exception {
        return super.execute(); //returns SUCCESS
    }

    @SupportedMethods(RequestMethod.GET)
    public String doDefault() throws Exception {
        return SUCCESS;
    }



}
