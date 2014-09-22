package net.noderunner.management;

import java.util.ArrayList;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Producer that locates the MBean server to use.
 * Set the system property {@link #AGENT_ID} to specify the agent ID to use.
 */
public class MBeanServerLocator {

    private static final Logger log = LoggerFactory.getLogger(MBeanServerLocator.class);

    /**
     * Agent ID property <code>net.noderunner.management.AgentId</code>.
     */
    public static final String AGENT_ID = "net.noderunner.management.AgentId";

    private final String agentId = System.getProperty(AGENT_ID);

    MBeanServerLocator() {
        log.debug("constructor");
    }

    /**
     * Produces and returns the default MBean server to use.
     * This is used to register MBeans.
     * The MBeanServer can be overridden by specifying another factory with {@link Alternative},
     * or by setting {@linkplain #AGENT_ID} at startup.
     */
    @Produces @ApplicationScoped
    public MBeanServer getMBeanServer() {
        log.debug("getMBeanServer agentId={}", agentId);
        MBeanServer mBeanServer;
        ArrayList<MBeanServer> al = MBeanServerFactory.findMBeanServer(agentId);
        if (al.isEmpty())
            mBeanServer = MBeanServerFactory.createMBeanServer();
        else
            mBeanServer = al.get(0);
        log.debug("getMBeanServer found {}", mBeanServer);
        return mBeanServer;
    }

}
