package ru.segezhagroup.alx.tools;

import com.atlassian.application.api.ApplicationManager;
import com.atlassian.jira.application.ApplicationKeys;
//import com.atlassian.application.api.ApplicationKey;
import com.atlassian.application.api.Application;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.customfields.option.LazyLoadedOption;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.servicedesk.api.ServiceDesk;
import com.atlassian.servicedesk.api.ServiceDeskManager;
import com.atlassian.servicedesk.api.sla.info.SlaInformation;
import com.atlassian.servicedesk.api.sla.info.SlaInformationOngoingCycle;
import com.atlassian.servicedesk.api.sla.info.SlaInformationQuery;
import com.atlassian.servicedesk.api.sla.info.SlaInformationService;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

        // если почта не настроена то отправлять ничего не будем
        if (mailServer == null) {
            log.warn("Не настроена отправка почты (mailServer=null), сообщение не будет отправлено");
            return;
        }

        // если почта не настроена то отправлять ничего не будем
        if (mailServer.getDefaultFrom() == null) {
            log.warn("В настройках не назначен отправитель (=null), сообщение не будет отправлено");
            return;
        }

        Email email = new Email(to);
        email.setFrom(mailServer.getDefaultFrom());
        email.setSubject(subject);
        email.setMimeType("text/html");
        email.setBody(body);
        SingleMailQueueItem item = new SingleMailQueueItem(email);
        ComponentAccessor.getMailQueue().addItem(item);

//        log.warn(emailType + " email was added to the queue ");
//        log.warn(" ===== email was added to the queue =====");
    }

    // сформировать отчет
    // getMail - формировать с как документ html (для отправки письмом)
    public static String getReportText(String reportname, String userKey , List<Issue> issueList, String strFieldId, int slaId, boolean getMail) {


        // получим объект пользовательского поля
        Long lFieldId = 0L;


        try {
            lFieldId = Long.valueOf(strFieldId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            lFieldId = 0L;
        }

        CustomField dzkCf = null;
        if (lFieldId != 0L) {
            dzkCf = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(lFieldId);
        }



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
            if (oneIssue.getAssignee() != null) {
                reportData.setAssignee(oneIssue.getAssignee().getDisplayName());
            } else {
                reportData.setAssignee("");
            }
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

                SlaInformationService slaInformationService = ComponentAccessor.getOSGiComponentInstanceOfType(SlaInformationService.class);

                SlaInformationQuery query = slaInformationService.newInfoQueryBuilder().issue(oneIssue.getId()).build();
                SlaInformationService.DurationFormatter slaFormatter = slaInformationService.getDurationFormatter();
                List<SlaInformation> sla = slaInformationService.getInfo(authorUser, query).getResults();

//                log.warn("====== " + oneIssue.getKey());
//                log.warn(sla.toString());

                // параметры sla будем сохранять отдельно
                List<String> slaStringList = new ArrayList<>();


                // ищем sla с идентификатором 3

//                SlaInformationImpl{
//                    id=3,
//                    name=Время до решения,
//                    completedCycles=[],
//                    ongoingCycle=Optional[
//                            SlaInformationOngoingCycleImpl{
//                                startTime=2021-11-06T14:13:51.674Z,
//                                breachTime=Optional[2021-11-09T06:00:00Z],
//                                breached=true,
//                                paused=false,
//                                withinCalendarHours=false,
//                                goalDuration=28800000,
//                                elapsedTime=115200000,
//                                remainingTime=-86400000
//                    }]
//                }

                // ищем параметры
//                breachTime=Optional[2021-11-09T06:00:00Z],
//                remainingTime=-86400000}


                String execDate = "";
                String exceedDays = "";


                for (SlaInformation oneSla : sla) {
                    slaStringList.add(oneSla.toString());

                    // здесь будет определение sla
                    if (oneSla.getId() == slaId) {
                        if (oneSla.getOngoingCycle().isPresent()) {
                            SlaInformationOngoingCycle slaInfoOngCycle = oneSla.getOngoingCycle().get();
                            long remainingTime = slaInfoOngCycle.getRemainingTime();

                            exceedDays = String.valueOf(Math.round( (-1) * remainingTime / (60 * 60 * 24 * 1000)));

                            if (slaInfoOngCycle.getBreachTime().isPresent()) {
                                Instant breachTime = slaInfoOngCycle.getBreachTime().get();

//                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.systemDefault());

                                execDate = formatter.format(breachTime);
                            }
                        }
                    }

                }

                reportData.setSlaInfo(slaStringList);
                reportData.setExecDate(execDate); // дата исполнения
                reportData.setExceedDays(exceedDays); // количество дней просрочки

            }


            // будет нужен serviceDeskProjectId для serviceDeskProject чтобы сгенерировать ссылку на задачу в customer portal
            ServiceDeskManager serviceDeskManager = ComponentAccessor.getOSGiComponentInstanceOfType(ServiceDeskManager.class);
            ServiceDesk serviceDesk = serviceDeskManager.getServiceDeskForProject(oneIssue.getProjectObject());
            reportData.setSdProjectId(String.valueOf(serviceDesk.getProjectId()));


            // поле дзк
            String dzkName = "";
            if (dzkCf != null) {
                // для поля вида селектабл значение нужно получать через LazyLoadedOption
                LazyLoadedOption lazyLoadedOption = (LazyLoadedOption)oneIssue.getCustomFieldValue(dzkCf);
                if (lazyLoadedOption != null) {
                    dzkName = lazyLoadedOption.getValue();
                }
            }

            reportData.setDepartment(dzkName); // дзк


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
        params.put("showslainfo", true);
        params.put("reportname", reportname);
        params.put("home_url", ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL));
        params.put("reportdate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
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
