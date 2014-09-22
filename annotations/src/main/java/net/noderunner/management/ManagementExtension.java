package net.noderunner.management;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.management.MBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CDI extension that registers MBean instances.
 */
public class ManagementExtension implements Extension {

    private static final Logger log = LoggerFactory.getLogger(ManagementExtension.class);

    /**
     * Set of beans to create at application startup.
     */
    private final Set<Bean<?>> startupBeans = new HashSet<Bean<?>>();

    /**
     * Called when an injection target is processed on bootstrap.
     * It wraps the InjectionTarget to add JMX registration.
     */
    public <T> void register(@Observes ProcessInjectionTarget<T> pit, BeanManager bm) {
        AnnotatedType<T> at = pit.getAnnotatedType();
        if (at.isAnnotationPresent(MBean.class)) {
            log.debug("adding automatic JMX registration for: " + at.getJavaClass().getName());
            InjectionTarget<T> delegate = pit.getInjectionTarget();
            InjectionTarget<T> wrapper = new ManagementInjectionTarget<T>(at, delegate, bm);
            pit.setInjectionTarget(wrapper);
        }
    }

    <X> void processBean(@Observes ProcessBean<X> event) {
        Annotated a = event.getAnnotated();
        if (a.isAnnotationPresent(MBean.class) && !a.isAnnotationPresent(OnDemand.class)) {
            startupBeans.add(event.getBean());
        }
    }

    void afterDeploymentValidation(@Observes AfterDeploymentValidation event, BeanManager manager) {
        for (Bean<?> bean : startupBeans) {
            // the call to toString() is a cheat to force the bean to be initialized
            manager.getReference(bean, bean.getBeanClass(), manager.createCreationalContext(bean)).toString();
        }
        startupBeans.clear();
    }

}