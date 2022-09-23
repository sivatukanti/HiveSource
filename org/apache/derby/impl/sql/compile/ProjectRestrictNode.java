// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import java.util.HashSet;
import java.util.Set;
import org.apache.derby.catalog.types.ReferencedColumnsDescriptorImpl;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.compile.OptimizableList;
import org.apache.derby.iapi.sql.compile.RequiredRowOrdering;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.sql.compile.OptimizablePredicate;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.sql.compile.AccessPath;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.RowOrdering;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.sql.compile.Optimizer;
import java.util.Properties;
import org.apache.derby.iapi.sql.compile.Optimizable;

public class ProjectRestrictNode extends SingleChildResultSetNode
{
    public ValueNode restriction;
    ValueNode constantRestriction;
    public PredicateList restrictionList;
    SubqueryList projectSubquerys;
    SubqueryList restrictSubquerys;
    private boolean accessPathModified;
    private boolean accessPathConsidered;
    private boolean childResultOptimized;
    private boolean materialize;
    private boolean getTableNumberHere;
    
    public ProjectRestrictNode() {
        this.constantRestriction = null;
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7) {
        super.init(o, o7);
        this.resultColumns = (ResultColumnList)o2;
        this.restriction = (ValueNode)o3;
        this.restrictionList = (PredicateList)o4;
        this.projectSubquerys = (SubqueryList)o5;
        this.restrictSubquerys = (SubqueryList)o6;
        if (o7 != null && o instanceof Optimizable) {
            ((Optimizable)o).setProperties(this.getProperties());
            this.setProperties(null);
        }
    }
    
    public boolean nextAccessPath(final Optimizer optimizer, final OptimizablePredicateList list, final RowOrdering rowOrdering) throws StandardException {
        if (this.childResult instanceof Optimizable) {
            return ((Optimizable)this.childResult).nextAccessPath(optimizer, this.restrictionList, rowOrdering);
        }
        return super.nextAccessPath(optimizer, list, rowOrdering);
    }
    
    public void rememberAsBest(final int n, final Optimizer optimizer) throws StandardException {
        super.rememberAsBest(n, optimizer);
        if (this.childResult instanceof Optimizable) {
            ((Optimizable)this.childResult).rememberAsBest(n, optimizer);
        }
    }
    
    void printRememberingBestAccessPath(final int n, final AccessPath accessPath) {
    }
    
    public void startOptimizing(final Optimizer optimizer, final RowOrdering rowOrdering) {
        if (this.childResult instanceof Optimizable) {
            ((Optimizable)this.childResult).startOptimizing(optimizer, rowOrdering);
        }
        else {
            this.accessPathConsidered = false;
            super.startOptimizing(optimizer, rowOrdering);
        }
    }
    
    public int getTableNumber() {
        if (this.getTableNumberHere) {
            return super.getTableNumber();
        }
        if (this.childResult instanceof Optimizable) {
            return ((Optimizable)this.childResult).getTableNumber();
        }
        return super.getTableNumber();
    }
    
    public CostEstimate optimizeIt(final Optimizer optimizer, final OptimizablePredicateList list, final CostEstimate costEstimate, final RowOrdering rowOrdering) throws StandardException {
        this.costEstimate = this.getCostEstimate(optimizer);
        this.updateBestPlanMap((short)1, this);
        if (this.childResult instanceof Optimizable) {
            final CostEstimate optimizeIt = ((Optimizable)this.childResult).optimizeIt(optimizer, this.restrictionList, costEstimate, rowOrdering);
            this.costEstimate.setCost(optimizeIt.getEstimatedCost(), optimizeIt.rowCount(), optimizeIt.singleScanRowCount());
        }
        else if (!this.accessPathModified) {
            this.childResult = this.childResult.optimize(optimizer.getDataDictionary(), this.restrictionList, costEstimate.rowCount());
            final CostEstimate costEstimate2 = this.childResult.costEstimate;
            this.costEstimate.setCost(costEstimate2.getEstimatedCost(), costEstimate2.rowCount(), costEstimate2.singleScanRowCount());
            optimizer.considerCost(this, this.restrictionList, this.getCostEstimate(), costEstimate);
        }
        return this.costEstimate;
    }
    
