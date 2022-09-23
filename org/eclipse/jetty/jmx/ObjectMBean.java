// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.jmx;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.TypeUtil;
import javax.management.MBeanParameterInfo;
import javax.management.InvalidAttributeValueException;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanException;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import javax.management.ReflectionException;
import java.util.Collection;
import java.lang.reflect.Array;
import javax.management.AttributeNotFoundException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.ResourceBundle;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanAttributeInfo;
import java.util.MissingResourceException;
import java.util.Locale;
import org.eclipse.jetty.util.LazyList;
import javax.management.ObjectName;
import java.util.HashSet;
import java.util.HashMap;
import java.lang.reflect.Constructor;
import javax.management.modelmbean.ModelMBean;
import org.eclipse.jetty.util.Loader;
import java.util.Set;
import java.util.Map;
import javax.management.MBeanInfo;
import org.eclipse.jetty.util.log.Logger;
import javax.management.DynamicMBean;

public class ObjectMBean implements DynamicMBean
{
    private static final Logger LOG;
    private static Class[] OBJ_ARG;
    protected Object _managed;
    private MBeanInfo _info;
    private Map _getters;
    private Map _setters;
    private Map _methods;
    private Set _convert;
    private ClassLoader _loader;
    private MBeanContainer _mbeanContainer;
    private static String OBJECT_NAME_CLASS;
    private static String OBJECT_NAME_ARRAY_CLASS;
    
    public static Object mbeanFor(final Object o) {
        try {
            Class oClass = o.getClass();
            for (Object mbean = null; mbean == null && oClass != null; oClass = oClass.getSuperclass()) {
                final String pName = oClass.getPackage().getName();
                final String cName = oClass.getName().substring(pName.length() + 1);
                final String mName = pName + ".jmx." + cName + "MBean";
                try {
                    final Class mClass = Object.class.equals(oClass) ? (oClass = ObjectMBean.class) : Loader.loadClass(oClass, mName, true);
                    if (ObjectMBean.LOG.isDebugEnabled()) {
                        ObjectMBean.LOG.debug("mbeanFor " + o + " mClass=" + mClass, new Object[0]);
                    }
                    try {
                        final Constructor constructor = mClass.getConstructor((Class[])ObjectMBean.OBJ_ARG);
                        mbean = constructor.newInstance(o);
                    }
                    catch (Exception e) {
                        ObjectMBean.LOG.ignore(e);
                        if (ModelMBean.class.isAssignableFrom(mClass)) {
                            mbean = mClass.newInstance();
                            ((ModelMBean)mbean).setManagedResource(o, "objectReference");
                        }
                    }
                    if (ObjectMBean.LOG.isDebugEnabled()) {
                        ObjectMBean.LOG.debug("mbeanFor " + o + " is " + mbean, new Object[0]);
                    }
                    return mbean;
                }
                catch (ClassNotFoundException e2) {
                    if (e2.getMessage().contains(mName)) {
                        ObjectMBean.LOG.ignore(e2);
                    }
                    else {
                        ObjectMBean.LOG.warn(e2);
                    }
                }
                catch (Error e3) {
                    ObjectMBean.LOG.warn(e3);
                    mbean = null;
                }
                catch (Exception e4) {
                    ObjectMBean.LOG.warn(e4);
                    mbean = null;
                }
            }
        }
        catch (Exception e5) {
            ObjectMBean.LOG.ignore(e5);
        }
        return null;
    }
    
    public ObjectMBean(final Object managedObject) {
        this._getters = new HashMap();
        this._setters = new HashMap();
        this._methods = new HashMap();
        this._convert = new HashSet();
        this._managed = managedObject;
        this._loader = Thread.currentThread().getContextClassLoader();
    }
    
    public Object getManagedObject() {
        return this._managed;
    }
    
    public ObjectName getObjectName() {
        return null;
    }
    
    public String getObjectContextBasis() {
        return null;
    }
    
    public String getObjectNameBasis() {
        return null;
    }
    
    protected void setMBeanContainer(final MBeanContainer container) {
        this._mbeanContainer = container;
    }
    
    public MBeanContainer getMBeanContainer() {
        return this._mbeanContainer;
    }
    
