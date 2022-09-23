// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.compile;

import org.apache.derby.iapi.store.access.StoreCostResult;

public interface CostEstimate extends StoreCostResult
{
    void setCost(final double p0, final double p1, final double p2);
    
    void setCost(final CostEstimate p0);
    
    void setSingleScanRowCount(final double p0);
    
    double compare(final CostEstimate p0);
    
    CostEstimate add(final CostEstimate p0, final CostEstimate p1);
    
    CostEstimate multiply(final double p0, final CostEstimate p1);
    
    CostEstimate divide(final double p0, final CostEstimate p1);
    
    double rowCount();
    
    double singleScanRowCount();
    
    CostEstimate cloneMe();
    
    boolean isUninitialized();
}
