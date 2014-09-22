package javax.management;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated class is a Standard MBean.  A Standard
 * MBean class can be defined as in this example:
 <PRE>
 &#64;MBean
 public class Configuration {
     &#64;ManagedAttribute
     public int getCacheSize() {...}
  }
</PRE>
 * The class must be public.  Public methods within the class can be
 * annotated with {@code @ManagedOperation} to indicate that they are
 * MBean operations.  Public getter and setter methods within the class
 * can be annotated with {@code @ManagedAttribute} to indicate that they define
 * MBean attributes.
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD, FIELD, PARAMETER })
public @interface MBean {
}
