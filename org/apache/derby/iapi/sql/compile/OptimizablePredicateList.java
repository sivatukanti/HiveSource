// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.error.StandardException;

public interface OptimizablePredicateList
{
    int size();
    
    OptimizablePredicate getOptPredicate(final int p0);
    
    void removeOptPredicate(final int p0) throws StandardException;
    
    void addOptPredicate(final OptimizablePredicate p0);
    
    boolean useful(final Optimizable p0, final ConglomerateDescriptor p1) throws StandardException;
    
    void pushUsefulPredicates(final Optimizable p0) throws StandardException;
    
    void classify(final Optimizable p0, final ConglomerateDescriptor p1) throws StandardException;
    
    void markAllPredicatesQualifiers();
    
    int hasEqualityPredicateOnOrderedColumn(final Optimizable p0, final int p1, final boolean p2) throws StandardException;
    
    boolean hasOptimizableEqualityPredicate(final Optimizable p0, final int p1, final boolean p2) throws StandardException;
    
    boolean hasOptimizableEquijoin(final Optimizable p0, final int p1) throws StandardException;
    
    void putOptimizableEqualityPredicateFirst(final Optimizable p0, final int p1) throws StandardException;
    
    void transferPredicates(final OptimizablePredicateList p0, final JBitSet p1, final Optimizable p2) throws StandardException;
    
    void transferAllPredicates(final OptimizablePredicateList p0) throws StandardException;
    
    void copyPredicatesToOtherList(final OptimizablePredicateList p0) throws StandardException;
    
    void setPredicatesAndProperties(final OptimizablePredicateList p0) throws StandardException;
    
    boolean isRedundantPredicate(final int p0);
    
    int startOperator(final Optimizable p0);
    
    int stopOperator(final Optimizable p0);
    
    void generateQualifiers(final ExpressionClassBuilderInterface p0, final MethodBuilder p1, final Optimizable p2, final boolean p3) throws StandardException;
    
    void generateStartKey(final ExpressionClassBuilderInterface p0, final MethodBuilder p1, final Optimizable p2) throws StandardException;
    
    void generateStopKey(final ExpressionClassBuilderInterface p0, final MethodBuilder p1, final Optimizable p2) throws StandardException;
    
    boolean sameStartStopPosition() throws StandardException;
    
    double selectivity(final Optimizable p0) throws StandardException;
    
    void adjustForSortElimination(final RequiredRowOrdering p0) throws StandardException;
}
