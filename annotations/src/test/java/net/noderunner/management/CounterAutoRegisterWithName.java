package net.noderunner.management;

import javax.enterprise.context.ApplicationScoped;
import javax.management.MBean;
import javax.management.ObjectNameTemplate;


@MBean
@ApplicationScoped
@ObjectNameTemplate(":type=CounterAutoRegisterWithName")
public class CounterAutoRegisterWithName extends Counter {
}
