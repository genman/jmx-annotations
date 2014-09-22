package javax.management;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that a method in an MBean class defines an MBean attribute.
 * This annotation must be applied to a public method of a public class
 * that is itself annotated.
 * The annotated method must be a getter or setter.
 */
@Documented
@Retention(value=RUNTIME)
@Target(value={METHOD})
public @interface ManagedAttribute {
}
