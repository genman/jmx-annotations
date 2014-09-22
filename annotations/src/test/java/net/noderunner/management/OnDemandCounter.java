package net.noderunner.management;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.management.MBean;
import javax.management.ObjectNameTemplate;

import net.noderunner.management.OnDemand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean created on demand.
 */
@MBean
@ObjectNameTemplate(":type=OnDemandCounter")
@OnDemand
public class OnDemandCounter extends Counter {

    private Logger log = LoggerFactory.getLogger(getClass().getName());

    public OnDemandCounter() {
    }

    @PostConstruct
    public void construct() {
        log.debug("post construct");
    }

    @PreDestroy
    public void destroy() {
        log.debug("pre destroy");
    }

}
