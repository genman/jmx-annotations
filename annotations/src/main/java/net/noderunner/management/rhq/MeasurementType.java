package net.noderunner.management.rhq;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.management.DescriptorKey;

/**
 * MBean attribute measurement type, similar to 'metricType', but indicates
 * trending up or down.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MeasurementType {

	/**
     * Metric measurement type: dynamic (default), trendsup, trendsdown
	 */
    @DescriptorKey("measurementType")
    String value();

}
