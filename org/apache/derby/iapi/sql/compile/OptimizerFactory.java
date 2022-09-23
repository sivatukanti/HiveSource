// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;

public interface OptimizerFactory
{
    public static final String MODULE = "org.apache.derby.iapi.sql.compile.OptimizerFactory";
    
    Optimizer getOptimizer(final OptimizableList p0, final OptimizablePredicateList p1, final DataDictionary p2, final RequiredRowOrdering p3, final int p4, final LanguageConnectionContext p5) throws StandardException;
    
    CostEstimate getCostEstimate() throws StandardException;
    
    boolean supportsOptimizerTrace();
    
    int getMaxMemoryPerTable();
}
