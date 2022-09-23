// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Unstable
public interface RefreshHandler
{
    RefreshResponse handleRefresh(final String p0, final String[] p1);
}
