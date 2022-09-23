// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client;

import java.util.Map;
import java.util.LinkedHashSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;
import com.sun.jersey.api.client.config.ClientConfig;

class ComponentsClientConfig implements ClientConfig
{
    private final ClientConfig cc;
    private final Set<Class<?>> providers;
    
    public ComponentsClientConfig(final ClientConfig cc, final Class<?>... components) {
        this(cc, new HashSet<Class<?>>(Arrays.asList(components)));
    }
    
    public ComponentsClientConfig(final ClientConfig cc, final Set<Class<?>> components) {
        this.providers = new LinkedHashSet<Class<?>>();
        this.cc = cc;
        this.providers.addAll(cc.getClasses());
        this.providers.addAll(components);
    }
    
    @Override
    public Set<Class<?>> getClasses() {
        return this.providers;
    }
    
    @Override
    public Set<Object> getSingletons() {
        return this.cc.getSingletons();
    }
    
    @Override
    public Map<String, Boolean> getFeatures() {
        return this.cc.getFeatures();
    }
    
    @Override
    public boolean getFeature(final String featureName) {
        return this.cc.getFeature(featureName);
    }
    
    @Override
    public Map<String, Object> getProperties() {
        return this.cc.getProperties();
    }
    
    @Override
    public Object getProperty(final String propertyName) {
        return this.cc.getProperty(propertyName);
    }
    
    @Override
    public boolean getPropertyAsFeature(final String name) {
        return this.cc.getPropertyAsFeature(name);
    }
}
