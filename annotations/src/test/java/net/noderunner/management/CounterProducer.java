package net.noderunner.management;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import net.noderunner.management.MBeanRegister;


/**
 * A producer returning instances for testing.
 */
public class CounterProducer {

    @Inject
    MBeanRegister register;

    BeanManager bm;

    @Produces @Testing
    Counter produces(InjectionPoint point) {
        Counter c = new Counter();
        c.setName(point.getMember().getName());
        register.register(c);
        return c;
    }

    void dispose(@Disposes @Testing Counter c) {
        register.unregister(c);
    }

}
