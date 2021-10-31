package ru.segezhagroup.alx.ao;

public interface ReportTaskDao {
    ReportTask findById(int id);
    ReportTask[] findAll();
    ReportTask create(String name, String filterString, String shedTime, boolean isActive, String userName);
    void update(ReportTask reportTask);
    void remove(ReportTask reportTask);

}
