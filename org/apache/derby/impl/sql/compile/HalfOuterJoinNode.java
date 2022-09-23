// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.compile.OptimizablePredicate;
import org.apache.derby.iapi.error.StandardException;

public class HalfOuterJoinNode extends JoinNode
{
    private boolean rightOuterJoin;
    private boolean transformed;
    
    public HalfOuterJoinNode() {
        this.transformed = false;
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) throws StandardException {
        super.init(o, o2, o3, o4, null, o6, null);
        this.rightOuterJoin = (boolean)o5;
        this.flattenableJoin = false;
    }
    
    public boolean pushOptPredicate(final OptimizablePredicate optimizablePredicate) throws StandardException {
        final FromTable fromTable = (FromTable)this.leftResultSet;
        return fromTable.getReferencedTableMap().contains(optimizablePredicate.getReferencedMap()) && fromTable.pushOptPredicate(optimizablePredicate);
    }
    
    public String toString() {
        return "";
    }
    
    public ResultSetNode preprocess(final int n, final GroupByList list, final FromList list2) throws StandardException {
        if (this.rightOuterJoin) {
            final ResultSetNode leftResultSet = this.leftResultSet;
            this.leftResultSet = this.rightResultSet;
            this.rightResultSet = leftResultSet;
            this.transformed = true;
        }
        return super.preprocess(n, list, list2);
    }
    
    public void pushExpressions(final PredicateList list) throws StandardException {
        final FromTable fromTable = (FromTable)this.leftResultSet;
        final FromTable fromTable2 = (FromTable)this.rightResultSet;
        this.pushExpressionsToLeft(list);
        for (int i = this.joinPredicates.size() - 1; i >= 0; --i) {
            final Predicate predicate = (Predicate)this.joinPredicates.elementAt(i);
            if (predicate.getPushable()) {
                this.getRightPredicateList().addPredicate(predicate);
                this.joinPredicates.removeElementAt(i);
            }
        }
        final PredicateList list2 = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
        fromTable.pushExpressions(this.getLeftPredicateList());
        fromTable2.pushExpressions(list2);
    }
    
    public boolean LOJ_reorderable(final int n) throws StandardException {
        boolean b = false;
        ResultSetNode resultSetNode;
        ResultSetNode leftResultSet;
        if (this.rightOuterJoin) {
            resultSetNode = this.rightResultSet;
            leftResultSet = this.leftResultSet;
        }
        else {
            resultSetNode = this.leftResultSet;
            leftResultSet = this.rightResultSet;
        }
        super.normExpressions();
        if (resultSetNode instanceof FromBaseTable && leftResultSet instanceof FromBaseTable) {
            return b;
        }
        if (resultSetNode instanceof HalfOuterJoinNode) {
            b = (((HalfOuterJoinNode)resultSetNode).LOJ_reorderable(n) || b);
        }
        else if (!(resultSetNode instanceof FromBaseTable)) {
            return b;
        }
        if (leftResultSet instanceof HalfOuterJoinNode) {
            b = (((HalfOuterJoinNode)leftResultSet).LOJ_reorderable(n) || b);
        }
        else if (!(leftResultSet instanceof FromBaseTable)) {
            return b;
        }
        if (this.rightOuterJoin || (leftResultSet instanceof HalfOuterJoinNode && ((HalfOuterJoinNode)leftResultSet).rightOuterJoin)) {
            return this.LOJ_bindResultColumns(b);
        }
        final JBitSet loJgetReferencedTables = resultSetNode.LOJgetReferencedTables(n);
        final JBitSet loJgetReferencedTables2 = leftResultSet.LOJgetReferencedTables(n);
        if ((loJgetReferencedTables == null || loJgetReferencedTables2 == null) && b) {
            return this.LOJ_bindResultColumns(b);
        }
        if (leftResultSet instanceof HalfOuterJoinNode) {
            final JBitSet loJgetRPReferencedTables = ((HalfOuterJoinNode)leftResultSet).LOJgetRPReferencedTables(n);
            if (!this.isNullRejecting(this.joinClause, loJgetReferencedTables, loJgetRPReferencedTables)) {
                return this.LOJ_bindResultColumns(b);
            }
            if (this.isNullRejecting(((HalfOuterJoinNode)leftResultSet).joinClause, loJgetRPReferencedTables, ((HalfOuterJoinNode)leftResultSet).LOJgetNPReferencedTables(n))) {
                if (super.subqueryList.size() != 0 || ((HalfOuterJoinNode)leftResultSet).subqueryList.size() != 0 || super.joinPredicates.size() != 0 || ((HalfOuterJoinNode)leftResultSet).joinPredicates.size() != 0 || super.usingClause != null || ((HalfOuterJoinNode)leftResultSet).usingClause != null) {
                    return this.LOJ_bindResultColumns(b);
                }
                b = true;
                final HalfOuterJoinNode leftResultSet2 = (HalfOuterJoinNode)resultSetNode;
                final ResultSetNode leftResultSet3 = ((HalfOuterJoinNode)leftResultSet).leftResultSet;
                final ResultSetNode rightResultSet = ((HalfOuterJoinNode)leftResultSet).rightResultSet;
                ((HalfOuterJoinNode)leftResultSet).rightResultSet = leftResultSet3;
                ((HalfOuterJoinNode)leftResultSet).leftResultSet = leftResultSet2;
                final ValueNode joinClause = this.joinClause;
                this.joinClause = ((HalfOuterJoinNode)leftResultSet).joinClause;
                ((HalfOuterJoinNode)leftResultSet).joinClause = joinClause;
                final FromList list = (FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager());
                this.leftResultSet = leftResultSet;
                this.rightResultSet = rightResultSet;
                ((HalfOuterJoinNode)this.leftResultSet).resultColumns = null;
                ((JoinNode)this.leftResultSet).bindResultColumns(list);
                ((HalfOuterJoinNode)this.leftResultSet).LOJ_reorderable(n);
            }
        }
        return this.LOJ_bindResultColumns(b);
    }
    
