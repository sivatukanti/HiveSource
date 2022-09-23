// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util.bloom;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public interface RemoveScheme
{
    public static final short RANDOM = 0;
    public static final short MINIMUM_FN = 1;
    public static final short MAXIMUM_FP = 2;
    public static final short RATIO = 3;
}
