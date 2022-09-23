// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.util.PropertyUtil;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import java.util.Iterator;
import java.util.Collection;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.types.TypeId;
import java.util.ArrayList;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.sql.compile.OptimizablePredicate;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.compile.RowOrdering;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.sql.compile.Optimizer;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.util.JBitSet;
import java.util.Properties;
import java.util.List;

public class JoinNode extends TableOperatorNode
{
    public static final int INNERJOIN = 1;
    public static final int CROSSJOIN = 2;
    public static final int LEFTOUTERJOIN = 3;
    public static final int RIGHTOUTERJOIN = 4;
    public static final int FULLOUTERJOIN = 5;
    public static final int UNIONJOIN = 6;
    private boolean naturalJoin;
    private boolean optimized;
    private PredicateList leftPredicateList;
    private PredicateList rightPredicateList;
    protected boolean flattenableJoin;
    List aggregateVector;
    SubqueryList subqueryList;
    ValueNode joinClause;
    boolean joinClauseNormalized;
    PredicateList joinPredicates;
    ResultColumnList usingClause;
    Properties joinOrderStrategyProperties;
    
    public JoinNode() {
        this.flattenableJoin = true;
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7) throws StandardException {
        super.init(o, o2, o6);
        this.resultColumns = (ResultColumnList)o5;
        this.joinClause = (ValueNode)o3;
        this.joinClauseNormalized = false;
        this.usingClause = (ResultColumnList)o4;
        this.joinOrderStrategyProperties = (Properties)o7;
        if (this.resultColumns != null && this.leftResultSet.getReferencedTableMap() != null) {
            (this.referencedTableMap = (JBitSet)this.leftResultSet.getReferencedTableMap().clone()).or(this.rightResultSet.getReferencedTableMap());
        }
        this.joinPredicates = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
    }
    
    public CostEstimate optimizeIt(final Optimizer optimizer, final OptimizablePredicateList list, final CostEstimate costEstimate, final RowOrdering rowOrdering) throws StandardException {
        optimizer.trace(27, 0, 0, 0.0, null);
        this.updateBestPlanMap((short)1, this);
        this.leftResultSet = this.optimizeSource(optimizer, this.leftResultSet, this.getLeftPredicateList(), costEstimate);
        for (int i = this.joinPredicates.size() - 1; i >= 0; --i) {
            final Predicate predicate = (Predicate)this.joinPredicates.elementAt(i);
            if (predicate.getPushable()) {
                this.joinPredicates.removeElementAt(i);
                this.getRightPredicateList().addElement(predicate);
            }
        }
        this.rightResultSet = this.optimizeSource(optimizer, this.rightResultSet, this.getRightPredicateList(), this.leftResultSet.getCostEstimate());
        (this.costEstimate = this.getCostEstimate(optimizer)).setCost(this.leftResultSet.getCostEstimate().getEstimatedCost() + this.rightResultSet.getCostEstimate().getEstimatedCost(), this.rightResultSet.getCostEstimate().rowCount(), this.rightResultSet.getCostEstimate().rowCount());
        this.adjustNumberOfRowsReturned(this.costEstimate);
        this.getCurrentAccessPath().getJoinStrategy().estimateCost(this, list, null, costEstimate, optimizer, this.costEstimate);
        optimizer.considerCost(this, list, this.costEstimate, costEstimate);
        if (!this.optimized && this.subqueryList != null) {
            this.subqueryList.optimize(optimizer.getDataDictionary(), this.costEstimate.rowCount());
            this.subqueryList.modifyAccessPaths();
        }
        this.optimized = true;
        return this.costEstimate;
    }
    
    public boolean pushOptPredicate(final OptimizablePredicate optimizablePredicate) throws StandardException {
        this.joinPredicates.addPredicate((Predicate)optimizablePredicate);
        ((Predicate)optimizablePredicate).getAndNode().accept(new RemapCRsVisitor(true));
        return true;
    }
    
    public Optimizable modifyAccessPath(final JBitSet set) throws StandardException {
        super.modifyAccessPath(set);
        return this;
    }
    
    protected void adjustNumberOfRowsReturned(final CostEstimate costEstimate) {
    }
    
