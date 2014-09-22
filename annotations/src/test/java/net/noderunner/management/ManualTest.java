package net.noderunner.management;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

/**
 * Manual and on demand registration of MBeans.
 */
public class ManualTest extends Arquillian {

    private final Logger log = LoggerFactory.getLogger(getClass().getName());
    private final static String BAR = ":type=Counter,name=bar";
    private final static String BAZ = ":type=Counter,name=baz";

    /**
     * Inject a new counter with name {@link #BAR}.
     */
    @Inject @Testing
    Counter bar;

    /**
     * Inject a new counter with name {@link #BAZ}.
     */
    @Inject @Testing
    Counter baz;

    @Inject
    MBeanServer server;

    @Inject
    MBeanRegister register;

    /**
     * On demand counter. Should be created and destroyed after test method is run.
     */
    @Inject @Any Instance<OnDemandCounter> demanded;

    @Deployment
    public static JavaArchive createDeployment() {
        return AutoRegistrationTest.createDeployment("manual.jar");
    }

    @Test
    public void testProducerMethods() throws Exception {
        log.debug("testProducerMethods");
        bar.setCounter(42);
        baz.setCounter(55);
        log.info("counter");

        log.debug("- mbean info");
        MBeanInfo info = server.getMBeanInfo(new ObjectName(BAR));
        assertEquals(info.getDescription(), "counts");
        List<String> desc = Arrays.asList(info.getDescriptor().getFields());
        assertTrue(desc.contains("subCategory=counters"));

        MBeanNotificationInfo ni = info.getNotifications()[0];
        assertEquals(ni.getDescription(), "note");
        assertEquals(ni.getName(), "java.lang.Void");
        assertEquals(ni.getNotifTypes()[0], "java.lang.Integer");

        Object attribute = server.getAttribute(new ObjectName(BAR), "Counter");
        assertEquals(attribute, 42);
        attribute = server.getAttribute(new ObjectName(BAZ), "Counter");
        assertEquals(attribute, 55);
        assertEquals(bar.getServer(), null);
    }

    @Test
    public void testManual() throws Exception {
        log.debug("testManual");
        ObjectName on = new ObjectName(":type=CounterX");
        Counter counterX = new Counter();
        register.register(counterX, on);
        register.unregister(counterX);
        register.unregister(counterX);
        assertEquals(counterX.getServer(), null);
    }

    @Test
    public void testDemand() throws Exception {
        log.debug("testDemand");
        ObjectName on = new ObjectName(":type=OnDemandCounter");
        assertEquals(this.server.isRegistered(on), false);
        OnDemandCounter od = demanded.get();
        Object attribute = server.getAttribute(on, "Counter");
        assertEquals(od.getCounter(), 0);
        assertEquals(attribute, 0);
    }

    @Test(dependsOnMethods={"testDemand"})
    public void testDemandDone() throws Exception {
        log.debug("testDemandDone");
        ObjectName on = new ObjectName(":type=OnDemandCounter");
        assertEquals(this.server.isRegistered(on), false);
    }

}
