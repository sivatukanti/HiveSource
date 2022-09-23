// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.conf;

import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ConfigurationProviderFactory
{
    public static ConfigurationProvider getConfigurationProvider(final Configuration bootstrapConf) {
        Class<? extends ConfigurationProvider> defaultProviderClass;
        try {
            defaultProviderClass = (Class<? extends ConfigurationProvider>)Class.forName("org.apache.hadoop.yarn.LocalConfigurationProvider");
        }
        catch (Exception e) {
            throw new YarnRuntimeException("Invalid default configuration provider classorg.apache.hadoop.yarn.LocalConfigurationProvider", e);
        }
        final ConfigurationProvider configurationProvider = ReflectionUtils.newInstance(bootstrapConf.getClass("yarn.resourcemanager.configuration.provider-class", defaultProviderClass, ConfigurationProvider.class), bootstrapConf);
        return configurationProvider;
    }
}
