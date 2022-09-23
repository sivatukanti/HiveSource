// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.compile;

import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import java.util.Properties;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.error.StandardException;

public interface Optimizable
{
    boolean nextAccessPath(final Optimizer p0, final OptimizablePredicateList p1, final RowOrdering p2) throws StandardException;
    
    CostEstimate optimizeIt(final Optimizer p0, final OptimizablePredicateList p1, final CostEstimate p2, final RowOrdering p3) throws StandardException;
    
    AccessPath getCurrentAccessPath();
    
    AccessPath getBestAccessPath();
    
    AccessPath getBestSortAvoidancePath();
    
    AccessPath getTrulyTheBestAccessPath();
    
    void rememberSortAvoidancePath();
    
    boolean considerSortAvoidancePath();
    
    void rememberJoinStrategyAsBest(final AccessPath p0);
    
    TableDescriptor getTableDescriptor();
    
    JBitSet getReferencedTableMap();
    
    boolean pushOptPredicate(final OptimizablePredicate p0) throws StandardException;
    
    void pullOptPredicates(final OptimizablePredicateList p0) throws StandardException;
    
    Optimizable modifyAccessPath(final JBitSet p0) throws StandardException;
    
    boolean isCoveringIndex(final ConglomerateDescriptor p0) throws StandardException;
    
    Properties getProperties();
    
    void setProperties(final Properties p0);
    
    void verifyProperties(final DataDictionary p0) throws StandardException;
    
    String getName() throws StandardException;
    
    String getBaseTableName();
    
    int convertAbsoluteToRelativeColumnPosition(final int p0);
    
    void updateBestPlanMap(final short p0, final Object p1) throws StandardException;
    
    void rememberAsBest(final int p0, final Optimizer p1) throws StandardException;
    
    void startOptimizing(final Optimizer p0, final RowOrdering p1);
    
    CostEstimate estimateCost(final OptimizablePredicateList p0, final ConglomerateDescriptor p1, final CostEstimate p2, final Optimizer p3, final RowOrdering p4) throws StandardException;
    
    boolean isBaseTable();
    
    boolean isMaterializable() throws StandardException;
    
    boolean supportsMultipleInstantiations();
    
    boolean hasLargeObjectColumns();
    
    int getResultSetNumber();
    
    int getTableNumber();
    
    boolean hasTableNumber();
    
    boolean forUpdate();
    
    int initialCapacity();
    
    float loadFactor();
    
    int[] hashKeyColumns();
    
    void setHashKeyColumns(final int[] p0);
    
    boolean feasibleJoinStrategy(final OptimizablePredicateList p0, final Optimizer p1) throws StandardException;
    
    boolean memoryUsageOK(final double p0, final int p1) throws StandardException;
    
    int maxCapacity(final JoinStrategy p0, final int p1) throws StandardException;
    
    boolean legalJoinOrder(final JBitSet p0);
    
    DataDictionary getDataDictionary() throws StandardException;
    
    boolean isTargetTable();
    
    int getNumColumnsReturned();
    
    boolean isOneRowScan() throws StandardException;
    
    void initAccessPaths(final Optimizer p0);
    
    double uniqueJoin(final OptimizablePredicateList p0) throws StandardException;
}
