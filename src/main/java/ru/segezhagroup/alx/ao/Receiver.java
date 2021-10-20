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

    @Accessor("Userkey")
    @Default("")
    public String getUserKey();

    @Mutator("Userkey")
    @Default("")
    public void setUserKey(String userKey);

    @Accessor("Useremail")
    @Default("")
    public String getUserEmail();

    @Mutator("Useremail")
    @Default("")
    public void setUserEmail(String userEmail);

    @Accessor("Username")
    @Default("")
    public String getUserName();

    @Mutator("Username")
    @Default("")
    public void setUserName(String userName);


}
