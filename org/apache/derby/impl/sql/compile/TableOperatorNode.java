// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.sql.compile.OptimizableList;
import org.apache.derby.iapi.sql.compile.RequiredRowOrdering;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.Optimizer;

abstract class TableOperatorNode extends FromTable
{
    ResultSetNode leftResultSet;
    ResultSetNode rightResultSet;
    Optimizer leftOptimizer;
    Optimizer rightOptimizer;
    private boolean leftModifyAccessPathsDone;
    private boolean rightModifyAccessPathsDone;
    
    public void init(final Object o, final Object o2, final Object o3) throws StandardException {
        this.init(null, o3);
        this.leftResultSet = (ResultSetNode)o;
        this.rightResultSet = (ResultSetNode)o2;
    }
    
    public void bindUntypedNullsToResultColumns(final ResultColumnList list) throws StandardException {
        this.leftResultSet.bindUntypedNullsToResultColumns(list);
        this.rightResultSet.bindUntypedNullsToResultColumns(list);
    }
    
    public Optimizable modifyAccessPath(final JBitSet set) throws StandardException {
        boolean b = false;
        if (this.leftResultSet instanceof FromTable) {
            if (this.leftOptimizer != null) {
                this.leftOptimizer.modifyAccessPaths();
                this.leftResultSet = (ResultSetNode)((OptimizerImpl)this.leftOptimizer).optimizableList.getOptimizable(0);
            }
            else {
                this.leftResultSet = (ResultSetNode)((FromTable)this.leftResultSet).modifyAccessPath(set);
            }
            this.leftModifyAccessPathsDone = true;
        }
        else {
            b = true;
        }
        if (this.rightResultSet instanceof FromTable) {
            if (this.rightOptimizer != null) {
                this.rightOptimizer.modifyAccessPaths();
                this.rightResultSet = (ResultSetNode)((OptimizerImpl)this.rightOptimizer).optimizableList.getOptimizable(0);
            }
            else {
                this.rightResultSet = (ResultSetNode)((FromTable)this.rightResultSet).modifyAccessPath(set);
            }
            this.rightModifyAccessPathsDone = true;
        }
        else {
            b = true;
        }
        if (b) {
            return (Optimizable)this.modifyAccessPaths();
        }
        return this;
    }
    
    public void verifyProperties(final DataDictionary dataDictionary) throws StandardException {
        if (this.leftResultSet instanceof Optimizable) {
            ((Optimizable)this.leftResultSet).verifyProperties(dataDictionary);
        }
        if (this.rightResultSet instanceof Optimizable) {
            ((Optimizable)this.rightResultSet).verifyProperties(dataDictionary);
        }
        super.verifyProperties(dataDictionary);
    }
    
    public void updateBestPlanMap(final short n, final Object o) throws StandardException {
        super.updateBestPlanMap(n, o);
        if (this.leftResultSet instanceof Optimizable) {
            ((Optimizable)this.leftResultSet).updateBestPlanMap(n, o);
        }
        else if (this.leftResultSet.getOptimizerImpl() != null) {
            this.leftResultSet.getOptimizerImpl().updateBestPlanMaps(n, o);
        }
        if (this.rightResultSet instanceof Optimizable) {
            ((Optimizable)this.rightResultSet).updateBestPlanMap(n, o);
        }
        else if (this.rightResultSet.getOptimizerImpl() != null) {
            this.rightResultSet.getOptimizerImpl().updateBestPlanMaps(n, o);
        }
    }
    
