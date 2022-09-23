// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.sql.compile.Optimizer;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.sql.compile.OptimizableList;
import org.apache.derby.iapi.sql.compile.OptimizablePredicate;
import org.apache.derby.iapi.sql.compile.RequiredRowOrdering;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import java.util.ArrayList;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.Visitor;
import java.util.List;

public class SelectNode extends ResultSetNode
{
    FromList fromList;
    FromTable targetTable;
    private List selectAggregates;
    private List whereAggregates;
    private List havingAggregates;
    ValueNode whereClause;
    ValueNode originalWhereClause;
    GroupByList groupByList;
    WindowList windows;
    List windowFuncCalls;
    private boolean wasGroupBy;
    OrderByList[] orderByLists;
    boolean orderByQuery;
    ValueNode offset;
    ValueNode fetchFirst;
    boolean hasJDBClimitClause;
    PredicateList wherePredicates;
    SubqueryList selectSubquerys;
    SubqueryList whereSubquerys;
    SubqueryList havingSubquerys;
    private boolean bindTargetListOnly;
    private boolean isDistinct;
    private boolean orderByAndDistinctMerged;
    boolean originalWhereClauseHadSubqueries;
    private FromList preJoinFL;
    ValueNode havingClause;
    private int nestingLevel;
    
    public SelectNode() {
        this.orderByLists = new OrderByList[1];
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7) throws StandardException {
        this.resultColumns = (ResultColumnList)o;
        if (this.resultColumns != null) {
            this.resultColumns.markInitialSize();
        }
        this.fromList = (FromList)o3;
        this.whereClause = (ValueNode)o4;
        this.originalWhereClause = (ValueNode)o4;
        this.groupByList = (GroupByList)o5;
        this.havingClause = (ValueNode)o6;
        this.windows = (WindowList)o7;
        this.bindTargetListOnly = false;
        this.originalWhereClauseHadSubqueries = false;
        if (this.whereClause != null) {
            final CollectNodesVisitor collectNodesVisitor = new CollectNodesVisitor(SubqueryNode.class, SubqueryNode.class);
            this.whereClause.accept(collectNodesVisitor);
            if (!collectNodesVisitor.getList().isEmpty()) {
                this.originalWhereClauseHadSubqueries = true;
            }
        }
        if (this.resultColumns != null) {
            final CollectNodesVisitor collectNodesVisitor2 = new CollectNodesVisitor(WindowFunctionNode.class, SelectNode.class);
            this.resultColumns.accept(collectNodesVisitor2);
            this.windowFuncCalls = collectNodesVisitor2.getList();
            for (int i = 0; i < this.windowFuncCalls.size(); ++i) {
                final WindowFunctionNode windowFunctionNode = this.windowFuncCalls.get(i);
                if (windowFunctionNode.getWindow() instanceof WindowDefinitionNode) {
                    this.windows = this.addInlinedWindowDefinition(this.windows, windowFunctionNode);
                }
            }
        }
    }
    
    private WindowList addInlinedWindowDefinition(WindowList list, final WindowFunctionNode windowFunctionNode) {
        final WindowDefinitionNode windowDefinitionNode = (WindowDefinitionNode)windowFunctionNode.getWindow();
        if (list == null) {
            list = new WindowList();
            list.setContextManager(this.getContextManager());
        }
        final WindowDefinitionNode equivalentWindow = windowDefinitionNode.findEquivalentWindow(list);
        if (equivalentWindow != null) {
            windowFunctionNode.setWindow(equivalentWindow);
        }
        else {
            list.addWindow((WindowDefinitionNode)windowFunctionNode.getWindow());
        }
        return list;
    }
    
    public String toString() {
        return "";
    }
    
    public String statementToString() {
        return "SELECT";
    }
    
    public void makeDistinct() {
        this.isDistinct = true;
    }
    
    public void clearDistinct() {
        this.isDistinct = false;
    }
    
    boolean hasDistinct() {
        return this.isDistinct;
    }
    
    public void printSubNodes(final int n) {
    }
    
    public FromList getFromList() {
        return this.fromList;
    }
    
