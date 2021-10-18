package ru.segezhagroup.alx.ao;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Mutator;
import net.java.ao.OneToMany;
import net.java.ao.schema.Default;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Table;

@Table("Reporttasks")
public interface ReportTasks extends Entity {

    @Accessor("Filterstring")
//    @NotNull
    @Default("")
    public String getFilterString();

    @Mutator("Filterstring")
//    @NotNull
    @Default("")
    public void setFilterString(String filterString);

    @Accessor("Shedtime")
    @Default("")
    public String getShedTime();

    @Mutator("Shedtime")
    @Default("")
    public void setShedTime(String ShedTime);

    @OneToMany
    public Receivers[] getReceivers();
}
