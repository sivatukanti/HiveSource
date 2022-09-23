// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.core;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Map;

public class ClassNamesResourceConfig extends DefaultResourceConfig
{
    public static final String PROPERTY_CLASSNAMES = "com.sun.jersey.config.property.classnames";
    
    public ClassNamesResourceConfig(final Class... classes) {
        for (final Class c : classes) {
            this.getClasses().add(c);
        }
    }
    
    public ClassNamesResourceConfig(final String... classNames) {
        super(getClasses(classNames));
    }
    
    public ClassNamesResourceConfig(final Map<String, Object> props) {
        super(getClasses(props));
        this.setPropertiesAndFeatures(props);
    }
    
    private static Set<Class<?>> getClasses(final Map<String, Object> props) {
        final Object v = props.get("com.sun.jersey.config.property.classnames");
        if (v == null) {
            throw new IllegalArgumentException("com.sun.jersey.config.property.classnames property is missing");
        }
        final Set<Class<?>> s = getClasses(v);
        if (s.isEmpty()) {
            throw new IllegalArgumentException("com.sun.jersey.config.property.classnames contains no classes");
        }
        return s;
    }
    
    private static Set<Class<?>> getClasses(final Object param) {
        return convertToSet(_getClasses(param));
    }
    
    private static Set<Class<?>> getClasses(final String[] elements) {
        return convertToSet(ResourceConfig.getElements(elements, " ,;\n"));
    }
    
    private static Set<Class<?>> convertToSet(final String[] classes) {
        final Set<Class<?>> s = new LinkedHashSet<Class<?>>();
        for (final String c : classes) {
            try {
                s.add(getClassLoader().loadClass(c));
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return s;
    }
    
    private static String[] _getClasses(final Object param) {
        if (param instanceof String) {
            return ResourceConfig.getElements(new String[] { (String)param }, " ,;\n");
        }
        if (param instanceof String[]) {
            return ResourceConfig.getElements((String[])param, " ,;\n");
        }
        throw new IllegalArgumentException("com.sun.jersey.config.property.classnames must have a property value of type String or String[]");
    }
    
    private static ClassLoader getClassLoader() {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return (classLoader == null) ? ClassNamesResourceConfig.class.getClassLoader() : classLoader;
    }
}
