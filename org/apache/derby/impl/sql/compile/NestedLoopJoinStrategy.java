// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.compile.ExpressionClassBuilderInterface;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.Optimizer;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.sql.compile.Optimizable;

public class NestedLoopJoinStrategy extends BaseJoinStrategy
{
    public boolean feasible(final Optimizable optimizable, final OptimizablePredicateList list, final Optimizer optimizer) throws StandardException {
        return optimizable.isMaterializable() || optimizable.supportsMultipleInstantiations();
    }
    
    public boolean multiplyBaseCostByOuterRows() {
        return true;
    }
    
    public OptimizablePredicateList getBasePredicates(final OptimizablePredicateList list, final OptimizablePredicateList list2, final Optimizable optimizable) throws StandardException {
        if (list != null) {
            list.transferAllPredicates(list2);
            list2.classify(optimizable, optimizable.getCurrentAccessPath().getConglomerateDescriptor());
        }
        return list2;
    }
    
    public double nonBasePredicateSelectivity(final Optimizable optimizable, final OptimizablePredicateList list) {
        return 1.0;
    }
    
    public void putBasePredicates(final OptimizablePredicateList list, final OptimizablePredicateList list2) throws StandardException {
        for (int i = list2.size() - 1; i >= 0; --i) {
            list.addOptPredicate(list2.getOptPredicate(i));
            list2.removeOptPredicate(i);
        }
    }
    
    public void estimateCost(final Optimizable optimizable, final OptimizablePredicateList list, final ConglomerateDescriptor conglomerateDescriptor, final CostEstimate costEstimate, final Optimizer optimizer, final CostEstimate costEstimate2) {
        costEstimate2.multiply(costEstimate.rowCount(), costEstimate2);
        optimizer.trace(23, optimizable.getTableNumber(), 0, costEstimate.rowCount(), costEstimate2);
    }
    
    public int maxCapacity(final int n, final int n2, final double n3) {
        return Integer.MAX_VALUE;
    }
    
    public String getName() {
        return "NESTEDLOOP";
    }
    
    public int scanCostType() {
        return 2;
    }
    
    public String resultSetMethodName(final boolean b, final boolean b2) {
        if (b) {
            return "getBulkTableScanResultSet";
        }
        if (b2) {
            return "getMultiProbeTableScanResultSet";
        }
        return "getTableScanResultSet";
    }
    
    public String joinResultSetMethodName() {
        return "getNestedLoopJoinResultSet";
    }
    
    public String halfOuterJoinResultSetMethodName() {
        return "getNestedLoopLeftOuterJoinResultSet";
    }
    
    public int getScanArgs(final TransactionController transactionController, final MethodBuilder methodBuilder, final Optimizable optimizable, final OptimizablePredicateList list, final OptimizablePredicateList list2, final ExpressionClassBuilderInterface expressionClassBuilderInterface, final int n, final int n2, final int n3, final int n4, final int n5, final boolean b, final int n6, final int n7, final boolean b2) throws StandardException {
        final ExpressionClassBuilder expressionClassBuilder = (ExpressionClassBuilder)expressionClassBuilderInterface;
        int n8;
        if (b2) {
            n8 = 26;
        }
        else if (n > 1) {
            n8 = 26;
        }
        else {
            n8 = 24;
        }
        this.fillInScanArgs1(transactionController, methodBuilder, optimizable, list, expressionClassBuilder, n2);
        if (b2) {
            ((PredicateList)list).generateInListValues(expressionClassBuilder, methodBuilder);
        }
        this.fillInScanArgs2(methodBuilder, optimizable, n, n3, n4, n5, b, n6);
        return n8;
    }
    
    public void divideUpPredicateLists(final Optimizable optimizable, final OptimizablePredicateList list, final OptimizablePredicateList predicatesAndProperties, final OptimizablePredicateList list2, final OptimizablePredicateList list3, final DataDictionary dataDictionary) throws StandardException {
        list.setPredicatesAndProperties(predicatesAndProperties);
    }
    
    public boolean doesMaterialization() {
        return false;
    }
    
    public String toString() {
        return this.getName();
    }
    
    protected boolean validForOutermostTable() {
        return true;
    }
}