    public ColumnReference findColumnReferenceInResult(final String anObject) throws StandardException {
        if (this.fromList.size() != 1) {
            return null;
        }
        final FromTable fromTable = (FromTable)this.fromList.elementAt(0);
        if ((!(fromTable instanceof ProjectRestrictNode) || !(((ProjectRestrictNode)fromTable).getChildResult() instanceof FromBaseTable)) && !(fromTable instanceof FromBaseTable)) {
            return null;
        }
        for (int size = this.resultColumns.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.resultColumns.elementAt(i);
            if (!(resultColumn.getExpression() instanceof ColumnReference)) {
                return null;
            }
            final ColumnReference columnReference = (ColumnReference)resultColumn.getExpression();
            if (columnReference.columnName.equals(anObject)) {
                return (ColumnReference)columnReference.getClone();
            }
        }
        return null;
    }
    
    public ValueNode getWhereClause() {
        return this.whereClause;
    }
    
    public PredicateList getWherePredicates() {
        return this.wherePredicates;
    }
    
    public SubqueryList getSelectSubquerys() {
        return this.selectSubquerys;
    }
    
    public SubqueryList getWhereSubquerys() {
        return this.whereSubquerys;
    }
    
    public ResultSetNode bindNonVTITables(final DataDictionary dataDictionary, final FromList list) throws StandardException {
        final int size = this.fromList.size();
        this.wherePredicates = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
        this.preJoinFL = (FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager());
        if (list.size() == 0) {
            this.nestingLevel = 0;
        }
        else {
            this.nestingLevel = ((FromTable)list.elementAt(0)).getLevel() + 1;
        }
        this.fromList.setLevel(this.nestingLevel);
        for (int i = 0; i < size; ++i) {
            list.insertElementAt(this.fromList.elementAt(i), 0);
        }
        this.fromList.bindTables(dataDictionary, list);
        for (int j = 0; j < size; ++j) {
            list.removeElementAt(0);
        }
        return this;
    }
    
    public void bindExpressions(final FromList list) throws StandardException {
        list.size();
        final int size = this.fromList.size();
        if (this.orderByLists[0] != null) {
            this.orderByLists[0].pullUpOrderByColumns(this);
        }
        if (!this.bindTargetListOnly) {
            this.fromList.bindExpressions(list);
        }
        this.selectSubquerys = (SubqueryList)this.getNodeFactory().getNode(11, this.getContextManager());
        this.selectAggregates = new ArrayList();
        for (int i = 0; i < size; ++i) {
            list.insertElementAt(this.fromList.elementAt(i), i);
        }
        list.setWindows(this.windows);
        this.resultColumns.bindExpressions(list, this.selectSubquerys, this.selectAggregates);
        if (this.bindTargetListOnly) {
            for (int j = 0; j < size; ++j) {
                list.removeElementAt(0);
            }
            return;
        }
        this.whereAggregates = new ArrayList();
        this.whereSubquerys = (SubqueryList)this.getNodeFactory().getNode(11, this.getContextManager());
        final CompilerContext compilerContext = this.getCompilerContext();
        if (this.whereClause != null) {
            compilerContext.pushCurrentPrivType(0);
            final int orReliability = this.orReliability(16384);
            this.whereClause = this.whereClause.bindExpression(list, this.whereSubquerys, this.whereAggregates);
            compilerContext.setReliability(orReliability);
            if (this.whereAggregates.size() > 0) {
                throw StandardException.newException("42903");
            }
            if (this.whereClause.isParameterNode()) {
                throw StandardException.newException("42X19.S.2");
            }
            this.whereClause = this.whereClause.checkIsBoolean();
            this.getCompilerContext().popCurrentPrivType();
            checkNoWindowFunctions(this.whereClause, "WHERE");
        }
        if (this.havingClause != null) {
            final int orReliability2 = this.orReliability(16384);
            this.havingAggregates = new ArrayList();
            this.havingSubquerys = (SubqueryList)this.getNodeFactory().getNode(11, this.getContextManager());
            this.havingClause.bindExpression(list, this.havingSubquerys, this.havingAggregates);
            checkNoWindowFunctions(this.havingClause = this.havingClause.checkIsBoolean(), "HAVING");
            compilerContext.setReliability(orReliability2);
        }
        for (int k = 0; k < size; ++k) {
            list.removeElementAt(0);
        }
        if (this.groupByList != null) {
            this.groupByList.bindGroupByColumns(this, new ArrayList());
            checkNoWindowFunctions(this.groupByList, "GROUP BY");
        }
        if (this.groupByList != null || this.selectAggregates.size() > 0) {
            this.resultColumns.accept(new VerifyAggregateExpressionsVisitor(this.groupByList));
        }
        final int numDistinctAggregates = ResultSetNode.numDistinctAggregates(this.selectAggregates);
        if (this.groupByList == null && numDistinctAggregates > 1) {
            throw StandardException.newException("42Z02");
        }
        if (this.orderByLists[0] != null) {
            this.orderByLists[0].bindOrderByColumns(this);
        }
        QueryTreeNode.bindOffsetFetch(this.offset, this.fetchFirst);
    }
    
