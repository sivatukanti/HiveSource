// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.jmx;

import org.apache.log4j.Priority;
import javax.management.MBeanServer;
import javax.management.InvalidAttributeValueException;
import org.apache.log4j.Level;
import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.io.InterruptedIOException;
import javax.management.MalformedObjectNameException;
import javax.management.RuntimeOperationsException;
import javax.management.JMException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.MBeanException;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.OptionHandler;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanInfo;
import java.lang.reflect.Method;
import java.beans.PropertyDescriptor;
import java.beans.BeanInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanAttributeInfo;
import java.beans.Introspector;
import java.lang.reflect.Constructor;
import java.beans.IntrospectionException;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import javax.management.MBeanOperationInfo;
import java.util.Hashtable;
import java.util.Vector;
import javax.management.MBeanConstructorInfo;

public class AppenderDynamicMBean extends AbstractDynamicMBean
{
    private MBeanConstructorInfo[] dConstructors;
    private Vector dAttributes;
    private String dClassName;
    private Hashtable dynamicProps;
    private MBeanOperationInfo[] dOperations;
    private String dDescription;
    private static Logger cat;
    private Appender appender;
    
    public AppenderDynamicMBean(final Appender appender) throws IntrospectionException {
        this.dConstructors = new MBeanConstructorInfo[1];
        this.dAttributes = new Vector();
        this.dClassName = this.getClass().getName();
        this.dynamicProps = new Hashtable(5);
        this.dOperations = new MBeanOperationInfo[2];
        this.dDescription = "This MBean acts as a management facade for log4j appenders.";
        this.appender = appender;
        this.buildDynamicMBeanInfo();
    }
    
    private void buildDynamicMBeanInfo() throws IntrospectionException {
        final Constructor[] constructors = this.getClass().getConstructors();
        this.dConstructors[0] = new MBeanConstructorInfo("AppenderDynamicMBean(): Constructs a AppenderDynamicMBean instance", constructors[0]);
        final BeanInfo bi = Introspector.getBeanInfo(this.appender.getClass());
        final PropertyDescriptor[] pd = bi.getPropertyDescriptors();
        for (int size = pd.length, i = 0; i < size; ++i) {
            final String name = pd[i].getName();
            final Method readMethod = pd[i].getReadMethod();
            final Method writeMethod = pd[i].getWriteMethod();
            if (readMethod != null) {
                final Class returnClass = readMethod.getReturnType();
                if (this.isSupportedType(returnClass)) {
                    String returnClassName;
                    if (returnClass.isAssignableFrom(Priority.class)) {
                        returnClassName = "java.lang.String";
                    }
                    else {
                        returnClassName = returnClass.getName();
                    }
                    this.dAttributes.add(new MBeanAttributeInfo(name, returnClassName, "Dynamic", true, writeMethod != null, false));
                    this.dynamicProps.put(name, new MethodUnion(readMethod, writeMethod));
                }
            }
        }
        MBeanParameterInfo[] params = new MBeanParameterInfo[0];
        this.dOperations[0] = new MBeanOperationInfo("activateOptions", "activateOptions(): add an appender", params, "void", 1);
        params = new MBeanParameterInfo[] { new MBeanParameterInfo("layout class", "java.lang.String", "layout class") };
        this.dOperations[1] = new MBeanOperationInfo("setLayout", "setLayout(): add a layout", params, "void", 1);
    }
    
    private boolean isSupportedType(final Class clazz) {
        return clazz.isPrimitive() || clazz == String.class || clazz.isAssignableFrom(Priority.class);
    }
    
    public MBeanInfo getMBeanInfo() {
        AppenderDynamicMBean.cat.debug("getMBeanInfo called.");
        final MBeanAttributeInfo[] attribs = new MBeanAttributeInfo[this.dAttributes.size()];
        this.dAttributes.toArray(attribs);
        return new MBeanInfo(this.dClassName, this.dDescription, attribs, this.dConstructors, this.dOperations, new MBeanNotificationInfo[0]);
    }
    
    public Object invoke(final String operationName, final Object[] params, final String[] signature) throws MBeanException, ReflectionException {
        if (operationName.equals("activateOptions") && this.appender instanceof OptionHandler) {
            final OptionHandler oh = (OptionHandler)this.appender;
            oh.activateOptions();
            return "Options activated.";
        }
        if (operationName.equals("setLayout")) {
            final Layout layout = (Layout)OptionConverter.instantiateByClassName((String)params[0], Layout.class, null);
            this.appender.setLayout(layout);
            this.registerLayoutMBean(layout);
        }
        return null;
    }
    
