package net.noderunner.management;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.management.Description;
import javax.management.Descriptor;
import javax.management.DynamicMBean;
import javax.management.ImmutableDescriptor;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ManagedAttribute;
import javax.management.ManagedOperation;
import javax.management.NotificationInfo;
import javax.management.NotificationInfos;


/**
 * Factory for dynamic MBean classes.
 */
public class MBeanFactory<T> {

    private static Method descriptorMethod;

    static {
        try {
            Class<?> c = Class.forName("com.sun.jmx.mbeanserver.Introspector");
            descriptorMethod = c.getMethod("descriptorForAnnotations", new Annotation[0].getClass());
        } catch (Exception e) {
            System.err.println("inspector unavailable " + e);
        }
    }

    /**
     * Name of the class.
     */
    private String className;

    /**
     * Description of the MBean.
     */
    private String description;

    /**
     * Constructors of the MBean.
     */
    private final Set<MBeanConstructorInfo> mBeanConstructors = new HashSet<MBeanConstructorInfo>();

    /**
     * Attributes of the MBean.
     */
    private final Set<MBeanAttributeInfo> mBeanAttributes = new HashSet<MBeanAttributeInfo>();

    /**
     * Operations of the MBean.
     */
    private final Set<MBeanOperationInfo> mBeanOperations = new HashSet<MBeanOperationInfo>();

    /**
     * Methods exposed in the MBean.
     */
    private final Set<Method> exposedMethods = new HashSet<Method>();

    /**
     * Methods that are allowed as attributes, introspected by {@link Introspector}.
     */
    private final Map<String, PropertyDescriptor> attributeMethods = new HashMap<String, PropertyDescriptor>();

    /**
     * Generated MBeanInfo.
     */
    private final MBeanInfo mbeanInfo;

    /**
     * Notification information.
     */
    private final Set<MBeanNotificationInfo> notificationInfo = new HashSet<MBeanNotificationInfo>();

    private final T instance;

    /**
     * Constructs a factory for the annotated type and instance.
     * The annotated type instance can be obtained from calling {@link BeanManager#createAnnotatedType(Class)}.
     * @throws IntrospectionException
     */
    public MBeanFactory(AnnotatedType<T> at, T instance) throws IntrospectionException {
        this.instance = instance;
        processAnnotatedType(at);

        Descriptor descriptor = null;
        if (descriptorMethod != null) {
            Annotation[] annotations = at.getAnnotations().toArray(new Annotation[0]);
            try {
                descriptor = (Descriptor) descriptorMethod.invoke(null, (Object)annotations);
            } catch (Exception e) {
                throw (IntrospectionException)new IntrospectionException().initCause(e);
            }
        } else {
            // no introspector -- write our own?
        }
        mbeanInfo = new MBeanInfo(this.className, this.description, getMBeanAttributes(),
                getMBeanConstructors(), getMBeanOperations(), getMBeanNotificationInfo(),
                descriptor);
    }

    /**
     * Returns a newly created dynamic MBean.
     *
     * @throws ManagementException upon failure
     */
    public DynamicMBean createMBean() {
        MBeanImpl mBean = new MBeanImpl(instance, this);
        return mBean;
    }

    private void processAnnotatedType(AnnotatedType<T> at) throws IntrospectionException {
        type(at);
        Set<AnnotatedConstructor<T>> annConstructors = at.getConstructors();
        for (AnnotatedConstructor<T> ac : annConstructors) {
            constructor(ac);
        }
        Set<AnnotatedMethod<? super T>> annMethods = at.getMethods();
        for (AnnotatedMethod<? super T> am : annMethods) {
            method(am);
        }
    }

    private String cap(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private void type(AnnotatedType<T> at) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(at.getJavaClass());
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                String name = pd.getName();
                attributeMethods.put(cap(name), pd);
            }
        } catch (java.beans.IntrospectionException e) {
            throw new ManagementException(e);
        }
        this.className = at.getJavaClass().getName();

        if (at.isAnnotationPresent(Description.class)) {
            Description annDescription = at.getAnnotation(Description.class);
            this.description = annDescription.value();
        }
        NotificationInfo ni = at.getAnnotation(NotificationInfo.class);
        if (ni != null) {
            addInfo(ni);
        }
        NotificationInfos nis = at.getAnnotation(NotificationInfos.class);
        if (nis != null) {
            for (NotificationInfo ni2 : nis.value())
                addInfo(ni2);
        }
    }

    void addInfo(NotificationInfo ni) {
        String types[] = ni.types();
        String name = ni.notificationClass().getName();
        ImmutableDescriptor id = new ImmutableDescriptor(ni.descriptorFields());
        MBeanNotificationInfo mni = new MBeanNotificationInfo(types, name, ni.description().value(), id);
        this.notificationInfo.add(mni);
    }

    protected PropertyDescriptor getPropertyDescriptor(Method m) {
        for (PropertyDescriptor pd : this.attributeMethods.values()) {
            if (m.equals(pd.getReadMethod()) || m.equals(pd.getWriteMethod())) {
                return pd;
            }
        }
        return null;
    }

    private void constructor(AnnotatedConstructor<T> ac) {
        String consDescription = "";
        if (ac.isAnnotationPresent(Description.class)) {
            Description annDescription = ac.getAnnotation(Description.class);
            consDescription = annDescription.value();
        }

        MBeanConstructorInfo constructorInfo = new MBeanConstructorInfo(consDescription,
                ac.getJavaMember());
        this.mBeanConstructors.add(constructorInfo);
    }

    private void method(AnnotatedMethod<? super T> am) throws javax.management.IntrospectionException {
        Method m = am.getJavaMember();
        String desc = "";
        if (am.isAnnotationPresent(Description.class)) {
            Description annDescription = am.getAnnotation(Description.class);
            desc = annDescription.value();
        }
        if (am.isAnnotationPresent(ManagedAttribute.class)) {
            PropertyDescriptor pd = getPropertyDescriptor(m);
            if (pd == null)
                throw new ManagementException("ManagedAttribute method not a JavaBean method: " + am);
            String name = cap(pd.getName());
            MBeanAttributeInfo attributeInfo = new MBeanAttributeInfo(name,
                    desc, pd.getReadMethod(), pd.getWriteMethod());
            mBeanAttributes.add(attributeInfo);
        }
        if (am.isAnnotationPresent(ManagedOperation.class)) {
            exposedMethods.add(m);
            Method method = am.getJavaMember();
            MBeanOperationInfo operationInfo = new MBeanOperationInfo(desc, method);
            mBeanOperations.add(operationInfo);
        }
    }

    public Map<String, PropertyDescriptor> getFields() {
        return attributeMethods;
    }

    public Set<Method> getMethods() {
        return exposedMethods;
    }

    public MBeanInfo getMBeanInfo() {
        return mbeanInfo;
    }

    private MBeanNotificationInfo[] getMBeanNotificationInfo() {
        return this.notificationInfo.toArray(new MBeanNotificationInfo[0]);
    }

    private MBeanConstructorInfo[] getMBeanConstructors() {
        return this.mBeanConstructors.toArray(new MBeanConstructorInfo[mBeanConstructors.size()]);
    }

    private MBeanAttributeInfo[] getMBeanAttributes() {
        return this.mBeanAttributes.toArray(new MBeanAttributeInfo[mBeanAttributes.size()]);
    }

    private MBeanOperationInfo[] getMBeanOperations() {
        return this.mBeanOperations.toArray(new MBeanOperationInfo[mBeanOperations.size()]);
    }

}
