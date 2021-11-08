import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.issue.Issue
import java.text.SimpleDateFormat



def customFieldManager = ComponentAccessor.getCustomFieldManager()
def issueManager = ComponentAccessor.getIssueManager()
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

def cf = customFieldManager.getCustomFieldObject(10000L)


def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchService = ComponentAccessor.getComponent(SearchService)

//def user = ComponentAccessor.getJiraAuthenticationContext().getUser()

// edit this query to suit
def query = jqlQueryParser.parseQuery("project = DSDP")

def results = (List<Issue>)searchService.search(user, query, PagerFilter.getUnlimitedFilter()).getResults()

//log.debug("Total issues: ${results.total}")

SimpleDateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
SimpleDateFormat formatFull = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
Date date = new Date()

results.each {documentIssue ->

    log.warn("=========================")

    log.warn(documentIssue.key)
    log.warn(" - type - ")
	log.warn(documentIssue.getIssueType().getName())
    log.warn(" - created - ")
    date.setTime(documentIssue.getDueDate().getTime())
	log.warn(formatFull.format(date.getTime()))
	log.warn(documentIssue.getCreated().toString())



    // if you need a mutable issue you can do:
    //def issue = issueManager.getIssueObject(documentIssue.id)

	//issue.setCustomFieldValue(cf,"alxlog")
	//issueManager.updateIssue(user, issue, EventDispatchOption.DO_NOT_DISPATCH, false)


    // do something to the issue...
//    log.debug(issue.summary)
//    log.debug(issue.summary)

}