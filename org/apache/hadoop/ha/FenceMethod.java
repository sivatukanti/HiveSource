// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public interface FenceMethod
{
    void checkArgs(final String p0) throws BadFencingConfigurationException;
    
    boolean tryFence(final HAServiceTarget p0, final String p1) throws BadFencingConfigurationException;
}
