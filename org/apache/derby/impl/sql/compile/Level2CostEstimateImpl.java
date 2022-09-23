// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.CostEstimate;

public class Level2CostEstimateImpl extends CostEstimateImpl
{
    public Level2CostEstimateImpl() {
    }
    
    public Level2CostEstimateImpl(final double n, final double n2, final double n3) {
        super(n, n2, n3);
    }
    
    public CostEstimate cloneMe() {
        return new Level2CostEstimateImpl(this.cost, this.rowCount, this.singleScanRowCount);
    }
    
    public String toString() {
        return "Level2CostEstimateImpl: at " + this.hashCode() + ", cost == " + this.cost + ", rowCount == " + this.rowCount + ", singleScanRowCount == " + this.singleScanRowCount;
    }
    
    public CostEstimateImpl setState(final double n, final double n2, CostEstimateImpl costEstimateImpl) {
        if (costEstimateImpl == null) {
            costEstimateImpl = new Level2CostEstimateImpl();
        }
        return super.setState(n, n2, costEstimateImpl);
    }
}
