// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.config;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigImpl implements Config
{
    private String resource;
    private Map<String, ConfigObject> properties;
    private List<Config> configs;
    
    protected ConfigImpl(final String resource) {
        this.resource = resource;
        this.properties = new HashMap<String, ConfigObject>();
        this.configs = new ArrayList<Config>(0);
    }
    
    protected void reset() {
        this.properties.clear();
        this.configs.clear();
    }
    
    @Override
    public String getResource() {
        return this.resource;
    }
    
    @Override
    public Set<String> getNames() {
        final Set<String> propNames = new HashSet<String>(this.properties.keySet());
        for (final Config config : this.configs) {
            propNames.addAll(config.getNames());
        }
        return propNames;
    }
    
    @Override
    public String getString(final String name) {
        String result = null;
        final ConfigObject co = this.properties.get(name);
        if (co != null) {
            result = co.getPropertyValue();
        }
        else {
            for (final Config config : this.configs) {
                result = config.getString(name);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }
    
    @Override
    public String getString(final ConfigKey name, final boolean useDefault) {
        if (useDefault) {
            return this.getString(name.getPropertyKey(), (String)name.getDefaultValue());
        }
        return this.getString(name.getPropertyKey());
    }
    
    @Override
    public String getString(final String name, final String defaultValue) {
        String result = this.getString(name);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }
    
    @Override
    public String getTrimmed(final String name) {
        String result = this.getString(name);
        if (null != result) {
            result = result.trim();
        }
        return result;
    }
    
    @Override
    public String getTrimmed(final ConfigKey name) {
        return this.getTrimmed(name.getPropertyKey());
    }
    
    @Override
    public Integer getInt(final String name) {
        Integer result = null;
        final String value = this.getTrimmed(name);
        if (value != null) {
            result = Integer.valueOf(value);
        }
        return result;
    }
    
    @Override
    public Integer getInt(final ConfigKey name, final boolean useDefault) {
        if (useDefault) {
            return this.getInt(name.getPropertyKey(), this.getDefaultValueAs(name, Integer.class));
        }
        return this.getInt(name.getPropertyKey());
    }
    
    private <T> T getDefaultValueAs(final ConfigKey confKey, final Class<T> cls) {
        final Object defValue = confKey.getDefaultValue();
        if (defValue != null && cls != null) {
            return (T)defValue;
        }
        return null;
    }
    
    @Override
    public Integer getInt(final String name, final Integer defaultValue) {
        Integer result = this.getInt(name);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }
    
    @Override
    public void setInt(final String name, final Integer value) {
        this.set(name, String.valueOf(value));
    }
    
    @Override
    public void setInt(final ConfigKey name, final Integer value) {
        this.set(name.getPropertyKey(), String.valueOf(value));
    }
    
    @Override
    public Long getLong(final String name) {
        Long result = null;
        final String value = this.getTrimmed(name);
        if (value != null) {
            result = Long.valueOf(value);
        }
        return result;
    }
    
    @Override
    public Long getLong(final ConfigKey name, final boolean useDefault) {
        if (useDefault) {
            return this.getLong(name.getPropertyKey(), this.getDefaultValueAs(name, Long.class));
        }
        return this.getLong(name.getPropertyKey());
    }
    
    @Override
    public Long getLong(final String name, final Long defaultValue) {
        Long result = this.getLong(name);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }
    
    @Override
    public void setLong(final String name, final Long value) {
        this.set(name, String.valueOf(value));
    }
    
    @Override
    public void setLong(final ConfigKey name, final Long value) {
        this.set(name.getPropertyKey(), String.valueOf(value));
    }
    
    @Override
    public Float getFloat(final String name) {
        Float result = null;
        final String value = this.getTrimmed(name);
        if (value != null) {
            result = Float.valueOf(value);
        }
        return result;
    }
    
    @Override
    public Float getFloat(final ConfigKey name, final boolean useDefault) {
        if (useDefault) {
            return this.getFloat(name.getPropertyKey(), this.getDefaultValueAs(name, Float.class));
        }
        return this.getFloat(name.getPropertyKey());
    }
    
    @Override
    public Float getFloat(final String name, final Float defaultValue) {
        Float result = this.getFloat(name);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }
    
    @Override
    public void setFloat(final String name, final Float value) {
        this.set(name, String.valueOf(value));
    }
    
    @Override
    public void setFloat(final ConfigKey name, final Float value) {
        this.set(name.getPropertyKey(), String.valueOf(value));
    }
    
    @Override
    public Boolean getBoolean(final String name) {
        Boolean result = null;
        final String value = this.getTrimmed(name);
        if (value != null) {
            result = Boolean.valueOf(value);
        }
        return result;
    }
    
    @Override
    public Boolean getBoolean(final ConfigKey name, final boolean useDefault) {
        if (useDefault) {
            return this.getBoolean(name.getPropertyKey(), (Boolean)name.getDefaultValue());
        }
        return this.getBoolean(name.getPropertyKey());
    }
    
    @Override
    public Boolean getBoolean(final String name, final Boolean defaultValue) {
        Boolean result = this.getBoolean(name);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }
    
    @Override
    public void setBoolean(final String name, final Boolean value) {
        this.set(name, String.valueOf(value));
    }
    
    @Override
    public void setBoolean(final ConfigKey name, final Boolean value) {
        this.set(name.getPropertyKey(), String.valueOf(value));
    }
    
    @Override
    public List<String> getList(final String name) {
        List<String> results = null;
        final ConfigObject co = this.properties.get(name);
        if (co != null) {
            results = co.getListValues();
        }
        else {
            for (final Config config : this.configs) {
                results = config.getList(name);
                if (results != null) {
                    break;
                }
            }
        }
        return results;
    }
    
    @Override
    public List<String> getList(final String name, final String[] defaultValue) {
        List<String> results = this.getList(name);
        if (results == null) {
            results = Arrays.asList(defaultValue);
        }
        return results;
    }
    
    @Override
    public List<String> getList(final ConfigKey name) {
        if (name.getDefaultValue() != null) {
            return this.getList(name.getPropertyKey(), (String[])name.getDefaultValue());
        }
        return this.getList(name.getPropertyKey());
    }
    
    @Override
    public Config getConfig(final String name) {
        Config result = null;
        final ConfigObject co = this.properties.get(name);
        if (co != null) {
            result = co.getConfigValue();
        }
        else {
            for (final Config config : this.configs) {
                result = config.getConfig(name);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }
    
    @Override
    public Config getConfig(final ConfigKey name) {
        return this.getConfig(name.getPropertyKey());
    }
    
    @Override
    public Class<?> getClass(final String name) throws ClassNotFoundException {
        Class<?> result = null;
        final String valueString = this.getString(name);
        if (valueString != null) {
            final Class<?> cls = result = Class.forName(name);
        }
        return result;
    }
    
    @Override
    public Class<?> getClass(final String name, final Class<?> defaultValue) throws ClassNotFoundException {
        Class<?> result = this.getClass(name);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }
    
    @Override
    public Class<?> getClass(final ConfigKey name, final boolean useDefault) throws ClassNotFoundException {
        if (useDefault) {
            return this.getClass(name.getPropertyKey(), (Class<?>)name.getDefaultValue());
        }
        return this.getClass(name.getPropertyKey());
    }
    
    @Override
    public <T> T getInstance(final String name) throws ClassNotFoundException {
        return this.getInstance(name, (Class<T>)null);
    }
    
    @Override
    public <T> T getInstance(final ConfigKey name) throws ClassNotFoundException {
        return this.getInstance(name.getPropertyKey());
    }
    
    @Override
    public <T> T getInstance(final String name, final Class<T> xface) throws ClassNotFoundException {
        T result = null;
        final Class<?> cls = this.getClass(name, null);
        if (xface != null && !xface.isAssignableFrom(cls)) {
            throw new RuntimeException(cls + " does not implement " + xface);
        }
        try {
            result = (T)cls.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to create instance with class " + cls.getName());
        }
        return result;
    }
    
    @Override
    public void setString(final String name, final String value) {
        this.set(name, value);
    }
    
    @Override
    public void setString(final ConfigKey name, final String value) {
        this.set(name.getPropertyKey(), value);
    }
    
    protected void set(final String name, final String value) {
        final ConfigObject co = new ConfigObject(value);
        this.set(name, co);
    }
    
    protected void set(final String name, final Config value) {
        final ConfigObject co = new ConfigObject(value);
        this.set(name, co);
    }
    
    protected void set(final String name, final ConfigObject value) {
        this.properties.put(name, value);
    }
    
    protected void add(final Config config) {
        if (config != null) {
            if (this == config) {
                throw new IllegalArgumentException("You can not add a config to itself");
            }
            this.configs.add(config);
        }
    }
}