    public ResultColumnList getAllResultColumns(final TableName tableName) throws StandardException {
        if (this.usingClause == null) {
            return this.getAllResultColumnsNoUsing(tableName);
        }
        final ResultColumnList joinColumns = this.getLogicalLeftResultSet().getAllResultColumns(null).getJoinColumns(this.usingClause);
        final ResultColumnList allResultColumns = this.leftResultSet.getAllResultColumns(tableName);
        final ResultColumnList allResultColumns2 = this.rightResultSet.getAllResultColumns(tableName);
        if (allResultColumns != null) {
            allResultColumns.removeJoinColumns(this.usingClause);
        }
        if (allResultColumns2 != null) {
            allResultColumns2.removeJoinColumns(this.usingClause);
        }
        if (allResultColumns == null) {
            if (allResultColumns2 == null) {
                return null;
            }
            allResultColumns2.resetVirtualColumnIds();
            return allResultColumns2;
        }
        else {
            if (allResultColumns2 == null) {
                allResultColumns.resetVirtualColumnIds();
                return allResultColumns;
            }
            joinColumns.destructiveAppend(allResultColumns);
            joinColumns.destructiveAppend(allResultColumns2);
            joinColumns.resetVirtualColumnIds();
            return joinColumns;
        }
    }
    
    private ResultColumnList getAllResultColumnsNoUsing(final TableName tableName) throws StandardException {
        final ResultColumnList allResultColumns = this.leftResultSet.getAllResultColumns(tableName);
        final ResultColumnList allResultColumns2 = this.rightResultSet.getAllResultColumns(tableName);
        if (allResultColumns == null) {
            return allResultColumns2;
        }
        if (allResultColumns2 == null) {
            return allResultColumns;
        }
        final ResultColumnList list = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
        list.nondestructiveAppend(allResultColumns);
        list.nondestructiveAppend(allResultColumns2);
        return list;
    }
    
    public ResultColumn getMatchingColumn(final ColumnReference columnReference) throws StandardException {
        final ResultSetNode logicalLeftResultSet = this.getLogicalLeftResultSet();
        final ResultSetNode logicalRightResultSet = this.getLogicalRightResultSet();
        ResultColumn resultColumn = null;
        ResultColumn matchingColumn = null;
        ResultColumn resultColumn2 = null;
        final ResultColumn matchingColumn2 = logicalLeftResultSet.getMatchingColumn(columnReference);
        if (matchingColumn2 != null) {
            resultColumn = matchingColumn2;
            if (this.usingClause != null) {
                resultColumn2 = this.usingClause.getResultColumn(matchingColumn2.getName());
            }
        }
        if (resultColumn2 == null) {
            matchingColumn = logicalRightResultSet.getMatchingColumn(columnReference);
        }
        else if (this instanceof HalfOuterJoinNode && ((HalfOuterJoinNode)this).isRightOuterJoin()) {
            matchingColumn2.setRightOuterJoinUsingClause(true);
        }
        if (matchingColumn != null) {
            if (matchingColumn2 != null) {
                throw StandardException.newException("42X03", columnReference.getSQLColumnName());
            }
            if (this instanceof HalfOuterJoinNode) {
                matchingColumn.setNullability(true);
            }
            resultColumn = matchingColumn;
        }
        if (this.resultColumns != null) {
            for (int size = this.resultColumns.size(), i = 0; i < size; ++i) {
                final ResultColumn resultColumn3 = (ResultColumn)this.resultColumns.elementAt(i);
                if (resultColumn == ((VirtualColumnNode)resultColumn3.getExpression()).getSourceColumn()) {
                    resultColumn = resultColumn3;
                    break;
                }
            }
        }
        return resultColumn;
    }
    
    public void bindExpressions(final FromList list) throws StandardException {
        super.bindExpressions(list);
        if (this.naturalJoin) {
            this.usingClause = this.getCommonColumnsForNaturalJoin();
        }
    }
    
    public void bindResultColumns(final FromList list) throws StandardException {
        super.bindResultColumns(list);
        this.buildRCL();
        this.deferredBindExpressions(list);
    }
    
    public void bindResultColumns(final TableDescriptor tableDescriptor, final FromVTI fromVTI, final ResultColumnList list, final DMLStatementNode dmlStatementNode, final FromList list2) throws StandardException {
        super.bindResultColumns(tableDescriptor, fromVTI, list, dmlStatementNode, list2);
        this.buildRCL();
        this.deferredBindExpressions(list2);
    }
    
