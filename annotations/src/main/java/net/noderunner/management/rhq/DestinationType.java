package net.noderunner.management.rhq;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.management.DescriptorKey;

/**
 * Calltime destination type value.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DestinationType {

    /**
     * Calltime destination type, string 1-100 characters long.
     */
    @DescriptorKey("destinationType")
    String value();

}
