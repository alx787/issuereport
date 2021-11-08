package ru.segezhagroup.alx.tools;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.issue.resolution.Resolution;
import com.atlassian.mail.Email;
import com.atlassian.mail.MailFactory;
import com.atlassian.mail.queue.SingleMailQueueItem;
import com.atlassian.mail.server.SMTPMailServer;

import com.atlassian.jira.issue.IssueManager;

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


    public static String getReportText(String reportname, List<Issue> issueList, boolean getMail) {


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

        SimpleDateFormat formatDay = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();

        List<ReportData> reportDataList = new ArrayList<ReportData>();
        for (Issue oneIssue: issueList) {

            ReportData reportData = new ReportData();

            reportData.setIssueNumber(oneIssue.getKey());
            reportData.setIssueType(oneIssue.getIssueType().getName());
            reportData.setSummary(oneIssue.getSummary());
            reportData.setAssignee(oneIssue.getAssignee().getDisplayName());
            reportData.setReporter(oneIssue.getReporter().getDisplayName());

            Priority priority = oneIssue.getPriority();
            if (priority != null) {
                reportData.setPriority(priority.getName());
            } else {
                reportData.setPriority("-");
            }


            reportData.setStatus(oneIssue.getStatus().getNameTranslation());

            Resolution resolution = oneIssue.getResolution();
            if (resolution != null) {
                reportData.setResolution(oneIssue.getResolution().getNameTranslation());
            } else {
                reportData.setResolution("Нет решения");
            }


            date.setTime(oneIssue.getCreated().getTime());
            reportData.setCreateDate(formatDay.format(date.getTime()));


            Timestamp updatedDate = oneIssue.getUpdated();
            if (updatedDate != null) {
                date.setTime(updatedDate.getTime());
                reportData.setUpdateDate(formatDay.format(date.getTime()));
            } else {
                reportData.setUpdateDate("");
            }


            reportData.setExecDate("?");
            reportData.setExceedDays("?");
            reportData.setDepartment("?");
            reportData.setLabels("?");


            Timestamp resolutionDate = oneIssue.getResolutionDate();
            if (resolutionDate != null) {
                date.setTime(resolutionDate.getTime());
                reportData.setResolutionDate(formatDay.format(date.getTime()));
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
