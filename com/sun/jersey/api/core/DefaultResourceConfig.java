// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.core;

import java.util.HashMap;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Arrays;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.Set;

public class DefaultResourceConfig extends ResourceConfig
{
    private final Set<Class<?>> classes;
    private final Set<Object> singletons;
    private final Map<String, MediaType> mediaExtentions;
    private final Map<String, String> languageExtentions;
    private final Map<String, Object> explicitRootResources;
    private final Map<String, Boolean> features;
    private final Map<String, Object> properties;
    
    public DefaultResourceConfig() {
        this((Set<Class<?>>)null);
    }
    
    public DefaultResourceConfig(final Class<?>... classes) {
        this(new LinkedHashSet<Class<?>>(Arrays.asList(classes)));
    }
    
    public DefaultResourceConfig(final Set<Class<?>> classes) {
        this.classes = new LinkedHashSet<Class<?>>();
        this.singletons = new LinkedHashSet<Object>(1);
        this.mediaExtentions = new HashMap<String, MediaType>(1);
        this.languageExtentions = new HashMap<String, String>(1);
        this.explicitRootResources = new HashMap<String, Object>(1);
        this.features = new HashMap<String, Boolean>();
        this.properties = new HashMap<String, Object>();
        if (null != classes) {
            this.classes.addAll(classes);
        }
    }
    
    @Override
    public Set<Class<?>> getClasses() {
        return this.classes;
    }
    
    @Override
    public Set<Object> getSingletons() {
        return this.singletons;
    }
    
    @Override
    public Map<String, MediaType> getMediaTypeMappings() {
        return this.mediaExtentions;
    }
    
    @Override
    public Map<String, String> getLanguageMappings() {
        return this.languageExtentions;
    }
    
    @Override
    public Map<String, Object> getExplicitRootResources() {
        return this.explicitRootResources;
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
}
