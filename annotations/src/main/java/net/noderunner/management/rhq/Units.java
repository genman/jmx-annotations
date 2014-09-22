package net.noderunner.management.rhq;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.management.DescriptorKey;

/**
 * MBean attribute units or parameter units.
 */
@Documented
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Units {

    /**
     * Units of this metric.
     * Supported strings in RHQ are:
     * <ul>
     * <li> none
     * <li> percentage (1 meaning 100%)
     * </ul>
     * Bytes:
     * <ul>
     * <li> bytes
     * <li> kilobytes
     * <li> megabytes
     * <li> gigabytes
     * <li> terabytes
     * <li> petabytes
     * </ul>
     * Bits:
     * <ul>
     * <li> bits
     * <li> kilobits
     * <li> megabits
     * <li> gigabits
     * <li> terabits
     * <li> petabits
     * </ul>
     * Time:
     * <ul>
     * <li> epoch_milliseconds
     * <li> epoch_seconds
     * <li> jiffys
     * <li> nanoseconds
     * <li> microseconds
     * <li> milliseconds
     * <li> seconds
     * <li> minutes
     * <li> hours
     * <li> days
     * </ul>
     * Temperature:
     * <ul>
     * <li> celsius
     * <li> kelvin
     * <li> fahrenheight
     * </ul>
     */
    @DescriptorKey("units")
    String value();

}
