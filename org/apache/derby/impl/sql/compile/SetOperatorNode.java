// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.sql.compile.OptimizablePredicate;
import java.util.List;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.error.StandardException;
import java.util.HashMap;

abstract class SetOperatorNode extends TableOperatorNode
{
    boolean all;
    OrderByList[] orderByLists;
    ValueNode offset;
    ValueNode fetchFirst;
    boolean hasJDBClimitClause;
    private PredicateList leftOptPredicates;
    private PredicateList rightOptPredicates;
    private PredicateList pushedPredicates;
    private HashMap leftScopedPreds;
    private HashMap rightScopedPreds;
    
    SetOperatorNode() {
        this.orderByLists = new OrderByList[1];
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4) throws StandardException {
        super.init(o, o2, o4);
        this.all = (boolean)o3;
        this.resultColumns = this.leftResultSet.getResultColumns().copyListAndObjects();
    }
    
    public Optimizable modifyAccessPath(final JBitSet set, final PredicateList list) throws StandardException {
        if (list != null && !this.getTrulyTheBestAccessPath().getJoinStrategy().isHashJoin()) {
            for (int i = list.size() - 1; i >= 0; --i) {
                if (this.pushOptPredicate(list.getOptPredicate(i))) {
                    list.removeOptPredicate(i);
                }
            }
        }
        final CostEstimate finalCostEstimate = this.getFinalCostEstimate();
        Object o = this.modifyAccessPath(set);
        final CollectNodesVisitor collectNodesVisitor = new CollectNodesVisitor(UnionNode.class);
        this.accept(collectNodesVisitor);
        final List list2 = collectNodesVisitor.getList();
        boolean b = false;
        for (int j = list2.size() - 1; j >= 0; --j) {
            if (list2.get(j).hasUnPushedPredicates()) {
                b = true;
                break;
            }
        }
        if (b) {
            final ResultSetNode resultSetNode = (ResultSetNode)this.getNodeFactory().getNode(151, o, ((ResultSetNode)o).getResultColumns(), null, this.pushedPredicates, null, null, null, this.getContextManager());
            resultSetNode.costEstimate = finalCostEstimate.cloneMe();
            resultSetNode.setReferencedTableMap(((ResultSetNode)o).getReferencedTableMap());
            o = resultSetNode;
        }
        return (Optimizable)o;
    }
    
    public boolean pushOptPredicate(final OptimizablePredicate optimizablePredicate) throws StandardException {
        if (!(this instanceof UnionNode)) {
            return false;
        }
        final Predicate predicate = (Predicate)optimizablePredicate;
        if (!predicate.pushableToSubqueries()) {
            return false;
        }
        final JBitSet set = new JBitSet(this.getReferencedTableMap().size());
        final BaseTableNumbersVisitor baseTableNumbersVisitor = new BaseTableNumbersVisitor(set);
        this.leftResultSet.accept(baseTableNumbersVisitor);
        if (set.getFirstSetBit() == -1) {
            return false;
        }
        set.clearAll();
        this.rightResultSet.accept(baseTableNumbersVisitor);
        if (set.getFirstSetBit() == -1) {
            return false;
        }
        set.clearAll();
        this.accept(baseTableNumbersVisitor);
        final int[] array = { -1 };
        Predicate predScopedForResultSet = null;
        if (this.leftScopedPreds == null) {
            this.leftScopedPreds = new HashMap();
        }
        else {
            predScopedForResultSet = this.leftScopedPreds.get(predicate);
        }
        if (predScopedForResultSet == null) {
            predScopedForResultSet = predicate.getPredScopedForResultSet(set, this.leftResultSet, array);
            this.leftScopedPreds.put(predicate, predScopedForResultSet);
        }
        this.getLeftOptPredicateList().addOptPredicate(predScopedForResultSet);
        Predicate predScopedForResultSet2 = null;
        if (this.rightScopedPreds == null) {
            this.rightScopedPreds = new HashMap();
        }
        else {
            predScopedForResultSet2 = this.rightScopedPreds.get(predicate);
        }
        if (predScopedForResultSet2 == null) {
            predScopedForResultSet2 = predicate.getPredScopedForResultSet(set, this.rightResultSet, array);
            this.rightScopedPreds.put(predicate, predScopedForResultSet2);
        }
        this.getRightOptPredicateList().addOptPredicate(predScopedForResultSet2);
        if (this.pushedPredicates == null) {
            this.pushedPredicates = new PredicateList();
        }
        this.pushedPredicates.addOptPredicate(predicate);
        return true;
    }
    