    private boolean isNullRejecting(final ValueNode valueNode, final JBitSet set, final JBitSet set2) throws StandardException {
        ValueNode valueNode2 = valueNode;
        boolean b = false;
        while (valueNode2 != null) {
            BinaryOperatorNode binaryOperatorNode = null;
            if (valueNode2 instanceof AndNode) {
                binaryOperatorNode = (AndNode)valueNode2;
                valueNode2 = binaryOperatorNode.getLeftOperand();
            }
            if (valueNode2 instanceof BinaryRelationalOperatorNode) {
                final BinaryRelationalOperatorNode binaryRelationalOperatorNode = (BinaryRelationalOperatorNode)valueNode2;
                final ValueNode leftOperand = binaryRelationalOperatorNode.getLeftOperand();
                final ValueNode rightOperand = binaryRelationalOperatorNode.getRightOperand();
                boolean b2 = false;
                boolean b3 = false;
                if (leftOperand instanceof ColumnReference) {
                    if (set.get(((ColumnReference)leftOperand).getTableNumber())) {
                        b2 = true;
                    }
                    else {
                        if (!set2.get(((ColumnReference)leftOperand).getTableNumber())) {
                            return false;
                        }
                        b3 = true;
                    }
                }
                if (rightOperand instanceof ColumnReference) {
                    if (set.get(((ColumnReference)rightOperand).getTableNumber())) {
                        b2 = true;
                    }
                    else {
                        if (!set2.get(((ColumnReference)rightOperand).getTableNumber())) {
                            return false;
                        }
                        b3 = true;
                    }
                }
                if (b2 && b3) {
                    b = true;
                }
            }
            else if (!(valueNode2 instanceof BooleanConstantNode) || !b) {
                return false;
            }
            if (binaryOperatorNode != null) {
                valueNode2 = binaryOperatorNode.getRightOperand();
            }
            else {
                valueNode2 = null;
            }
        }
        return b;
    }
    
    public boolean LOJ_bindResultColumns(final boolean b) throws StandardException {
        if (b) {
            this.resultColumns = null;
            this.bindResultColumns((FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager()));
        }
        return b;
    }
    
