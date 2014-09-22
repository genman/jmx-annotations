/**
 * This package defines a CDI extension, a marker annotation, and some JMX
 * utilities for registering your annotated Java Beans in a {@link javax.management.MBeanServer}.
 * <p>
 * Available beans available from this package:
 * <ul>
 * <li> {@linkplain javax.management.MBeanServer} created by {@linkplain net.noderunner.management.MBeanServerLocator}
 * <li> {@linkplain net.noderunner.management.MBeanRegister}
 * </ul>
 * <p>
 * There are two patterns:
 * <p>
 * <h2>Register a 'singleton' or {@linkplain javax.enterprise.context.ApplicationScoped} in JMX
 * using a fixed {@linkplain javax.management.ObjectName}</h2>
 * <ol>
 * <li>Mark your attributes (defined using 'getters' and 'setters') using {@linkplain javax.management.ManagedAttribute}
 * <li>Mark your operations using {@linkplain javax.management.ManagedOperation}
 * <li>Mark your class using {@linkplain javax.management.MBean}
 * </ol>
 *
 * <h2>Register multiple instances of an MBean using templetized ObjectNames. </h2>
 * <ol>
 * <li>Annotate your MBean with {@linkplain net.noderunner.management.OnDemand} so it isn't
 * registered automatically.
 * <li>Create a producer that injects {@linkplain net.noderunner.management.MBeanRegister}.
 * <li>Call {@linkplain net.noderunner.management.MBeanRegister#register(Object)}
 * when creating this instance.
 * <li>Call {@linkplain net.noderunner.management.MBeanRegister#unregister(Object)} on this object when done.
 * </ol>
 *
 * <h2>Notes</h2>
 * By default, MBeans are registered at startup. This may be not what you want.
 * Add the annotation {@linkplain net.noderunner.management.OnDemand} to prevent startup registration.
 * <p>
 */
package net.noderunner.management;
