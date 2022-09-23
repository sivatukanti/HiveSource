// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.config;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesFileConfigLoader extends PropertiesConfigLoader
{
    @Override
    protected void loadConfig(final ConfigImpl config, final Resource resource) throws Exception {
        final Properties propConfig = new Properties();
        propConfig.load((InputStream)resource.getResource());
        this.loadConfig(config, propConfig);
    }
}
