// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;

public interface AccessPath
{
    void setConglomerateDescriptor(final ConglomerateDescriptor p0);
    
    ConglomerateDescriptor getConglomerateDescriptor();
    
    void setCostEstimate(final CostEstimate p0);
    
    CostEstimate getCostEstimate();
    
    void setCoveringIndexScan(final boolean p0);
    
    boolean getCoveringIndexScan();
    
    void setNonMatchingIndexScan(final boolean p0);
    
    boolean getNonMatchingIndexScan();
    
    void setJoinStrategy(final JoinStrategy p0);
    
    JoinStrategy getJoinStrategy();
    
    void setLockMode(final int p0);
    
    int getLockMode();
    
    void copy(final AccessPath p0);
    
    Optimizer getOptimizer();
    
    void initializeAccessPathName(final DataDictionary p0, final TableDescriptor p1) throws StandardException;
}
