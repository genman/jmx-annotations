package net.noderunner.management.rhq;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.management.DescriptorKey;

/**
 * Indicates if metric should be gathered by default.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultOn {

    /**
     * If metric should be gathered by default, by default false.
     */
    @DescriptorKey("defaultOn")
    boolean value();

}