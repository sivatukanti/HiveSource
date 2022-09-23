// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "MapReduce" })
@InterfaceStability.Unstable
public interface IndexedSortable
{
    int compare(final int p0, final int p1);
    
    void swap(final int p0, final int p1);
}
