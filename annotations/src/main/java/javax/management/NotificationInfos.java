package javax.management;

/**
 * Specifies the kinds of notification an MBean can emit, when this cannot be
 * represented by a single NotificationInfo annotation.
 */
@java.lang.annotation.Documented
@java.lang.annotation.Inherited
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.TYPE })
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface NotificationInfos {

    /**
     * The NotificationInfo annotations.
     */
    NotificationInfo[] value();

}