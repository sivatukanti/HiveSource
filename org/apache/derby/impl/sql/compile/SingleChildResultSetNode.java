// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import java.util.List;
import org.apache.derby.iapi.sql.compile.OptimizableList;
import org.apache.derby.iapi.sql.compile.RequiredRowOrdering;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.compile.Optimizer;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.sql.compile.AccessPath;
import org.apache.derby.iapi.util.JBitSet;

abstract class SingleChildResultSetNode extends FromTable
{
    ResultSetNode childResult;
    protected boolean hasTrulyTheBestAccessPath;
    
    public void init(final Object o, final Object o2) {
        super.init(null, o2);
        this.childResult = (ResultSetNode)o;
        if (this.childResult.getReferencedTableMap() != null) {
            this.referencedTableMap = (JBitSet)this.childResult.getReferencedTableMap().clone();
        }
    }
    
    public AccessPath getTrulyTheBestAccessPath() {
        if (this.hasTrulyTheBestAccessPath) {
            return super.getTrulyTheBestAccessPath();
        }
        if (this.childResult instanceof Optimizable) {
            return ((Optimizable)this.childResult).getTrulyTheBestAccessPath();
        }
        return super.getTrulyTheBestAccessPath();
    }
    
    public ResultSetNode getChildResult() {
        return this.childResult;
    }
    
    void setChildResult(final ResultSetNode childResult) {
        this.childResult = childResult;
    }
    
    public void pullOptPredicates(final OptimizablePredicateList list) throws StandardException {
        if (this.childResult instanceof Optimizable) {
            ((Optimizable)this.childResult).pullOptPredicates(list);
        }
    }
    
    public boolean forUpdate() {
        if (this.childResult instanceof Optimizable) {
            return ((Optimizable)this.childResult).forUpdate();
        }
        return super.forUpdate();
    }
    
    public void initAccessPaths(final Optimizer optimizer) {
        super.initAccessPaths(optimizer);
        if (this.childResult instanceof Optimizable) {
            ((Optimizable)this.childResult).initAccessPaths(optimizer);
        }
    }
    
    public void updateBestPlanMap(final short n, final Object o) throws StandardException {
        super.updateBestPlanMap(n, o);
        if (this.childResult instanceof Optimizable) {
            ((Optimizable)this.childResult).updateBestPlanMap(n, o);
        }
        else if (this.childResult.getOptimizerImpl() != null) {
            this.childResult.getOptimizerImpl().updateBestPlanMaps(n, o);
        }
    }
    
    public void printSubNodes(final int n) {
    }
    
    public boolean referencesTarget(final String s, final boolean b) throws StandardException {
        return this.childResult.referencesTarget(s, b);
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        return this.childResult.referencesSessionSchema();
    }
    
    public void setLevel(final int n) {
        super.setLevel(n);
        if (this.childResult instanceof FromTable) {
            ((FromTable)this.childResult).setLevel(n);
        }
    }
    
    boolean subqueryReferencesTarget(final String s, final boolean b) throws StandardException {
        return this.childResult.subqueryReferencesTarget(s, b);
    }
    
    public ResultSetNode preprocess(final int n, final GroupByList list, final FromList list2) throws StandardException {
        this.childResult = this.childResult.preprocess(n, list, list2);
        this.referencedTableMap = (JBitSet)this.childResult.getReferencedTableMap().clone();
        return this;
    }
    
    public ResultSetNode addNewPredicate(final Predicate predicate) throws StandardException {
        this.childResult = this.childResult.addNewPredicate(predicate);
        return this;
    }
    
    public void pushExpressions(final PredicateList list) throws StandardException {
        if (this.childResult instanceof FromTable) {
            ((FromTable)this.childResult).pushExpressions(list);
        }
    }
    
    public boolean flattenableInFromSubquery(final FromList list) {
        return false;
    }
    
    public ResultSetNode ensurePredicateList(final int n) throws StandardException {
        return this;
    }
    
    public ResultSetNode optimize(final DataDictionary dataDictionary, final PredicateList list, final double n) throws StandardException {
        this.childResult = this.childResult.optimize(dataDictionary, list, n);
        (this.costEstimate = this.getOptimizer((OptimizableList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager()), list, dataDictionary, null).newCostEstimate()).setCost(this.childResult.getCostEstimate().getEstimatedCost(), this.childResult.getCostEstimate().rowCount(), this.childResult.getCostEstimate().singleScanRowCount());
        return this;
    }
    
    public ResultSetNode modifyAccessPaths() throws StandardException {
        this.childResult = this.childResult.modifyAccessPaths();
        return this;
    }
    
    public ResultSetNode changeAccessPath() throws StandardException {
        this.childResult = this.childResult.changeAccessPath();
        return this;
    }
    
    protected FromTable getFromTableByName(final String s, final String s2, final boolean b) throws StandardException {
        return this.childResult.getFromTableByName(s, s2, b);
    }
    
    void decrementLevel(final int n) {
        super.decrementLevel(n);
        this.childResult.decrementLevel(n);
    }
    
    public int updateTargetLockMode() {
        return this.childResult.updateTargetLockMode();
    }
    
    boolean isOrderedOn(final ColumnReference[] array, final boolean b, final List list) throws StandardException {
        return this.childResult.isOrderedOn(array, b, list);
    }
    
    public boolean isOneRowResultSet() throws StandardException {
        return this.childResult.isOneRowResultSet();
    }
    
    public boolean isNotExists() {
        return this.childResult.isNotExists();
    }
    
    protected boolean reflectionNeededForProjection() {
        return !this.resultColumns.allExpressionsAreColumns(this.childResult);
    }
    
    void adjustForSortElimination() {
        this.childResult.adjustForSortElimination();
    }
    
    void adjustForSortElimination(final RequiredRowOrdering requiredRowOrdering) throws StandardException {
        this.childResult.adjustForSortElimination(requiredRowOrdering);
    }
    
    public CostEstimate getFinalCostEstimate() throws StandardException {
        if (this.costEstimate == null) {
            return this.childResult.getFinalCostEstimate();
        }
        return this.costEstimate;
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.childResult != null) {
            this.childResult = (ResultSetNode)this.childResult.accept(visitor);
        }
    }
}
