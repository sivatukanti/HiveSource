// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.spring;

import java.net.URL;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.configuration2.ConfigurationConverter;
import org.springframework.util.Assert;
import org.springframework.core.io.Resource;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.CompositeConfiguration;
import java.util.Properties;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class ConfigurationPropertiesFactoryBean implements InitializingBean, FactoryBean<Properties>
{
    private CompositeConfiguration compositeConfiguration;
    private Configuration[] configurations;
    private Resource[] locations;
    private boolean throwExceptionOnMissing;
    
    public ConfigurationPropertiesFactoryBean() {
        this.throwExceptionOnMissing = true;
    }
    
    public ConfigurationPropertiesFactoryBean(final Configuration configuration) {
        this.throwExceptionOnMissing = true;
        Assert.notNull((Object)configuration);
        this.compositeConfiguration = new CompositeConfiguration(configuration);
    }
    
    public Properties getObject() throws Exception {
        return (this.compositeConfiguration != null) ? ConfigurationConverter.getProperties(this.compositeConfiguration) : null;
    }
    
    public Class getObjectType() {
        return Properties.class;
    }
    
    public boolean isSingleton() {
        return true;
    }
    
    public void afterPropertiesSet() throws Exception {
        if (this.compositeConfiguration == null && ArrayUtils.isEmpty(this.configurations) && ArrayUtils.isEmpty(this.locations)) {
            throw new IllegalArgumentException("no configuration object or location specified");
        }
        if (this.compositeConfiguration == null) {
            this.compositeConfiguration = new CompositeConfiguration();
        }
        this.compositeConfiguration.setThrowExceptionOnMissing(this.throwExceptionOnMissing);
        if (this.configurations != null) {
            for (final Configuration configuration : this.configurations) {
                this.compositeConfiguration.addConfiguration(configuration);
            }
        }
        if (this.locations != null) {
            for (final Resource location : this.locations) {
                final URL url = location.getURL();
                final Configuration props = new Configurations().properties(url);
                this.compositeConfiguration.addConfiguration(props);
            }
        }
    }
    
    public Configuration[] getConfigurations() {
        return defensiveCopy(this.configurations);
    }
    
    public void setConfigurations(final Configuration[] configurations) {
        this.configurations = defensiveCopy(configurations);
    }
    
    public Resource[] getLocations() {
        return defensiveCopy(this.locations);
    }
    
    public void setLocations(final Resource[] locations) {
        this.locations = defensiveCopy(locations);
    }
    
    public boolean isThrowExceptionOnMissing() {
        return this.throwExceptionOnMissing;
    }
    
    public void setThrowExceptionOnMissing(final boolean throwExceptionOnMissing) {
        this.throwExceptionOnMissing = throwExceptionOnMissing;
    }
    
    public CompositeConfiguration getConfiguration() {
        return this.compositeConfiguration;
    }
    
    private static <T> T[] defensiveCopy(final T[] src) {
        return (T[])((src != null) ? ((T[])src.clone()) : null);
    }
}