    public void pullOptPredicates(final OptimizablePredicateList list) throws StandardException {
        if (this.pushedPredicates == null) {
            return;
        }
        if (this.leftOptPredicates != null) {
            this.leftOptPredicates.removeAllElements();
        }
        if (this.rightOptPredicates != null) {
            this.rightOptPredicates.removeAllElements();
        }
        final RemapCRsVisitor remapCRsVisitor = new RemapCRsVisitor(false);
        for (int i = 0; i < this.pushedPredicates.size(); ++i) {
            final Predicate predicate = (Predicate)this.pushedPredicates.getOptPredicate(i);
            if (predicate.isScopedForPush()) {
                predicate.getAndNode().accept(remapCRsVisitor);
            }
            else {
                list.addOptPredicate(predicate);
            }
        }
        this.pushedPredicates.removeAllElements();
    }
    
    protected boolean hasUnPushedPredicates() {
        return (this.leftOptPredicates != null && this.leftOptPredicates.size() > 0) || (this.rightOptPredicates != null && this.rightOptPredicates.size() > 0);
    }
    
    public String toString() {
        return "";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public void bindResultColumns(final FromList list) throws StandardException {
        super.bindResultColumns(list);
        this.buildRCL();
    }
    
    public void bindResultColumns(final TableDescriptor tableDescriptor, final FromVTI fromVTI, final ResultColumnList list, final DMLStatementNode dmlStatementNode, final FromList list2) throws StandardException {
        super.bindResultColumns(tableDescriptor, fromVTI, list, dmlStatementNode, list2);
        this.buildRCL();
    }
    
    private void buildRCL() throws StandardException {
        if (this.leftResultSet.getResultColumns().visibleSize() != this.rightResultSet.getResultColumns().visibleSize()) {
            throw StandardException.newException("42X58", this.getOperatorName());
        }
        (this.resultColumns = this.leftResultSet.getResultColumns().copyListAndObjects()).removeGeneratedGroupingColumns();
        this.resultColumns.removeOrderByColumns();
        this.resultColumns.setUnionResultExpression(this.rightResultSet.getResultColumns(), this.tableNumber, this.level, this.getOperatorName());
    }
    
    public void bindUntypedNullsToResultColumns(final ResultColumnList list) throws StandardException {
        if (list == null) {
            final ResultColumnList resultColumns = this.rightResultSet.getResultColumns();
            this.leftResultSet.bindUntypedNullsToResultColumns(this.leftResultSet.getResultColumns());
            this.rightResultSet.bindUntypedNullsToResultColumns(resultColumns);
        }
        else {
            this.leftResultSet.bindUntypedNullsToResultColumns(list);
            this.rightResultSet.bindUntypedNullsToResultColumns(list);
        }
    }
    
    void replaceOrForbidDefaults(final TableDescriptor tableDescriptor, final ResultColumnList list, final boolean b) throws StandardException {
        this.leftResultSet.replaceOrForbidDefaults(tableDescriptor, list, b);
        this.rightResultSet.replaceOrForbidDefaults(tableDescriptor, list, b);
    }
    
    int getParamColumnTypes(final DataTypeDescriptor[] array, final RowResultSetNode rowResultSetNode) throws StandardException {
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == null) {
                final ResultColumn resultColumn = (ResultColumn)rowResultSetNode.getResultColumns().elementAt(i);
                if (!resultColumn.getExpression().requiresTypeFromContext()) {
                    array[i] = resultColumn.getExpression().getTypeServices();
                    ++n;
                }
            }
        }
        return n;
    }
    
    void setParamColumnTypes(final DataTypeDescriptor[] array, final RowResultSetNode rowResultSetNode) throws StandardException {
        final ResultColumnList resultColumns = rowResultSetNode.getResultColumns();
        for (int size = resultColumns.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)resultColumns.elementAt(i);
            if (resultColumn.getExpression().requiresTypeFromContext()) {
                resultColumn.getExpression().setType(array[i]);
            }
        }
    }
    
    public void bindExpressions(final FromList list) throws StandardException {
        if (this.orderByLists[0] != null) {
            this.orderByLists[0].bindOrderByColumns(this);
            this.orderByLists[0].pullUpOrderByColumns(this);
        }
        QueryTreeNode.bindOffsetFetch(this.offset, this.fetchFirst);
        super.bindExpressions(list);
    }
    
    public void bindTargetExpressions(final FromList list) throws StandardException {
        this.leftResultSet.bindTargetExpressions(list);
        this.rightResultSet.bindTargetExpressions(list);
    }
    
    void pushOrderByList(final OrderByList list) {
        if (this.orderByLists[0] != null) {
            this.orderByLists = new OrderByList[] { this.orderByLists[0], list };
        }
        else {
            this.orderByLists[0] = list;
        }
    }
    
    void pushOffsetFetchFirst(final ValueNode offset, final ValueNode fetchFirst, final boolean hasJDBClimitClause) {
        this.offset = offset;
        this.fetchFirst = fetchFirst;
        this.hasJDBClimitClause = hasJDBClimitClause;
    }
    
    public ResultSetNode preprocess(final int n, final GroupByList list, final FromList list2) throws StandardException {
        this.leftResultSet = this.leftResultSet.preprocess(n, list, list2);
        this.rightResultSet = this.rightResultSet.preprocess(n, list, list2);
        (this.referencedTableMap = (JBitSet)this.leftResultSet.getReferencedTableMap().clone()).or(this.rightResultSet.getReferencedTableMap());
        for (int i = 0; i < this.orderByLists.length; ++i) {
            if (!this.all && this.orderByLists[i] != null && this.orderByLists[i].allAscending() && this.orderByLists[i].isInOrderPrefix(this.resultColumns)) {
                this.orderByLists[i] = null;
            }
            if (this.orderByLists[i] != null && this.orderByLists[i].size() > 1) {
                this.orderByLists[i].removeDupColumns();
            }
        }
        return this;
    }
    
    public ResultSetNode ensurePredicateList(final int n) throws StandardException {
        return this.genProjectRestrict(n);
    }
    
    public void verifySelectStarSubquery(final FromList list, final int n) throws StandardException {
        this.leftResultSet.verifySelectStarSubquery(list, n);
        this.rightResultSet.verifySelectStarSubquery(list, n);
    }
    
    protected FromTable getFromTableByName(final String s, final String s2, final boolean b) throws StandardException {
        return this.leftResultSet.getFromTableByName(s, s2, b);
    }
    
    public ResultSetNode setResultToBooleanTrueNode(final boolean resultToBooleanTrueNode) throws StandardException {
        final FromList list = (FromList)this.getNodeFactory().getNode(37, this.getContextManager());
        list.addFromTable(this);
        list.markAsTransparent();
        final ResultColumnList list2 = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
        list2.addResultColumn((ResultColumn)this.getNodeFactory().getNode(16, null, this.getContextManager()));
        return ((ResultSetNode)this.getNodeFactory().getNode(129, list2, null, list, null, null, null, null, this.getContextManager())).setResultToBooleanTrueNode(resultToBooleanTrueNode);
    }
    
    public boolean flattenableInFromSubquery(final FromList list) {
        return false;
    }
    
    public boolean performMaterialization(final JBitSet set) throws StandardException {
        return false;
    }
    
    abstract String getOperatorName();
    
    PredicateList getLeftOptPredicateList() throws StandardException {
        if (this.leftOptPredicates == null) {
            this.leftOptPredicates = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
        }
        return this.leftOptPredicates;
    }
    
    PredicateList getRightOptPredicateList() throws StandardException {
        if (this.rightOptPredicates == null) {
            this.rightOptPredicates = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
        }
        return this.rightOptPredicates;
    }
}