    private void buildRCL() throws StandardException {
        if (this.resultColumns != null) {
            return;
        }
        this.resultColumns = this.leftResultSet.getResultColumns();
        final ResultColumnList copyListAndObjects = this.resultColumns.copyListAndObjects();
        this.leftResultSet.setResultColumns(copyListAndObjects);
        this.resultColumns.genVirtualColumnNodes(this.leftResultSet, copyListAndObjects, false);
        if (this instanceof HalfOuterJoinNode && ((HalfOuterJoinNode)this).isRightOuterJoin()) {
            this.resultColumns.setNullability(true);
        }
        final ResultColumnList resultColumns = this.rightResultSet.getResultColumns();
        final ResultColumnList copyListAndObjects2 = resultColumns.copyListAndObjects();
        this.rightResultSet.setResultColumns(copyListAndObjects2);
        resultColumns.genVirtualColumnNodes(this.rightResultSet, copyListAndObjects2, false);
        resultColumns.adjustVirtualColumnIds(this.resultColumns.size());
        if (this instanceof HalfOuterJoinNode && !((HalfOuterJoinNode)this).isRightOuterJoin()) {
            resultColumns.setNullability(true);
        }
        this.resultColumns.nondestructiveAppend(resultColumns);
    }
    
    private void deferredBindExpressions(final FromList list) throws StandardException {
        this.subqueryList = (SubqueryList)this.getNodeFactory().getNode(11, this.getContextManager());
        this.aggregateVector = new ArrayList();
        final CompilerContext compilerContext = this.getCompilerContext();
        if (this.joinClause != null) {
            final FromList list2 = (FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager());
            list2.addElement(this.leftResultSet);
            list2.addElement(this.rightResultSet);
            final int orReliability = this.orReliability(16384);
            this.joinClause = this.joinClause.bindExpression(list2, this.subqueryList, this.aggregateVector);
            compilerContext.setReliability(orReliability);
            SelectNode.checkNoWindowFunctions(this.joinClause, "ON");
            if (this.aggregateVector.size() > 0) {
                throw StandardException.newException("42Z07");
            }
        }
        else if (this.usingClause != null) {
            this.joinClause = (ValueNode)this.getNodeFactory().getNode(38, Boolean.TRUE, this.getContextManager());
            for (int size = this.usingClause.size(), i = 0; i < size; ++i) {
                final ResultColumn resultColumn = (ResultColumn)this.usingClause.elementAt(i);
                list.insertElementAt(this.leftResultSet, 0);
                final ColumnReference columnReference = (ColumnReference)((ColumnReference)this.getNodeFactory().getNode(62, resultColumn.getName(), ((FromTable)this.leftResultSet).getTableName(), this.getContextManager())).bindExpression(list, this.subqueryList, this.aggregateVector);
                list.removeElementAt(0);
                list.insertElementAt(this.rightResultSet, 0);
                final ColumnReference columnReference2 = (ColumnReference)((ColumnReference)this.getNodeFactory().getNode(62, resultColumn.getName(), ((FromTable)this.rightResultSet).getTableName(), this.getContextManager())).bindExpression(list, this.subqueryList, this.aggregateVector);
                list.removeElementAt(0);
                final BinaryComparisonOperatorNode binaryComparisonOperatorNode = (BinaryComparisonOperatorNode)this.getNodeFactory().getNode(41, columnReference, columnReference2, Boolean.FALSE, this.getContextManager());
                binaryComparisonOperatorNode.bindComparisonOperator();
                final AndNode joinClause = (AndNode)this.getNodeFactory().getNode(39, binaryComparisonOperatorNode, this.joinClause, this.getContextManager());
                joinClause.postBindFixup();
                this.joinClause = joinClause;
            }
        }
        if (this.joinClause != null) {
            if (this.joinClause.requiresTypeFromContext()) {
                this.joinClause.setType(new DataTypeDescriptor(TypeId.BOOLEAN_ID, true));
            }
            if (this.joinClause.getTypeId().userType()) {
                this.joinClause = this.joinClause.genSQLJavaSQLTree();
            }
            if (!this.joinClause.getTypeServices().getTypeId().equals(TypeId.BOOLEAN_ID)) {
                throw StandardException.newException("42Y12", this.joinClause.getTypeServices().getTypeId().getSQLTypeName());
            }
        }
    }
    
