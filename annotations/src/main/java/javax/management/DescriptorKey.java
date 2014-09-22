package javax.management;

/**
 * Meta-annotation that describes how an annotation element relates to a field
 * in a Descriptor. This can be the Descriptor for an MBean, or for an
 * attribute, operation, or constructor in an MBean, or for a parameter of an
 * operation or constructor.
 */
@java.lang.annotation.Documented
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.METHOD })
public abstract @interface DescriptorKey {

    public java.lang.String value();

    public boolean omitIfDefault() default false;

}