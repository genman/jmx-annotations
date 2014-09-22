package net.noderunner.management;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.management.Attribute;
import javax.management.MBean;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

/**
 * Unit tests this extension.
 */
public class AutoRegistrationTest extends Arquillian {

    @Inject
    private CounterAutoRegisterWithName counterWithName;

    @Inject
    private MBeanServer mBeanServer;

    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    @Test
    public void shouldRegisterAnnotatedWithNameMBean() throws Exception {
        log.info("shouldRegisterAnnotatedWithNameMBean");
        ObjectName name = new ObjectName(":type=CounterAutoRegisterWithName");
        assert mBeanServer.isRegistered(name) : "registered";

        MBeanInfo info = mBeanServer.getMBeanInfo(name);

        assertEquals(info.getConstructors().length, 1);
        assertEquals(info.getAttributes().length, 4);
        boolean ok = false;
        for (MBeanAttributeInfo i : info.getAttributes()) {
            if (i.getName().equals("Counter")) {
                assertEquals(i.getType(), "int");
                assertEquals(i.getDescription(), "42");
                List<String> desc = Arrays.asList(i.getDescriptor().getFields());
                log.info("desc " + desc);
                assertTrue(desc.contains("units=bytes"));
                assertTrue("" + desc, desc.contains("defaultInterval=(60000)"));
                assertTrue("" + desc, desc.contains("category=utilization"));
                ok = true;
            }
        }
        assertEquals(ok, true);
        assertEquals(info.getOperations().length, 2);

        assertNotNull(counterWithName);

        // check counter is set
        CounterMBean mbean = MBeanServerInvocationHandler.newProxyInstance(mBeanServer, name, CounterMBean.class, false);
        assertEquals(mbean.getCounter(), 0);

        // check we can add the counter
        mBeanServer.setAttribute(name, new Attribute("Counter", 42));
        assertEquals(counterWithName.getCounter(), 42);

        // check we can retrieve the counter
        assertEquals(mbean.getCounter(), 42);

        // check we can't add the counter with wrong type
        try {
            mBeanServer.setAttribute(name, new Attribute("Counter", "42"));
            fail();
        } catch (ReflectionException e) {
        }

        mbean.resetCounter();
        assertEquals(counterWithName.getCounter(), 0);

        mbean.resetCounter2(55);
        assertEquals(mbean.getCounter(), 55);

        mBeanServer.invoke(name, "resetCounter2", new Object[] { 5 }, new String[] { "int" });
        assertEquals(counterWithName.getCounter(), 5);

        // check call fails with unknown method
        try {
            mBeanServer.invoke(name, "foo", new Object[] { }, new String[] { });
            fail();
        } catch (ReflectionException e) {
        }

        // check call fails with wrong type
        try {
            mBeanServer.invoke(name, "resetCounter2", new Object[] { 5L }, new String[] { "java.lang.Long" });
            fail();
        } catch (ReflectionException e) {
        }
    }

    @Test
    public void shouldRegisterAnnotatedWithNoNameMBean() throws Exception {
        log.info("shouldRegisterAnnotatedWithNoNameMBean");
        String pack = getClass().getPackage().getName();
        Object result = mBeanServer.getAttribute(new ObjectName(pack + ":type=CounterAutoRegisterNoName"), "Counter");
        assertNotNull(result);
    }

    @Deployment
    public static JavaArchive createDeployment() {
        String jar = "test.jar";
        return createDeployment(jar);
    }

    public static JavaArchive createDeployment(String jar) {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class, jar)
                .addPackage(MBean.class.getPackage())
                .addPackage(MBeanServerLocator.class.getPackage())
                .addPackage(AutoRegistrationTest.class.getPackage())
                .addAsManifestResource("META-INF/services/javax.enterprise.inject.spi.Extension",
                        "services/javax.enterprise.inject.spi.Extension")
                .addAsManifestResource("META-INF/jboss-deployment-structure.xml",
                        "jboss-deployment-structure.xml")
                        .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));

        File f = new File(jar);
        f.delete();
        archive.as(ZipExporter.class).exportTo(f);
        return archive;
    }

}
