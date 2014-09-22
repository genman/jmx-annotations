package net.noderunner.management.rhq;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.management.DescriptorKey;

/**
 * For an MBean attribute which is a metric, describes how often to gather data.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultInterval {

    /**
     * Metric gathering interval in milliseconds.
     */
    @DescriptorKey("defaultInterval")
    int value();

}
