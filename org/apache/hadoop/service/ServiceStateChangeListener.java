// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.service;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public interface ServiceStateChangeListener
{
    void stateChanged(final Service p0);
}
