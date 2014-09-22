package javax.management;

/**
 * Annotation to allow an MBean to provide its name. This annotation can be used
 * on the following types:
 * <ul>
 * <li>MBean or MXBean Java interface.
 * <li>Java class annotated with <code>@MBean</code> annotation.
 * <li>Java class annotated with <code>@MXBean</code> annotation.
 * </ul>
 *
 * The value of this annotation is used to build the ObjectName when instances
 * of the annotated type are registered in an MBeanServer and no explicit name
 * is given to the createMBean or registerMBean method (the ObjectName is null).
 * <p>
 * For Dynamic MBeans, which define their own MBeanInfo, you can produce the
 * same effect as this annotation by including a field objectNameTemplate in the
 * Descriptor for the MBeanInfo returned by DynamicMBean.getMBeanInfo().
 * <p>
 * For Standard MBeans and MXBeans, this annotation automatically produces an
 * objectNameTemplate field in the Descriptor.
 * <p>
 * The template can contain variables so that the name of the MBean depends on
 * the value of one or more of its attributes. A variable that identifies an
 * MBean attribute is of the form {attribute name}. For example, to make an
 * MBean name depend on the Name attribute, use the variable {Name}. Attribute
 * names are case sensitive. Naming attributes can be of any type. The String
 * returned by toString() is included in the constructed name.
 * <p>
 * If you need the attribute value to be quoted by a call to ObjectName.quote,
 * surround the variable with quotes. Quoting only applies to key values. For
 * example, @ObjectNameTemplate("java.lang:type=MemoryPool,name=\"{Name}\""),
 * quotes the Name attribute value. You can notice the
 * "\" character needed to escape a quote within a String. A name produced by this template might look like
 * java.lang:type=MemoryPool,name="Code Cache".
 * <p>
 * Variables can be used anywhere in the String. Be sure to make the template
 * derived name comply with ObjectName syntax.
 * <p>
 * If an MBean is registered with a null name and it implements
 * MBeanRegistration, then the computed name is provided to the preRegister
 * method. Similarly, if the MBean uses resource injection to discover its name,
 * it is the computed name that will be injected.
 * <p>
 * All of the above can be used with the StandardMBean class and the annotation
 * is effective in that case too.
 * <p>
 * If any exception occurs (such as unknown attribute, invalid syntax or
 * exception thrown by the MBean) when the name is computed it is wrapped in a
 * NotCompliantMBeanException.
 * <p>
 * Some ObjectName template examples:
 * <ul>
 * <li>"com.example:type=Memory". Fixed ObjectName. Used to name a singleton MBean.
 * <li>"com.example:type=MemoryPool,name={Name}". Variable ObjectName. Name
 * attribute is retrieved to compose the name key value.
 * <li>"com.example:type=SomeType,name={InstanceName},id={InstanceId}". Variable
 * ObjectName. InstanceName and InstanceId attributes are retrieved to compose
 * respectively the name and id key values.
 * <li>"com.example:type=OtherType,name=\"{ComplexName}\"". Variable ObjectName.
 * ComplexName attribute is retrieved to compose the name key quoted value.
 * <li>"com.example:{TypeKey}=SomeOtherType". Variable ObjectName. TypeKey attribute
 * is retrieved to compose the first key name.
 * <li>"{Domain}:type=YetAnotherType". Variable ObjectName. Domain attribute is
 * retrieved to compose the management domain. "{Naming}". Variable ObjectName.
 * Naming attribute is retrieved to compose the complete name.
 * </ul>
 */
@java.lang.annotation.Documented
@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value={java.lang.annotation.ElementType.TYPE})
public abstract @interface ObjectNameTemplate {

  @javax.management.DescriptorKey(value="objectNameTemplate")
  public abstract java.lang.String value();

}