package javax.management;

/**
 * Annotation that adds fields to a Descriptor. This can be the Descriptor for
 * an MBean, or for an attribute, operation, or constructor in an MBean, or for
 * a parameter of an operation or constructor.
 */
@java.lang.annotation.Documented
@java.lang.annotation.Inherited
@java.lang.annotation.Target(value = {
        java.lang.annotation.ElementType.CONSTRUCTOR,
        java.lang.annotation.ElementType.METHOD,
        java.lang.annotation.ElementType.PARAMETER,
        java.lang.annotation.ElementType.TYPE })
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface DescriptorFields {

    /**
     * The descriptor fields.
     */
    java.lang.String[] value();

}