    public MBeanInfo getMBeanInfo() {
        try {
            if (this._info == null) {
                String desc = null;
                Object attributes = null;
                final Object constructors = null;
                Object operations = null;
                final Object notifications = null;
                final Class o_class = this._managed.getClass();
                final Object influences = findInfluences(null, this._managed.getClass());
                final Set defined = new HashSet();
                for (int i = 0; i < LazyList.size(influences); ++i) {
                    Class oClass = LazyList.get(influences, i);
                    if (Object.class.equals(oClass)) {
                        oClass = ObjectMBean.class;
                    }
                    final String pName = oClass.getPackage().getName();
                    final String cName = oClass.getName().substring(pName.length() + 1);
                    final String rName = pName.replace('.', '/') + "/jmx/" + cName + "-mbean";
                    try {
                        ObjectMBean.LOG.debug(rName, new Object[0]);
                        final ResourceBundle bundle = Loader.getResourceBundle(o_class, rName, true, Locale.getDefault());
                        final Enumeration e = bundle.getKeys();
                        while (e.hasMoreElements()) {
                            final String key = e.nextElement();
                            final String value = bundle.getString(key);
                            if (key.equals(cName)) {
                                if (desc != null) {
                                    continue;
                                }
                                desc = value;
                            }
                            else if (key.indexOf(40) > 0) {
                                if (defined.contains(key) || key.indexOf(91) >= 0) {
                                    continue;
                                }
                                defined.add(key);
                                operations = LazyList.add(operations, this.defineOperation(key, value, bundle));
                            }
                            else {
                                if (defined.contains(key)) {
                                    continue;
                                }
                                defined.add(key);
                                final MBeanAttributeInfo info = this.defineAttribute(key, value);
                                if (info == null) {
                                    continue;
                                }
                                attributes = LazyList.add(attributes, info);
                            }
                        }
                    }
                    catch (MissingResourceException e2) {
                        ObjectMBean.LOG.ignore(e2);
                    }
                }
                this._info = new MBeanInfo(o_class.getName(), desc, (MBeanAttributeInfo[])LazyList.toArray(attributes, MBeanAttributeInfo.class), (MBeanConstructorInfo[])LazyList.toArray(constructors, MBeanConstructorInfo.class), (MBeanOperationInfo[])LazyList.toArray(operations, MBeanOperationInfo.class), (MBeanNotificationInfo[])LazyList.toArray(notifications, MBeanNotificationInfo.class));
            }
        }
        catch (RuntimeException e3) {
            ObjectMBean.LOG.warn(e3);
            throw e3;
        }
        return this._info;
    }
    
    public Object getAttribute(final String name) throws AttributeNotFoundException, MBeanException, ReflectionException {
        final Method getter = this._getters.get(name);
        if (getter == null) {
            throw new AttributeNotFoundException(name);
        }
        try {
            Object o = this._managed;
            if (getter.getDeclaringClass().isInstance(this)) {
                o = this;
            }
            Object r = getter.invoke(o, (Object[])null);
            if (r != null && this._convert.contains(name)) {
                if (r.getClass().isArray()) {
                    final ObjectName[] on = new ObjectName[Array.getLength(r)];
                    for (int i = 0; i < on.length; ++i) {
                        on[i] = this._mbeanContainer.findMBean(Array.get(r, i));
                    }
                    r = on;
                }
                else if (r instanceof Collection) {
                    final Collection<Object> c = (Collection<Object>)r;
                    final ObjectName[] on2 = new ObjectName[c.size()];
                    int j = 0;
                    for (final Object obj : c) {
                        on2[j++] = this._mbeanContainer.findMBean(obj);
                    }
                    r = on2;
                }
                else {
                    final ObjectName mbean = this._mbeanContainer.findMBean(r);
                    if (mbean == null) {
                        return null;
                    }
                    r = mbean;
                }
            }
            return r;
        }
        catch (IllegalAccessException e) {
            ObjectMBean.LOG.warn("EXCEPTION ", e);
            throw new AttributeNotFoundException(e.toString());
        }
        catch (InvocationTargetException e2) {
            ObjectMBean.LOG.warn("EXCEPTION ", e2);
            throw new ReflectionException(new Exception(e2.getCause()));
        }
    }
    
