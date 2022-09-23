// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.config;

import java.util.Iterator;
import java.util.Properties;

public class PropertiesConfigLoader extends ConfigLoader
{
    @Override
    protected void loadConfig(final ConfigImpl config, final Resource resource) throws Exception {
        final Properties propConfig = (Properties)resource.getResource();
        this.loadConfig(config, propConfig);
    }
    
    protected void loadConfig(final ConfigImpl config, final Properties propConfig) {
        for (final Object key : propConfig.keySet()) {
            if (key instanceof String) {
                final String value = propConfig.getProperty((String)key);
                if (value == null) {
                    continue;
                }
                config.set((String)key, value);
            }
        }
    }
}
