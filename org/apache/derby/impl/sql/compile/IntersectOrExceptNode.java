// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.compile.RowOrdering;
import org.apache.derby.iapi.sql.compile.Optimizer;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.sql.compile.NodeFactory;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.util.ReuseFactory;
import java.util.BitSet;
import org.apache.derby.iapi.error.StandardException;

public class IntersectOrExceptNode extends SetOperatorNode
{
    private int opType;
    public static final int INTERSECT_OP = 1;
    public static final int EXCEPT_OP = 2;
    private boolean addNewNodesCalled;
    private int[] intermediateOrderByColumns;
    private int[] intermediateOrderByDirection;
    private boolean[] intermediateOrderByNullsLow;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5) throws StandardException {
        super.init(o2, o3, o4, o5);
        this.opType = (int)o;
    }
    
    private int getOpType() {
        return this.opType;
    }
    
    public ResultSetNode preprocess(final int n, final GroupByList list, final FromList list2) throws StandardException {
        this.intermediateOrderByColumns = new int[this.getResultColumns().size()];
        this.intermediateOrderByDirection = new int[this.intermediateOrderByColumns.length];
        this.intermediateOrderByNullsLow = new boolean[this.intermediateOrderByColumns.length];
        if (this.orderByLists[0] != null) {
            final BitSet set = new BitSet(this.intermediateOrderByColumns.length);
            final int size = this.orderByLists[0].size();
            int n2 = 0;
            for (int i = 0; i < size; ++i) {
                if (!set.get(i)) {
                    final OrderByColumn orderByColumn = this.orderByLists[0].getOrderByColumn(i);
                    this.intermediateOrderByDirection[n2] = (orderByColumn.isAscending() ? 1 : -1);
                    this.intermediateOrderByNullsLow[n2] = orderByColumn.isNullsOrderedLow();
                    set.set(this.intermediateOrderByColumns[n2] = orderByColumn.getResultColumn().getColumnPosition() - 1);
                    ++n2;
                }
            }
            for (int j = 0; j < this.intermediateOrderByColumns.length; ++j) {
                if (!set.get(j)) {
                    this.intermediateOrderByDirection[n2] = 1;
                    this.intermediateOrderByNullsLow[n2] = false;
                    this.intermediateOrderByColumns[n2] = j;
                    ++n2;
                }
            }
            this.orderByLists[0] = null;
        }
        else {
            for (int k = 0; k < this.intermediateOrderByColumns.length; ++k) {
                this.intermediateOrderByDirection[k] = 1;
                this.intermediateOrderByNullsLow[k] = false;
                this.intermediateOrderByColumns[k] = k;
            }
        }
        this.pushOrderingDown(this.leftResultSet);
        this.pushOrderingDown(this.rightResultSet);
        return super.preprocess(n, list, list2);
    }
    
    private void pushOrderingDown(final ResultSetNode resultSetNode) throws StandardException {
        final ContextManager contextManager = this.getContextManager();
        final NodeFactory nodeFactory = this.getNodeFactory();
        final OrderByList list = (OrderByList)nodeFactory.getNode(7, contextManager);
        for (int i = 0; i < this.intermediateOrderByColumns.length; ++i) {
            final OrderByColumn orderByColumn = (OrderByColumn)nodeFactory.getNode(104, nodeFactory.getNode(70, ReuseFactory.getInteger(this.intermediateOrderByColumns[i] + 1), contextManager), contextManager);
            if (this.intermediateOrderByDirection[i] < 0) {
                orderByColumn.setDescending();
            }
            if (this.intermediateOrderByNullsLow[i]) {
                orderByColumn.setNullsOrderedLow();
            }
            list.addOrderByColumn(orderByColumn);
        }
        list.bindOrderByColumns(resultSetNode);
        resultSetNode.pushOrderByList(list);
    }
    
    public CostEstimate estimateCost(final OptimizablePredicateList list, final ConglomerateDescriptor conglomerateDescriptor, final CostEstimate costEstimate, final Optimizer optimizer, final RowOrdering rowOrdering) throws StandardException {
        this.leftResultSet = this.optimizeSource(optimizer, this.leftResultSet, null, costEstimate);
        this.rightResultSet = this.optimizeSource(optimizer, this.rightResultSet, null, costEstimate);
        final CostEstimate costEstimate2 = this.getCostEstimate(optimizer);
        final CostEstimate costEstimate3 = this.leftResultSet.getCostEstimate();
        final CostEstimate costEstimate4 = this.rightResultSet.getCostEstimate();
        costEstimate2.setCost(costEstimate3.getEstimatedCost() + costEstimate4.getEstimatedCost(), this.getRowCountEstimate(costEstimate3.rowCount(), costEstimate4.rowCount()), this.getSingleScanRowCountEstimate(costEstimate3.singleScanRowCount(), costEstimate4.singleScanRowCount()));
        return costEstimate2;
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
        if (this.addNewNodesCalled) {
            return this;
        }
        this.addNewNodesCalled = true;
        ResultSetNode resultSetNode = this;
        if (this.orderByLists[0] != null) {
            resultSetNode = (ResultSetNode)this.getNodeFactory().getNode(140, resultSetNode, this.orderByLists[0], this.tableProperties, this.getContextManager());
        }
        if (this.offset != null || this.fetchFirst != null) {
            final ResultColumnList copyListAndObjects = resultSetNode.getResultColumns().copyListAndObjects();
            copyListAndObjects.genVirtualColumnNodes(resultSetNode, resultSetNode.getResultColumns());
            resultSetNode = (ResultSetNode)this.getNodeFactory().getNode(223, resultSetNode, copyListAndObjects, this.offset, this.fetchFirst, this.hasJDBClimitClause, this.getContextManager());
        }
        return resultSetNode;
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.assignResultSetNumber();
        this.costEstimate = this.getFinalCostEstimate();
        activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        this.getLeftResultSet().generate(activationClassBuilder, methodBuilder);
        this.getRightResultSet().generate(activationClassBuilder, methodBuilder);
        activationClassBuilder.pushThisAsActivation(methodBuilder);
        methodBuilder.push(this.resultSetNumber);
        methodBuilder.push(this.costEstimate.getEstimatedRowCount());
        methodBuilder.push(this.costEstimate.getEstimatedCost());
        methodBuilder.push(this.getOpType());
        methodBuilder.push(this.all);
        methodBuilder.push(this.getCompilerContext().addSavedObject(this.intermediateOrderByColumns));
        methodBuilder.push(this.getCompilerContext().addSavedObject(this.intermediateOrderByDirection));
        methodBuilder.push(this.getCompilerContext().addSavedObject(this.intermediateOrderByNullsLow));
        methodBuilder.callMethod((short)185, null, "getSetOpResultSet", "org.apache.derby.iapi.sql.execute.NoPutResultSet", 11);
    }
    
    public CostEstimate getFinalCostEstimate() throws StandardException {
        if (this.finalCostEstimate != null) {
            return this.finalCostEstimate;
        }
        final CostEstimate finalCostEstimate = this.leftResultSet.getFinalCostEstimate();
        final CostEstimate finalCostEstimate2 = this.rightResultSet.getFinalCostEstimate();
        (this.finalCostEstimate = this.getNewCostEstimate()).setCost(finalCostEstimate.getEstimatedCost() + finalCostEstimate2.getEstimatedCost(), this.getRowCountEstimate(finalCostEstimate.rowCount(), finalCostEstimate2.rowCount()), this.getSingleScanRowCountEstimate(finalCostEstimate.singleScanRowCount(), finalCostEstimate2.singleScanRowCount()));
        return this.finalCostEstimate;
    }
    
    String getOperatorName() {
        switch (this.opType) {
            case 1: {
                return "INTERSECT";
            }
            case 2: {
                return "EXCEPT";
            }
            default: {
                return "?";
            }
        }
    }
    
    double getRowCountEstimate(final double a, final double b) {
        switch (this.opType) {
            case 1: {
                return Math.min(a, b) / 2.0;
            }
            case 2: {
                return (a + Math.max(0.0, a - b)) / 2.0;
            }
            default: {
                return 1.0;
            }
        }
    }
    
    double getSingleScanRowCountEstimate(final double n, final double n2) {
        return this.getRowCountEstimate(n, n2);
    }
}
