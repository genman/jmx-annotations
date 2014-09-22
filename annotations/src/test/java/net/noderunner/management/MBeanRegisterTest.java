package net.noderunner.management;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import javax.management.AttributeNotFoundException;
import javax.management.JMException;
import javax.management.StandardMBean;

import net.noderunner.management.MBeanRegister;

import org.testng.annotations.Test;

@Test
public class MBeanRegisterTest {

    public void buildName() throws JMException {
        Counter c = new Counter();
        c.setName("foo");
        StandardMBean mbean = new StandardMBean(c, CounterMBean.class);
        String name = ":name={Name},counter={Counter}";
        name = MBeanRegister.buildName(mbean, name);
        assertEquals(name, ":name=foo,counter=0");
        name = ":name={Unknown}";
        try {
            name = MBeanRegister.buildName(mbean, name);
            fail("no attribute");
        } catch (AttributeNotFoundException e) {}
        
        name = ":name=\"{Name}\"";
        c.setName("*hi?");
        name = MBeanRegister.buildName(mbean, name);
        assertEquals(name, ":name=\"\\*hi\\?\"");
    }

}
