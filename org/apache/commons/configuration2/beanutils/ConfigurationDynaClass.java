// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.beanutils;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.beanutils.DynaBean;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.beanutils.DynaClass;

public class ConfigurationDynaClass implements DynaClass
{
    private static final Log LOG;
    private final Configuration configuration;
    
    public ConfigurationDynaClass(final Configuration configuration) {
        if (ConfigurationDynaClass.LOG.isTraceEnabled()) {
            ConfigurationDynaClass.LOG.trace("ConfigurationDynaClass(" + configuration + ")");
        }
        this.configuration = configuration;
    }
    
    @Override
    public DynaProperty getDynaProperty(final String name) {
        if (ConfigurationDynaClass.LOG.isTraceEnabled()) {
            ConfigurationDynaClass.LOG.trace("getDynaProperty(" + name + ")");
        }
        if (name == null) {
            throw new IllegalArgumentException("Property name must not be null!");
        }
        final Object value = this.configuration.getProperty(name);
        if (value == null) {
            return null;
        }
        Class<?> type = value.getClass();
        if (type == Byte.class) {
            type = Byte.TYPE;
        }
        if (type == Character.class) {
            type = Character.TYPE;
        }
        else if (type == Boolean.class) {
            type = Boolean.TYPE;
        }
        else if (type == Double.class) {
            type = Double.TYPE;
        }
        else if (type == Float.class) {
            type = Float.TYPE;
        }
        else if (type == Integer.class) {
            type = Integer.TYPE;
        }
        else if (type == Long.class) {
            type = Long.TYPE;
        }
        else if (type == Short.class) {
            type = Short.TYPE;
        }
        return new DynaProperty(name, type);
    }
    
    @Override
    public DynaProperty[] getDynaProperties() {
        if (ConfigurationDynaClass.LOG.isTraceEnabled()) {
            ConfigurationDynaClass.LOG.trace("getDynaProperties()");
        }
        final Iterator<String> keys = this.configuration.getKeys();
        final List<DynaProperty> properties = new ArrayList<DynaProperty>();
        while (keys.hasNext()) {
            final String key = keys.next();
            final DynaProperty property = this.getDynaProperty(key);
            properties.add(property);
        }
        final DynaProperty[] propertyArray = new DynaProperty[properties.size()];
        properties.toArray(propertyArray);
        if (ConfigurationDynaClass.LOG.isDebugEnabled()) {
            ConfigurationDynaClass.LOG.debug("Found " + properties.size() + " properties.");
        }
        return propertyArray;
    }
    
    @Override
    public String getName() {
        return ConfigurationDynaBean.class.getName();
    }
    
    @Override
    public DynaBean newInstance() throws IllegalAccessException, InstantiationException {
        return new ConfigurationDynaBean(this.configuration);
    }
    
    static {
        LOG = LogFactory.getLog(ConfigurationDynaClass.class);
    }
}
