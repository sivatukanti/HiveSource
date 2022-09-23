// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.config;

import org.slf4j.LoggerFactory;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

public class Conf implements Config
{
    private static final Logger LOGGER;
    private List<ConfigLoader> resourceConfigs;
    private final ConfigImpl config;
    
    public Conf() {
        this.resourceConfigs = new ArrayList<ConfigLoader>(1);
        this.config = new ConfigImpl("Conf");
    }
    
    public void addXmlConfig(final File xmlFile) throws IOException {
        this.addResource(Resource.createXmlResource(xmlFile));
    }
    
    public void addIniConfig(final File iniFile) throws IOException {
        this.addResource(Resource.createIniResource(iniFile));
    }
    
    public void addJsonConfig(final File jsonFile) throws IOException {
        this.addResource(Resource.createJsonResource(jsonFile));
    }
    
    public void addPropertiesConfig(final File propertiesFile) throws IOException {
        this.addResource(Resource.createPropertiesFileResource(propertiesFile));
    }
    
    public void addPropertiesConfig(final Properties propertiesConfig) {
        this.addResource(Resource.createPropertiesResource(propertiesConfig));
    }
    
    public void addMapConfig(final Map<String, Object> mapConfig) {
        this.addResource(Resource.createMapResource(mapConfig));
    }
    
    public synchronized void addResource(final Resource resource) {
        final ConfigLoader loader = getLoader(resource);
        this.resourceConfigs.add(loader);
        final Config loaded = loader.load();
        this.config.add(loaded);
    }
    
    private static ConfigLoader getLoader(final Resource resource) {
        ConfigLoader loader = null;
        final Class<? extends ConfigLoader> loaderClass = resource.getFormat().getLoaderClass();
        try {
            loader = (ConfigLoader)loaderClass.newInstance();
        }
        catch (Exception e) {
            Conf.LOGGER.error("Failed to create " + Conf.class.getPackage().getName() + " for " + loaderClass.getName(), e);
            throw new RuntimeException("Failed to create " + Conf.class.getPackage().getName() + " for " + loaderClass.getName(), e);
        }
        loader.setResource(resource);
        return loader;
    }
    
    public synchronized void reload() {
        this.config.reset();
        for (final ConfigLoader loader : this.resourceConfigs) {
            final Config loaded = loader.load();
            this.config.add(loaded);
        }
    }
    
    @Override
    public String getResource() {
        return this.config.getResource();
    }
    
    @Override
    public Set<String> getNames() {
        return this.config.getNames();
    }
    
    @Override
    public String getString(final String name) {
        return this.config.getString(name);
    }
    
    @Override
    public String getString(final ConfigKey name, final boolean useDefault) {
        return this.config.getString(name, useDefault);
    }
    
    @Override
    public synchronized String getString(final String name, final String defaultValue) {
        return this.config.getString(name, defaultValue);
    }
    
    @Override
    public synchronized void setString(final String name, final String value) {
        this.config.set(name, value);
    }
    
    @Override
    public void setString(final ConfigKey name, final String value) {
        this.setString(name.getPropertyKey(), value);
    }
    
    @Override
    public String getTrimmed(final String name) {
        return this.config.getTrimmed(name);
    }
    
    @Override
    public String getTrimmed(final ConfigKey name) {
        return this.config.getTrimmed(name);
    }
    
    @Override
    public Boolean getBoolean(final String name) {
        return this.config.getBoolean(name);
    }
    
    @Override
    public Boolean getBoolean(final ConfigKey name, final boolean useDefault) {
        return this.config.getBoolean(name, useDefault);
    }
    
    @Override
    public Boolean getBoolean(final String name, final Boolean defaultValue) {
        return this.config.getBoolean(name, defaultValue);
    }
    
    @Override
    public void setBoolean(final String name, final Boolean value) {
        this.setString(name, String.valueOf(value));
    }
    
    @Override
    public void setBoolean(final ConfigKey name, final Boolean value) {
        this.setString(name.getPropertyKey(), String.valueOf(value));
    }
    
    @Override
    public Integer getInt(final String name) {
        return this.config.getInt(name);
    }
    
    @Override
    public Integer getInt(final ConfigKey name, final boolean useDefault) {
        return this.config.getInt(name, useDefault);
    }
    
    @Override
    public Integer getInt(final String name, final Integer defaultValue) {
        return this.config.getInt(name, defaultValue);
    }
    
    @Override
    public void setInt(final String name, final Integer value) {
        this.setString(name, String.valueOf(value));
    }
    
    @Override
    public void setInt(final ConfigKey name, final Integer value) {
        this.setString(name.getPropertyKey(), String.valueOf(value));
    }
    
    @Override
    public Long getLong(final String name) {
        return this.config.getLong(name);
    }
    
    @Override
    public Long getLong(final ConfigKey name, final boolean useDefault) {
        return this.config.getLong(name, useDefault);
    }
    
    @Override
    public Long getLong(final String name, final Long defaultValue) {
        return this.config.getLong(name, defaultValue);
    }
    
    @Override
    public void setLong(final String name, final Long value) {
        this.setString(name, String.valueOf(value));
    }
    
    @Override
    public void setLong(final ConfigKey name, final Long value) {
        this.setString(name.getPropertyKey(), String.valueOf(value));
    }
    
    @Override
    public Float getFloat(final String name) {
        return this.config.getFloat(name);
    }
    
    @Override
    public Float getFloat(final ConfigKey name, final boolean useDefault) {
        return this.config.getFloat(name, useDefault);
    }
    
    @Override
    public Float getFloat(final String name, final Float defaultValue) {
        return this.config.getFloat(name, defaultValue);
    }
    
    @Override
    public void setFloat(final String name, final Float value) {
        this.setString(name, String.valueOf(value));
    }
    
    @Override
    public void setFloat(final ConfigKey name, final Float value) {
        this.setString(name.getPropertyKey(), String.valueOf(value));
    }
    
    @Override
    public List<String> getList(final String name) {
        return this.config.getList(name);
    }
    
    @Override
    public List<String> getList(final String name, final String[] defaultValue) {
        return this.config.getList(name, defaultValue);
    }
    
    @Override
    public List<String> getList(final ConfigKey name) {
        return this.config.getList(name);
    }
    
    @Override
    public Config getConfig(final String name) {
        return this.config.getConfig(name);
    }
    
    @Override
    public Config getConfig(final ConfigKey name) {
        return this.config.getConfig(name);
    }
    
    @Override
    public Class<?> getClass(final String name) throws ClassNotFoundException {
        return this.config.getClass(name);
    }
    
    @Override
    public Class<?> getClass(final String name, final Class<?> defaultValue) throws ClassNotFoundException {
        return this.config.getClass(name, defaultValue);
    }
    
    @Override
    public Class<?> getClass(final ConfigKey name, final boolean useDefault) throws ClassNotFoundException {
        return this.config.getClass(name, useDefault);
    }
    
    @Override
    public <T> T getInstance(final String name) throws ClassNotFoundException {
        return this.config.getInstance(name);
    }
    
    @Override
    public <T> T getInstance(final ConfigKey name) throws ClassNotFoundException {
        return this.config.getInstance(name);
    }
    
    @Override
    public <T> T getInstance(final String name, final Class<T> xface) throws ClassNotFoundException {
        return this.config.getInstance(name, xface);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(Conf.class);
    }
}