    public void bindExpressionsWithTables(final FromList list) throws StandardException {
        this.bindExpressions(list);
    }
    
    public void bindTargetExpressions(final FromList list) throws StandardException {
        final CollectNodesVisitor collectNodesVisitor = new CollectNodesVisitor(FromSubquery.class, FromSubquery.class);
        this.fromList.accept(collectNodesVisitor);
        if (!collectNodesVisitor.getList().isEmpty()) {
            this.bindTargetListOnly = false;
        }
        else {
            this.bindTargetListOnly = true;
        }
        this.bindExpressions(list);
        this.bindTargetListOnly = false;
    }
    
    public void bindResultColumns(final FromList list) throws StandardException {
        this.fromList.bindResultColumns(list);
        super.bindResultColumns(list);
        if (this.resultColumns.size() > 1012) {
            throw StandardException.newException("54004");
        }
        if (this.resultColumns.size() == 0) {
            throw StandardException.newException("42X81");
        }
    }
    
    public void bindResultColumns(final TableDescriptor tableDescriptor, final FromVTI fromVTI, final ResultColumnList list, final DMLStatementNode dmlStatementNode, final FromList list2) throws StandardException {
        this.fromList.bindResultColumns(list2);
        super.bindResultColumns(tableDescriptor, fromVTI, list, dmlStatementNode, list2);
    }
    
    void pushExpressionsIntoSelect(final Predicate predicate) throws StandardException {
        this.wherePredicates.pullExpressions(this.referencedTableMap.size(), predicate.getAndNode());
        this.fromList.pushPredicates(this.wherePredicates);
    }
    
    public void verifySelectStarSubquery(final FromList list, final int n) throws StandardException {
        for (int i = 0; i < this.resultColumns.size(); ++i) {
            if (((ResultColumn)this.resultColumns.elementAt(i)) instanceof AllResultColumn) {
                if (n != 15) {
                    throw StandardException.newException("42X38");
                }
                final String fullTableName = ((AllResultColumn)this.resultColumns.elementAt(i)).getFullTableName();
                if (fullTableName != null && this.fromList.getFromTableByName(fullTableName, null, true) == null && list.getFromTableByName(fullTableName, null, true) == null && this.fromList.getFromTableByName(fullTableName, null, false) == null && list.getFromTableByName(fullTableName, null, false) == null) {
                    throw StandardException.newException("42X10", fullTableName);
                }
            }
        }
    }
    
    protected FromTable getFromTableByName(final String s, final String s2, final boolean b) throws StandardException {
        return this.fromList.getFromTableByName(s, s2, b);
    }
    
    public void rejectParameters() throws StandardException {
        super.rejectParameters();
        this.fromList.rejectParameters();
    }
    
    void pushOrderByList(final OrderByList list) {
        if (this.orderByLists[0] != null) {
            this.orderByLists = new OrderByList[] { this.orderByLists[0], list };
        }
        else {
            this.orderByLists[0] = list;
        }
        this.orderByQuery = true;
    }
    
    void pushOffsetFetchFirst(final ValueNode offset, final ValueNode fetchFirst, final boolean hasJDBClimitClause) {
        this.offset = offset;
        this.fetchFirst = fetchFirst;
        this.hasJDBClimitClause = hasJDBClimitClause;
    }
    
