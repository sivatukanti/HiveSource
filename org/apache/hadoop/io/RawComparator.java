// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.util.Comparator;

@InterfaceAudience.Public
@InterfaceStability.Stable
public interface RawComparator<T> extends Comparator<T>
{
    int compare(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4, final int p5);
}
