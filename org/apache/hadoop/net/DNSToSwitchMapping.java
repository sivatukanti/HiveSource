// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface DNSToSwitchMapping
{
    List<String> resolve(final List<String> p0);
    
    void reloadCachedMappings();
    
    void reloadCachedMappings(final List<String> p0);
}
