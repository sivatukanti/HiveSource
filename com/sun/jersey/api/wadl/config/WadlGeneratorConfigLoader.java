// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.wadl.config;

import java.security.AccessController;
import com.sun.jersey.core.reflection.ReflectionHelper;
import java.util.Collections;
import java.util.List;
import com.sun.jersey.api.core.ResourceConfig;

public class WadlGeneratorConfigLoader
{
    public static WadlGeneratorConfig loadWadlGeneratorsFromConfig(final ResourceConfig resourceConfig) {
        final Object wadlGeneratorConfigProperty = resourceConfig.getProperty("com.sun.jersey.config.property.WadlGeneratorConfig");
        if (wadlGeneratorConfigProperty == null) {
            final WadlGeneratorConfig config = new WadlGeneratorConfig() {
                @Override
                public List<WadlGeneratorDescription> configure() {
                    return (List<WadlGeneratorDescription>)Collections.EMPTY_LIST;
                }
            };
            return config;
        }
        try {
            if (wadlGeneratorConfigProperty instanceof WadlGeneratorConfig) {
                return (WadlGeneratorConfig)wadlGeneratorConfigProperty;
            }
            Class<? extends WadlGeneratorConfig> configClazz;
            if (wadlGeneratorConfigProperty instanceof Class) {
                configClazz = ((Class)wadlGeneratorConfigProperty).asSubclass(WadlGeneratorConfig.class);
            }
            else {
                if (!(wadlGeneratorConfigProperty instanceof String)) {
                    throw new RuntimeException("The property com.sun.jersey.config.property.WadlGeneratorConfig is an invalid type: " + wadlGeneratorConfigProperty.getClass().getName() + " (supported: String, Class<? extends WadlGeneratorConfiguration>," + " WadlGeneratorConfiguration)");
                }
                configClazz = AccessController.doPrivileged(ReflectionHelper.classForNameWithExceptionPEA((String)wadlGeneratorConfigProperty)).asSubclass(WadlGeneratorConfig.class);
            }
            final WadlGeneratorConfig config2 = (WadlGeneratorConfig)configClazz.newInstance();
            return config2;
        }
        catch (Exception e) {
            throw new RuntimeException("Could not load WadlGeneratorConfiguration, check the configuration of com.sun.jersey.config.property.WadlGeneratorConfig", e);
        }
    }
}