    public AttributeList getAttributes(final String[] names) {
        final AttributeList results = new AttributeList(names.length);
        for (int i = 0; i < names.length; ++i) {
            try {
                results.add(new Attribute(names[i], this.getAttribute(names[i])));
            }
            catch (Exception e) {
                ObjectMBean.LOG.warn("EXCEPTION ", e);
            }
        }
        return results;
    }
    
    public void setAttribute(final Attribute attr) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        if (attr == null) {
            return;
        }
        if (ObjectMBean.LOG.isDebugEnabled()) {
            ObjectMBean.LOG.debug("setAttribute " + this._managed + ":" + attr.getName() + "=" + attr.getValue(), new Object[0]);
        }
        final Method setter = this._setters.get(attr.getName());
        if (setter == null) {
            throw new AttributeNotFoundException(attr.getName());
        }
        try {
            Object o = this._managed;
            if (setter.getDeclaringClass().isInstance(this)) {
                o = this;
            }
            Object value = attr.getValue();
            if (value != null && this._convert.contains(attr.getName())) {
                if (value.getClass().isArray()) {
                    final Class t = setter.getParameterTypes()[0].getComponentType();
                    final Object na = Array.newInstance(t, Array.getLength(value));
                    int i = Array.getLength(value);
                    while (i-- > 0) {
                        Array.set(na, i, this._mbeanContainer.findBean((ObjectName)Array.get(value, i)));
                    }
                    value = na;
                }
                else {
                    value = this._mbeanContainer.findBean((ObjectName)value);
                }
            }
            setter.invoke(o, value);
        }
        catch (IllegalAccessException e) {
            ObjectMBean.LOG.warn("EXCEPTION ", e);
            throw new AttributeNotFoundException(e.toString());
        }
        catch (InvocationTargetException e2) {
            ObjectMBean.LOG.warn("EXCEPTION ", e2);
            throw new ReflectionException(new Exception(e2.getCause()));
        }
    }
    
    public AttributeList setAttributes(final AttributeList attrs) {
        ObjectMBean.LOG.debug("setAttributes", new Object[0]);
        final AttributeList results = new AttributeList(attrs.size());
        final Iterator iter = attrs.iterator();
        while (iter.hasNext()) {
            try {
                final Attribute attr = iter.next();
                this.setAttribute(attr);
                results.add(new Attribute(attr.getName(), this.getAttribute(attr.getName())));
            }
            catch (Exception e) {
                ObjectMBean.LOG.warn("EXCEPTION ", e);
            }
        }
        return results;
    }
    
    public Object invoke(final String name, final Object[] params, final String[] signature) throws MBeanException, ReflectionException {
        if (ObjectMBean.LOG.isDebugEnabled()) {
            ObjectMBean.LOG.debug("invoke " + name, new Object[0]);
        }
        String methodKey = name + "(";
        if (signature != null) {
            for (int i = 0; i < signature.length; ++i) {
                methodKey = methodKey + ((i > 0) ? "," : "") + signature[i];
            }
        }
        methodKey += ")";
        final ClassLoader old_loader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this._loader);
            final Method method = this._methods.get(methodKey);
            if (method == null) {
                throw new NoSuchMethodException(methodKey);
            }
            Object o = this._managed;
            if (method.getDeclaringClass().isInstance(this)) {
                o = this;
            }
            return method.invoke(o, params);
        }
        catch (NoSuchMethodException e) {
            ObjectMBean.LOG.warn("EXCEPTION ", e);
            throw new ReflectionException(e);
        }
        catch (IllegalAccessException e2) {
            ObjectMBean.LOG.warn("EXCEPTION ", e2);
            throw new MBeanException(e2);
        }
        catch (InvocationTargetException e3) {
            ObjectMBean.LOG.warn("EXCEPTION ", e3);
            throw new ReflectionException(new Exception(e3.getCause()));
        }
        finally {
            Thread.currentThread().setContextClassLoader(old_loader);
        }
    }
    
    private static Object findInfluences(Object influences, final Class aClass) {
        if (aClass != null) {
            influences = LazyList.add(influences, aClass);
            influences = findInfluences(influences, aClass.getSuperclass());
            final Class[] ifs = aClass.getInterfaces();
            for (int i = 0; ifs != null && i < ifs.length; ++i) {
                influences = findInfluences(influences, ifs[i]);
            }
        }
        return influences;
    }
    
    public MBeanAttributeInfo defineAttribute(final String name, final String metaData) {
        String description = "";
        boolean writable = true;
        boolean onMBean = false;
        boolean convert = false;
        if (metaData != null) {
            final String[] tokens = metaData.split(":", 3);
            for (int t = 0; t < tokens.length - 1; ++t) {
                tokens[t] = tokens[t].trim();
                if ("RO".equals(tokens[t])) {
                    writable = false;
                }
                else {
                    onMBean = ("MMBean".equalsIgnoreCase(tokens[t]) || "MBean".equalsIgnoreCase(tokens[t]));
                    convert = ("MMBean".equalsIgnoreCase(tokens[t]) || "MObject".equalsIgnoreCase(tokens[t]));
                }
            }
            description = tokens[tokens.length - 1];
        }
        final String uName = name.substring(0, 1).toUpperCase() + name.substring(1);
        final Class oClass = onMBean ? this.getClass() : this._managed.getClass();
        if (ObjectMBean.LOG.isDebugEnabled()) {
            ObjectMBean.LOG.debug("defineAttribute " + name + " " + onMBean + ":" + writable + ":" + oClass + ":" + description, new Object[0]);
        }
        Class type = null;
        Method getter = null;
        Method setter = null;
        final Method[] methods = oClass.getMethods();
        for (int m = 0; m < methods.length; ++m) {
            if ((methods[m].getModifiers() & 0x1) != 0x0) {
                if (methods[m].getName().equals("get" + uName) && methods[m].getParameterTypes().length == 0) {
                    if (getter != null) {
                        ObjectMBean.LOG.warn("Multiple mbean getters for attr " + name + " in " + oClass, new Object[0]);
                        continue;
                    }
                    getter = methods[m];
                    if (type != null && !type.equals(methods[m].getReturnType())) {
                        ObjectMBean.LOG.warn("Type conflict for mbean attr " + name + " in " + oClass, new Object[0]);
                        continue;
                    }
                    type = methods[m].getReturnType();
                }
                if (methods[m].getName().equals("is" + uName) && methods[m].getParameterTypes().length == 0) {
                    if (getter != null) {
                        ObjectMBean.LOG.warn("Multiple mbean getters for attr " + name + " in " + oClass, new Object[0]);
                        continue;
                    }
                    getter = methods[m];
                    if (type != null && !type.equals(methods[m].getReturnType())) {
                        ObjectMBean.LOG.warn("Type conflict for mbean attr " + name + " in " + oClass, new Object[0]);
                        continue;
                    }
                    type = methods[m].getReturnType();
                }
                if (writable && methods[m].getName().equals("set" + uName) && methods[m].getParameterTypes().length == 1) {
                    if (setter != null) {
                        ObjectMBean.LOG.warn("Multiple setters for mbean attr " + name + " in " + oClass, new Object[0]);
                    }
                    else {
                        setter = methods[m];
                        if (type != null && !type.equals(methods[m].getParameterTypes()[0])) {
                            ObjectMBean.LOG.warn("Type conflict for mbean attr " + name + " in " + oClass, new Object[0]);
                        }
                        else {
                            type = methods[m].getParameterTypes()[0];
                        }
                    }
                }
            }
        }
        if (convert) {
            if (type == null) {
                ObjectMBean.LOG.warn("No mbean type for " + name + " on " + this._managed.getClass(), new Object[0]);
                return null;
            }
            if (type.isPrimitive() && !type.isArray()) {
                ObjectMBean.LOG.warn("Cannot convert mbean primative " + name, new Object[0]);
                return null;
            }
        }
        if (getter == null && setter == null) {
            ObjectMBean.LOG.warn("No mbean getter or setters found for " + name + " in " + oClass, new Object[0]);
            return null;
        }
        try {
            this._getters.put(name, getter);
            this._setters.put(name, setter);
            MBeanAttributeInfo info = null;
            if (convert) {
                this._convert.add(name);
                if (type.isArray()) {
                    info = new MBeanAttributeInfo(name, ObjectMBean.OBJECT_NAME_ARRAY_CLASS, description, getter != null, setter != null, getter != null && getter.getName().startsWith("is"));
                }
                else {
                    info = new MBeanAttributeInfo(name, ObjectMBean.OBJECT_NAME_CLASS, description, getter != null, setter != null, getter != null && getter.getName().startsWith("is"));
                }
            }
            else {
                info = new MBeanAttributeInfo(name, description, getter, setter);
            }
            return info;
        }
        catch (Exception e) {
            ObjectMBean.LOG.warn(name + ": " + metaData, e);
            throw new IllegalArgumentException(e.toString());
        }
    }
    
    private MBeanOperationInfo defineOperation(String signature, final String metaData, final ResourceBundle bundle) {
        final String[] tokens = metaData.split(":", 3);
        int i = tokens.length - 1;
        final String description = tokens[i--];
        final String impact_name = (i < 0) ? "UNKNOWN" : tokens[i--].trim();
        if (i == 0) {
            tokens[0] = tokens[0].trim();
        }
        final boolean onMBean = i == 0 && ("MBean".equalsIgnoreCase(tokens[0]) || "MMBean".equalsIgnoreCase(tokens[0]));
        final boolean convert = i == 0 && ("MObject".equalsIgnoreCase(tokens[0]) || "MMBean".equalsIgnoreCase(tokens[0]));
        if (ObjectMBean.LOG.isDebugEnabled()) {
            ObjectMBean.LOG.debug("defineOperation " + signature + " " + onMBean + ":" + impact_name + ":" + description, new Object[0]);
        }
        final Class oClass = onMBean ? this.getClass() : this._managed.getClass();
        try {
            int impact = 3;
            if (impact_name == null || impact_name.equals("UNKNOWN")) {
                impact = 3;
            }
            else if (impact_name.equals("ACTION")) {
                impact = 1;
            }
            else if (impact_name.equals("INFO")) {
                impact = 0;
            }
            else if (impact_name.equals("ACTION_INFO")) {
                impact = 2;
            }
            else {
                ObjectMBean.LOG.warn("Unknown impact '" + impact_name + "' for " + signature, new Object[0]);
            }
            String[] parts = signature.split("[\\(\\)]");
            final String method_name = parts[0];
            final String arguments = (parts.length == 2) ? parts[1] : null;
            final String[] args = (arguments == null) ? new String[0] : arguments.split(" *, *");
            final Class[] types = new Class[args.length];
            final MBeanParameterInfo[] pInfo = new MBeanParameterInfo[args.length];
            signature = method_name;
            for (i = 0; i < args.length; ++i) {
                Class type = TypeUtil.fromName(args[i]);
                if (type == null) {
                    type = Thread.currentThread().getContextClassLoader().loadClass(args[i]);
                }
                types[i] = type;
                args[i] = (type.isPrimitive() ? TypeUtil.toName(type) : args[i]);
                signature = signature + ((i > 0) ? "," : "(") + args[i];
            }
            signature += ((i > 0) ? ")" : "()");
            for (i = 0; i < args.length; ++i) {
                final String param_desc = bundle.getString(signature + "[" + i + "]");
                parts = param_desc.split(" *: *", 2);
                if (ObjectMBean.LOG.isDebugEnabled()) {
                    ObjectMBean.LOG.debug(parts[0] + ": " + parts[1], new Object[0]);
                }
                pInfo[i] = new MBeanParameterInfo(parts[0].trim(), args[i], parts[1].trim());
            }
            final Method method = oClass.getMethod(method_name, (Class[])types);
            final Class returnClass = method.getReturnType();
            this._methods.put(signature, method);
            if (convert) {
                this._convert.add(signature);
            }
            return new MBeanOperationInfo(method_name, description, pInfo, returnClass.isPrimitive() ? TypeUtil.toName(returnClass) : returnClass.getName(), impact);
        }
        catch (Exception e) {
            ObjectMBean.LOG.warn("Operation '" + signature + "'", e);
            throw new IllegalArgumentException(e.toString());
        }
    }
    
    static {
        LOG = Log.getLogger(ObjectMBean.class);
        ObjectMBean.OBJ_ARG = new Class[] { Object.class };
        ObjectMBean.OBJECT_NAME_CLASS = ObjectName.class.getName();
        ObjectMBean.OBJECT_NAME_ARRAY_CLASS = ObjectName[].class.getName();
    }
}