    private ResultColumnList getCommonColumnsForNaturalJoin() throws StandardException {
        final ResultColumnList allResultColumns = this.getLeftResultSet().getAllResultColumns(null);
        final ResultColumnList allResultColumns2 = this.getRightResultSet().getAllResultColumns(null);
        final List columnNames = extractColumnNames(allResultColumns);
        columnNames.retainAll(extractColumnNames(allResultColumns2));
        final ResultColumnList list = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
        final Iterator<String> iterator = columnNames.iterator();
        while (iterator.hasNext()) {
            list.addResultColumn((ResultColumn)this.getNodeFactory().getNode(80, iterator.next(), null, this.getContextManager()));
        }
        return list;
    }
    
    private static List extractColumnNames(final ResultColumnList list) {
        final ArrayList<String> list2 = new ArrayList<String>();
        for (int i = 0; i < list.size(); ++i) {
            list2.add(((ResultColumn)list.elementAt(i)).getName());
        }
        return list2;
    }
    
    public ResultSetNode preprocess(final int n, final GroupByList list, final FromList list2) throws StandardException {
        final ResultSetNode preprocess = super.preprocess(n, list, list2);
        if (this.joinClause != null) {
            this.normExpressions();
            if (this.subqueryList != null) {
                this.joinClause.preprocess(n, (FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager()), (SubqueryList)this.getNodeFactory().getNode(11, this.getContextManager()), (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager()));
            }
            this.joinPredicates.pullExpressions(n, this.joinClause);
            this.joinPredicates.categorize();
            this.joinClause = null;
        }
        return preprocess;
    }
    
    void projectResultColumns() throws StandardException {
        this.leftResultSet.projectResultColumns();
        this.rightResultSet.projectResultColumns();
        this.resultColumns.pullVirtualIsReferenced();
        super.projectResultColumns();
    }
    
    public void normExpressions() throws StandardException {
        if (this.joinClauseNormalized) {
            return;
        }
        this.joinClause = this.joinClause.eliminateNots(false);
        this.joinClause = this.joinClause.putAndsOnTop();
        this.joinClause = this.joinClause.changeToCNF(false);
        this.joinClauseNormalized = true;
    }
    
    public void pushExpressions(final PredicateList list) throws StandardException {
        final FromTable fromTable = (FromTable)this.leftResultSet;
        final FromTable fromTable2 = (FromTable)this.rightResultSet;
        this.pushExpressionsToLeft(list);
        fromTable.pushExpressions(this.getLeftPredicateList());
        this.pushExpressionsToRight(list);
        fromTable2.pushExpressions(this.getRightPredicateList());
        this.grabJoinPredicates(list);
    }
    
    protected void pushExpressionsToLeft(final PredicateList list) throws StandardException {
        final JBitSet referencedTableMap = ((FromTable)this.leftResultSet).getReferencedTableMap();
        for (int i = list.size() - 1; i >= 0; --i) {
            final Predicate predicate = (Predicate)list.elementAt(i);
            if (predicate.getPushable()) {
                if (referencedTableMap.contains(predicate.getReferencedSet())) {
                    this.getLeftPredicateList().addPredicate(predicate);
                    final RemapCRsVisitor remapCRsVisitor = new RemapCRsVisitor(true);
                    predicate.getAndNode().accept(remapCRsVisitor);
                    predicate.getAndNode().accept(remapCRsVisitor);
                    list.removeElementAt(i);
                }
            }
        }
    }
    
    private void pushExpressionsToRight(final PredicateList list) throws StandardException {
        final JBitSet referencedTableMap = ((FromTable)this.rightResultSet).getReferencedTableMap();
        for (int i = list.size() - 1; i >= 0; --i) {
            final Predicate predicate = (Predicate)list.elementAt(i);
            if (predicate.getPushable()) {
                if (referencedTableMap.contains(predicate.getReferencedSet())) {
                    this.getRightPredicateList().addPredicate(predicate);
                    final RemapCRsVisitor remapCRsVisitor = new RemapCRsVisitor(true);
                    predicate.getAndNode().accept(remapCRsVisitor);
                    predicate.getAndNode().accept(remapCRsVisitor);
                    list.removeElementAt(i);
                }
            }
        }
    }
    
