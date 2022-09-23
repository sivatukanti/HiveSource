// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.util.ReuseFactory;
import java.util.ArrayList;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.services.io.FormatableArrayHolder;
import org.apache.derby.iapi.services.io.FormatableIntHolder;
import org.apache.derby.iapi.sql.compile.JoinStrategy;
import org.apache.derby.iapi.sql.compile.ExpressionClassBuilderInterface;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.services.cache.ClassSize;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.sql.compile.OptimizablePredicate;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.compile.Optimizer;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.sql.compile.Optimizable;

public class HashJoinStrategy extends BaseJoinStrategy
{
    public boolean feasible(final Optimizable optimizable, final OptimizablePredicateList list, final Optimizer optimizer) throws StandardException {
        ConglomerateDescriptor conglomerateDescriptor = null;
        if (!optimizable.isMaterializable()) {
            optimizer.trace(24, 0, 0, 0.0, null);
            return false;
        }
        if (optimizable.isTargetTable()) {
            return false;
        }
        if (list != null && list.size() > 0 && !(optimizable instanceof FromBaseTable)) {
            final FromTable fromTable = (FromTable)optimizable;
            final JBitSet set = new JBitSet(fromTable.getReferencedTableMap().size());
            fromTable.accept(new BaseTableNumbersVisitor(set));
            final JBitSet set2 = new JBitSet(set.size());
            for (int i = 0; i < list.size(); ++i) {
                final Predicate predicate = (Predicate)list.getOptPredicate(i);
                if (predicate.isJoinPredicate()) {
                    set2.or(predicate.getReferencedSet());
                }
            }
            set.and(set2);
            if (set.getFirstSetBit() != -1) {
                return false;
            }
        }
        if (optimizable.isBaseTable()) {
            conglomerateDescriptor = optimizable.getCurrentAccessPath().getConglomerateDescriptor();
        }
        return this.findHashKeyColumns(optimizable, conglomerateDescriptor, list) != null;
    }
    
    public boolean ignoreBulkFetch() {
        return true;
    }
    
    public boolean multiplyBaseCostByOuterRows() {
        return false;
    }
    
    public OptimizablePredicateList getBasePredicates(final OptimizablePredicateList list, final OptimizablePredicateList list2, final Optimizable optimizable) throws StandardException {
        for (int i = list.size() - 1; i >= 0; --i) {
            final OptimizablePredicate optPredicate = list.getOptPredicate(i);
            if (optimizable.getReferencedTableMap().contains(optPredicate.getReferencedMap())) {
                list2.addOptPredicate(optPredicate);
                list.removeOptPredicate(i);
            }
        }
        list2.classify(optimizable, optimizable.getCurrentAccessPath().getConglomerateDescriptor());
        return list2;
    }
    
    public double nonBasePredicateSelectivity(final Optimizable optimizable, final OptimizablePredicateList list) throws StandardException {
        double n = 1.0;
        if (list != null) {
            for (int i = 0; i < list.size(); ++i) {
                if (!list.isRedundantPredicate(i)) {
                    n *= list.getOptPredicate(i).selectivity(optimizable);
                }
            }
        }
        return n;
    }
    
    public void putBasePredicates(final OptimizablePredicateList list, final OptimizablePredicateList list2) throws StandardException {
        for (int i = list2.size() - 1; i >= 0; --i) {
            list.addOptPredicate(list2.getOptPredicate(i));
            list2.removeOptPredicate(i);
        }
    }
    
    public void estimateCost(final Optimizable optimizable, final OptimizablePredicateList list, final ConglomerateDescriptor conglomerateDescriptor, final CostEstimate costEstimate, final Optimizer optimizer, final CostEstimate costEstimate2) {
    }
    
    public int maxCapacity(final int n, final int n2, double n3) {
        if (n >= 0) {
            return n;
        }
        n3 += ClassSize.estimateHashEntrySize();
        if (n3 <= 1.0) {
            return n2;
        }
        return (int)(n2 / n3);
    }
    
    public String getName() {
        return "HASH";
    }
    
    public int scanCostType() {
        return 1;
    }
    
    public String resultSetMethodName(final boolean b, final boolean b2) {
        return "getHashScanResultSet";
    }
    
    public String joinResultSetMethodName() {
        return "getHashJoinResultSet";
    }
    
    public String halfOuterJoinResultSetMethodName() {
        return "getHashLeftOuterJoinResultSet";
    }
    
