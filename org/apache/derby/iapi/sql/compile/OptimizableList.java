// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;

public interface OptimizableList
{
    int size();
    
    Optimizable getOptimizable(final int p0);
    
    void setOptimizable(final int p0, final Optimizable p1);
    
    void verifyProperties(final DataDictionary p0) throws StandardException;
    
    void reOrder(final int[] p0);
    
    boolean useStatistics();
    
    boolean optimizeJoinOrder();
    
    boolean legalJoinOrder(final int p0);
    
    void initAccessPaths(final Optimizer p0);
}
