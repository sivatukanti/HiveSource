// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public interface Clock
{
    long getTime();
}
