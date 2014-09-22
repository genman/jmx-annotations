package net.noderunner.management;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;

/**
 * Implementation of a DynamicMBean, using reflection to retrieve the exposed
 * fields and operations of the instance.
 */
class MBeanImpl implements DynamicMBean {

    private final Object implementation;
    private final MBeanFactory<?> builder;
    private final MBeanInfo mBeanInfo;

    /**
     * Constructs a new MBean implementation.
     */
    public MBeanImpl(Object implementation, MBeanFactory<?> factory) {
        this.implementation = implementation;
        this.builder = factory;
        this.mBeanInfo = factory.getMBeanInfo();
    }

    @Override
    public Object getAttribute(String name) throws AttributeNotFoundException,
    MBeanException, ReflectionException {
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("attributeName cannot be null"));
        }
        PropertyDescriptor pd = builder.getFields().get(name);
        if (pd == null) {
            throw new AttributeNotFoundException("Cannot find " + name + " attribute in " + implementation.getClass());
        }
        if (pd.getReadMethod() == null) {
            throw new AttributeNotFoundException("Cannot read " + name);
        }

        try {
            return pd.getReadMethod().invoke(implementation);
        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }

    @Override
    public AttributeList getAttributes(String[] attributesNames) {
        if (attributesNames == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("attributeNames[] cannot be null"));
        }
        AttributeList resultList = new AttributeList();
        if (attributesNames.length == 0)
            return resultList;

        // build the result attribute list
        for (int i=0 ; i < attributesNames.length ; i++){
            try {
                Object value = getAttribute(attributesNames[i]);
                resultList.add(new Attribute(attributesNames[i], value));
            } catch (Exception e) {
            }
        }

        return resultList;
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException,
    MBeanException, ReflectionException {
        // check attribute is not null to avoid NullPointerException later on
        if (attribute == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"), "Cannot invoke a setter of " + mBeanInfo.getClassName() + " with null attribute");
        }

        String name = attribute.getName();
        Object value = attribute.getValue();

        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke the setter of " + mBeanInfo.getClassName() + " with null attribute name");
        }

        PropertyDescriptor pd = builder.getFields().get(name);
        if (pd == null) {
            throw new AttributeNotFoundException("Attribute " + name + " not found in " + mBeanInfo.getClassName());
        }
        if (pd.getWriteMethod() == null) {
            throw new AttributeNotFoundException("cannot write " + name);
        }

        try {
            pd.getWriteMethod().invoke(implementation, value);
        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        // check attributes is not null to avoid NullPointerException later on
        if (attributes == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("AttributeList attributes cannot be null"),
                    "Cannot invoke a setter of " + mBeanInfo.getClassName());
        }

        AttributeList resultList = new AttributeList();

        for (Attribute attr : attributes.asList()) {
            try {
                setAttribute(attr);
                String name = attr.getName();
                Object value = getAttribute(name);
                resultList.add(new Attribute(name,value));
            } catch (Exception e) {
            }
        }

        return resultList;
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature)
            throws MBeanException, ReflectionException {
        // check operationName is not null to avoid NullPointerException later on
        if (actionName == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"),
                    "Cannot invoke a null operation in " + mBeanInfo.getClassName());
        }

        // check for a recognized operation name and call the corresponding operation
        boolean operationFound = false;
        for (Method m : this.builder.getMethods()) {
            if (m.getName().equals(actionName) && same(m.getParameterTypes(), signature)) {
                operationFound = true;
                try {
                    return m.invoke(implementation, params);
                } catch (Exception e) {
                    throw new ReflectionException(e);
                }
            }
        }

        if (!operationFound) {
            // unrecognized operation name:
            throw new ReflectionException(new NoSuchMethodException(actionName),
                    "Cannot find the operation " + actionName + " in " + mBeanInfo.getClassName());
        }

        return null;
    }

    private boolean same(Class<?>[] types, String[] signature) {
        if (signature == null)
            return true; // always match on null
        if (types.length != signature.length)
            return false;
        for (int i = 0; i < types.length; i++) {
            if (!types[i].getName().equals(signature[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        return this.mBeanInfo;
    }

}
