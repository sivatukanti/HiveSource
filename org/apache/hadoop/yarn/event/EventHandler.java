// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.event;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface EventHandler<T extends Event>
{
    void handle(final T p0);
}