    public ResultSetNode preprocess(final int n, final GroupByList list, final FromList list2) throws StandardException {
        ResultSetNode genProjectRestrictForReordering = this;
        this.whereClause = this.normExpressions(this.whereClause);
        this.havingClause = this.normExpressions(this.havingClause);
        if (this.fromList.LOJ_reorderable(n)) {
            final FromList list3 = (FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager());
            this.bindExpressions(list3);
            this.fromList.bindResultColumns(list3);
        }
        this.fromList.preprocess(n, this.groupByList, this.whereClause);
        this.resultColumns.preprocess(n, this.fromList, this.whereSubquerys, this.wherePredicates);
        if (this.whereClause != null) {
            if (this.whereSubquerys != null) {
                this.whereSubquerys.markWhereSubqueries();
            }
            this.whereClause.preprocess(n, this.fromList, this.whereSubquerys, this.wherePredicates);
        }
        if (this.groupByList != null) {
            this.groupByList.preprocess(n, this.fromList, this.whereSubquerys, this.wherePredicates);
        }
        if (this.havingClause != null) {
            this.havingSubquerys.markHavingSubqueries();
            this.havingClause = this.havingClause.preprocess(n, this.fromList, this.havingSubquerys, this.wherePredicates);
        }
        if (this.whereClause != null) {
            this.wherePredicates.pullExpressions(n, this.whereClause);
            this.whereClause = null;
        }
        this.fromList.flattenFromTables(this.resultColumns, this.wherePredicates, this.whereSubquerys, this.groupByList, this.havingClause);
        if (this.wherePredicates != null && this.wherePredicates.size() > 0 && this.fromList.size() > 0) {
            if (this.fromList.size() > 1) {
                this.performTransitiveClosure(n);
            }
            for (int i = 0; i < this.orderByLists.length; ++i) {
                if (this.orderByLists[i] != null) {
                    this.orderByLists[i].removeConstantColumns(this.wherePredicates);
                    if (this.orderByLists[i].size() == 0) {
                        this.orderByLists[i] = null;
                        this.resultColumns.removeOrderByColumns();
                    }
                }
            }
        }
        if (this.groupByList != null && this.havingClause == null && this.selectAggregates.isEmpty() && this.whereAggregates.isEmpty()) {
            this.isDistinct = true;
            this.groupByList = null;
            this.wasGroupBy = true;
        }
        if (this.isDistinct && this.groupByList == null) {
            if (this.resultColumns.allTopCRsFromSameTable() != -1 && this.fromList.returnsAtMostSingleRow(this.resultColumns, this.whereClause, this.wherePredicates, this.getDataDictionary())) {
                this.isDistinct = false;
            }
            for (int j = 0; j < this.orderByLists.length; ++j) {
                if (this.isDistinct && this.orderByLists[j] != null && this.orderByLists[j].allAscending()) {
                    if (this.orderByLists[j].isInOrderPrefix(this.resultColumns)) {
                        this.orderByLists[j] = null;
                    }
                    else {
                        genProjectRestrictForReordering = this.genProjectRestrictForReordering();
                        this.orderByLists[j].resetToSourceRCs();
                        this.resultColumns = this.orderByLists[j].reorderRCL(this.resultColumns);
                        genProjectRestrictForReordering.getResultColumns().removeOrderByColumns();
                        this.orderByLists[j] = null;
                    }
                    this.orderByAndDistinctMerged = true;
                }
            }
        }
        this.fromList.pushPredicates(this.wherePredicates);
        this.referencedTableMap = new JBitSet(n);
        for (int size = this.fromList.size(), k = 0; k < size; ++k) {
            this.referencedTableMap.or(((FromTable)this.fromList.elementAt(k)).getReferencedTableMap());
        }
        if (genProjectRestrictForReordering != this) {
            genProjectRestrictForReordering.setReferencedTableMap((JBitSet)this.referencedTableMap.clone());
        }
        if (this.orderByLists[0] != null) {
            final CollectNodesVisitor collectNodesVisitor = new CollectNodesVisitor(WindowFunctionNode.class);
            this.orderByLists[0].accept(collectNodesVisitor);
            final List list4 = collectNodesVisitor.getList();
            for (int l = 0; l < list4.size(); ++l) {
                final WindowFunctionNode windowFunctionNode = list4.get(l);
                this.windowFuncCalls.add(windowFunctionNode);
                if (windowFunctionNode.getWindow() instanceof WindowDefinitionNode) {
                    this.windows = this.addInlinedWindowDefinition(this.windows, windowFunctionNode);
                }
            }
        }
        return genProjectRestrictForReordering;
    }
    
    private void performTransitiveClosure(final int n) throws StandardException {
        this.wherePredicates.joinClauseTransitiveClosure(n, this.fromList, this.getCompilerContext());
        this.wherePredicates.searchClauseTransitiveClosure(n, this.fromList.hashJoinSpecified());
    }
    
    private ValueNode normExpressions(ValueNode valueNode) throws StandardException {
        if (valueNode != null) {
            valueNode = valueNode.eliminateNots(false);
            valueNode = valueNode.putAndsOnTop();
            valueNode = valueNode.changeToCNF(true);
        }
        return valueNode;
    }
    
    public ResultSetNode addNewPredicate(final Predicate predicate) throws StandardException {
        this.wherePredicates.addPredicate(predicate);
        return this;
    }
    
