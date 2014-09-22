package javax.management;

/**
 * Specifies the kinds of notification an MBean can emit.
 */
@java.lang.annotation.Documented
@java.lang.annotation.Inherited
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.TYPE })
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface NotificationInfo {

    /**
     * The notification types that this MBean can emit.
     */
    String[] types();

    /**
     * The class that emitted notifications will have. It is recommended that
     * this be Notification, or one of its standard subclasses in the JMX API.
     */
    Class<?> notificationClass() default Notification.class;

    /**
     * The description of this notification.
     */
    Description description() default @Description(value = "");

    /**
     * Additional descriptor fields for the derived MBeanNotificationInfo. They
     * are specified in the same way as for the @DescriptorFields annotation.
     */
    String[] descriptorFields() default {};

}