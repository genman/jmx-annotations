package net.noderunner.management;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.JMException;
import javax.management.MBean;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles registration of an MBean.
 *
 * @param <T> type wrapped
 */
class ManagementInjectionTarget<T> implements InjectionTarget<T> {

    private static Logger log = LoggerFactory.getLogger(ManagementInjectionTarget.class);

    private final AnnotatedType<T> at;
    private final InjectionTarget<T> delegate;
    private final BeanManager bm;
    private MBeanServer mbeanServer;
    private ObjectName objectName;

    /**
     * Constructs a new wrapper.
     */
    public ManagementInjectionTarget(AnnotatedType<T> at, InjectionTarget<T> delegate, BeanManager bm) {
        this.at = at;
        this.delegate = delegate;
        this.bm = bm;
    }

    @Override
    public void inject(T instance, CreationalContext<T> ctx) {
        delegate.inject(instance, ctx);
    }

    /**
     * After constructing an instance, registers the MBean in the MBean server.
     * Note that because {@link #preDestroy(Object)} often fails to be called,
     * this method will replace any existing MBean with the same name. This happens
     * quite frequently with unit testing.
     */
    @Override
    public void postConstruct(T instance) {
        delegate.postConstruct(instance);

        if (!at.isAnnotationPresent(MBean.class)) {
            log.debug(at.getJavaClass().getName() + " has no @MBean; not registering");
            return;
        }

        try {
            MBeanFactory<T> mbeanFactory = new MBeanFactory<T>(at, instance);
            DynamicMBean mbean = mbeanFactory.createMBean();
            mbeanServer = getServer();
            objectName = this.getObjectName(mbean, at);

            log.info("registering " + objectName);
            try {
                mbeanServer.registerMBean(mbean, objectName);
            } catch (InstanceAlreadyExistsException e) {
                log.error("replacing instance of " + objectName + " due to " + e);
                mbeanServer.unregisterMBean(objectName);
                mbeanServer.registerMBean(mbean, objectName);
            }
            log.debug("success");
        } catch (JMException e) {
            throw new ManagementException(e);
        }

    }

    private MBeanServer getServer() {
        Bean<MBeanServer> bean = (Bean<MBeanServer>) bm.resolve(bm.getBeans(MBeanServer.class));
        if (bean == null)
            throw new IllegalStateException("MBeanServer bean null");
        CreationalContext<MBeanServer> creationalContext = bm.createCreationalContext(bean);
        return bean.create(creationalContext);
    }

    @Override
    public void preDestroy(T instance) {
        delegate.preDestroy(instance);
        if (objectName == null) {
            return;
        }
        if (!mbeanServer.isRegistered(objectName)) {
            return;
        }
        try {
            log.debug("unregistering " + objectName);
            mbeanServer.unregisterMBean(objectName);
        } catch (JMException e) {
            throw new ManagementException(e);
        }

    }

    @Override
    public void dispose(T instance) {
        delegate.dispose(instance);
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return delegate.getInjectionPoints();
    }

    @Override
    public T produce(CreationalContext<T> ctx) {
        return delegate.produce(ctx);
    }

    private ObjectName getObjectName(DynamicMBean mbean, AnnotatedType<T> at) throws JMException {
        MBeanRegister register = new MBeanRegister(bm, mbeanServer);
        return register.createObjectName(mbean, at);
    }

}
