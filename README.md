jmx-annotations
===============

JMX annotations for CDI. These extensions were tested to work with
Weld under JBoss, Tomcat, and Java SE.

Example use:
````
import javax.enterprise.context.ApplicationScoped;
import javax.management.Description;
import javax.management.MBean;
import javax.management.ManagedAttribute;
import javax.management.ManagedOperation;

@MBean
@ApplicationScoped
public class BusinessBean {

    @ManagedAttribute
    @Description("HTTP response status returned to the user")
    public int getStatus() {
...
    }

    @ManagedOperation
    @Description("Resets the error and success counts")
    public void resetCount() {
...
}

```

Refer to the JavaDoc for more information.

Note that extensions for supporting use with RHQ exist as well.
These annotations will work with an RHQ plugin generator for JMX.

See: http://rhq.jboss.org
