// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.conf;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class Configured implements Configurable
{
    private Configuration conf;
    
    public Configured() {
        this(null);
    }
    
    public Configured(final Configuration conf) {
        this.setConf(conf);
    }
    
    @Override
    public void setConf(final Configuration conf) {
        this.conf = conf;
    }
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
}
