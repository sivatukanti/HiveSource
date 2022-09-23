// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.beanutils;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.beanutils.DynaClass;
import java.util.List;
import org.apache.commons.configuration2.SubsetConfiguration;
import java.util.Iterator;
import java.lang.reflect.Array;
import java.util.Collection;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.configuration2.ConfigurationMap;

public class ConfigurationDynaBean extends ConfigurationMap implements DynaBean
{
    private static final String PROPERTY_DELIMITER = ".";
    private static final Log LOG;
    
    public ConfigurationDynaBean(final Configuration configuration) {
        super(configuration);
        if (ConfigurationDynaBean.LOG.isTraceEnabled()) {
            ConfigurationDynaBean.LOG.trace("ConfigurationDynaBean(" + configuration + ")");
        }
    }
    
    @Override
    public void set(final String name, final Object value) {
        if (ConfigurationDynaBean.LOG.isTraceEnabled()) {
            ConfigurationDynaBean.LOG.trace("set(" + name + "," + value + ")");
        }
        if (value == null) {
            throw new NullPointerException("Error trying to set property to null.");
        }
        if (value instanceof Collection) {
            final Collection<?> collection = (Collection<?>)value;
            for (final Object v : collection) {
                this.getConfiguration().addProperty(name, v);
            }
        }
        else if (value.getClass().isArray()) {
            for (int length = Array.getLength(value), i = 0; i < length; ++i) {
                this.getConfiguration().addProperty(name, Array.get(value, i));
            }
        }
        else {
            this.getConfiguration().setProperty(name, value);
        }
    }
    
    @Override
    public Object get(final String name) {
        if (ConfigurationDynaBean.LOG.isTraceEnabled()) {
            ConfigurationDynaBean.LOG.trace("get(" + name + ")");
        }
        Object result = this.getConfiguration().getProperty(name);
        if (result == null) {
            final Configuration subset = new SubsetConfiguration(this.getConfiguration(), name, ".");
            if (!subset.isEmpty()) {
                result = new ConfigurationDynaBean(subset);
            }
        }
        if (ConfigurationDynaBean.LOG.isDebugEnabled()) {
            ConfigurationDynaBean.LOG.debug(name + "=[" + result + "]");
        }
        if (result == null) {
            throw new IllegalArgumentException("Property '" + name + "' does not exist.");
        }
        return result;
    }
    
    @Override
    public boolean contains(final String name, final String key) {
        final Configuration subset = this.getConfiguration().subset(name);
        if (subset == null) {
            throw new IllegalArgumentException("Mapped property '" + name + "' does not exist.");
        }
        return subset.containsKey(key);
    }
    
    @Override
    public Object get(final String name, final int index) {
        if (!this.checkIndexedProperty(name)) {
            throw new IllegalArgumentException("Property '" + name + "' is not indexed.");
        }
        final List<Object> list = this.getConfiguration().getList(name);
        return list.get(index);
    }
    
    @Override
    public Object get(final String name, final String key) {
        final Configuration subset = this.getConfiguration().subset(name);
        if (subset == null) {
            throw new IllegalArgumentException("Mapped property '" + name + "' does not exist.");
        }
        return subset.getProperty(key);
    }
    
    @Override
    public DynaClass getDynaClass() {
        return new ConfigurationDynaClass(this.getConfiguration());
    }
    
    @Override
    public void remove(final String name, final String key) {
        final Configuration subset = new SubsetConfiguration(this.getConfiguration(), name, ".");
        subset.setProperty(key, null);
    }
    
    @Override
    public void set(final String name, final int index, final Object value) {
        if (!this.checkIndexedProperty(name) && index > 0) {
            throw new IllegalArgumentException("Property '" + name + "' is not indexed.");
        }
        final Object property = this.getConfiguration().getProperty(name);
        if (property instanceof List) {
            final List<Object> list = (List<Object>)property;
            list.set(index, value);
            this.getConfiguration().setProperty(name, list);
        }
        else if (property.getClass().isArray()) {
            Array.set(property, index, value);
        }
        else if (index == 0) {
            this.getConfiguration().setProperty(name, value);
        }
    }
    
    @Override
    public void set(final String name, final String key, final Object value) {
        this.getConfiguration().setProperty(name + "." + key, value);
    }
    
    private boolean checkIndexedProperty(final String name) {
        final Object property = this.getConfiguration().getProperty(name);
        if (property == null) {
            throw new IllegalArgumentException("Property '" + name + "' does not exist.");
        }
        return property instanceof List || property.getClass().isArray();
    }
    
    static {
        LOG = LogFactory.getLog(ConfigurationDynaBean.class);
    }
}
