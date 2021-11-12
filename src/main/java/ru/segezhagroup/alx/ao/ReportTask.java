package ru.segezhagroup.alx.ao;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Mutator;
import net.java.ao.OneToMany;
import net.java.ao.schema.Default;
import net.java.ao.schema.Table;

@Table("Reporttasks")
public interface ReportTask extends Entity {

//    @Accessor("Name")
//    @Default("")
    public String getName();

//    @Mutator("Name")
//    @Default("")
    public void setName(String name);

//    @Accessor("Filterstring")
//    @NotNull
//    @Default("")
    public String getFilterString();

//    @Mutator("Filterstring")
//    @NotNull
//    @Default("")
    public void setFilterString(String filterString);

//    @Accessor("Shedtime")
//    @Default("")
    public String getShedTime();

//    @Mutator("Shedtime")
//    @Default("")
    public void setShedTime(String ShedTime);

//    @Accessor("Isactive")
//    @Default("false")
    public boolean getIsActive();

//    @Mutator("Isactive")
//    @Default("false")
    public void setIsActive(boolean isActive);

    public String getUserKey();
    public void setUserKey(String userKey);

    public int getSlaId();
    public void setSlaId(int slaId);

    @OneToMany
    public Receiver[] getReceivers();
}
