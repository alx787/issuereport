package ru.segezhagroup.alx.tools;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
//import com.atlassian.jira.issue.search.SearchProvider;
//import com.atlassian.jira.jql.parser.JqlQueryParser;
//import com.atlassian.jira.web.bean.PagerFilter;

public class QueryIssues {


    private static final Logger log = LoggerFactory.getLogger(QueryIssues.class);


    public static boolean validateJql(String userKey, String jqlQuery) {
        SearchService.ParseResult parseResult = getParseResult(userKey, jqlQuery);

        if (!parseResult.isValid())
        {
            return false;
        }

        return true;
    }

    public static List<Issue> getIssueFromJql(String userKey, String jqlQuery) {

        ApplicationUser authorUser = ComponentAccessor.getUserManager().getUserByKey(userKey);

        SearchService.ParseResult parseResult = getParseResult(userKey, jqlQuery);

        SearchService searchService = ComponentAccessor.getComponent(SearchService.class);

        if (parseResult.isValid())
        {
            // Carry On
        } else {
            return null;
        }


        Query query = parseResult.getQuery();

        SearchResults results = null;
        try {
            results = searchService.search(authorUser, query, PagerFilter.getUnlimitedFilter());
        } catch (SearchException e) {
            e.printStackTrace();
        }


        List<Issue> issueList = (List<Issue>)results.getResults();

//        for (Issue oneIssue : issueList) {
//            log.warn(oneIssue.getKey());
//        }


        return issueList;

    }


    private static SearchService.ParseResult getParseResult(String userKey, String jqlQuery) {
        //        String authorUserName = "admin";
        ApplicationUser authorUser = ComponentAccessor.getUserManager().getUserByKey(userKey);

        JiraAuthenticationContext jAC = ComponentAccessor.getJiraAuthenticationContext();
        jAC.setLoggedInUser(authorUser);
//        log.warn(authorUser.getKey());

        SearchService searchService = ComponentAccessor.getComponent(SearchService.class);


//        String jqlQuery = "project = \"DEMO\" and assignee = currentUser()";
//        String jqlQuery = "assignee = admin AND resolution = Unresolved order by updated DESC";
        SearchService.ParseResult parseResult = searchService.parseQuery(authorUser, jqlQuery);

        return parseResult;
    }


}