    public boolean flattenableInFromSubquery(final FromList list) {
        return !this.isDistinct && this.fromList.size() <= 1 && (this.selectSubquerys == null || this.selectSubquerys.size() <= 0) && this.groupByList == null && this.havingClause == null && this.resultColumns.isCloneable() && (this.selectAggregates == null || this.selectAggregates.size() <= 0) && (this.orderByLists[0] == null || this.orderByLists[0].size() <= 0) && this.offset == null && this.fetchFirst == null;
    }
    
    public ResultSetNode genProjectRestrict(final int n) throws StandardException {
        final boolean[] array = new boolean[this.orderByLists.length];
        ResultSetNode resultSetNode = (ResultSetNode)this.getNodeFactory().getNode(151, this.fromList.elementAt(0), this.resultColumns, this.whereClause, this.wherePredicates, this.selectSubquerys, this.whereSubquerys, null, this.getContextManager());
        if ((this.selectAggregates != null && this.selectAggregates.size() > 0) || this.groupByList != null) {
            List list = this.selectAggregates;
            if (this.havingAggregates != null && !this.havingAggregates.isEmpty()) {
                this.havingAggregates.addAll(this.selectAggregates);
                list = this.havingAggregates;
            }
            final GroupByNode groupByNode = (GroupByNode)this.getNodeFactory().getNode(137, resultSetNode, this.groupByList, list, this.havingClause, this.havingSubquerys, null, new Integer(this.nestingLevel), this.getContextManager());
            groupByNode.considerPostOptimizeOptimizations(this.originalWhereClause != null);
            groupByNode.assignCostEstimate(this.optimizer.getOptimizedCost());
            this.groupByList = null;
            resultSetNode = groupByNode.getParent();
            for (int i = 0; i < array.length; ++i) {
                array[i] = (array[i] || groupByNode.getIsInSortedOrder());
            }
        }
        if (this.windows != null) {
            if (this.windows.size() > 1) {
                throw StandardException.newException("42ZC1");
            }
            final WindowResultSetNode windowResultSetNode = (WindowResultSetNode)this.getNodeFactory().getNode(230, resultSetNode, this.windows.elementAt(0), this.windowFuncCalls, new Integer(this.nestingLevel), this.getContextManager());
            resultSetNode = windowResultSetNode.getParent();
            windowResultSetNode.assignCostEstimate(this.optimizer.getOptimizedCost());
        }
        if (this.isDistinct) {
            this.resultColumns.verifyAllOrderable();
            boolean b = false;
            if (n == 1 && !this.orderByAndDistinctMerged) {
                boolean b2 = true;
                final HashSet<BaseColumnNode> set = new HashSet<BaseColumnNode>();
                for (int size = this.resultColumns.size(), j = 1; j <= size; ++j) {
                    final BaseColumnNode baseColumnNode = this.resultColumns.getResultColumn(j).getBaseColumnNode();
                    if (baseColumnNode == null) {
                        b2 = false;
                        break;
                    }
                    set.add(baseColumnNode);
                }
                if (b2 && resultSetNode.isPossibleDistinctScan(set)) {
                    resultSetNode.markForDistinctScan();
                    b = true;
                }
            }
            if (!b) {
                final boolean orderedResult = this.isOrderedResult(this.resultColumns, resultSetNode, !this.orderByAndDistinctMerged);
                resultSetNode = (ResultSetNode)this.getNodeFactory().getNode(124, resultSetNode, new Boolean(orderedResult), null, this.getContextManager());
                resultSetNode.costEstimate = this.costEstimate.cloneMe();
                for (int k = 0; k < array.length; ++k) {
                    array[k] = (array[k] || orderedResult);
                }
            }
        }
        for (int l = 0; l < this.orderByLists.length; ++l) {
            if (this.orderByLists[l] != null) {
                if (this.orderByLists[l].getSortNeeded()) {
                    resultSetNode = (ResultSetNode)this.getNodeFactory().getNode(140, resultSetNode, this.orderByLists[l], null, this.getContextManager());
                    resultSetNode.costEstimate = this.costEstimate.cloneMe();
                }
                if (this.getResultColumns().getOrderBySelect() > 0) {
                    final ResultColumnList resultColumns = resultSetNode.getResultColumns();
                    final ResultColumnList copyListAndObjects = resultColumns.copyListAndObjects();
                    resultSetNode.setResultColumns(copyListAndObjects);
                    resultColumns.removeOrderByColumns();
                    resultColumns.genVirtualColumnNodes(resultSetNode, copyListAndObjects);
                    resultSetNode = (ResultSetNode)this.getNodeFactory().getNode(151, resultSetNode, resultColumns, null, null, null, null, null, this.getContextManager());
                }
            }
            if (l == 0 && (this.offset != null || this.fetchFirst != null)) {
                final ResultColumnList resultColumns2 = resultSetNode.getResultColumns();
                final ResultColumnList copyListAndObjects2 = resultColumns2.copyListAndObjects();
                resultSetNode.setResultColumns(copyListAndObjects2);
                resultColumns2.genVirtualColumnNodes(resultSetNode, copyListAndObjects2);
                resultSetNode = (ResultSetNode)this.getNodeFactory().getNode(223, resultSetNode, resultColumns2, this.offset, this.fetchFirst, this.hasJDBClimitClause, this.getContextManager());
            }
        }
        if (this.wasGroupBy && this.resultColumns.numGeneratedColumnsForGroupBy() > 0 && this.windows == null) {
            final ResultColumnList resultColumns3 = resultSetNode.getResultColumns();
            final ResultColumnList copyListAndObjects3 = resultColumns3.copyListAndObjects();
            resultSetNode.setResultColumns(copyListAndObjects3);
            resultColumns3.removeGeneratedGroupingColumns();
            resultColumns3.genVirtualColumnNodes(resultSetNode, copyListAndObjects3);
            resultSetNode = (ResultSetNode)this.getNodeFactory().getNode(151, resultSetNode, resultColumns3, null, null, null, null, null, this.getContextManager());
        }
        for (int n2 = 0; n2 < this.orderByLists.length; ++n2) {
            if ((this.orderByLists[n2] == null || !this.orderByLists[n2].getSortNeeded()) && this.orderByQuery) {
                array[n2] = true;
            }
            if (array[n2]) {
                resultSetNode.adjustForSortElimination(this.orderByLists[n2]);
            }
            resultSetNode.costEstimate = this.costEstimate.cloneMe();
        }
        return resultSetNode;
    }
    
