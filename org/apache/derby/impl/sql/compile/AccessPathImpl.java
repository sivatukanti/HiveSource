// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.compile.Optimizer;
import org.apache.derby.iapi.sql.compile.JoinStrategy;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.compile.AccessPath;

class AccessPathImpl implements AccessPath
{
    ConglomerateDescriptor cd;
    private CostEstimate costEstimate;
    boolean coveringIndexScan;
    boolean nonMatchingIndexScan;
    JoinStrategy joinStrategy;
    int lockMode;
    Optimizer optimizer;
    private String accessPathName;
    
    AccessPathImpl(final Optimizer optimizer) {
        this.cd = null;
        this.costEstimate = null;
        this.coveringIndexScan = false;
        this.nonMatchingIndexScan = false;
        this.joinStrategy = null;
        this.accessPathName = "";
        this.optimizer = optimizer;
    }
    
    public void setConglomerateDescriptor(final ConglomerateDescriptor cd) {
        this.cd = cd;
    }
    
    public ConglomerateDescriptor getConglomerateDescriptor() {
        return this.cd;
    }
    
    public void setCostEstimate(final CostEstimate cost) {
        if (this.costEstimate == null) {
            if (cost != null) {
                this.costEstimate = cost.cloneMe();
            }
        }
        else if (cost == null) {
            this.costEstimate = null;
        }
        else {
            this.costEstimate.setCost(cost);
        }
    }
    
    public CostEstimate getCostEstimate() {
        return this.costEstimate;
    }
    
    public void setCoveringIndexScan(final boolean coveringIndexScan) {
        this.coveringIndexScan = coveringIndexScan;
    }
    
    public boolean getCoveringIndexScan() {
        return this.coveringIndexScan;
    }
    
    public void setNonMatchingIndexScan(final boolean nonMatchingIndexScan) {
        this.nonMatchingIndexScan = nonMatchingIndexScan;
    }
    
    public boolean getNonMatchingIndexScan() {
        return this.nonMatchingIndexScan;
    }
    
    public void setJoinStrategy(final JoinStrategy joinStrategy) {
        this.joinStrategy = joinStrategy;
    }
    
    public JoinStrategy getJoinStrategy() {
        return this.joinStrategy;
    }
    
    public void setLockMode(final int lockMode) {
        this.lockMode = lockMode;
    }
    
    public int getLockMode() {
        return this.lockMode;
    }
    
    public void copy(final AccessPath accessPath) {
        this.setConglomerateDescriptor(accessPath.getConglomerateDescriptor());
        this.setCostEstimate(accessPath.getCostEstimate());
        this.setCoveringIndexScan(accessPath.getCoveringIndexScan());
        this.setNonMatchingIndexScan(accessPath.getNonMatchingIndexScan());
        this.setJoinStrategy(accessPath.getJoinStrategy());
        this.setLockMode(accessPath.getLockMode());
    }
    
    public Optimizer getOptimizer() {
        return this.optimizer;
    }
    
    public String toString() {
        return "";
    }
    
    public void initializeAccessPathName(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor) throws StandardException {
        if (this.cd == null) {
            return;
        }
        if (this.cd.isConstraint()) {
            final ConstraintDescriptor constraintDescriptor = dataDictionary.getConstraintDescriptor(tableDescriptor, this.cd.getUUID());
            if (constraintDescriptor == null) {
                throw StandardException.newException("42X94", "CONSTRAINT on TABLE", tableDescriptor.getName());
            }
            this.accessPathName = constraintDescriptor.getConstraintName();
        }
        else if (this.cd.isIndex()) {
            this.accessPathName = this.cd.getConglomerateName();
        }
        else {
            this.accessPathName = "";
        }
    }
}
