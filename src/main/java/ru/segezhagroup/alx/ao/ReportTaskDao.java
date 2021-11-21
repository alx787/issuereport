package ru.segezhagroup.alx.ao;

public interface ReportTaskDao {
    ReportTask findById(int id);
    ReportTask[] findAll();
    ReportTask[] findActive();
    ReportTask create(String name, String filterString, String shedTime, boolean isActive, String userKey, int slaId);
    void update(ReportTask reportTask);
    void remove(ReportTask reportTask);

}
