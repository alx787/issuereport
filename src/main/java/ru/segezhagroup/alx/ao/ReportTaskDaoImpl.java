package ru.segezhagroup.alx.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import net.java.ao.DBParam;
import net.java.ao.Query;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;

@Transactional
@Named
public class ReportTaskDaoImpl implements ReportTaskDao {

    @ComponentImport
    private final ActiveObjects ao;

    @Inject
    public ReportTaskDaoImpl(ActiveObjects ao) {
        this.ao = ao;
    }

    @Override
    public ReportTask findById(int id) {
        return ao.get(ReportTask.class, id);
    }

    @Override
    public ReportTask[] findAll() {
        return ao.find(ReportTask.class, Query.select().order("ID ASC"));
    }

    @Override
    public ReportTask create(String name, String filterString, String shedTime, boolean isActive) {

//        final ReportTask reportTask = ao.create(ReportTask.class,
//                new DBParam("NAME", name),
//                new DBParam("FILTER_STRING", filterString),
//                new DBParam("SHED_TIME", shedTime),
//                new DBParam("IS_ACTIVE", isActive)
//        );

        final ReportTask reportTask = ao.create(ReportTask.class);
        reportTask.setName(name);
        reportTask.setFilterString(filterString);
        reportTask.setShedTime(shedTime);
        reportTask.setIsActive(isActive);
        reportTask.save();

         reportTask.save();

        return reportTask;
    }

    @Override
    public void update(ReportTask reportTask) {
        ReportTask rTask = ao.get(ReportTask.class, reportTask.getID());
        rTask.setFilterString(reportTask.getFilterString());
        rTask.setShedTime(reportTask.getShedTime());
        rTask.setIsActive(reportTask.getIsActive());
        rTask.save();
    }

    @Override
    public void remove(ReportTask reportTask) {

        Receiver[] receivers = reportTask.getReceivers();
        for (int i = 0; i < receivers.length; i++) {
            Receiver receiver = ao.get(Receiver.class, receivers[i].getID());
            ao.delete(receiver);
        }

//        ao.deleteWithSQL(ReportTask.class, "\"ID\" > ?", 0);
        ao.delete(reportTask);
    }
}