    public int getScanArgs(final TransactionController transactionController, final MethodBuilder methodBuilder, final Optimizable optimizable, final OptimizablePredicateList list, final OptimizablePredicateList list2, final ExpressionClassBuilderInterface expressionClassBuilderInterface, final int n, final int n2, final int n3, final int n4, final int n5, final boolean b, final int n6, final int n7, final boolean b2) throws StandardException {
        final ExpressionClassBuilder expressionClassBuilder = (ExpressionClassBuilder)expressionClassBuilderInterface;
        this.fillInScanArgs1(transactionController, methodBuilder, optimizable, list, expressionClassBuilder, n2);
        list2.generateQualifiers(expressionClassBuilder, methodBuilder, optimizable, true);
        methodBuilder.push(optimizable.initialCapacity());
        methodBuilder.push(optimizable.loadFactor());
        methodBuilder.push(optimizable.maxCapacity(this, n7));
        methodBuilder.push(expressionClassBuilder.addItem(new FormatableArrayHolder(FormatableIntHolder.getFormatableIntHolders(optimizable.hashKeyColumns()))));
        this.fillInScanArgs2(methodBuilder, optimizable, n, n3, n4, n5, b, n6);
        return 28;
    }
    
    public void divideUpPredicateLists(final Optimizable optimizable, final OptimizablePredicateList list, final OptimizablePredicateList list2, final OptimizablePredicateList list3, final OptimizablePredicateList list4, final DataDictionary dataDictionary) throws StandardException {
        list.copyPredicatesToOtherList(list4);
        final ConglomerateDescriptor conglomerateDescriptor = optimizable.getTrulyTheBestAccessPath().getConglomerateDescriptor();
        list.transferPredicates(list2, optimizable.getReferencedTableMap(), optimizable);
        for (int i = list2.size() - 1; i >= 0; --i) {
            final Predicate predicate = (Predicate)list2.getOptPredicate(i);
            if (!predicate.isStoreQualifier() && !predicate.isStartKey() && !predicate.isStopKey()) {
                list2.removeOptPredicate(i);
            }
        }
        for (int j = list.size() - 1; j >= 0; --j) {
            if (!((Predicate)list.getOptPredicate(j)).isStoreQualifier()) {
                list.removeOptPredicate(j);
            }
        }
        list.copyPredicatesToOtherList(list3);
        Optimizable optimizable2 = optimizable;
        if (optimizable instanceof ProjectRestrictNode) {
            final ProjectRestrictNode projectRestrictNode = (ProjectRestrictNode)optimizable;
            if (projectRestrictNode.getChildResult() instanceof Optimizable) {
                optimizable2 = (Optimizable)projectRestrictNode.getChildResult();
            }
        }
        final int[] hashKeyColumns = this.findHashKeyColumns(optimizable2, conglomerateDescriptor, list3);
        if (hashKeyColumns != null) {
            optimizable.setHashKeyColumns(hashKeyColumns);
            list3.markAllPredicatesQualifiers();
            final int[] array = new int[hashKeyColumns.length];
            if (conglomerateDescriptor != null && conglomerateDescriptor.isIndex()) {
                for (int k = 0; k < hashKeyColumns.length; ++k) {
                    array[k] = conglomerateDescriptor.getIndexDescriptor().baseColumnPositions()[hashKeyColumns[k]];
                }
            }
            else {
                for (int l = 0; l < hashKeyColumns.length; ++l) {
                    array[l] = hashKeyColumns[l] + 1;
                }
            }
            for (int n = hashKeyColumns.length - 1; n >= 0; --n) {
                list3.putOptimizableEqualityPredicateFirst(optimizable, array[n]);
            }
            return;
        }
        String s;
        if (conglomerateDescriptor != null && conglomerateDescriptor.isIndex()) {
            s = conglomerateDescriptor.getConglomerateName();
        }
        else {
            s = optimizable.getBaseTableName();
        }
        throw StandardException.newException("42Y63", s, optimizable.getBaseTableName());
    }
    
    public boolean isHashJoin() {
        return true;
    }
    
    public boolean doesMaterialization() {
        return true;
    }
    
    private int[] findHashKeyColumns(final Optimizable optimizable, final ConglomerateDescriptor conglomerateDescriptor, final OptimizablePredicateList list) throws StandardException {
        if (list == null) {
            return null;
        }
        int[] baseColumnPositions;
        if (conglomerateDescriptor == null) {
            baseColumnPositions = new int[optimizable.getNumColumnsReturned()];
            for (int i = 0; i < baseColumnPositions.length; ++i) {
                baseColumnPositions[i] = i + 1;
            }
        }
        else if (conglomerateDescriptor.isIndex()) {
            baseColumnPositions = conglomerateDescriptor.getIndexDescriptor().baseColumnPositions();
        }
        else {
            baseColumnPositions = new int[optimizable.getTableDescriptor().getNumberOfColumns()];
            for (int j = 0; j < baseColumnPositions.length; ++j) {
                baseColumnPositions[j] = j + 1;
            }
        }
        final ArrayList list2 = new ArrayList<Integer>();
        for (int k = 0; k < baseColumnPositions.length; ++k) {
            if (list.hasOptimizableEquijoin(optimizable, baseColumnPositions[k])) {
                list2.add(ReuseFactory.getInteger(k));
            }
        }
        if (list2.isEmpty()) {
            return null;
        }
        final int[] array = new int[list2.size()];
        for (int l = 0; l < array.length; ++l) {
            array[l] = list2.get(l);
        }
        return array;
    }
    
    public String toString() {
        return this.getName();
    }
}
