package net.noderunner.management;

import static org.testng.AssertJUnit.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.inject.Inject;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

/**
 * Unit tests this servlet.
 */
public class DemoTest extends Arquillian {

    @Inject
    private MBeanServer mBeanServer;

    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    @Deployment
    public static WebArchive createTestArchive() {

        File f[] = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .resolve("net.noderunner:jmx-annotations")
                .withoutTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class, "demo.war") // Create jar
                .addPackages(true, "net.noderunner.management.servlet")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml") // b
                .addAsLibraries(f);
    }

    @Test(dataProvider = Arquillian.ARQUILLIAN_DATA_PROVIDER)
    @RunAsClient
    public void testClient(@ArquillianResource URL baseUrl) throws Exception {
        URL url = new URL(baseUrl, "/demo/demo");
        InputStream is = url.openStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = br.readLine();
        br.close();
        assertEquals("Request count 1", line);
    }

    @Test(dependsOnMethods="testClient")
    public void testMBean() throws Exception {
        log.info("shouldRegisterAnnotatedWithNameMBean");

        ObjectName name = new ObjectName("net.noderunner:type=BusinessBean");
        assert mBeanServer.isRegistered(name) : "registered";
        assertEquals(1, mBeanServer.getAttribute(name, "RequestCount"));
        assertEquals(0, mBeanServer.getAttribute(name, "ErrorCount"));
    }

}