    public FromTable transformOuterJoins(final ValueNode valueNode, final int n) throws StandardException {
        if (valueNode == null) {
            this.leftResultSet.notFlattenableJoin();
            this.rightResultSet.notFlattenableJoin();
            return this;
        }
        super.transformOuterJoins(valueNode, n);
        ResultSetNode resultSetNode;
        if (this.rightOuterJoin) {
            resultSetNode = this.leftResultSet;
        }
        else {
            resultSetNode = this.rightResultSet;
        }
        final JBitSet loJgetReferencedTables = resultSetNode.LOJgetReferencedTables(n);
        ValueNode valueNode2 = valueNode;
        while (valueNode2 instanceof AndNode) {
            final AndNode andNode = (AndNode)valueNode2;
            final ValueNode leftOperand = andNode.getLeftOperand();
            if (leftOperand.isInstanceOf(25)) {
                valueNode2 = andNode.getRightOperand();
            }
            else {
                if (leftOperand instanceof RelationalOperator) {
                    final JBitSet set = new JBitSet(n);
                    if (!leftOperand.categorize(set, true)) {
                        valueNode2 = andNode.getRightOperand();
                        continue;
                    }
                    for (int i = 0; i < n; ++i) {
                        if (set.get(i) && loJgetReferencedTables.get(i)) {
                            final JoinNode joinNode = (JoinNode)this.getNodeFactory().getNode(139, this.leftResultSet, this.rightResultSet, this.joinClause, null, this.resultColumns, null, null, this.getContextManager());
                            joinNode.setTableNumber(this.tableNumber);
                            joinNode.setSubqueryList(this.subqueryList);
                            joinNode.setAggregateVector(this.aggregateVector);
                            return joinNode;
                        }
                    }
                }
                valueNode2 = andNode.getRightOperand();
            }
        }
        this.leftResultSet.notFlattenableJoin();
        this.rightResultSet.notFlattenableJoin();
        return this;
    }
    
    protected void adjustNumberOfRowsReturned(final CostEstimate costEstimate) {
        final CostEstimate costEstimate2 = this.getLeftResultSet().getCostEstimate();
        if (costEstimate.rowCount() < costEstimate2.rowCount()) {
            costEstimate.setCost(costEstimate.getEstimatedCost(), costEstimate2.rowCount(), costEstimate2.rowCount());
        }
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        super.generateCore(activationClassBuilder, methodBuilder, 3);
    }
    
    protected int addOuterJoinArguments(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.rightResultSet.getResultColumns().generateNulls(activationClassBuilder, methodBuilder);
        methodBuilder.push(this.rightOuterJoin);
        return 2;
    }
    
    protected int getNumJoinArguments() {
        return super.getNumJoinArguments() + 2;
    }
    
    protected void oneRowRightSide(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) {
        methodBuilder.push(false);
        methodBuilder.push(false);
    }
    
    ResultSetNode getLogicalLeftResultSet() {
        if (this.rightOuterJoin) {
            return this.rightResultSet;
        }
        return this.leftResultSet;
    }
    
    ResultSetNode getLogicalRightResultSet() {
        if (this.rightOuterJoin) {
            return this.leftResultSet;
        }
        return this.rightResultSet;
    }
    
    public boolean isRightOuterJoin() {
        return this.rightOuterJoin;
    }
    
    public void isJoinColumnForRightOuterJoin(final ResultColumn resultColumn) {
        if (this.isRightOuterJoin() && this.usingClause != null && this.usingClause.getResultColumn(resultColumn.getUnderlyingOrAliasName()) != null) {
            resultColumn.setRightOuterJoinUsingClause(true);
            resultColumn.setJoinResultset(this);
        }
    }
    
    public JBitSet LOJgetNPReferencedTables(final int n) throws StandardException {
        if (this.rightOuterJoin && !this.transformed) {
            return this.leftResultSet.LOJgetReferencedTables(n);
        }
        return this.rightResultSet.LOJgetReferencedTables(n);
    }
    
    public JBitSet LOJgetRPReferencedTables(final int n) throws StandardException {
        if (this.rightOuterJoin && !this.transformed) {
            return this.rightResultSet.LOJgetReferencedTables(n);
        }
        return this.leftResultSet.LOJgetReferencedTables(n);
    }
}
