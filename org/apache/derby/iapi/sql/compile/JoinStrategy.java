// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.compile;

import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.error.StandardException;

public interface JoinStrategy
{
    boolean feasible(final Optimizable p0, final OptimizablePredicateList p1, final Optimizer p2) throws StandardException;
    
    boolean bulkFetchOK();
    
    boolean ignoreBulkFetch();
    
    boolean multiplyBaseCostByOuterRows();
    
    OptimizablePredicateList getBasePredicates(final OptimizablePredicateList p0, final OptimizablePredicateList p1, final Optimizable p2) throws StandardException;
    
    double nonBasePredicateSelectivity(final Optimizable p0, final OptimizablePredicateList p1) throws StandardException;
    
    void putBasePredicates(final OptimizablePredicateList p0, final OptimizablePredicateList p1) throws StandardException;
    
    void estimateCost(final Optimizable p0, final OptimizablePredicateList p1, final ConglomerateDescriptor p2, final CostEstimate p3, final Optimizer p4, final CostEstimate p5) throws StandardException;
    
    int maxCapacity(final int p0, final int p1, final double p2);
    
    String getName();
    
    int scanCostType();
    
    String resultSetMethodName(final boolean p0, final boolean p1);
    
    String joinResultSetMethodName();
    
    String halfOuterJoinResultSetMethodName();
    
    int getScanArgs(final TransactionController p0, final MethodBuilder p1, final Optimizable p2, final OptimizablePredicateList p3, final OptimizablePredicateList p4, final ExpressionClassBuilderInterface p5, final int p6, final int p7, final int p8, final int p9, final int p10, final boolean p11, final int p12, final int p13, final boolean p14) throws StandardException;
    
    void divideUpPredicateLists(final Optimizable p0, final OptimizablePredicateList p1, final OptimizablePredicateList p2, final OptimizablePredicateList p3, final OptimizablePredicateList p4, final DataDictionary p5) throws StandardException;
    
    boolean isHashJoin();
    
    boolean doesMaterialization();
}
