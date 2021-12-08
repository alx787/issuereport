package ru.segezhagroup.alx.tools;

import com.atlassian.jira.issue.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.segezhagroup.alx.ao.Receiver;
import ru.segezhagroup.alx.ao.ReportTask;
import ru.segezhagroup.alx.ao.ReportTaskDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class JobsProcedures {

    private static final Logger log = LoggerFactory.getLogger(JobsProcedures.class);


    public static void ExecReporting(ReportTaskDao reportTaskDao, String fieldId) {
        // здесь выполняется задача
        // 1 - нужно получить все активные задачи
        // 2 - проанализировать время, нужно ли выполнять задачу
        // 3 - получить отчет по каждой выполняемой задаче
        // 4 - поставить отчет в очередь отправки почтой для получаетей


        SimpleDateFormat formatDay = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        SimpleDateFormat formaterDate = new SimpleDateFormat("HH");
        Date date = new Date();

        // если сравнение по часам, то берем только текущий час
        Integer currHour = null;

        try {
            currHour = Integer.valueOf(formaterDate.format(date));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (currHour == null) {
            log.warn("ошибка при получении времени выполнения из даты " + formatDay.format(date));
        }

        // 1 - нужно получить все активные задачи
        ReportTask[] activeTasks = reportTaskDao.findActive();

        // 2 - проанализировать время, нужно ли выполнять задачу
        for (ReportTask oneTask : activeTasks) {
            // время запуска, представляет собой строку из времени для запуска, например 2,3,4 - в 2 часа, в 3 часа и в 4 часа соответственно
            // если не заполнено то запускать как есть
            String shedTime = oneTask.getShedTime();

            if (shedTime == null || shedTime.equals("")) {
                JobsProcedures.ReportForTask(oneTask, fieldId);
                return;
            }

            String[] strings = shedTime.split(",");

            for (int i = 0; i < strings.length; i++) {

                Integer taskHour = null;

                try {
                    taskHour = Integer.valueOf(strings[i]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (taskHour != null) {
                    if (taskHour == currHour) {
                        JobsProcedures.ReportForTask(oneTask, fieldId);
                        return;
                    }
                }

            }

        }
    }

    private static void ReportForTask(ReportTask reportTask, String fieldId) {
        Receiver[] receivers = reportTask.getReceivers();

        if (receivers.length == 0) {
            return;
        }


        // будем делать последовательно - получим список задач
        List<Issue> issueList = QueryIssues.getIssueFromJql(reportTask.getUserKey(), reportTask.getFilterString());

        // получим тело сообщения
        String mailText = MailSender.getReportText(reportTask.getName(), reportTask.getUserKey(), issueList, fieldId, reportTask.getSlaId(), true);

        for (Receiver oneReceiver : receivers) {
            MailSender.sendEmail(oneReceiver.getUserEmail(), "JIRA - отчет по задачам", mailText);

//            log.warn("==================");
//            log.warn("send " + reportTask.getName() + " to " + oneReceiver.getUserEmail());

        }

    }
}