    public String toString() {
        return "";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public ResultSetNode getLeftResultSet() {
        return this.leftResultSet;
    }
    
    public ResultSetNode getRightResultSet() {
        return this.rightResultSet;
    }
    
    public ResultSetNode getLeftmostResultSet() {
        if (this.leftResultSet instanceof TableOperatorNode) {
            return ((TableOperatorNode)this.leftResultSet).getLeftmostResultSet();
        }
        return this.leftResultSet;
    }
    
    public void setLeftmostResultSet(final ResultSetNode resultSetNode) {
        if (this.leftResultSet instanceof TableOperatorNode) {
            ((TableOperatorNode)this.leftResultSet).setLeftmostResultSet(resultSetNode);
        }
        else {
            this.leftResultSet = resultSetNode;
        }
    }
    
    public void setLevel(final int level) {
        super.setLevel(level);
        if (this.leftResultSet instanceof FromTable) {
            ((FromTable)this.leftResultSet).setLevel(level);
        }
        if (this.rightResultSet instanceof FromTable) {
            ((FromTable)this.rightResultSet).setLevel(level);
        }
    }
    
    public String getExposedName() {
        return null;
    }
    
    public void setNestedInParens(final boolean b) {
    }
    
    public ResultSetNode bindNonVTITables(final DataDictionary dataDictionary, final FromList list) throws StandardException {
        this.leftResultSet = this.leftResultSet.bindNonVTITables(dataDictionary, list);
        this.rightResultSet = this.rightResultSet.bindNonVTITables(dataDictionary, list);
        if (this.tableNumber == -1) {
            this.tableNumber = this.getCompilerContext().getNextTableNumber();
        }
        return this;
    }
    
    public ResultSetNode bindVTITables(final FromList list) throws StandardException {
        this.leftResultSet = this.leftResultSet.bindVTITables(list);
        this.rightResultSet = this.rightResultSet.bindVTITables(list);
        return this;
    }
    
    public void bindExpressions(final FromList list) throws StandardException {
        if (!(this instanceof UnionNode) || !((UnionNode)this).tableConstructor()) {
            this.leftResultSet.rejectParameters();
            this.rightResultSet.rejectParameters();
        }
        this.leftResultSet.bindExpressions(list);
        this.rightResultSet.bindExpressions(list);
    }
    
    public void rejectParameters() throws StandardException {
        this.leftResultSet.rejectParameters();
        this.rightResultSet.rejectParameters();
    }
    
    public void bindExpressionsWithTables(final FromList list) throws StandardException {
        if (!(this instanceof UnionNode) || !((UnionNode)this).tableConstructor()) {
            this.leftResultSet.rejectParameters();
            this.rightResultSet.rejectParameters();
        }
        this.leftResultSet.bindExpressionsWithTables(list);
        this.rightResultSet.bindExpressionsWithTables(list);
    }
    
    public void bindResultColumns(final FromList list) throws StandardException {
        this.leftResultSet.bindResultColumns(list);
        this.rightResultSet.bindResultColumns(list);
    }
    
    public void bindResultColumns(final TableDescriptor tableDescriptor, final FromVTI fromVTI, final ResultColumnList list, final DMLStatementNode dmlStatementNode, final FromList list2) throws StandardException {
        this.leftResultSet.bindResultColumns(tableDescriptor, fromVTI, list, dmlStatementNode, list2);
        this.rightResultSet.bindResultColumns(tableDescriptor, fromVTI, list, dmlStatementNode, list2);
    }
    
    protected FromTable getFromTableByName(final String s, final String s2, final boolean b) throws StandardException {
        FromTable fromTable = this.leftResultSet.getFromTableByName(s, s2, b);
        if (fromTable == null) {
            fromTable = this.rightResultSet.getFromTableByName(s, s2, b);
        }
        return fromTable;
    }
    
    public ResultSetNode preprocess(final int n, final GroupByList list, final FromList list2) throws StandardException {
        this.leftResultSet = this.leftResultSet.preprocess(n, list, list2);
        if (this.leftResultSet instanceof FromSubquery) {
            this.leftResultSet = ((FromSubquery)this.leftResultSet).extractSubquery(n);
        }
        this.rightResultSet = this.rightResultSet.preprocess(n, list, list2);
        if (this.rightResultSet instanceof FromSubquery) {
            this.rightResultSet = ((FromSubquery)this.rightResultSet).extractSubquery(n);
        }
        (this.referencedTableMap = (JBitSet)this.leftResultSet.getReferencedTableMap().clone()).or(this.rightResultSet.getReferencedTableMap());
        this.referencedTableMap.set(this.tableNumber);
        if (this.isFlattenableJoinNode()) {
            return this;
        }
        this.projectResultColumns();
        return this.genProjectRestrict(n);
    }
    
    void projectResultColumns() throws StandardException {
        this.resultColumns.doProjection();
    }
    
    void setReferencedColumns() {
    }
    
    public ResultSetNode optimize(final DataDictionary dataDictionary, final PredicateList list, final double n) throws StandardException {
        this.costEstimate = this.getOptimizer((OptimizableList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this, this.getContextManager()), list, dataDictionary, null).newCostEstimate();
        this.leftResultSet = this.leftResultSet.optimize(dataDictionary, list, n);
        this.rightResultSet = this.rightResultSet.optimize(dataDictionary, list, n);
        this.costEstimate.setCost(this.leftResultSet.getCostEstimate().getEstimatedCost(), this.leftResultSet.getCostEstimate().rowCount(), this.leftResultSet.getCostEstimate().singleScanRowCount() + this.rightResultSet.getCostEstimate().singleScanRowCount());
        this.costEstimate.add(this.rightResultSet.costEstimate, this.costEstimate);
        return this;
    }
    
    public ResultSetNode modifyAccessPaths() throws StandardException {
        if (!this.leftModifyAccessPathsDone) {
            if (this.leftOptimizer != null) {
                this.leftOptimizer.modifyAccessPaths();
                this.leftResultSet = (ResultSetNode)((OptimizerImpl)this.leftOptimizer).optimizableList.getOptimizable(0);
            }
            else if (this instanceof SetOperatorNode) {
                this.leftResultSet = this.leftResultSet.modifyAccessPaths(((SetOperatorNode)this).getLeftOptPredicateList());
            }
            else {
                this.leftResultSet = this.leftResultSet.modifyAccessPaths();
            }
        }
        if (!this.rightModifyAccessPathsDone) {
            if (this.rightOptimizer != null) {
                this.rightOptimizer.modifyAccessPaths();
                this.rightResultSet = (ResultSetNode)((OptimizerImpl)this.rightOptimizer).optimizableList.getOptimizable(0);
            }
            else if (this instanceof SetOperatorNode) {
                this.rightResultSet = this.rightResultSet.modifyAccessPaths(((SetOperatorNode)this).getRightOptPredicateList());
            }
            else {
                this.rightResultSet = this.rightResultSet.modifyAccessPaths();
            }
        }
        return this;
    }
    
    public boolean referencesTarget(final String s, final boolean b) throws StandardException {
        return this.leftResultSet.referencesTarget(s, b) || this.rightResultSet.referencesTarget(s, b);
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        return this.leftResultSet.referencesSessionSchema() || this.rightResultSet.referencesSessionSchema();
    }
    
    protected ResultSetNode optimizeSource(Optimizer optimizer, final ResultSetNode resultSetNode, PredicateList list, final CostEstimate costEstimate) throws StandardException {
        ResultSetNode optimize;
        if (resultSetNode instanceof FromTable) {
            final FromList list2 = (FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), resultSetNode, this.getContextManager());
            if (list == null) {
                list = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
            }
            final LanguageConnectionContext languageConnectionContext = this.getLanguageConnectionContext();
            optimizer = languageConnectionContext.getOptimizerFactory().getOptimizer(list2, list, this.getDataDictionary(), null, this.getCompilerContext().getNumTables(), languageConnectionContext);
            optimizer.prepForNextRound();
            if (resultSetNode == this.leftResultSet) {
                this.leftOptimizer = optimizer;
            }
            else if (resultSetNode == this.rightResultSet) {
                this.rightOptimizer = optimizer;
            }
            optimizer.setOuterRows(costEstimate.rowCount());
            while (optimizer.getNextPermutation()) {
                while (optimizer.getNextDecoratedPermutation()) {
                    optimizer.costPermutation();
                }
            }
            optimize = resultSetNode;
        }
        else {
            optimize = resultSetNode.optimize(optimizer.getDataDictionary(), list, costEstimate.rowCount());
        }
        return optimize;
    }
    
    void decrementLevel(final int n) {
        this.leftResultSet.decrementLevel(n);
        this.rightResultSet.decrementLevel(n);
    }
    
    void adjustForSortElimination() {
        this.leftResultSet.adjustForSortElimination();
        this.rightResultSet.adjustForSortElimination();
    }
    
    void adjustForSortElimination(final RequiredRowOrdering requiredRowOrdering) throws StandardException {
        this.leftResultSet.adjustForSortElimination(requiredRowOrdering);
        this.rightResultSet.adjustForSortElimination(requiredRowOrdering);
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.leftResultSet != null) {
            this.leftResultSet = (ResultSetNode)this.leftResultSet.accept(visitor);
        }
        if (this.rightResultSet != null) {
            this.rightResultSet = (ResultSetNode)this.rightResultSet.accept(visitor);
        }
    }
    
    public boolean needsSpecialRCLBinding() {
        return true;
    }
}
