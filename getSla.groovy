import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.servicedesk.api.sla.info.SlaInformationService

import com.onresolve.scriptrunner.runner.customisers.PluginModule
import com.onresolve.scriptrunner.runner.customisers.WithPlugin

@WithPlugin("com.atlassian.servicedesk")
@PluginModule SlaInformationService slaInformationService

IssueManager issueManager = ComponentAccessor.getIssueManager()

// Замените PRJ-1 на ключ существующего issue.
MutableIssue curIssue = issueManager.getIssueObject("DSDP-39")
String result = curIssue.key + ": " + curIssue.summary

// SLA field name
final slaName = '<SLA name>'

// Gets the SLA information querying SLA service for the current issue
def query = slaInformationService.newInfoQueryBuilder().issue(curIssue.id).build()
//def slaFormatter = slaInformationService.durationFormatter

def user = ComponentAccessor.jiraAuthenticationContext.loggedInUser
def slaFormatter = slaInformationService.durationFormatter
def sla = slaInformationService.getInfo(user, query).results

log.warn(sla)
