package net.noderunner.management;

import javax.enterprise.context.ApplicationScoped;
import javax.management.MBean;


@MBean
@ApplicationScoped
public class CounterAutoRegisterNoName extends Counter {
}