    private void grabJoinPredicates(final PredicateList list) throws StandardException {
        final FromTable fromTable = (FromTable)this.leftResultSet;
        final FromTable fromTable2 = (FromTable)this.rightResultSet;
        final JBitSet referencedTableMap = fromTable.getReferencedTableMap();
        final JBitSet referencedTableMap2 = fromTable2.getReferencedTableMap();
        for (int i = list.size() - 1; i >= 0; --i) {
            final Predicate predicate = (Predicate)list.elementAt(i);
            if (predicate.getPushable()) {
                final JBitSet referencedSet = predicate.getReferencedSet();
                final JBitSet set = (JBitSet)referencedTableMap2.clone();
                set.or(referencedTableMap);
                if (set.contains(referencedSet)) {
                    this.joinPredicates.addPredicate(predicate);
                    final RemapCRsVisitor remapCRsVisitor = new RemapCRsVisitor(true);
                    predicate.getAndNode().accept(remapCRsVisitor);
                    predicate.getAndNode().accept(remapCRsVisitor);
                    list.removeElementAt(i);
                }
            }
        }
    }
    
    public FromList flatten(final ResultColumnList list, final PredicateList list2, final SubqueryList list3, final GroupByList list4, final ValueNode valueNode) throws StandardException {
        final FromList list5 = (FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager());
        list5.addElement(this.leftResultSet);
        list5.addElement(this.rightResultSet);
        this.resultColumns.setRedundant();
        list.remapColumnReferencesToExpressions();
        list2.remapColumnReferencesToExpressions();
        if (list4 != null) {
            list4.remapColumnReferencesToExpressions();
        }
        if (valueNode != null) {
            valueNode.remapColumnReferencesToExpressions();
        }
        if (this.joinPredicates.size() > 0) {
            list2.destructiveAppend(this.joinPredicates);
        }
        if (this.subqueryList != null && this.subqueryList.size() > 0) {
            list3.destructiveAppend(this.subqueryList);
        }
        return list5;
    }
    
    public boolean LOJ_reorderable(final int n) throws StandardException {
        return false;
    }
    
    public FromTable transformOuterJoins(final ValueNode valueNode, final int n) throws StandardException {
        if (valueNode == null) {
            ((FromTable)this.leftResultSet).transformOuterJoins(null, n);
            ((FromTable)this.rightResultSet).transformOuterJoins(null, n);
            return this;
        }
        this.leftResultSet = ((FromTable)this.leftResultSet).transformOuterJoins(valueNode, n);
        this.rightResultSet = ((FromTable)this.rightResultSet).transformOuterJoins(valueNode, n);
        return this;
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.generateCore(activationClassBuilder, methodBuilder, 1, null, null);
    }
    
    public void generateCore(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder, final int n) throws StandardException {
        this.generateCore(activationClassBuilder, methodBuilder, n, this.joinClause, this.subqueryList);
    }
    
    protected void generateCore(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder, final int n, ValueNode restorePredicates, final SubqueryList list) throws StandardException {
        if (this.joinPredicates != null) {
            restorePredicates = this.joinPredicates.restorePredicates();
            this.joinPredicates = null;
        }
        this.assignResultSetNumber();
        if (list != null && list.size() > 0) {
            list.setPointOfAttachment(this.resultSetNumber);
        }
        String s;
        if (n == 3) {
            s = ((Optimizable)this.rightResultSet).getTrulyTheBestAccessPath().getJoinStrategy().halfOuterJoinResultSetMethodName();
        }
        else {
            s = ((Optimizable)this.rightResultSet).getTrulyTheBestAccessPath().getJoinStrategy().joinResultSetMethodName();
        }
        activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        methodBuilder.callMethod((short)185, null, s, "org.apache.derby.iapi.sql.execute.NoPutResultSet", this.getJoinArguments(activationClassBuilder, methodBuilder, restorePredicates));
    }
    
    private int getJoinArguments(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder, final ValueNode valueNode) throws StandardException {
        final int numJoinArguments = this.getNumJoinArguments();
        this.leftResultSet.generate(activationClassBuilder, methodBuilder);
        methodBuilder.push(this.leftResultSet.resultColumns.size());
        this.rightResultSet.generate(activationClassBuilder, methodBuilder);
        methodBuilder.push(this.rightResultSet.resultColumns.size());
        this.costEstimate = this.getFinalCostEstimate();
        if (valueNode == null) {
            methodBuilder.pushNull("org.apache.derby.iapi.services.loader.GeneratedMethod");
        }
        else {
            final MethodBuilder userExprFun = activationClassBuilder.newUserExprFun();
            valueNode.generate(activationClassBuilder, userExprFun);
            userExprFun.methodReturn();
            userExprFun.complete();
            activationClassBuilder.pushMethodReference(methodBuilder, userExprFun);
        }
        methodBuilder.push(this.resultSetNumber);
        this.addOuterJoinArguments(activationClassBuilder, methodBuilder);
        this.oneRowRightSide(activationClassBuilder, methodBuilder);
        methodBuilder.push(this.costEstimate.rowCount());
        methodBuilder.push(this.costEstimate.getEstimatedCost());
        if (this.joinOrderStrategyProperties != null) {
            methodBuilder.push(PropertyUtil.sortProperties(this.joinOrderStrategyProperties));
        }
        else {
            methodBuilder.pushNull("java.lang.String");
        }
        return numJoinArguments;
    }
    
