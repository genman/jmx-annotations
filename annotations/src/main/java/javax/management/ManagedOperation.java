package javax.management;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that a method in an MBean class defines an MBean operation.
 */
@Documented
@Retention(value=RUNTIME)
@Target(value={METHOD})
public @interface ManagedOperation {

    /**
     * Returns the impact; default is {@link Impact#UNKNOWN}.
     */
    Impact impact() default Impact.UNKNOWN;

}
