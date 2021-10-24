package ru.segezhagroup.alx.ao;

public interface ReceiverDao {
//    Receiver findById(int id);
//    Receiver[] findAll();
    Receiver create(ReportTask reportTask, String userKey, String userEmail, String UserName);
    void removeFromTask(ReportTask reportTask);
    void remove(Receiver receiver);
}
