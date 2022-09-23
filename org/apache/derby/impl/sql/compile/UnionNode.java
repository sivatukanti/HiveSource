// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.compile.RowOrdering;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.sql.compile.Optimizer;
import org.apache.derby.iapi.error.StandardException;

public class UnionNode extends SetOperatorNode
{
    private boolean addNewNodesCalled;
    boolean tableConstructor;
    boolean topTableConstructor;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5) throws StandardException {
        super.init(o, o2, o3, o5);
        this.tableConstructor = (boolean)o4;
    }
    
    public void markTopTableConstructor() {
        this.topTableConstructor = true;
    }
    
    boolean tableConstructor() {
        return this.tableConstructor;
    }
    
    public void rejectParameters() throws StandardException {
        if (!this.tableConstructor()) {
            super.rejectParameters();
        }
    }
    
    void setTableConstructorTypes(final ResultColumnList list) throws StandardException {
        if (this.tableConstructor()) {
            ResultSetNode leftResultSet;
            UnionNode unionNode;
            for (leftResultSet = this; leftResultSet instanceof UnionNode; leftResultSet = unionNode.leftResultSet) {
                unionNode = (UnionNode)leftResultSet;
                ((RowResultSetNode)unionNode.rightResultSet).setTableConstructorTypes(list);
            }
            ((RowResultSetNode)leftResultSet).setTableConstructorTypes(list);
        }
    }
    
    ResultSetNode enhanceRCLForInsert(final InsertNode insertNode, final boolean b, final int[] array) throws StandardException {
        if (this.tableConstructor()) {
            this.leftResultSet = insertNode.enhanceAndCheckForAutoincrement(this.leftResultSet, b, array);
            this.rightResultSet = insertNode.enhanceAndCheckForAutoincrement(this.rightResultSet, b, array);
            if (!b || this.resultColumns.size() < insertNode.resultColumnList.size()) {
                this.resultColumns = this.getRCLForInsert(insertNode, array);
            }
            return this;
        }
        return super.enhanceRCLForInsert(insertNode, b, array);
    }
    
    public CostEstimate optimizeIt(final Optimizer optimizer, final OptimizablePredicateList list, final CostEstimate costEstimate, final RowOrdering rowOrdering) throws StandardException {
        if (list != null && !this.getCurrentAccessPath().getJoinStrategy().isHashJoin()) {
            for (int i = list.size() - 1; i >= 0; --i) {
                if (this.pushOptPredicate(list.getOptPredicate(i))) {
                    list.removeOptPredicate(i);
                }
            }
        }
        this.updateBestPlanMap((short)1, this);
        this.leftResultSet = this.optimizeSource(optimizer, this.leftResultSet, this.getLeftOptPredicateList(), costEstimate);
        this.rightResultSet = this.optimizeSource(optimizer, this.rightResultSet, this.getRightOptPredicateList(), costEstimate);
        final CostEstimate costEstimate2 = this.getCostEstimate(optimizer);
        costEstimate2.setCost(this.leftResultSet.getCostEstimate().getEstimatedCost(), this.leftResultSet.getCostEstimate().rowCount(), this.leftResultSet.getCostEstimate().singleScanRowCount() + this.rightResultSet.getCostEstimate().singleScanRowCount());
        costEstimate2.add(this.rightResultSet.costEstimate, costEstimate2);
        this.getCurrentAccessPath().getJoinStrategy().estimateCost(this, list, null, costEstimate, optimizer, costEstimate2);
        optimizer.considerCost(this, list, costEstimate2, costEstimate);
        return costEstimate2;
    }
    
    public void pushExpressions(final PredicateList list) throws StandardException {
        if (this.leftResultSet instanceof UnionNode) {
            ((UnionNode)this.leftResultSet).pushExpressions(list);
        }
        else if (this.leftResultSet instanceof SelectNode) {
            list.pushExpressionsIntoSelect((SelectNode)this.leftResultSet, true);
        }
        if (this.rightResultSet instanceof UnionNode) {
            ((UnionNode)this.rightResultSet).pushExpressions(list);
        }
        else if (this.rightResultSet instanceof SelectNode) {
            list.pushExpressionsIntoSelect((SelectNode)this.rightResultSet, true);
        }
    }
    
    public Optimizable modifyAccessPath(final JBitSet set) throws StandardException {
        final Optimizable modifyAccessPath = super.modifyAccessPath(set);
        if (this.addNewNodesCalled) {
            return modifyAccessPath;
        }
        return (Optimizable)this.addNewNodes();
    }
    
    public ResultSetNode modifyAccessPaths() throws StandardException {
        final ResultSetNode modifyAccessPaths = super.modifyAccessPaths();
        if (this.addNewNodesCalled) {
            return modifyAccessPaths;
        }
        return this.addNewNodes();
    }
    
    private ResultSetNode addNewNodes() throws StandardException {
        ResultSetNode resultSetNode = this;
        if (this.addNewNodesCalled) {
            return this;
        }
        this.addNewNodesCalled = true;
        if (!this.all) {
            if (!this.columnTypesAndLengthsMatch()) {
                resultSetNode = (ResultSetNode)this.getNodeFactory().getNode(122, resultSetNode, null, null, Boolean.FALSE, this.getContextManager());
            }
            resultSetNode = (ResultSetNode)this.getNodeFactory().getNode(124, resultSetNode.genProjectRestrict(), Boolean.FALSE, this.tableProperties, this.getContextManager());
            ((FromTable)resultSetNode).setTableNumber(this.tableNumber);
            resultSetNode.setReferencedTableMap((JBitSet)this.referencedTableMap.clone());
            this.all = true;
        }
        for (int i = 0; i < this.orderByLists.length; ++i) {
            if (this.orderByLists[i] != null) {
                resultSetNode = (ResultSetNode)this.getNodeFactory().getNode(140, resultSetNode, this.orderByLists[i], this.tableProperties, this.getContextManager());
            }
            if (i == 0 && (this.offset != null || this.fetchFirst != null)) {
                final ResultColumnList copyListAndObjects = resultSetNode.getResultColumns().copyListAndObjects();
                copyListAndObjects.genVirtualColumnNodes(resultSetNode, resultSetNode.getResultColumns());
                resultSetNode = (ResultSetNode)this.getNodeFactory().getNode(223, resultSetNode, copyListAndObjects, this.offset, this.fetchFirst, this.hasJDBClimitClause, this.getContextManager());
            }
        }
        return resultSetNode;
    }
    
    public String toString() {
        return "";
    }
    
    public void bindExpressions(final FromList list) throws StandardException {
        super.bindExpressions(list);
        if (this.topTableConstructor && !this.insertSource) {
            final DataTypeDescriptor[] array = new DataTypeDescriptor[this.leftResultSet.getResultColumns().size()];
            int n = 0;
            ResultSetNode leftResultSet;
            UnionNode unionNode;
            for (leftResultSet = this; leftResultSet instanceof SetOperatorNode; leftResultSet = unionNode.leftResultSet) {
                unionNode = (UnionNode)leftResultSet;
                n += this.getParamColumnTypes(array, (RowResultSetNode)unionNode.rightResultSet);
            }
            if (n + this.getParamColumnTypes(array, (RowResultSetNode)leftResultSet) < array.length) {
                throw StandardException.newException("42Y10");
            }
            ResultSetNode leftResultSet2;
            UnionNode unionNode2;
            for (leftResultSet2 = this; leftResultSet2 instanceof SetOperatorNode; leftResultSet2 = unionNode2.leftResultSet) {
                unionNode2 = (UnionNode)leftResultSet2;
                this.setParamColumnTypes(array, (RowResultSetNode)unionNode2.rightResultSet);
            }
            this.setParamColumnTypes(array, (RowResultSetNode)leftResultSet2);
        }
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.assignResultSetNumber();
        this.costEstimate = this.getFinalCostEstimate();
        activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        this.leftResultSet.generate(activationClassBuilder, methodBuilder);
        if (!this.resultColumns.isExactTypeAndLengthMatch(this.leftResultSet.getResultColumns())) {
            activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
            methodBuilder.swap();
            this.generateNormalizationResultSet(activationClassBuilder, methodBuilder, this.getCompilerContext().getNextResultSetNumber(), this.makeResultDescription());
        }
        this.rightResultSet.generate(activationClassBuilder, methodBuilder);
        if (!this.resultColumns.isExactTypeAndLengthMatch(this.rightResultSet.getResultColumns())) {
            activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
            methodBuilder.swap();
            this.generateNormalizationResultSet(activationClassBuilder, methodBuilder, this.getCompilerContext().getNextResultSetNumber(), this.makeResultDescription());
        }
        methodBuilder.push(this.resultSetNumber);
        methodBuilder.push(this.costEstimate.rowCount());
        methodBuilder.push(this.costEstimate.getEstimatedCost());
        methodBuilder.callMethod((short)185, null, "getUnionResultSet", "org.apache.derby.iapi.sql.execute.NoPutResultSet", 5);
    }
    
    public CostEstimate getFinalCostEstimate() throws StandardException {
        if (this.finalCostEstimate != null) {
            return this.finalCostEstimate;
        }
        final CostEstimate finalCostEstimate = this.leftResultSet.getFinalCostEstimate();
        final CostEstimate finalCostEstimate2 = this.rightResultSet.getFinalCostEstimate();
        (this.finalCostEstimate = this.getNewCostEstimate()).setCost(finalCostEstimate.getEstimatedCost(), finalCostEstimate.rowCount(), finalCostEstimate.singleScanRowCount() + finalCostEstimate2.singleScanRowCount());
        this.finalCostEstimate.add(finalCostEstimate2, this.finalCostEstimate);
        return this.finalCostEstimate;
    }
    
    String getOperatorName() {
        return "UNION";
    }
}
