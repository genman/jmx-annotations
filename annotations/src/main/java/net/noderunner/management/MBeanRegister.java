package net.noderunner.management;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.management.DynamicMBean;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ObjectNameTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper for {@link javax.enterprise.inject.Produces Producer} beans which produce MBean
 * instances. This 'register' holds a mapping of Object to ObjectNames, making
 * it possible to easily unregister in a {@linkplain javax.enterprise.inject.Disposes} method.
 * <p>
 * A typical producer looks like:
 *
 * <pre>
 * &#64;MBean
 * &#64;ObjectNameTemplate(":type=Counter,name={Name}")
 * &#64;OnDemand
 * class Counter {
 *     String name;
 *     public Counter(String name) { this.name = name; }
 *     public String getName() { return name; }
 * }
 *
 * public class CounterProducer {
 *
 *     &#64;Inject MBeanRegister register;
 *
 *     &#64;Produces Counter produces(InjectionPoint point) {
 *         Counter c = new Counter("foo");
 *         register.register(c);
 *         return c;
 *     }
 *
 *     void dispose(&#64;Disposes Counter c) {
 *         register.unregister(c);
 *     }
 *
 * }
 * </pre>
 */
@ApplicationScoped
public class MBeanRegister {

    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    /**
     * Java literal.
     */
    private static final Pattern literal = Pattern.compile("\"?\\{([\\p{L}\\p{Nd}_]+)\\}\"?");

    /**
     * Remembers the object name for objects registered. To be removed when
     * moving to CDI 1.1.
     */
    private final Map<Object, ObjectName> registry = new IdentityHashMap<Object, ObjectName>();

    private BeanManager beanManager;

    private MBeanServer server;

    /**
     * Constructs a new instance.
     */
    @Inject
    public MBeanRegister(BeanManager beanManager, MBeanServer server) {
        this.beanManager = beanManager;
        this.server = server;
    }

    MBeanRegister() {
    }

    /**
     * Register an object in the MBeanServer, as well as associates an instance
     * to an object name in an internal map.
     * {@link #unregister(Object)} must be called to free a reference to this object.
     *
     * @param instance
     *            annotated instance
     * @param name
     *            name to register this object under; if null then the name is
     *            build using {@link #createObjectName}.
     *
     * @return object instance value, containing the built name
     * @throws ManagementException
     *             if registration fails
     */
    public <T> ObjectInstance register(T instance, ObjectName name) {
        if (instance == null)
            throw new NullPointerException("instance");
        Class<T> clazz = (Class<T>) instance.getClass();
        AnnotatedType<T> at = beanManager.createAnnotatedType(clazz);
        try {
            MBeanFactory<T> mbeanFactory = new MBeanFactory<T>(at, instance);
            DynamicMBean mbean = mbeanFactory.createMBean();
            if (name == null) {
                name = createObjectName(mbean, at);
            }
            log.debug("register " + instance.getClass().getSimpleName() + " " + name);
            // inject(instance, name);
            ObjectInstance oi = server.registerMBean(mbean, name);
            synchronized (registry) {
                registry.put(instance, name);
            }
            return oi;
        } catch (Exception e) {
            throw new ManagementException(e);
        }
    }

    /**
     * Supported in JDK 8 - probably not needed.
    private void inject(Object instance, ObjectName name) throws Exception {
        for (Field f : instance.getClass().getDeclaredFields()) {
            Resource r = f.getAnnotation(Resource.class);
            if (r != null) {
                Class c = r.type().equals(Object.class) ? f.getType() : r.type();
                f.setAccessible(true);
                if (c.equals(MBeanServer.class))
                    f.set(instance, this.server);
                if (c.equals(ObjectName.class))
                    f.set(instance, name);
            }
        }
    }
     */

    /**
     * Register a new instance, creating a name using
     * {@link #createObjectName(DynamicMBean, AnnotatedType)}.
     *
     * @return new instance created, including its object name
     */
    public ObjectInstance register(Object object) {
        return register(object, null);
    }

    /**
     * Unregister an MBean registered and returned by {@link #register(Object)}.
     */
    public void unregister(Object o) {
        ObjectName name;
        synchronized (registry) {
            name = registry.remove(o);
        }
        if (name == null)
            // There seems to be a bug with multiple disposes
            // throw new IllegalStateException(o + " not registered");
            return;
        log.debug("unregister " + o.getClass().getSimpleName() + " " + name);
        unregister(name);
    }

    private void unregister(ObjectName name) {
        try {
            server.unregisterMBean(name);
        } catch (JMException e) {
            throw new ManagementException(e);
        }
    }

    /**
     * Returns a built object name for the given {@link DynamicMBean} and
     * annotated type. If the type is not annotated with
     * {@linkplain ObjectNameTemplate}, then a name is created using to the
     * package as domain and Java class name as a type.
     * For example: {@literal java.lang:type=Object} would be the MBean name for {@linkplain Object}.
     *
     * @see ObjectNameTemplate
     */
    public ObjectName createObjectName(DynamicMBean mbean, AnnotatedType<?> at) throws JMException {
        ObjectNameTemplate named = at.getAnnotation(ObjectNameTemplate.class);
        String name;
        if (named == null) {
            name = at.getJavaClass().getPackage().getName() + ":type=" + at.getJavaClass().getSimpleName();
        } else {
            name = named.value();
            name = buildName(mbean, name);
        }

        return new ObjectName(name);
    }

    /**
     * Returns true if the name is fixed.
     */
    static boolean fixedName(String name) {
        Matcher m = literal.matcher(name);
        return !m.find();
    }

    /**
     * Returns from a templatized name and MBean an ObjectName in String format.
     * Does not yet handle all the unusual rules, but supports quoting.
     */
    static String buildName(DynamicMBean mbean, String name) throws JMException {
        Matcher m = literal.matcher(name);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            boolean quote = m.group(0).charAt(0) == '"';
            String attribute = m.group(1);
            Object attr = mbean.getAttribute(attribute);
            String s = String.valueOf(attr);
            if (quote) {
                s = ObjectName.quote(s);
                m.appendReplacement(sb, "");
                sb.append(s);
            } else {
                m.appendReplacement(sb, String.valueOf(attr));
            }
        }
        m.appendTail(sb);
        name = sb.toString();
        return name;
    }

    /**
     * Catch unremoved instances.
     */
    @PreDestroy
    void destroy() {
        for (ObjectName on : registry.values())
            unregister(on);
        registry.clear();
    }

}