    public CostEstimate getFinalCostEstimate() throws StandardException {
        if (this.finalCostEstimate != null) {
            return this.finalCostEstimate;
        }
        final CostEstimate finalCostEstimate = this.leftResultSet.getFinalCostEstimate();
        final CostEstimate finalCostEstimate2 = this.rightResultSet.getFinalCostEstimate();
        (this.finalCostEstimate = this.getNewCostEstimate()).setCost(finalCostEstimate.getEstimatedCost() + finalCostEstimate2.getEstimatedCost(), finalCostEstimate2.rowCount(), finalCostEstimate2.rowCount());
        return this.finalCostEstimate;
    }
    
    protected void oneRowRightSide(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        methodBuilder.push(this.rightResultSet.isOneRowResultSet());
        methodBuilder.push(this.rightResultSet.isNotExists());
    }
    
    protected int getNumJoinArguments() {
        return 11;
    }
    
    protected int addOuterJoinArguments(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        return 0;
    }
    
    public static String joinTypeToString(final int n) {
        switch (n) {
            case 1: {
                return "INNER JOIN";
            }
            case 2: {
                return "CROSS JOIN";
            }
            case 3: {
                return "LEFT OUTER JOIN";
            }
            case 4: {
                return "RIGHT OUTER JOIN";
            }
            case 5: {
                return "FULL OUTER JOIN";
            }
            case 6: {
                return "UNION JOIN";
            }
            default: {
                return null;
            }
        }
    }
    
    protected PredicateList getLeftPredicateList() throws StandardException {
        if (this.leftPredicateList == null) {
            this.leftPredicateList = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
        }
        return this.leftPredicateList;
    }
    
    protected PredicateList getRightPredicateList() throws StandardException {
        if (this.rightPredicateList == null) {
            this.rightPredicateList = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
        }
        return this.rightPredicateList;
    }
    
    public int updateTargetLockMode() {
        return 6;
    }
    
    void notFlattenableJoin() {
        this.flattenableJoin = false;
        this.leftResultSet.notFlattenableJoin();
        this.rightResultSet.notFlattenableJoin();
    }
    
    public boolean isFlattenableJoinNode() {
        return this.flattenableJoin;
    }
    
    boolean isOrderedOn(final ColumnReference[] array, final boolean b, final List list) throws StandardException {
        return this.leftResultSet.isOrderedOn(array, b, list);
    }
    
    public void printSubNodes(final int n) {
    }
    
    void setSubqueryList(final SubqueryList subqueryList) {
        this.subqueryList = subqueryList;
    }
    
    void setAggregateVector(final List aggregateVector) {
        this.aggregateVector = aggregateVector;
    }
    
    void setNaturalJoin() {
        this.naturalJoin = true;
    }
    
    ResultSetNode getLogicalLeftResultSet() {
        return this.leftResultSet;
    }
    
    ResultSetNode getLogicalRightResultSet() {
        return this.rightResultSet;
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.resultColumns != null) {
            this.resultColumns = (ResultColumnList)this.resultColumns.accept(visitor);
        }
        if (this.joinClause != null) {
            this.joinClause = (ValueNode)this.joinClause.accept(visitor);
        }
        if (this.usingClause != null) {
            this.usingClause = (ResultColumnList)this.usingClause.accept(visitor);
        }
        if (this.joinPredicates != null) {
            this.joinPredicates = (PredicateList)this.joinPredicates.accept(visitor);
        }
    }
    
    public JBitSet LOJgetReferencedTables(final int n) throws StandardException {
        final JBitSet loJgetReferencedTables = this.leftResultSet.LOJgetReferencedTables(n);
        if (loJgetReferencedTables == null) {
            return null;
        }
        loJgetReferencedTables.or(this.rightResultSet.LOJgetReferencedTables(n));
        return loJgetReferencedTables;
    }
}