    private boolean isOrderedResult(final ResultColumnList list, final ResultSetNode resultSetNode, final boolean b) throws StandardException {
        final int size = list.size();
        int n = 0;
        for (int i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)list.elementAt(i);
            if (resultColumn.getExpression() instanceof ColumnReference) {
                ++n;
            }
            else if (!(resultColumn.getExpression() instanceof ConstantNode)) {
                return false;
            }
        }
        if (n == 0) {
            return true;
        }
        final ColumnReference[] array = new ColumnReference[n];
        int n2 = 0;
        for (int j = 0; j < size; ++j) {
            final ResultColumn resultColumn2 = (ResultColumn)list.elementAt(j);
            if (resultColumn2.getExpression() instanceof ColumnReference) {
                array[n2++] = (ColumnReference)resultColumn2.getExpression();
            }
        }
        return resultSetNode.isOrderedOn(array, b, null);
    }
    
    public ResultSetNode ensurePredicateList(final int n) throws StandardException {
        return this;
    }
    
    public ResultSetNode optimize(final DataDictionary dataDictionary, final PredicateList list, final double outerRows) throws StandardException {
        for (int i = 0; i < this.orderByLists.length; ++i) {
            if (this.orderByLists[i] != null && this.orderByLists[i].size() > 1) {
                this.orderByLists[i].removeDupColumns();
            }
        }
        if (this.wherePredicates != null) {
            for (int j = this.wherePredicates.size() - 1; j >= 0; --j) {
                if (((Predicate)this.wherePredicates.elementAt(j)).isScopedForPush()) {
                    this.wherePredicates.removeOptPredicate(j);
                }
            }
        }
        if (list != null) {
            if (this.wherePredicates == null) {
                this.wherePredicates = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
            }
            for (int k = list.size() - 1; k >= 0; --k) {
                final Predicate predicate = (Predicate)list.getOptPredicate(k);
                if (predicate.isScopedToSourceResultSet()) {
                    this.wherePredicates.addOptPredicate(predicate);
                    list.removeOptPredicate(predicate);
                }
            }
        }
        final Optimizer optimizer = this.getOptimizer(this.fromList, this.wherePredicates, dataDictionary, this.orderByLists[0]);
        optimizer.setOuterRows(outerRows);
        while (optimizer.getNextPermutation()) {
            while (optimizer.getNextDecoratedPermutation()) {
                optimizer.costPermutation();
            }
        }
        if (this.wherePredicates != null) {
            for (int l = this.wherePredicates.size() - 1; l >= 0; --l) {
                final Predicate predicate2 = (Predicate)this.wherePredicates.getOptPredicate(l);
                if (predicate2.isScopedForPush()) {
                    list.addOptPredicate(predicate2);
                    this.wherePredicates.removeOptPredicate(predicate2);
                }
            }
        }
        this.costEstimate = optimizer.getOptimizedCost();
        if (this.selectAggregates != null && this.selectAggregates.size() > 0) {
            this.costEstimate.setEstimatedRowCount((long)outerRows);
            this.costEstimate.setSingleScanRowCount(1.0);
        }
        this.selectSubquerys.optimize(dataDictionary, this.costEstimate.rowCount());
        if (this.whereSubquerys != null && this.whereSubquerys.size() > 0) {
            this.whereSubquerys.optimize(dataDictionary, this.costEstimate.rowCount());
        }
        if (this.havingSubquerys != null && this.havingSubquerys.size() > 0) {
            this.havingSubquerys.optimize(dataDictionary, this.costEstimate.rowCount());
        }
        return this;
    }
    
    public ResultSetNode modifyAccessPaths(final PredicateList list) throws StandardException {
        ((OptimizerImpl)this.optimizer).addScopedPredicatesToList(list);
        return this.modifyAccessPaths();
    }
    
    public ResultSetNode modifyAccessPaths() throws StandardException {
        final int size = this.fromList.size();
        this.optimizer.modifyAccessPaths();
        this.costEstimate = this.optimizer.getFinalCost();
        this.selectSubquerys.modifyAccessPaths();
        if (this.whereSubquerys != null && this.whereSubquerys.size() > 0) {
            this.whereSubquerys.modifyAccessPaths();
        }
        if (this.havingSubquerys != null && this.havingSubquerys.size() > 0) {
            this.havingSubquerys.modifyAccessPaths();
        }
        this.preJoinFL.removeAllElements();
        this.preJoinFL.nondestructiveAppend(this.fromList);
        while (this.fromList.size() > 1) {
            final ResultSetNode resultSetNode = (ResultSetNode)this.fromList.elementAt(0);
            final ResultColumnList resultColumns = resultSetNode.getResultColumns();
            resultSetNode.setResultColumns(resultColumns.copyListAndObjects());
            resultColumns.genVirtualColumnNodes(resultSetNode, resultSetNode.resultColumns);
            final ResultSetNode resultSetNode2 = (ResultSetNode)this.fromList.elementAt(1);
            final ResultColumnList resultColumns2 = resultSetNode2.getResultColumns();
            resultSetNode2.setResultColumns(resultColumns2.copyListAndObjects());
            resultColumns2.genVirtualColumnNodes(resultSetNode2, resultSetNode2.resultColumns);
            resultColumns2.adjustVirtualColumnIds(resultColumns.size());
            resultColumns.nondestructiveAppend(resultColumns2);
            this.fromList.setElementAt((QueryTreeNode)this.getNodeFactory().getNode(139, resultSetNode, resultSetNode2, null, null, resultColumns, null, this.fromList.properties, this.getContextManager()), 0);
            this.fromList.removeElementAt(1);
        }
        return this.genProjectRestrict(size);
    }
    
    public CostEstimate getFinalCostEstimate() throws StandardException {
        return this.optimizer.getFinalCost();
    }
    
    boolean isUpdatableCursor(final DataDictionary dataDictionary) throws StandardException {
        if (this.isDistinct) {
            return false;
        }
        if (this.selectAggregates == null || this.selectAggregates.size() > 0) {
            return false;
        }
        if (this.groupByList != null || this.havingClause != null) {
            return false;
        }
        if (this.fromList.size() != 1) {
            return false;
        }
        this.targetTable = (FromTable)this.fromList.elementAt(0);
        if (this.targetTable instanceof FromVTI) {
            return ((FromVTI)this.targetTable).isUpdatableCursor();
        }
        if (!(this.targetTable instanceof FromBaseTable)) {
            return false;
        }
        final TableDescriptor tableDescriptor = this.getTableDescriptor(((FromBaseTable)this.targetTable).getBaseTableName(), this.getSchemaDescriptor(((FromBaseTable)this.targetTable).getTableNameField().getSchemaName()));
        return tableDescriptor.getTableType() != 1 && tableDescriptor.getTableType() != 2 && (this.getSelectSubquerys() == null || this.getSelectSubquerys().size() == 0) && (this.getWhereSubquerys() == null || this.getWhereSubquerys().size() == 0);
    }
    
    FromTable getCursorTargetTable() {
        return this.targetTable;
    }
    
    public boolean referencesTarget(final String s, final boolean b) throws StandardException {
        return this.fromList.referencesTarget(s, b) || (this.selectSubquerys != null && this.selectSubquerys.referencesTarget(s, b)) || (this.whereSubquerys != null && this.whereSubquerys.referencesTarget(s, b));
    }
    
    boolean subqueryReferencesTarget(final String s, final boolean b) throws StandardException {
        return (this.selectSubquerys != null && this.selectSubquerys.referencesTarget(s, b)) || (this.whereSubquerys != null && this.whereSubquerys.referencesTarget(s, b));
    }
    
    public void bindUntypedNullsToResultColumns(final ResultColumnList list) throws StandardException {
        this.fromList.bindUntypedNullsToResultColumns(list);
    }
    
    void decrementLevel(final int n) {
        this.fromList.decrementLevel(n);
        this.selectSubquerys.decrementLevel(n);
        this.whereSubquerys.decrementLevel(n);
        this.wherePredicates.decrementLevel(this.fromList, n);
    }
    
    boolean uniqueSubquery(final boolean b) throws StandardException {
        ColumnReference columnReference = null;
        final ResultColumn resultColumn = (ResultColumn)this.getResultColumns().elementAt(0);
        if (b && resultColumn.getExpression() instanceof ColumnReference) {
            columnReference = (ColumnReference)resultColumn.getExpression();
            if (columnReference.getCorrelated()) {
                columnReference = null;
            }
        }
        return this.fromList.returnsAtMostSingleRow((columnReference == null) ? null : this.getResultColumns(), this.whereClause, this.wherePredicates, this.getDataDictionary());
    }
    
    public int updateTargetLockMode() {
        return this.fromList.updateTargetLockMode();
    }
    
    boolean returnsAtMostOneRow() {
        return this.groupByList == null && this.selectAggregates != null && this.selectAggregates.size() != 0;
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        return this.fromList.referencesSessionSchema() || (this.selectSubquerys != null && this.selectSubquerys.referencesSessionSchema()) || (this.whereSubquerys != null && this.whereSubquerys.referencesSessionSchema());
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.fromList != null) {
            this.fromList = (FromList)this.fromList.accept(visitor);
        }
        if (this.whereClause != null) {
            this.whereClause = (ValueNode)this.whereClause.accept(visitor);
        }
        if (this.wherePredicates != null) {
            this.wherePredicates = (PredicateList)this.wherePredicates.accept(visitor);
        }
        if (this.havingClause != null) {
            this.havingClause = (ValueNode)this.havingClause.accept(visitor);
        }
        if (!(visitor instanceof HasCorrelatedCRsVisitor)) {
            if (this.selectSubquerys != null) {
                this.selectSubquerys = (SubqueryList)this.selectSubquerys.accept(visitor);
            }
            if (this.whereSubquerys != null) {
                this.whereSubquerys = (SubqueryList)this.whereSubquerys.accept(visitor);
            }
            if (this.groupByList != null) {
                this.groupByList = (GroupByList)this.groupByList.accept(visitor);
            }
            if (this.orderByLists[0] != null) {
                for (int i = 0; i < this.orderByLists.length; ++i) {
                    this.orderByLists[i] = (OrderByList)this.orderByLists[i].accept(visitor);
                }
            }
            if (this.offset != null) {
                this.offset = (ValueNode)this.offset.accept(visitor);
            }
            if (this.fetchFirst != null) {
                this.fetchFirst = (ValueNode)this.fetchFirst.accept(visitor);
            }
            if (this.preJoinFL != null) {
                this.preJoinFL = (FromList)this.preJoinFL.accept(visitor);
            }
            if (this.windows != null) {
                this.windows = (WindowList)this.windows.accept(visitor);
            }
        }
    }
    
    public boolean hasAggregatesInSelectList() {
        return !this.selectAggregates.isEmpty();
    }
    
    public boolean hasWindows() {
        return this.windows != null;
    }
    
    public static void checkNoWindowFunctions(final QueryTreeNode queryTreeNode, final String s) throws StandardException {
        final HasNodeVisitor hasNodeVisitor = new HasNodeVisitor(WindowFunctionNode.class, SubqueryNode.class);
        queryTreeNode.accept(hasNodeVisitor);
        if (hasNodeVisitor.hasNode()) {
            throw StandardException.newException("42ZC2", s);
        }
    }
    
    void replaceOrForbidDefaults(final TableDescriptor tableDescriptor, final ResultColumnList list, final boolean b) throws StandardException {
    }
}