    public boolean feasibleJoinStrategy(final OptimizablePredicateList list, final Optimizer optimizer) throws StandardException {
        if (this.childResult instanceof Optimizable) {
            if (this.childResult instanceof UnionNode) {
                ((UnionNode)this.childResult).pullOptPredicates(this.restrictionList);
            }
            return ((Optimizable)this.childResult).feasibleJoinStrategy(this.restrictionList, optimizer);
        }
        return super.feasibleJoinStrategy(this.restrictionList, optimizer);
    }
    
    public AccessPath getCurrentAccessPath() {
        if (this.childResult instanceof Optimizable) {
            return ((Optimizable)this.childResult).getCurrentAccessPath();
        }
        return super.getCurrentAccessPath();
    }
    
    public AccessPath getBestAccessPath() {
        if (this.childResult instanceof Optimizable) {
            return ((Optimizable)this.childResult).getBestAccessPath();
        }
        return super.getBestAccessPath();
    }
    
    public AccessPath getBestSortAvoidancePath() {
        if (this.childResult instanceof Optimizable) {
            return ((Optimizable)this.childResult).getBestSortAvoidancePath();
        }
        return super.getBestSortAvoidancePath();
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
    
    public void rememberSortAvoidancePath() {
        if (this.childResult instanceof Optimizable) {
            ((Optimizable)this.childResult).rememberSortAvoidancePath();
        }
        else {
            super.rememberSortAvoidancePath();
        }
    }
    
    public boolean considerSortAvoidancePath() {
        if (this.childResult instanceof Optimizable) {
            return ((Optimizable)this.childResult).considerSortAvoidancePath();
        }
        return super.considerSortAvoidancePath();
    }
    
    public boolean pushOptPredicate(final OptimizablePredicate optimizablePredicate) throws StandardException {
        if (this.restrictionList == null) {
            this.restrictionList = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
        }
        this.restrictionList.addPredicate((Predicate)optimizablePredicate);
        final Predicate predicate = (Predicate)optimizablePredicate;
        if (!predicate.remapScopedPred()) {
            predicate.getAndNode().accept(new RemapCRsVisitor(true));
        }
        return true;
    }
    
    public void pullOptPredicates(final OptimizablePredicateList list) throws StandardException {
        if (this.restrictionList != null && !this.isNotExists()) {
            if (this.childResult instanceof UnionNode) {
                ((UnionNode)this.childResult).pullOptPredicates(this.restrictionList);
            }
            final RemapCRsVisitor remapCRsVisitor = new RemapCRsVisitor(false);
            for (int i = this.restrictionList.size() - 1; i >= 0; --i) {
                final OptimizablePredicate optPredicate = this.restrictionList.getOptPredicate(i);
                ((Predicate)optPredicate).getAndNode().accept(remapCRsVisitor);
                list.addOptPredicate(optPredicate);
                this.restrictionList.removeOptPredicate(i);
            }
        }
    }
    
    public Optimizable modifyAccessPath(final JBitSet set) throws StandardException {
        boolean b = true;
        if (this.accessPathModified) {
            return this;
        }
        boolean b2 = false;
        if (!(this.childResult instanceof Optimizable)) {
            b = false;
            this.childResult = this.childResult.modifyAccessPaths(this.restrictionList);
            this.hasTrulyTheBestAccessPath = true;
            if (!this.trulyTheBestAccessPath.getJoinStrategy().isHashJoin()) {
                return (Optimizable)this.considerMaterialization(set);
            }
            this.getTableNumberHere = true;
        }
        else if (!(this.childResult instanceof FromBaseTable)) {
            if (this.trulyTheBestAccessPath.getJoinStrategy() == null) {
                this.trulyTheBestAccessPath = (AccessPathImpl)((Optimizable)this.childResult).getTrulyTheBestAccessPath();
            }
            if (this.childResult instanceof SetOperatorNode) {
                this.childResult = (ResultSetNode)((SetOperatorNode)this.childResult).modifyAccessPath(set, this.restrictionList);
                b2 = true;
            }
            else {
                this.childResult = (ResultSetNode)((FromTable)this.childResult).modifyAccessPath(set);
            }
        }
        final boolean b3 = this.hasTrulyTheBestAccessPath && this.trulyTheBestAccessPath.getJoinStrategy() != null && this.trulyTheBestAccessPath.getJoinStrategy().isHashJoin();
        if (this.restrictionList != null && !b2 && !b3) {
            this.restrictionList.pushUsefulPredicates((Optimizable)this.childResult);
        }
        if (b) {
            this.childResult = this.childResult.changeAccessPath();
        }
        this.accessPathModified = true;
        if (this.trulyTheBestAccessPath.getJoinStrategy() != null && this.trulyTheBestAccessPath.getJoinStrategy().isHashJoin()) {
            return this.replaceWithHashTableNode();
        }
        return (Optimizable)this.considerMaterialization(set);
    }
    
    private Optimizable replaceWithHashTableNode() throws StandardException {
        if (this.hasTrulyTheBestAccessPath) {
            ((FromTable)this.childResult).trulyTheBestAccessPath = (AccessPathImpl)this.getTrulyTheBestAccessPath();
            if (this.childResult instanceof SingleChildResultSetNode) {
                ((SingleChildResultSetNode)this.childResult).hasTrulyTheBestAccessPath = this.hasTrulyTheBestAccessPath;
                this.childResult.getReferencedTableMap().set(this.tableNumber);
            }
        }
        final PredicateList list = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
        final PredicateList list2 = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
        final PredicateList list3 = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
        this.trulyTheBestAccessPath.getJoinStrategy().divideUpPredicateLists(this, this.restrictionList, list, list2, list3, this.getDataDictionary());
        this.restrictionList = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
        for (int i = 0; i < list.size(); ++i) {
            list3.removeOptPredicate((OptimizablePredicate)list.elementAt(i));
        }
        for (int j = 0; j < list2.size(); ++j) {
            list3.removeOptPredicate((OptimizablePredicate)list2.elementAt(j));
        }
        list2.transferNonQualifiers(this, this.restrictionList);
        list3.copyPredicatesToOtherList(this.restrictionList);
        final ResultColumnList resultColumns = this.childResult.getResultColumns();
        this.childResult.setResultColumns(resultColumns.copyListAndObjects());
        resultColumns.genVirtualColumnNodes(this.childResult, this.childResult.getResultColumns(), false);
        list.accept(new RemapCRsVisitor(true));
        this.childResult = (ResultSetNode)this.getNodeFactory().getNode(148, this.childResult, this.tableProperties, resultColumns, list, list2, this.trulyTheBestAccessPath, this.getCostEstimate(), this.projectSubquerys, this.restrictSubquerys, this.hashKeyColumns(), this.getContextManager());
        return this;
    }
    
    public void verifyProperties(final DataDictionary dataDictionary) throws StandardException {
        if (this.childResult instanceof Optimizable) {
            ((Optimizable)this.childResult).verifyProperties(dataDictionary);
        }
        else {
            super.verifyProperties(dataDictionary);
        }
    }
    
    public boolean legalJoinOrder(final JBitSet set) {
        return !(this.childResult instanceof Optimizable) || ((Optimizable)this.childResult).legalJoinOrder(set);
    }
    
    public double uniqueJoin(final OptimizablePredicateList list) throws StandardException {
        if (this.childResult instanceof Optimizable) {
            return ((Optimizable)this.childResult).uniqueJoin(list);
        }
        return super.uniqueJoin(list);
    }
    
    PredicateList getRestrictionList() {
        return this.restrictionList;
    }
    
    String getUserSpecifiedJoinStrategy() {
        if (this.childResult instanceof FromTable) {
            return ((FromTable)this.childResult).getUserSpecifiedJoinStrategy();
        }
        return this.userSpecifiedJoinStrategy;
    }
    
    public void printSubNodes(final int n) {
    }
    
    public ResultSetNode preprocess(final int n, final GroupByList list, final FromList list2) throws StandardException {
        this.childResult = this.childResult.preprocess(n, list, list2);
        this.referencedTableMap = (JBitSet)this.childResult.getReferencedTableMap().clone();
        return this;
    }
    
    public void pushExpressions(final PredicateList list) throws StandardException {
        if (this.childResult instanceof JoinNode) {
            ((FromTable)this.childResult).pushExpressions(list);
        }
        final PredicateList pushablePredicates = list.getPushablePredicates(this.referencedTableMap);
        if (pushablePredicates != null && this.childResult instanceof SelectNode) {
            final SelectNode selectNode = (SelectNode)this.childResult;
            if (!selectNode.hasWindows() && selectNode.fetchFirst == null) {
                if (selectNode.offset == null) {
                    pushablePredicates.pushExpressionsIntoSelect((SelectNode)this.childResult, false);
                }
            }
        }
        if (pushablePredicates != null && this.childResult instanceof UnionNode) {
            ((UnionNode)this.childResult).pushExpressions(pushablePredicates);
        }
        if (this.restrictionList == null) {
            this.restrictionList = pushablePredicates;
        }
        else if (pushablePredicates != null && pushablePredicates.size() != 0) {
            this.restrictionList.destructiveAppend(pushablePredicates);
        }
    }
    
    public ResultSetNode addNewPredicate(final Predicate predicate) throws StandardException {
        if (this.restrictionList == null) {
            this.restrictionList = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
        }
        this.restrictionList.addPredicate(predicate);
        return this;
    }
    
    public boolean flattenableInFromSubquery(final FromList list) {
        return false;
    }
    
    public ResultSetNode ensurePredicateList(final int n) throws StandardException {
        return this;
    }
    
    public ResultSetNode optimize(final DataDictionary dataDictionary, final PredicateList list, final double n) throws StandardException {
        this.childResult = this.childResult.optimize(dataDictionary, this.restrictionList, n);
        (this.costEstimate = this.getOptimizer((OptimizableList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this, this.getContextManager()), list, dataDictionary, null).newCostEstimate()).setCost(this.childResult.getCostEstimate().getEstimatedCost(), this.childResult.getCostEstimate().rowCount(), this.childResult.getCostEstimate().singleScanRowCount());
        return this;
    }
    
    public CostEstimate getCostEstimate() {
        if (this.costEstimate == null) {
            return this.childResult.getCostEstimate();
        }
        return this.costEstimate;
    }
    
    public CostEstimate getFinalCostEstimate() throws StandardException {
        if (this.finalCostEstimate != null) {
            return this.finalCostEstimate;
        }
        if (this.childResult instanceof Optimizable) {
            this.finalCostEstimate = this.childResult.getFinalCostEstimate();
        }
        else {
            this.finalCostEstimate = this.getTrulyTheBestAccessPath().getCostEstimate();
        }
        return this.finalCostEstimate;
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        if (this.childResult instanceof FromVTI) {
            ((FromVTI)this.childResult).computeProjectionAndRestriction(this.restrictionList);
        }
        this.generateMinion(activationClassBuilder, methodBuilder, false);
    }
    
    public void generateResultSet(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.generateMinion(expressionClassBuilder, methodBuilder, true);
    }
    
    private void generateMinion(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder, final boolean b) throws StandardException {
        if (this.restrictionList != null && this.restrictionList.size() > 0) {
            this.restrictionList.eliminateBooleanTrueAndBooleanTrue();
        }
        if (this.nopProjectRestrict()) {
            this.generateNOPProjectRestrict();
            if (b) {
                this.childResult.generateResultSet(expressionClassBuilder, methodBuilder);
            }
            else {
                this.childResult.generate((ActivationClassBuilder)expressionClassBuilder, methodBuilder);
            }
            this.costEstimate = this.childResult.getFinalCostEstimate();
            return;
        }
        if (this.restrictionList != null) {
            this.constantRestriction = this.restrictionList.restoreConstantPredicates();
            this.restrictionList.removeRedundantPredicates();
            this.restriction = this.restrictionList.restorePredicates();
            this.restrictionList = null;
        }
        final ResultColumnList.ColumnMapping mapSourceColumns = this.resultColumns.mapSourceColumns();
        final int[] mapArray = mapSourceColumns.mapArray;
        final boolean[] cloneMap = mapSourceColumns.cloneMap;
        final int addItem = expressionClassBuilder.addItem(new ReferencedColumnsDescriptorImpl(mapArray));
        final int addItem2 = expressionClassBuilder.addItem(cloneMap);
        boolean b2 = true;
        if (!this.reflectionNeededForProjection() && mapArray != null && mapArray.length == this.childResult.getResultColumns().size()) {
            int n;
            for (n = 0; n < mapArray.length && mapArray[n] == n + 1; ++n) {}
            if (n == mapArray.length) {
                b2 = false;
            }
        }
        expressionClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        if (b) {
            this.childResult.generateResultSet(expressionClassBuilder, methodBuilder);
        }
        else {
            this.childResult.generate((ActivationClassBuilder)expressionClassBuilder, methodBuilder);
        }
        this.assignResultSetNumber();
        if (this.projectSubquerys != null && this.projectSubquerys.size() > 0) {
            this.projectSubquerys.setPointOfAttachment(this.resultSetNumber);
        }
        if (this.restrictSubquerys != null && this.restrictSubquerys.size() > 0) {
            this.restrictSubquerys.setPointOfAttachment(this.resultSetNumber);
        }
        this.costEstimate = this.getFinalCostEstimate();
        if (this.restriction == null) {
            methodBuilder.pushNull("org.apache.derby.iapi.services.loader.GeneratedMethod");
        }
        else {
            final MethodBuilder userExprFun = expressionClassBuilder.newUserExprFun();
            this.restriction.generateExpression(expressionClassBuilder, userExprFun);
            userExprFun.methodReturn();
            userExprFun.complete();
            expressionClassBuilder.pushMethodReference(methodBuilder, userExprFun);
        }
        if (this.reflectionNeededForProjection()) {
            this.resultColumns.generateCore(expressionClassBuilder, methodBuilder, false);
        }
        else {
            methodBuilder.pushNull("org.apache.derby.iapi.services.loader.GeneratedMethod");
        }
        methodBuilder.push(this.resultSetNumber);
        if (this.constantRestriction == null) {
            methodBuilder.pushNull("org.apache.derby.iapi.services.loader.GeneratedMethod");
        }
        else {
            final MethodBuilder userExprFun2 = expressionClassBuilder.newUserExprFun();
            this.constantRestriction.generateExpression(expressionClassBuilder, userExprFun2);
            userExprFun2.methodReturn();
            userExprFun2.complete();
            expressionClassBuilder.pushMethodReference(methodBuilder, userExprFun2);
        }
        methodBuilder.push(addItem);
        methodBuilder.push(addItem2);
        methodBuilder.push(this.resultColumns.reusableResult());
        methodBuilder.push(b2);
        methodBuilder.push(this.costEstimate.rowCount());
        methodBuilder.push(this.costEstimate.getEstimatedCost());
        methodBuilder.callMethod((short)185, null, "getProjectRestrictResultSet", "org.apache.derby.iapi.sql.execute.NoPutResultSet", 11);
    }
    
    boolean nopProjectRestrict() {
        return this.restriction == null && this.constantRestriction == null && (this.restrictionList == null || this.restrictionList.size() <= 0) && this.getResultColumns().nopProjection(this.childResult.getResultColumns());
    }
    
    public void generateNOPProjectRestrict() throws StandardException {
        this.getResultColumns().setRedundant();
    }
    
    public ResultSetNode considerMaterialization(final JBitSet set) throws StandardException {
        this.childResult = this.childResult.considerMaterialization(set);
        if (this.childResult.performMaterialization(set)) {
            final ReferencedTablesVisitor referencedTablesVisitor = new ReferencedTablesVisitor((JBitSet)this.childResult.getReferencedTableMap().clone());
            final boolean b = this.restrictionList == null || this.restrictionList.size() == 0;
            if (!b) {
                this.restrictionList.accept(referencedTablesVisitor);
            }
            if (b || this.childResult.getReferencedTableMap().contains(referencedTablesVisitor.getTableMap())) {
                final ResultColumnList resultColumns = this.resultColumns;
                this.setResultColumns(this.resultColumns.copyListAndObjects());
                resultColumns.genVirtualColumnNodes(this, this.resultColumns);
                final MaterializeResultSetNode materializeResultSetNode = (MaterializeResultSetNode)this.getNodeFactory().getNode(121, this, resultColumns, this.tableProperties, this.getContextManager());
                if (this.referencedTableMap != null) {
                    materializeResultSetNode.setReferencedTableMap((JBitSet)this.referencedTableMap.clone());
                }
                return materializeResultSetNode;
            }
            final ResultColumnList resultColumns2 = this.childResult.getResultColumns();
            this.childResult.setResultColumns(resultColumns2.copyListAndObjects());
            resultColumns2.genVirtualColumnNodes(this.childResult, this.childResult.getResultColumns());
            final MaterializeResultSetNode childResult = (MaterializeResultSetNode)this.getNodeFactory().getNode(121, this.childResult, resultColumns2, this.tableProperties, this.getContextManager());
            if (this.childResult.getReferencedTableMap() != null) {
                childResult.setReferencedTableMap((JBitSet)this.childResult.getReferencedTableMap().clone());
            }
            this.childResult = childResult;
        }
        return this;
    }
    
    protected FromTable getFromTableByName(final String s, final String s2, final boolean b) throws StandardException {
        return this.childResult.getFromTableByName(s, s2, b);
    }
    
    public int updateTargetLockMode() {
        if (this.restriction != null || this.constantRestriction != null) {
            return 6;
        }
        return this.childResult.updateTargetLockMode();
    }
    
    boolean isPossibleDistinctScan(final Set o) {
        if (this.restriction != null || (this.restrictionList != null && this.restrictionList.size() != 0)) {
            return false;
        }
        final HashSet set = new HashSet<BaseColumnNode>();
        for (int i = 0; i < this.resultColumns.size(); ++i) {
            final BaseColumnNode baseColumnNode = ((ResultColumn)this.resultColumns.elementAt(i)).getBaseColumnNode();
            if (baseColumnNode == null) {
                return false;
            }
            set.add(baseColumnNode);
        }
        return set.equals(o) && this.childResult.isPossibleDistinctScan(o);
    }
    
    void markForDistinctScan() {
        this.childResult.markForDistinctScan();
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.restriction != null) {
            this.restriction = (ValueNode)this.restriction.accept(visitor);
        }
        if (this.restrictionList != null) {
            this.restrictionList = (PredicateList)this.restrictionList.accept(visitor);
        }
    }
    
    public void setRefActionInfo(final long n, final int[] array, final String s, final boolean b) {
        this.childResult.setRefActionInfo(n, array, s, b);
    }
    
    public void setRestriction(final ValueNode restriction) {
        this.restriction = restriction;
    }
    
    void pushOrderByList(final OrderByList list) {
        this.childResult.pushOrderByList(list);
    }
    
    void pushOffsetFetchFirst(final ValueNode valueNode, final ValueNode valueNode2, final boolean b) {
        this.childResult.pushOffsetFetchFirst(valueNode, valueNode2, b);
    }
}
