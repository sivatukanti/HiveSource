// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import org.apache.derby.iapi.sql.compile.AccessPath;
import org.apache.derby.iapi.services.io.FormatableArrayHolder;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import org.apache.derby.iapi.sql.compile.OptimizableList;
import org.apache.derby.iapi.sql.compile.RequiredRowOrdering;
import org.apache.derby.iapi.sql.compile.OptimizablePredicate;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.sql.compile.RowOrdering;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.sql.compile.Optimizer;
import org.apache.derby.iapi.sql.LanguageFactory;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import java.util.Iterator;
import org.apache.derby.iapi.sql.compile.Visitor;
import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import org.apache.derby.impl.sql.execute.AggregatorInfo;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.impl.sql.execute.AggregatorInfoList;
import java.util.List;

public class GroupByNode extends SingleChildResultSetNode
{
    GroupByList groupingList;
    private List aggregateVector;
    private AggregatorInfoList aggInfo;
    FromTable parent;
    private boolean addDistinctAggregate;
    private boolean singleInputRowOptimization;
    private int addDistinctAggregateColumnNum;
    private boolean isInSortedOrder;
    private ValueNode havingClause;
    private SubqueryList havingSubquerys;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7) throws StandardException {
        super.init(o, o6);
        this.setLevel((int)o7);
        this.havingClause = (ValueNode)o4;
        this.havingSubquerys = (SubqueryList)o5;
        this.groupingList = (GroupByList)o2;
        this.aggregateVector = (List)o3;
        this.parent = this;
        final ResultColumnList copyListAndObjects = this.childResult.getResultColumns().copyListAndObjects();
        this.resultColumns = this.childResult.getResultColumns();
        this.childResult.setResultColumns(copyListAndObjects);
        this.addAggregates();
        if (this.groupingList != null && this.groupingList.isRollup()) {
            this.resultColumns.setNullability(true);
            this.parent.getResultColumns().setNullability(true);
        }
        if (!this.addDistinctAggregate && o2 != null) {
            final ColumnReference[] array = new ColumnReference[this.groupingList.size()];
            int size;
            int i;
            for (size = this.groupingList.size(), i = 0; i < size; ++i) {
                final GroupByColumn groupByColumn = (GroupByColumn)this.groupingList.elementAt(i);
                if (!(groupByColumn.getColumnExpression() instanceof ColumnReference)) {
                    this.isInSortedOrder = false;
                    break;
                }
                array[i] = (ColumnReference)groupByColumn.getColumnExpression();
            }
            if (i == size) {
                this.isInSortedOrder = this.childResult.isOrderedOn(array, true, null);
            }
        }
    }
    
    boolean getIsInSortedOrder() {
        return this.isInSortedOrder;
    }
    
    private void addAggregates() throws StandardException {
        this.addNewPRNode();
        this.addNewColumnsForAggregation();
        this.addDistinctAggregatesToOrderBy();
    }
    
    private void addDistinctAggregatesToOrderBy() {
        if (ResultSetNode.numDistinctAggregates(this.aggregateVector) != 0) {
            AggregatorInfo aggregatorInfo = null;
            for (int size = this.aggInfo.size(), i = 0; i < size; ++i) {
                aggregatorInfo = (AggregatorInfo)this.aggInfo.elementAt(i);
                if (aggregatorInfo.isDistinct()) {
                    break;
                }
            }
            this.addDistinctAggregate = true;
            this.addDistinctAggregateColumnNum = aggregatorInfo.getInputColNum();
        }
    }
    
    private void addNewPRNode() throws StandardException {
        final ResultColumnList list = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
        for (int size = this.resultColumns.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.resultColumns.elementAt(i);
            if (!resultColumn.isGenerated()) {
                list.addElement(resultColumn);
            }
        }
        list.copyOrderBySelect(this.resultColumns);
        this.parent = (FromTable)this.getNodeFactory().getNode(151, this, list, null, null, null, this.havingSubquerys, this.tableProperties, this.getContextManager());
        this.childResult.setResultColumns((ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager()));
        this.resultColumns = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
    }
    
    private ArrayList addUnAggColumns() throws StandardException {
        final ResultColumnList resultColumns = this.childResult.getResultColumns();
        final ResultColumnList resultColumns2 = this.resultColumns;
        final ArrayList<Object> list = new ArrayList<Object>();
        List<Object> list2 = null;
        if (this.havingClause != null) {
            list2 = new ArrayList<Object>();
        }
        for (int size = this.groupingList.size(), i = 0; i < size; ++i) {
            final GroupByColumn groupByColumn = (GroupByColumn)this.groupingList.elementAt(i);
            final ResultColumn resultColumn = (ResultColumn)this.getNodeFactory().getNode(80, "##UnaggColumn", groupByColumn.getColumnExpression(), this.getContextManager());
            resultColumns.addElement(resultColumn);
            resultColumn.markGenerated();
            resultColumn.bindResultColumnToExpression();
            resultColumn.setVirtualColumnId(resultColumns.size());
            final ResultColumn resultColumn2 = (ResultColumn)this.getNodeFactory().getNode(80, "##UnaggColumn", groupByColumn.getColumnExpression(), this.getContextManager());
            resultColumns2.addElement(resultColumn2);
            resultColumn2.markGenerated();
            resultColumn2.bindResultColumnToExpression();
            resultColumn2.setVirtualColumnId(resultColumns2.size());
            final VirtualColumnNode virtualColumnNode = (VirtualColumnNode)this.getNodeFactory().getNode(107, this, resultColumn2, new Integer(resultColumns2.size()), this.getContextManager());
            final ValueNode columnExpression = groupByColumn.getColumnExpression();
            list.add(new SubstituteExpressionVisitor(columnExpression, virtualColumnNode, AggregateNode.class));
            if (this.havingClause != null) {
                ((ArrayList<SubstituteExpressionVisitor>)list2).add(new SubstituteExpressionVisitor(columnExpression, virtualColumnNode, null));
            }
            groupByColumn.setColumnPosition(resultColumns.size());
        }
        final ExpressionSorter expressionSorter = new ExpressionSorter();
        Collections.sort(list, expressionSorter);
        for (int j = 0; j < list.size(); ++j) {
            this.parent.getResultColumns().accept(list.get(j));
        }
        if (list2 != null) {
            Collections.sort(list2, expressionSorter);
        }
        return (ArrayList)list2;
    }
    
    private void addNewColumnsForAggregation() throws StandardException {
        this.aggInfo = new AggregatorInfoList();
        ArrayList<SubstituteExpressionVisitor> addUnAggColumns = null;
        if (this.groupingList != null) {
            addUnAggColumns = (ArrayList<SubstituteExpressionVisitor>)this.addUnAggColumns();
        }
        this.addAggregateColumns();
        if (this.havingClause != null) {
            if (addUnAggColumns != null) {
                for (int i = 0; i < addUnAggColumns.size(); ++i) {
                    this.havingClause.accept(addUnAggColumns.get(i));
                }
            }
            final CollectNodesVisitor collectNodesVisitor = new CollectNodesVisitor(ColumnReference.class, AggregateNode.class);
            this.havingClause.accept(collectNodesVisitor);
            for (final ColumnReference columnReference : collectNodesVisitor.getList()) {
                if (!columnReference.getGeneratedToReplaceAggregate() && !columnReference.getGeneratedToReplaceWindowFunctionCall() && columnReference.getSourceLevel() == this.level) {
                    throw StandardException.newException("42X24", columnReference.getSQLColumnName());
                }
            }
        }
    }
    
    private void addAggregateColumns() throws StandardException {
        final DataDictionary dataDictionary = this.getDataDictionary();
        final ResultColumnList resultColumns = this.childResult.getResultColumns();
        final ResultColumnList resultColumns2 = this.resultColumns;
        final LanguageFactory languageFactory = this.getLanguageConnectionContext().getLanguageFactory();
        this.parent.getResultColumns().accept(new ReplaceAggregatesWithCRVisitor((ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager()), ((FromTable)this.childResult).getTableNumber(), ResultSetNode.class));
        if (this.havingClause != null) {
            this.havingClause.accept(new ReplaceAggregatesWithCRVisitor((ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager()), ((FromTable)this.childResult).getTableNumber()));
            ((ProjectRestrictNode)this.parent).setRestriction(this.havingClause);
        }
        for (int size = this.aggregateVector.size(), i = 0; i < size; ++i) {
            final AggregateNode aggregateNode = this.aggregateVector.get(i);
            final ResultColumn source = (ResultColumn)this.getNodeFactory().getNode(80, "##aggregate result", aggregateNode.getNewNullResultExpression(), this.getContextManager());
            source.markGenerated();
            source.bindResultColumnToExpression();
            resultColumns.addElement(source);
            source.setVirtualColumnId(resultColumns.size());
            final int virtualColumnId = source.getVirtualColumnId();
            final ColumnReference columnReference = (ColumnReference)this.getNodeFactory().getNode(62, source.getName(), null, this.getContextManager());
            columnReference.setSource(source);
            columnReference.setNestingLevel(this.getLevel());
            columnReference.setSourceLevel(this.getLevel());
            final ResultColumn source2 = (ResultColumn)this.getNodeFactory().getNode(80, source.getColumnName(), columnReference, this.getContextManager());
            source2.markGenerated();
            source2.bindResultColumnToExpression();
            resultColumns2.addElement(source2);
            source2.setVirtualColumnId(resultColumns2.size());
            aggregateNode.getGeneratedRef().setSource(source2);
            final ResultColumn newExpressionResultColumn = aggregateNode.getNewExpressionResultColumn(dataDictionary);
            newExpressionResultColumn.markGenerated();
            newExpressionResultColumn.bindResultColumnToExpression();
            resultColumns.addElement(newExpressionResultColumn);
            newExpressionResultColumn.setVirtualColumnId(resultColumns.size());
            final int virtualColumnId2 = newExpressionResultColumn.getVirtualColumnId();
            final ResultColumn resultColumn = (ResultColumn)this.getNodeFactory().getNode(80, "##aggregate expression", aggregateNode.getNewNullResultExpression(), this.getContextManager());
            final ResultColumn columnReference2 = this.getColumnReference(newExpressionResultColumn, dataDictionary);
            resultColumns2.addElement(columnReference2);
            columnReference2.setVirtualColumnId(resultColumns2.size());
            final ResultColumn newAggregatorResultColumn = aggregateNode.getNewAggregatorResultColumn(dataDictionary);
            newAggregatorResultColumn.markGenerated();
            newAggregatorResultColumn.bindResultColumnToExpression();
            resultColumns.addElement(newAggregatorResultColumn);
            newAggregatorResultColumn.setVirtualColumnId(resultColumns.size());
            final int virtualColumnId3 = newAggregatorResultColumn.getVirtualColumnId();
            final ResultColumn columnReference3 = this.getColumnReference(newAggregatorResultColumn, dataDictionary);
            resultColumns2.addElement(columnReference3);
            columnReference3.setVirtualColumnId(resultColumns2.size());
            final ResultColumnList list = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
            list.addElement(resultColumn);
            this.aggInfo.addElement(new AggregatorInfo(aggregateNode.getAggregateName(), aggregateNode.getAggregatorClassName(), virtualColumnId2 - 1, virtualColumnId - 1, virtualColumnId3 - 1, aggregateNode.isDistinct(), languageFactory.getResultDescription(list.makeResultDescriptors(), "SELECT")));
        }
    }
    
    public FromTable getParent() {
        return this.parent;
    }
    
    public CostEstimate optimizeIt(final Optimizer optimizer, final OptimizablePredicateList list, final CostEstimate costEstimate, final RowOrdering rowOrdering) throws StandardException {
        ((Optimizable)this.childResult).optimizeIt(optimizer, list, costEstimate, rowOrdering);
        return super.optimizeIt(optimizer, list, costEstimate, rowOrdering);
    }
    
    public CostEstimate estimateCost(final OptimizablePredicateList list, final ConglomerateDescriptor conglomerateDescriptor, final CostEstimate costEstimate, final Optimizer optimizer, final RowOrdering rowOrdering) throws StandardException {
        final CostEstimate estimateCost = ((Optimizable)this.childResult).estimateCost(list, conglomerateDescriptor, costEstimate, optimizer, rowOrdering);
        final CostEstimate costEstimate2 = this.getCostEstimate(optimizer);
        costEstimate2.setCost(estimateCost.getEstimatedCost(), estimateCost.rowCount(), estimateCost.singleScanRowCount());
        return costEstimate2;
    }
    
    public boolean pushOptPredicate(final OptimizablePredicate optimizablePredicate) throws StandardException {
        return ((Optimizable)this.childResult).pushOptPredicate(optimizablePredicate);
    }
    
    public String toString() {
        return "";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public boolean flattenableInFromSubquery(final FromList list) {
        return false;
    }
    
    public ResultSetNode optimize(final DataDictionary dataDictionary, final PredicateList list, final double n) throws StandardException {
        this.childResult = this.childResult.optimize(dataDictionary, list, n);
        (this.costEstimate = this.getOptimizer((OptimizableList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager()), list, dataDictionary, null).newCostEstimate()).setCost(this.childResult.getCostEstimate().getEstimatedCost(), this.childResult.getCostEstimate().rowCount(), this.childResult.getCostEstimate().singleScanRowCount());
        return this;
    }
    
    ResultColumnDescriptor[] makeResultDescriptors() {
        return this.childResult.makeResultDescriptors();
    }
    
    public boolean isOneRowResultSet() throws StandardException {
        return this.groupingList == null || this.groupingList.size() == 0;
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.assignResultSetNumber();
        this.costEstimate = this.childResult.getFinalCostEstimate();
        FormatableArrayHolder formatableArrayHolder = activationClassBuilder.getColumnOrdering(this.groupingList);
        if (this.addDistinctAggregate) {
            formatableArrayHolder = activationClassBuilder.addColumnToOrdering(formatableArrayHolder, this.addDistinctAggregateColumnNum);
        }
        final int addItem = activationClassBuilder.addItem(formatableArrayHolder);
        final int addItem2 = activationClassBuilder.addItem(this.aggInfo);
        activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        this.childResult.generate(activationClassBuilder, methodBuilder);
        methodBuilder.push(this.isInSortedOrder);
        methodBuilder.push(addItem2);
        methodBuilder.push(addItem);
        methodBuilder.push(activationClassBuilder.addItem(this.resultColumns.buildRowTemplate()));
        methodBuilder.push(this.resultColumns.getTotalColumnSize());
        methodBuilder.push(this.resultSetNumber);
        if (this.groupingList == null || this.groupingList.size() == 0) {
            this.genScalarAggregateResultSet(activationClassBuilder, methodBuilder);
        }
        else {
            this.genGroupedAggregateResultSet(activationClassBuilder, methodBuilder);
        }
    }
    
    private void genScalarAggregateResultSet(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) {
        final String s = this.addDistinctAggregate ? "getDistinctScalarAggregateResultSet" : "getScalarAggregateResultSet";
        methodBuilder.push(this.singleInputRowOptimization);
        methodBuilder.push(this.costEstimate.rowCount());
        methodBuilder.push(this.costEstimate.getEstimatedCost());
        methodBuilder.callMethod((short)185, null, s, "org.apache.derby.iapi.sql.execute.NoPutResultSet", 10);
    }
    
    private void genGroupedAggregateResultSet(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final String s = this.addDistinctAggregate ? "getDistinctGroupedAggregateResultSet" : "getGroupedAggregateResultSet";
        methodBuilder.push(this.costEstimate.rowCount());
        methodBuilder.push(this.costEstimate.getEstimatedCost());
        methodBuilder.push(this.groupingList.isRollup());
        methodBuilder.callMethod((short)185, null, s, "org.apache.derby.iapi.sql.execute.NoPutResultSet", 10);
    }
    
    private ResultColumn getColumnReference(final ResultColumn source, final DataDictionary dataDictionary) throws StandardException {
        final ColumnReference columnReference = (ColumnReference)this.getNodeFactory().getNode(62, source.getName(), null, this.getContextManager());
        columnReference.setSource(source);
        columnReference.setNestingLevel(this.getLevel());
        columnReference.setSourceLevel(this.getLevel());
        final ResultColumn resultColumn = (ResultColumn)this.getNodeFactory().getNode(80, source.getColumnName(), columnReference, this.getContextManager());
        resultColumn.markGenerated();
        resultColumn.bindResultColumnToExpression();
        return resultColumn;
    }
    
    void considerPostOptimizeOptimizations(final boolean b) throws StandardException {
        if (this.groupingList == null && this.aggregateVector.size() == 1) {
            final AggregateNode aggregateNode = this.aggregateVector.get(0);
            final AggregateDefinition aggregateDefinition = aggregateNode.getAggregateDefinition();
            if (aggregateDefinition instanceof MaxMinAggregateDefinition) {
                if (aggregateNode.getOperand() instanceof ColumnReference) {
                    final ColumnReference[] array = { (ColumnReference)aggregateNode.getOperand() };
                    final ArrayList<FromBaseTable> list = new ArrayList<FromBaseTable>(1);
                    if (this.isOrderedOn(array, false, list)) {
                        boolean b2 = true;
                        final int columnNumber = array[0].getColumnNumber();
                        final AccessPath trulyTheBestAccessPath = this.getTrulyTheBestAccessPath();
                        if (trulyTheBestAccessPath == null || trulyTheBestAccessPath.getConglomerateDescriptor() == null || trulyTheBestAccessPath.getConglomerateDescriptor().getIndexDescriptor() == null) {
                            return;
                        }
                        final IndexRowGenerator indexDescriptor = trulyTheBestAccessPath.getConglomerateDescriptor().getIndexDescriptor();
                        final int[] baseColumnPositions = indexDescriptor.baseColumnPositions();
                        final boolean[] ascending = indexDescriptor.isAscending();
                        int i = 0;
                        while (i < baseColumnPositions.length) {
                            if (columnNumber == baseColumnPositions[i]) {
                                if (!ascending[i]) {
                                    b2 = false;
                                    break;
                                }
                                break;
                            }
                            else {
                                ++i;
                            }
                        }
                        final FromBaseTable fromBaseTable = list.get(0);
                        final MaxMinAggregateDefinition maxMinAggregateDefinition = (MaxMinAggregateDefinition)aggregateDefinition;
                        if ((!maxMinAggregateDefinition.isMax() && b2) || (maxMinAggregateDefinition.isMax() && !b2)) {
                            fromBaseTable.disableBulkFetch();
                            this.singleInputRowOptimization = true;
                        }
                        else if (!b && ((maxMinAggregateDefinition.isMax() && b2) || (!maxMinAggregateDefinition.isMax() && !b2))) {
                            fromBaseTable.disableBulkFetch();
                            fromBaseTable.doSpecialMaxScan();
                            this.singleInputRowOptimization = true;
                        }
                    }
                }
                else if (aggregateNode.getOperand() instanceof ConstantNode) {
                    this.singleInputRowOptimization = true;
                }
            }
        }
    }
    
    private static final class ExpressionSorter implements Comparator
    {
        public int compare(final Object o, final Object o2) {
            try {
                final ValueNode source = ((SubstituteExpressionVisitor)o).getSource();
                final ValueNode source2 = ((SubstituteExpressionVisitor)o2).getSource();
                final CollectNodesVisitor collectNodesVisitor = new CollectNodesVisitor(ColumnReference.class);
                source.accept(collectNodesVisitor);
                final int size = collectNodesVisitor.getList().size();
                final CollectNodesVisitor collectNodesVisitor2 = new CollectNodesVisitor(ColumnReference.class);
                source2.accept(collectNodesVisitor2);
                return collectNodesVisitor2.getList().size() - size;
            }
            catch (StandardException cause) {
                throw new RuntimeException(cause);
            }
        }
    }
}
