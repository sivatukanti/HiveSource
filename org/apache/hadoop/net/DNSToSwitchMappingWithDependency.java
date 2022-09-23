// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public interface DNSToSwitchMappingWithDependency extends DNSToSwitchMapping
{
    List<String> getDependency(final String p0);
}
