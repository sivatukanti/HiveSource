// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.CostEstimate;

public class CostEstimateImpl implements CostEstimate
{
    public double cost;
    public double rowCount;
    public double singleScanRowCount;
    
    public CostEstimateImpl() {
    }
    
    public CostEstimateImpl(final double cost, final double rowCount, final double singleScanRowCount) {
        this.cost = cost;
        this.rowCount = rowCount;
        this.singleScanRowCount = singleScanRowCount;
    }
    
    public void setCost(final double cost, final double rowCount, final double singleScanRowCount) {
        this.cost = cost;
        this.rowCount = rowCount;
        this.singleScanRowCount = singleScanRowCount;
    }
    
    public void setCost(final CostEstimate costEstimate) {
        this.cost = costEstimate.getEstimatedCost();
        this.rowCount = costEstimate.rowCount();
        this.singleScanRowCount = costEstimate.singleScanRowCount();
    }
    
    public void setSingleScanRowCount(final double singleScanRowCount) {
        this.singleScanRowCount = singleScanRowCount;
    }
    
    public double compare(final CostEstimate costEstimate) {
        if (this.cost != Double.POSITIVE_INFINITY || costEstimate.getEstimatedCost() != Double.POSITIVE_INFINITY) {
            return this.cost - ((CostEstimateImpl)costEstimate).cost;
        }
        if (this.rowCount != Double.POSITIVE_INFINITY || costEstimate.rowCount() != Double.POSITIVE_INFINITY) {
            return this.rowCount - costEstimate.rowCount();
        }
        if (this.singleScanRowCount != Double.POSITIVE_INFINITY || costEstimate.singleScanRowCount() != Double.POSITIVE_INFINITY) {
            return this.singleScanRowCount - costEstimate.singleScanRowCount();
        }
        return 0.0;
    }
    
    public CostEstimate add(final CostEstimate costEstimate, final CostEstimate costEstimate2) {
        final CostEstimateImpl costEstimateImpl = (CostEstimateImpl)costEstimate;
        return this.setState(this.cost + costEstimateImpl.cost, this.rowCount + costEstimateImpl.rowCount, (CostEstimateImpl)costEstimate2);
    }
    
    public CostEstimate multiply(final double n, final CostEstimate costEstimate) {
        return this.setState(this.cost * n, this.rowCount * n, (CostEstimateImpl)costEstimate);
    }
    
    public CostEstimate divide(final double n, final CostEstimate costEstimate) {
        return this.setState(this.cost / n, this.rowCount / n, (CostEstimateImpl)costEstimate);
    }
    
    public double rowCount() {
        return this.rowCount;
    }
    
    public double singleScanRowCount() {
        return this.singleScanRowCount;
    }
    
    public CostEstimate cloneMe() {
        return new CostEstimateImpl(this.cost, this.rowCount, this.singleScanRowCount);
    }
    
    public boolean isUninitialized() {
        return this.cost == Double.MAX_VALUE && this.rowCount == Double.MAX_VALUE && this.singleScanRowCount == Double.MAX_VALUE;
    }
    
    public double getEstimatedCost() {
        return this.cost;
    }
    
    public void setEstimatedCost(final double cost) {
        this.cost = cost;
    }
    
    public long getEstimatedRowCount() {
        return (long)this.rowCount;
    }
    
    public void setEstimatedRowCount(final long n) {
        this.rowCount = (double)n;
        this.singleScanRowCount = (double)n;
    }
    
    public CostEstimateImpl setState(final double cost, final double rowCount, CostEstimateImpl costEstimateImpl) {
        if (costEstimateImpl == null) {
            costEstimateImpl = new CostEstimateImpl();
        }
        costEstimateImpl.cost = cost;
        costEstimateImpl.rowCount = rowCount;
        return costEstimateImpl;
    }
}
