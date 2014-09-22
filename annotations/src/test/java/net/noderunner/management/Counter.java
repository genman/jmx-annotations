package net.noderunner.management;

import javax.inject.Inject;
import javax.management.Description;
import javax.management.Impact;
import javax.management.MBean;
import javax.management.MBeanServer;
import javax.management.ManagedAttribute;
import javax.management.ManagedOperation;
import javax.management.NotificationInfo;
import javax.management.NotificationInfos;
import javax.management.ObjectNameTemplate;

import net.noderunner.management.rhq.Category;
import net.noderunner.management.rhq.DefaultInterval;
import net.noderunner.management.rhq.DisplayName;
import net.noderunner.management.rhq.SubCategory;
import net.noderunner.management.rhq.Units;

@MBean
@Description("counts")
@ObjectNameTemplate(":type=Counter,name={Name}")
@NotificationInfos(
        {@NotificationInfo(types="java.lang.Integer",description=@Description("note"),notificationClass=Void.class)}
    )
@OnDemand
@SubCategory("counters")
public class Counter implements CounterMBean {

    @Inject
    private MBeanServer server;

    private int counter;
    private String name;

    @Override
    @ManagedOperation(impact=Impact.ACTION)
    @Description("Resets the counter to zero")
    public void resetCounter() {
        counter = 0;
    }

    @Override
    @ManagedOperation(impact=Impact.ACTION)
    public void resetCounter2(@Description("New value to use") @DisplayName("Value") int value) {
        counter = value;
    }

    @Override
    @ManagedAttribute
    @Description("42")
    @Units("bytes")
    @DefaultInterval(60000)
    @Category("utilization")
    public int getCounter() {
        return counter;
    }

    @Override
    public void setCounter(int counter) {
        this.counter = counter;
    }

    @ManagedAttribute
    public void setCounterHidden(int counter) {
        setCounter(counter);
    }

    @ManagedAttribute
    public String getCounterString() {
        return String.valueOf(counter);
    }

    @ManagedAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MBeanServer getServer() {
        return server;
    }

}
