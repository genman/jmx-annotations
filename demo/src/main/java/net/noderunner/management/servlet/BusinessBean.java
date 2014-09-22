package net.noderunner.management.servlet;

import static javax.servlet.http.HttpServletResponse.SC_OK;

import javax.enterprise.context.ApplicationScoped;
import javax.management.Description;
import javax.management.MBean;
import javax.management.ManagedAttribute;
import javax.management.ManagedOperation;
import javax.management.ObjectNameTemplate;

import net.noderunner.management.rhq.DataType;
import net.noderunner.management.rhq.DefaultInterval;
import net.noderunner.management.rhq.MeasurementType;
import net.noderunner.management.rhq.Units;

@MBean
@ApplicationScoped
@ObjectNameTemplate("net.noderunner:type=BusinessBean")
public class BusinessBean {

    private volatile int status = SC_OK;
    private volatile int requestCount;
    private volatile int errorCount;
    private volatile int bytesSent;

    public String doSomething(String input) {
        requestCount++;
        String result;
        if (status != SC_OK)
            result = "Error! " + errorCount++;
        else
            result = "Request count " + requestCount;
        bytesSent += result.length();
        return result;
    }

    @ManagedAttribute
    @Description("HTTP response status returned to the user")
    @DataType("trait")
    @DefaultInterval(60000)
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @ManagedAttribute
    @Description("Number of non-200 HTTP status responses")
    @DefaultInterval(60000)
    @MeasurementType("trendsup")
    public int getErrorCount() {
        return errorCount;
    }

    @ManagedAttribute
    @Description("Number of 200 HTTP status responses")
    @DefaultInterval(60000)
    @MeasurementType("trendsup")
    public int getRequestCount() {
        return requestCount;
    }

    @ManagedAttribute
    @Units("bytes")
    @Description("Total bytes sent in response")
    @MeasurementType("trendsup")
    @DefaultInterval(1000 * 60 * 5)
    public long getBytesSent() {
        return bytesSent;
    }

    @ManagedOperation
    @Description("Resets the error and success counts")
    public void resetCount() {
        requestCount = 0;
        errorCount = 0;
    }

}
