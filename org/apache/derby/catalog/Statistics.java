// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog;

public interface Statistics
{
    long getRowEstimate();
    
    double selectivity(final Object[] p0);
}
