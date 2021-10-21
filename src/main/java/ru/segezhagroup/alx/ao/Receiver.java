package ru.segezhagroup.alx.ao;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Mutator;
import net.java.ao.schema.Default;
import net.java.ao.schema.Table;

//https://developer.atlassian.com/server/framework/atlassian-sdk/onetomany-relationship/

@Table("Receiver")
public interface Receiver extends Entity {

    public ReportTask getReportTask();
    public void setReportTask(ReportTask reportTask);

    public String getUserKey();
    public void setUserKey(String userKey);

    public String getUserEmail();
    public void setUserEmail(String userEmail);

    public String getUserName();
    public void setUserName(String userName);


}
