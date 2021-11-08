package ru.segezhagroup.alx.tools;

import com.atlassian.application.api.ApplicationManager;
import com.atlassian.jira.application.ApplicationKeys;
//import com.atlassian.application.api.ApplicationKey;
import com.atlassian.application.api.Application;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import io.atlassian.fugue.Option;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.label.Label;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.issue.resolution.Resolution;
import com.atlassian.mail.Email;
import com.atlassian.mail.MailFactory;
import com.atlassian.mail.queue.SingleMailQueueItem;
import com.atlassian.mail.server.SMTPMailServer;

//import com.atlassian.jira.issue.IssueManager;

import com.atlassian.velocity.VelocityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class MailSender {
    private static final Logger log = LoggerFactory.getLogger(MailSender.class);

    // \target\jira\webapp\WEB-INF\classes\com\atlassian\jira\web\action\JiraWebActionSupport.properties
    // \target\container\tomcat8x\cargo-jira-home\webapps\jira\WEB-INF\classes\com\atlassian\jira\web\action\JiraWebActionSupport.properties

    // ссылки
//    https://www.javatips.net/api/com.atlassian.jira.mail.email
//    http://biercoff.com/how-to-write-a-custom-jira-plugin-that-will-send-email-on-custom-field-value-change/
//    https://community.atlassian.com/t5/Jira-questions/How-can-send-email-using-jira-JAVA-API/qaq-p/934932

//    public static void sendEmail(String to, String subject, String body, String emailType) {
    public static void sendEmail(String to, String subject, String body) {
        SMTPMailServer mailServer = MailFactory.getServerManager().getDefaultSMTPMailServer();
        Email email = new Email(to);
        email.setFrom(mailServer.getDefaultFrom());
        email.setSubject(subject);
        email.setMimeType("text/html");
        email.setBody(body);
        SingleMailQueueItem item = new SingleMailQueueItem(email);
        ComponentAccessor.getMailQueue().addItem(item);

//        log.warn(emailType + " email was added to the queue ");
        log.warn(" ===== email was added to the queue =====");
    }


    public static String getReportText(String reportname, String userKey , List<Issue> issueList, boolean getMail) {


//        CustomField userNameCf = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(10300L);
//        CustomField userEmailCf = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(10302L);
//
//        String userName = (String)issue.getCustomFieldValue(userNameCf);
//        String userEmail = (String)issue.getCustomFieldValue(userEmailCf);
//
//        String reporter = "";
//
//        if (userName.equals("")) {
//            userName = null;
//        }
//
//        if (userEmail.equals("")) {
//            userEmail = null;
//        }
//
//        if (userName != null) {
//            reporter = userName;
//        } else {
//            if (userEmail != null) {
//                reporter = userEmail;
//            }
//        }

        ApplicationManager appManager = ComponentAccessor.getComponent(ApplicationManager.class);

//        public static final ApplicationKey CORE = ApplicationKey.valueOf("jira-core");
//        public static final ApplicationKey SERVICE_DESK = ApplicationKey.valueOf("jira-servicedesk");
//        public static final ApplicationKey SOFTWARE = ApplicationKey.valueOf("jira-software");

        // проверяем установлен ли Service Desk на сервере
//        Option<Application> jiraSoftwareApplication = appManager.getApplication(ApplicationKeys.SOFTWARE);
        Option<Application> jiraServiceDeskApplication = appManager.getApplication(ApplicationKeys.SERVICE_DESK);

//        log.warn(jiraSoftwareApplication.toString());
//        log.warn(jiraServiceDeskApplication.toString());

        SimpleDateFormat formatDay = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();

        List<ReportData> reportDataList = new ArrayList<ReportData>();
        for (Issue oneIssue: issueList) {

            ReportData reportData = new ReportData();

            // идентификатор задачи
            reportData.setIssueNumber(oneIssue.getKey());
            // тип
            reportData.setIssueType(oneIssue.getIssueType().getName());
            // описание
            reportData.setSummary(oneIssue.getSummary());
            // исполнитель
            reportData.setAssignee(oneIssue.getAssignee().getDisplayName());
            // автор
            reportData.setReporter(oneIssue.getReporter().getDisplayName());

            // приоритет
            Priority priority = oneIssue.getPriority();
            if (priority != null) {
                reportData.setPriority(priority.getName());
            } else {
                reportData.setPriority("-");
            }

            // статус
            reportData.setStatus(oneIssue.getStatus().getNameTranslation());

            // решение
            Resolution resolution = oneIssue.getResolution();
            if (resolution != null) {
                reportData.setResolution(oneIssue.getResolution().getNameTranslation());
            } else {
                reportData.setResolution("Нет решения");
            }

            // дата создания
            date.setTime(oneIssue.getCreated().getTime());
            reportData.setCreateDate(formatDay.format(date.getTime()));

            // дата обновления
            Timestamp updatedDate = oneIssue.getUpdated();
            if (updatedDate != null) {
                date.setTime(updatedDate.getTime());
                reportData.setUpdateDate(formatDay.format(date.getTime()));
            } else {
                reportData.setUpdateDate("");
            }

            // эти параметры задач доступны только если на сервере установлен ServiceDesk
            if (jiraServiceDeskApplication.toString() == "none()") {
                // SD не установлен
                reportData.setExecDate(""); // дата исполнения
                reportData.setExceedDays(""); // количество дней просрочки
            } else {
                ApplicationUser authorUser = ComponentAccessor.getUserManager().getUserByKey(userKey);

                JiraAuthenticationContext jAC = ComponentAccessor.getJiraAuthenticationContext();
                jAC.setLoggedInUser(authorUser);

//                def query = slaInformationService.newInfoQueryBuilder().issue(oneIssue.getId()).build()
//                //def slaFormatter = slaInformationService.durationFormatter
//
//                def user = ComponentAccessor.jiraAuthenticationContext.loggedInUser
//                def slaFormatter = slaInformationService.durationFormatter
//                def sla = slaInformationService.getInfo(user, query).results

                reportData.setExecDate(""); // дата исполнения
                reportData.setExceedDays(""); // количество дней просрочки


            }




            reportData.setDepartment("-"); // дзк


            // метки
            String issueLabels = "";
            Set<Label> labels = oneIssue.getLabels();
            for (Label oneLabel : labels) {
                issueLabels = issueLabels + oneLabel.getLabel() + ", ";
            }

            reportData.setLabels(issueLabels);


            // дата решения
            Timestamp resolutionDate = oneIssue.getResolutionDate();
            if (resolutionDate != null) {
                date.setTime(resolutionDate.getTime());
                reportData.setResolutionDate(formatDay.format(date.getTime()));

//                log.warn("=======");
//                log.warn(oneIssue.getKey());
//                log.warn(date.toString());
//                log.warn(formatDay.format(date.getTime()));

            } else {
                reportData.setResolutionDate("");
            }


            reportDataList.add(reportData);
        }


        Map params = new HashMap();
        params.put("reportdata", reportDataList);
        params.put("reportname", reportname);
//        params.put("summary", issue.getSummary());
//        params.put("description", issue.getDescription());
//        params.put("assignee", issue.getAssignee().getDisplayName());

        String result = "";

        VelocityManager vm = ComponentAccessor.getVelocityManager();

        if (getMail) {
            result = vm.getEncodedBody("/templates/", "mail.vm", "UTF-8", params);
        } else {
            result = vm.getEncodedBody("/templates/", "report.vm", "UTF-8", params);
        }

        return result;

    }

}
