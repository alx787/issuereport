package ru.segezhagroup.alx.tools;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.mail.Email;
import com.atlassian.mail.MailFactory;
import com.atlassian.mail.queue.SingleMailQueueItem;
import com.atlassian.mail.server.SMTPMailServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
