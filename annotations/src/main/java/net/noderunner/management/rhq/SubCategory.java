package net.noderunner.management.rhq;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.management.DescriptorKey;

/**
 * For MBeans, indicates the sub category of this service.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubCategory {

	/**
	 * Returns the sub category of this service.
	 */
    @DescriptorKey("subCategory")
    String value();

    /**
     * Option description of this service.
     */
    @DescriptorKey(value="subCategory.description", omitIfDefault=true)
    String description() default "";

}
