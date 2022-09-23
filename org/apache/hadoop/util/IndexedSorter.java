// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "MapReduce" })
@InterfaceStability.Unstable
public interface IndexedSorter
{
    void sort(final IndexedSortable p0, final int p1, final int p2);
    
    void sort(final IndexedSortable p0, final int p1, final int p2, final Progressable p3);
}