    void registerLayoutMBean(final Layout layout) {
        if (layout == null) {
            return;
        }
        final String name = AbstractDynamicMBean.getAppenderName(this.appender) + ",layout=" + layout.getClass().getName();
        AppenderDynamicMBean.cat.debug("Adding LayoutMBean:" + name);
        ObjectName objectName = null;
        try {
            final LayoutDynamicMBean appenderMBean = new LayoutDynamicMBean(layout);
            objectName = new ObjectName("log4j:appender=" + name);
            if (!this.server.isRegistered(objectName)) {
                this.registerMBean(appenderMBean, objectName);
                this.dAttributes.add(new MBeanAttributeInfo("appender=" + name, "javax.management.ObjectName", "The " + name + " layout.", true, true, false));
            }
        }
        catch (JMException e) {
            AppenderDynamicMBean.cat.error("Could not add DynamicLayoutMBean for [" + name + "].", e);
        }
        catch (IntrospectionException e2) {
            AppenderDynamicMBean.cat.error("Could not add DynamicLayoutMBean for [" + name + "].", e2);
        }
        catch (RuntimeException e3) {
            AppenderDynamicMBean.cat.error("Could not add DynamicLayoutMBean for [" + name + "].", e3);
        }
    }
    
    protected Logger getLogger() {
        return AppenderDynamicMBean.cat;
    }
    
    public Object getAttribute(final String attributeName) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (attributeName == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke a getter of " + this.dClassName + " with null attribute name");
        }
        AppenderDynamicMBean.cat.debug("getAttribute called with [" + attributeName + "].");
        if (attributeName.startsWith("appender=" + this.appender.getName() + ",layout")) {
            try {
                return new ObjectName("log4j:" + attributeName);
            }
            catch (MalformedObjectNameException e) {
                AppenderDynamicMBean.cat.error("attributeName", e);
            }
            catch (RuntimeException e2) {
                AppenderDynamicMBean.cat.error("attributeName", e2);
            }
        }
        final MethodUnion mu = this.dynamicProps.get(attributeName);
        if (mu != null && mu.readMethod != null) {
            try {
                return mu.readMethod.invoke(this.appender, (Object[])null);
            }
            catch (IllegalAccessException e4) {
                return null;
            }
            catch (InvocationTargetException e3) {
                if (e3.getTargetException() instanceof InterruptedException || e3.getTargetException() instanceof InterruptedIOException) {
                    Thread.currentThread().interrupt();
                }
                return null;
            }
            catch (RuntimeException e5) {
                return null;
            }
        }
        throw new AttributeNotFoundException("Cannot find " + attributeName + " attribute in " + this.dClassName);
    }
    
    public void setAttribute(final Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        if (attribute == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"), "Cannot invoke a setter of " + this.dClassName + " with null attribute");
        }
        final String name = attribute.getName();
        Object value = attribute.getValue();
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke the setter of " + this.dClassName + " with null attribute name");
        }
        final MethodUnion mu = this.dynamicProps.get(name);
        if (mu != null && mu.writeMethod != null) {
            final Object[] o = { null };
            final Class[] params = mu.writeMethod.getParameterTypes();
            if (params[0] == Priority.class) {
                value = OptionConverter.toLevel((String)value, (Level)this.getAttribute(name));
            }
            o[0] = value;
            try {
                mu.writeMethod.invoke(this.appender, o);
            }
            catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof InterruptedException || e.getTargetException() instanceof InterruptedIOException) {
                    Thread.currentThread().interrupt();
                }
                AppenderDynamicMBean.cat.error("FIXME", e);
            }
            catch (IllegalAccessException e2) {
                AppenderDynamicMBean.cat.error("FIXME", e2);
            }
            catch (RuntimeException e3) {
                AppenderDynamicMBean.cat.error("FIXME", e3);
            }
        }
        else if (!name.endsWith(".layout")) {
            throw new AttributeNotFoundException("Attribute " + name + " not found in " + this.getClass().getName());
        }
    }
    
    public ObjectName preRegister(final MBeanServer server, final ObjectName name) {
        AppenderDynamicMBean.cat.debug("preRegister called. Server=" + server + ", name=" + name);
        this.server = server;
        this.registerLayoutMBean(this.appender.getLayout());
        return name;
    }
    
    static {
        AppenderDynamicMBean.cat = Logger.getLogger(AppenderDynamicMBean.class);
    }
}
