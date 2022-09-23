// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.wadl.config;

import java.util.Properties;
import com.sun.jersey.server.wadl.WadlGenerator;

public class WadlGeneratorDescription
{
    private Class<? extends WadlGenerator> _generatorClass;
    private Properties _properties;
    
    public WadlGeneratorDescription() {
    }
    
    public WadlGeneratorDescription(final Class<? extends WadlGenerator> generatorClass, final Properties properties) {
        this._generatorClass = generatorClass;
        this._properties = properties;
    }
    
    public Class<? extends WadlGenerator> getGeneratorClass() {
        return this._generatorClass;
    }
    
    public void setGeneratorClass(final Class<? extends WadlGenerator> generatorClass) {
        this._generatorClass = generatorClass;
    }
    
    public Properties getProperties() {
        return this._properties;
    }
    
    public void setProperties(final Properties properties) {
        this._properties = properties;
    }
}
