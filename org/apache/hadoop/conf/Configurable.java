// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.conf;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public interface Configurable
{
    void setConf(final Configuration p0);
    
    Configuration getConf();
}
