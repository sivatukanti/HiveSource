// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client.config;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class DefaultClientConfig implements ClientConfig
{
    private final Set<Class<?>> providers;
    private final Set<Object> providerInstances;
    private final Map<String, Boolean> features;
    private final Map<String, Object> properties;
    
    public DefaultClientConfig() {
        this.providers = new LinkedHashSet<Class<?>>();
        this.providerInstances = new LinkedHashSet<Object>();
        this.features = new HashMap<String, Boolean>();
        this.properties = new HashMap<String, Object>();
    }
    
    public DefaultClientConfig(final Class<?>... providers) {
        this.providers = new LinkedHashSet<Class<?>>();
        this.providerInstances = new LinkedHashSet<Object>();
        this.features = new HashMap<String, Boolean>();
        this.properties = new HashMap<String, Object>();
        Collections.addAll(this.providers, providers);
    }
    
    public DefaultClientConfig(final Set<Class<?>> providers) {
        this.providers = new LinkedHashSet<Class<?>>();
        this.providerInstances = new LinkedHashSet<Object>();
        this.features = new HashMap<String, Boolean>();
        this.properties = new HashMap<String, Object>();
        this.providers.addAll(providers);
    }
    
    @Override
    public Set<Class<?>> getClasses() {
        return this.providers;
    }
    
    @Override
    public Set<Object> getSingletons() {
        return this.providerInstances;
    }
    
    @Override
    public Map<String, Boolean> getFeatures() {
        return this.features;
    }
    
    @Override
    public boolean getFeature(final String featureName) {
        final Boolean v = this.features.get(featureName);
        return v != null && v;
    }
    
    @Override
    public Map<String, Object> getProperties() {
        return this.properties;
    }
    
    @Override
    public Object getProperty(final String propertyName) {
        return this.properties.get(propertyName);
    }
    
    @Override
    public boolean getPropertyAsFeature(final String name) {
        final Boolean v = this.getProperties().get(name);
        return v != null && v;
    }
}
