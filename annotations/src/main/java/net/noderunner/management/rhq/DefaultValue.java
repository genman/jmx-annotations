package net.noderunner.management.rhq;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.management.DescriptorKey;

/**
 * MBean operation default parameter value.
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultValue {

	/**
     * Default value for this operation paramater.
	 */
    @DescriptorKey("defaultValue")
    String value();

}
