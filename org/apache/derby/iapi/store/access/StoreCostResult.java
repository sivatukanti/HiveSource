// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

public interface StoreCostResult
{
    long getEstimatedRowCount();
    
    void setEstimatedRowCount(final long p0);
    
    double getEstimatedCost();
    
    void setEstimatedCost(final double p0);
}
