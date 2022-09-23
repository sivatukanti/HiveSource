// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn;

import java.io.IOException;
import java.io.FileInputStream;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import java.io.InputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.conf.ConfigurationProvider;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class LocalConfigurationProvider extends ConfigurationProvider
{
    @Override
    public InputStream getConfigurationInputStream(final Configuration bootstrapConf, final String name) throws IOException, YarnException {
        if (name == null || name.isEmpty()) {
            throw new YarnException("Illegal argument! The parameter should not be null or empty");
        }
        if (YarnConfiguration.RM_CONFIGURATION_FILES.contains(name)) {
            return bootstrapConf.getConfResourceAsInputStream(name);
        }
        return new FileInputStream(name);
    }
    
    @Override
    public void initInternal(final Configuration bootstrapConf) throws Exception {
    }
    
    @Override
    public void closeInternal() throws Exception {
    }
}
