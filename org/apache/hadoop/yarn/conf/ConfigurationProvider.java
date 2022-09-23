// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.conf;

import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import java.io.InputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public abstract class ConfigurationProvider
{
    public void init(final Configuration bootstrapConf) throws Exception {
        this.initInternal(bootstrapConf);
    }
    
    public void close() throws Exception {
        this.closeInternal();
    }
    
    public abstract InputStream getConfigurationInputStream(final Configuration p0, final String p1) throws YarnException, IOException;
    
    public abstract void initInternal(final Configuration p0) throws Exception;
    
    public abstract void closeInternal() throws Exception;
}
