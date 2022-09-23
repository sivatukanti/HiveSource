// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.sql.compile.OptimizablePredicate;
import org.apache.derby.iapi.sql.compile.AccessPath;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.error.StandardException;
import java.util.HashMap;
import org.apache.derby.iapi.sql.compile.RowOrdering;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.sql.compile.RequiredRowOrdering;
import org.apache.derby.iapi.sql.compile.JoinStrategy;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.sql.compile.OptimizableList;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.compile.Optimizer;

public class OptimizerImpl implements Optimizer
{
    DataDictionary dDictionary;
    int numTablesInQuery;
    int numOptimizables;
    protected JBitSet assignedTableMap;
    protected OptimizableList optimizableList;
    OptimizablePredicateList predicateList;
    JBitSet nonCorrelatedTableMap;
    protected int[] proposedJoinOrder;
    protected int[] bestJoinOrder;
    protected int joinPosition;
    boolean desiredJoinOrderFound;
    private static final int NO_JUMP = 0;
    private static final int READY_TO_JUMP = 1;
    private static final int JUMPING = 2;
    private static final int WALK_HIGH = 3;
    private static final int WALK_LOW = 4;
    private int permuteState;
    private int[] firstLookOrder;
    private boolean ruleBasedOptimization;
    private CostEstimateImpl outermostCostEstimate;
    protected CostEstimateImpl currentCost;
    protected CostEstimateImpl currentSortAvoidanceCost;
    protected CostEstimateImpl bestCost;
    protected long timeOptimizationStarted;
    protected long currentTime;
    protected boolean timeExceeded;
    private boolean noTimeout;
    private boolean useStatistics;
    private int tableLockThreshold;
    private JoinStrategy[] joinStrategies;
    protected RequiredRowOrdering requiredRowOrdering;
    private boolean foundABestPlan;
    protected CostEstimate sortCost;
    private RowOrdering currentRowOrdering;
    private RowOrdering bestRowOrdering;
    private boolean conglomerate_OneRowResultSet;
    protected boolean optimizerTrace;
    protected boolean optimizerTraceHtml;
    protected int maxMemoryPerTable;
    private boolean reloadBestPlan;
    private HashMap savedJoinOrders;
    protected double timeLimit;
    CostEstimate finalCostEstimate;
    private boolean usingPredsPushedFromAbove;
    private boolean bestJoinOrderUsedPredsFromAbove;
    
    protected OptimizerImpl(final OptimizableList optimizableList, final OptimizablePredicateList predicateList, final DataDictionary dDictionary, final boolean ruleBasedOptimization, final boolean noTimeout, final boolean useStatistics, final int maxMemoryPerTable, final JoinStrategy[] joinStrategies, final int tableLockThreshold, final RequiredRowOrdering requiredRowOrdering, final int numTablesInQuery) throws StandardException {
        this.currentRowOrdering = new RowOrderingImpl();
        this.bestRowOrdering = new RowOrderingImpl();
        this.outermostCostEstimate = this.getNewCostEstimate(0.0, 1.0, 1.0);
        this.currentCost = this.getNewCostEstimate(0.0, 0.0, 0.0);
        this.currentSortAvoidanceCost = this.getNewCostEstimate(0.0, 0.0, 0.0);
        this.bestCost = this.getNewCostEstimate(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        optimizableList.verifyProperties(dDictionary);
        this.numTablesInQuery = numTablesInQuery;
        this.numOptimizables = optimizableList.size();
        this.proposedJoinOrder = new int[this.numOptimizables];
        if (this.initJumpState() == 1) {
            this.firstLookOrder = new int[this.numOptimizables];
        }
        for (int i = 0; i < this.numOptimizables; ++i) {
            this.proposedJoinOrder[i] = -1;
        }
        this.bestJoinOrder = new int[this.numOptimizables];
        this.joinPosition = -1;
        this.optimizableList = optimizableList;
        this.predicateList = predicateList;
        this.dDictionary = dDictionary;
        this.ruleBasedOptimization = ruleBasedOptimization;
        this.noTimeout = noTimeout;
        this.maxMemoryPerTable = maxMemoryPerTable;
        this.joinStrategies = joinStrategies;
        this.tableLockThreshold = tableLockThreshold;
        this.requiredRowOrdering = requiredRowOrdering;
        this.useStatistics = useStatistics;
        this.assignedTableMap = new JBitSet(numTablesInQuery);
        this.nonCorrelatedTableMap = new JBitSet(numTablesInQuery);
        for (int j = 0; j < this.numOptimizables; ++j) {
            this.nonCorrelatedTableMap.or(optimizableList.getOptimizable(j).getReferencedTableMap());
        }
        this.timeOptimizationStarted = System.currentTimeMillis();
        this.reloadBestPlan = false;
        this.savedJoinOrders = null;
        this.timeLimit = Double.MAX_VALUE;
        this.usingPredsPushedFromAbove = false;
        this.bestJoinOrderUsedPredsFromAbove = false;
    }
    
    public void prepForNextRound() {
        this.reloadBestPlan = false;
        this.bestCost = this.getNewCostEstimate(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        this.usingPredsPushedFromAbove = false;
        if (this.predicateList != null && this.predicateList.size() > 0) {
            for (int i = this.predicateList.size() - 1; i >= 0; --i) {
                if (((Predicate)this.predicateList.getOptPredicate(i)).isScopedForPush()) {
                    this.usingPredsPushedFromAbove = true;
                    break;
                }
            }
        }
        if (this.usingPredsPushedFromAbove) {
            this.timeOptimizationStarted = System.currentTimeMillis();
            this.timeExceeded = false;
        }
        this.desiredJoinOrderFound = false;
        this.initJumpState();
    }
    
    private int initJumpState() {
        return this.permuteState = ((this.numTablesInQuery >= 6) ? 1 : 0);
    }
    
    public int getMaxMemoryPerTable() {
        return this.maxMemoryPerTable;
    }
    
    public boolean getNextPermutation() throws StandardException {
        if (this.numOptimizables < 1) {
            if (this.optimizerTrace) {
                this.trace(3, 0, 0, 0.0, null);
            }
            this.endOfRoundCleanup();
            return false;
        }
        this.optimizableList.initAccessPaths(this);
        if (!this.timeExceeded && this.numTablesInQuery > 6 && !this.noTimeout) {
            this.currentTime = System.currentTimeMillis();
            this.timeExceeded = (this.currentTime - this.timeOptimizationStarted > this.timeLimit);
            if (this.optimizerTrace && this.timeExceeded) {
                this.trace(2, 0, 0, 0.0, null);
            }
        }
        if (this.bestCost.isUninitialized() && this.foundABestPlan && ((!this.usingPredsPushedFromAbove && !this.bestJoinOrderUsedPredsFromAbove) || this.timeExceeded)) {
            if (this.permuteState != 2) {
                if (this.firstLookOrder == null) {
                    this.firstLookOrder = new int[this.numOptimizables];
                }
                for (int i = 0; i < this.numOptimizables; ++i) {
                    this.firstLookOrder[i] = this.bestJoinOrder[i];
                }
                this.permuteState = 2;
                if (this.joinPosition >= 0) {
                    this.rewindJoinOrder();
                    this.joinPosition = -1;
                }
            }
            this.timeExceeded = false;
        }
        boolean b = false;
        final boolean b2 = !this.bestCost.isUninitialized() && this.currentCost.compare(this.bestCost) > 0.0 && (this.requiredRowOrdering == null || this.currentSortAvoidanceCost.compare(this.bestCost) > 0.0);
        if (this.joinPosition < this.numOptimizables - 1 && !b2 && !this.timeExceeded) {
            if (this.joinPosition < 0 || this.optimizableList.getOptimizable(this.proposedJoinOrder[this.joinPosition]).getBestAccessPath().getCostEstimate() != null) {
                ++this.joinPosition;
                b = true;
                this.bestRowOrdering.copy(this.currentRowOrdering);
            }
        }
        else {
            if (this.optimizerTrace && this.joinPosition < this.numOptimizables - 1) {
                this.trace(8, 0, 0, 0.0, null);
            }
            if (this.joinPosition < this.numOptimizables - 1) {
                this.reloadBestPlan = true;
            }
        }
        if (this.permuteState == 2 && !b && this.joinPosition >= 0) {
            this.reloadBestPlan = true;
            this.rewindJoinOrder();
            this.permuteState = 0;
        }
        while (this.joinPosition >= 0) {
            int j = this.proposedJoinOrder[this.joinPosition] + 1;
            if (this.proposedJoinOrder[this.joinPosition] >= 0) {
                this.pullOptimizableFromJoinOrder();
            }
            if (this.desiredJoinOrderFound || this.timeExceeded) {
                j = this.numOptimizables;
            }
            else if (this.permuteState == 2) {
                final int n = j = this.firstLookOrder[this.joinPosition];
                int numOptimizables = this.numOptimizables;
                int n2 = -1;
                for (Optimizable optimizable = this.optimizableList.getOptimizable(j); !optimizable.legalJoinOrder(this.assignedTableMap); optimizable = this.optimizableList.getOptimizable(j)) {
                    if (n2 >= 0) {
                        this.firstLookOrder[this.joinPosition] = n;
                        this.firstLookOrder[numOptimizables] = n2;
                    }
                    if (numOptimizables <= this.joinPosition + 1) {
                        if (this.joinPosition > 0) {
                            --this.joinPosition;
                            this.reloadBestPlan = true;
                            this.rewindJoinOrder();
                        }
                        this.permuteState = 0;
                        break;
                    }
                    n2 = this.firstLookOrder[--numOptimizables];
                    this.firstLookOrder[this.joinPosition] = n2;
                    this.firstLookOrder[numOptimizables] = n;
                    j = n2;
                }
                if (this.permuteState == 0) {
                    continue;
                }
                if (this.joinPosition == this.numOptimizables - 1) {
                    this.permuteState = 3;
                }
            }
            else {
                while (j < this.numOptimizables) {
                    boolean b3 = false;
                    for (int k = 0; k < this.joinPosition; ++k) {
                        if (this.proposedJoinOrder[k] == j) {
                            b3 = true;
                            break;
                        }
                    }
                    if (!b3) {
                        if (j >= this.numOptimizables || this.joinOrderMeetsDependencies(j)) {
                            break;
                        }
                        if (this.optimizerTrace) {
                            this.trace(9, j, 0, 0.0, null);
                        }
                        if (!this.optimizableList.optimizeJoinOrder()) {
                            if (this.optimizerTrace) {
                                this.trace(10, 0, 0, 0.0, null);
                            }
                            throw StandardException.newException("42Y70");
                        }
                    }
                    ++j;
                }
            }
            if (j < this.numOptimizables) {
                this.proposedJoinOrder[this.joinPosition] = j;
                if (this.permuteState == 4) {
                    boolean b4 = true;
                    for (int l = 0; l < this.numOptimizables; ++l) {
                        if (this.proposedJoinOrder[l] < this.firstLookOrder[l]) {
                            b4 = false;
                            break;
                        }
                        if (this.proposedJoinOrder[l] > this.firstLookOrder[l]) {
                            break;
                        }
                    }
                    if (b4) {
                        this.proposedJoinOrder[this.joinPosition] = -1;
                        --this.joinPosition;
                        if (this.joinPosition >= 0) {
                            this.reloadBestPlan = true;
                            this.rewindJoinOrder();
                            this.joinPosition = -1;
                        }
                        this.permuteState = 1;
                        this.endOfRoundCleanup();
                        return false;
                    }
                }
                this.optimizableList.getOptimizable(j).getBestAccessPath().setCostEstimate(null);
                if (this.optimizerTrace) {
                    this.trace(12, 0, 0, 0.0, null);
                }
                final Optimizable optimizable2 = this.optimizableList.getOptimizable(j);
                this.assignedTableMap.or(optimizable2.getReferencedTableMap());
                optimizable2.startOptimizing(this, this.currentRowOrdering);
                this.pushPredicates(this.optimizableList.getOptimizable(j), this.assignedTableMap);
                return true;
            }
            if (!this.optimizableList.optimizeJoinOrder()) {
                if (!this.optimizableList.legalJoinOrder(this.numTablesInQuery)) {
                    if (this.optimizerTrace) {
                        this.trace(10, 0, 0, 0.0, null);
                    }
                    throw StandardException.newException("42Y70");
                }
                if (this.optimizerTrace) {
                    this.trace(11, 0, 0, 0.0, null);
                }
                this.desiredJoinOrderFound = true;
            }
            if (this.permuteState == 1 && this.joinPosition > 0 && this.joinPosition == this.numOptimizables - 1) {
                this.permuteState = 2;
                final double[] array = new double[this.numOptimizables];
                for (int n3 = 0; n3 < this.numOptimizables; ++n3) {
                    this.firstLookOrder[n3] = n3;
                    final CostEstimate costEstimate = this.optimizableList.getOptimizable(n3).getBestAccessPath().getCostEstimate();
                    if (costEstimate == null) {
                        this.permuteState = 1;
                        break;
                    }
                    array[n3] = costEstimate.singleScanRowCount();
                }
                if (this.permuteState == 2) {
                    boolean b5 = false;
                    for (int n4 = 0; n4 < this.numOptimizables; ++n4) {
                        int n5 = n4;
                        for (int n6 = n4 + 1; n6 < this.numOptimizables; ++n6) {
                            if (array[n6] < array[n5]) {
                                n5 = n6;
                            }
                        }
                        if (n5 != n4) {
                            array[n5] = array[n4];
                            final int n7 = this.firstLookOrder[n4];
                            this.firstLookOrder[n4] = this.firstLookOrder[n5];
                            this.firstLookOrder[n5] = n7;
                            b5 = true;
                        }
                    }
                    if (b5) {
                        --this.joinPosition;
                        this.rewindJoinOrder();
                        continue;
                    }
                    this.permuteState = 0;
                }
            }
            --this.joinPosition;
            if (this.joinPosition >= 0 || this.permuteState != 3) {
                continue;
            }
            this.joinPosition = 0;
            this.permuteState = 4;
        }
        this.endOfRoundCleanup();
        return false;
    }
    
    private void rewindJoinOrder() throws StandardException {
        while (true) {
            final Optimizable optimizable = this.optimizableList.getOptimizable(this.proposedJoinOrder[this.joinPosition]);
            optimizable.pullOptPredicates(this.predicateList);
            if (this.reloadBestPlan) {
                optimizable.updateBestPlanMap((short)2, this);
            }
            this.proposedJoinOrder[this.joinPosition] = -1;
            if (this.joinPosition == 0) {
                break;
            }
            --this.joinPosition;
        }
        this.currentCost.setCost(0.0, 0.0, 0.0);
        this.currentSortAvoidanceCost.setCost(0.0, 0.0, 0.0);
        this.assignedTableMap.clearAll();
    }
    
    private void endOfRoundCleanup() throws StandardException {
        for (int i = 0; i < this.numOptimizables; ++i) {
            this.optimizableList.getOptimizable(i).updateBestPlanMap((short)0, this);
        }
    }
    
    private double recoverCostFromProposedJoinOrder(final boolean b) throws StandardException {
        double n = 0.0;
        for (int i = 0; i < this.joinPosition; ++i) {
            if (b) {
                n += this.optimizableList.getOptimizable(this.proposedJoinOrder[i]).getBestSortAvoidancePath().getCostEstimate().getEstimatedCost();
            }
            else {
                n += this.optimizableList.getOptimizable(this.proposedJoinOrder[i]).getBestAccessPath().getCostEstimate().getEstimatedCost();
            }
        }
        return n;
    }
    
    private boolean joinOrderMeetsDependencies(final int n) throws StandardException {
        return this.optimizableList.getOptimizable(n).legalJoinOrder(this.assignedTableMap);
    }
    
    private void pullOptimizableFromJoinOrder() throws StandardException {
        final Optimizable optimizable = this.optimizableList.getOptimizable(this.proposedJoinOrder[this.joinPosition]);
        int n = 0;
        double n2;
        double n3;
        if (this.joinPosition == 0) {
            n2 = this.outermostCostEstimate.rowCount();
            n3 = this.outermostCostEstimate.singleScanRowCount();
        }
        else {
            n = this.proposedJoinOrder[this.joinPosition - 1];
            final CostEstimate costEstimate = this.optimizableList.getOptimizable(n).getBestAccessPath().getCostEstimate();
            n2 = costEstimate.rowCount();
            n3 = costEstimate.singleScanRowCount();
        }
        double n4 = this.currentCost.getEstimatedCost();
        final CostEstimate costEstimate2 = optimizable.getBestAccessPath().getCostEstimate();
        if (costEstimate2 != null) {
            n4 -= costEstimate2.getEstimatedCost();
            if (n4 <= 0.0) {
                if (this.joinPosition == 0) {
                    n4 = 0.0;
                }
                else {
                    n4 = this.recoverCostFromProposedJoinOrder(false);
                }
            }
        }
        if (this.joinPosition == 0) {
            if (this.outermostCostEstimate != null) {
                n4 = this.outermostCostEstimate.getEstimatedCost();
            }
            else {
                n4 = 0.0;
            }
        }
        this.currentCost.setCost(n4, n2, n3);
        if (this.requiredRowOrdering != null && optimizable.considerSortAvoidancePath()) {
            final AccessPath bestSortAvoidancePath = optimizable.getBestSortAvoidancePath();
            double n5;
            double n6;
            double n7;
            if (this.joinPosition == 0) {
                n5 = this.outermostCostEstimate.rowCount();
                n6 = this.outermostCostEstimate.singleScanRowCount();
                n7 = this.outermostCostEstimate.getEstimatedCost();
            }
            else {
                final CostEstimate costEstimate3 = this.optimizableList.getOptimizable(n).getBestSortAvoidancePath().getCostEstimate();
                n5 = costEstimate3.rowCount();
                n6 = costEstimate3.singleScanRowCount();
                n7 = this.currentSortAvoidanceCost.getEstimatedCost() - bestSortAvoidancePath.getCostEstimate().getEstimatedCost();
            }
            if (n7 <= 0.0) {
                if (this.joinPosition == 0) {
                    n7 = 0.0;
                }
                else {
                    n7 = this.recoverCostFromProposedJoinOrder(true);
                }
            }
            this.currentSortAvoidanceCost.setCost(n7, n5, n6);
            this.bestRowOrdering.removeOptimizable(optimizable.getTableNumber());
            this.bestRowOrdering.copy(this.currentRowOrdering);
        }
        optimizable.pullOptPredicates(this.predicateList);
        if (this.reloadBestPlan) {
            optimizable.updateBestPlanMap((short)2, this);
        }
        this.proposedJoinOrder[this.joinPosition] = -1;
        this.assignedTableMap.xor(optimizable.getReferencedTableMap());
    }
    
    void pushPredicates(final Optimizable optimizable, final JBitSet set) throws StandardException {
        final int size = this.predicateList.size();
        final JBitSet tableMap = new JBitSet(this.numTablesInQuery);
        JBitSet tableMap2 = null;
        BaseTableNumbersVisitor baseTableNumbersVisitor = null;
        for (int i = size - 1; i >= 0; --i) {
            final Predicate predicate = (Predicate)this.predicateList.getOptPredicate(i);
            if (this.isPushable(predicate)) {
                tableMap.setTo(predicate.getReferencedMap());
                for (int j = 0; j < tableMap.size(); ++j) {
                    if (set.get(j)) {
                        tableMap.clear(j);
                    }
                }
                tableMap.and(this.nonCorrelatedTableMap);
                int n = (tableMap.getFirstSetBit() == -1) ? 1 : 0;
                if (n != 0 && predicate.isScopedForPush() && this.numOptimizables > 1) {
                    if (baseTableNumbersVisitor == null) {
                        tableMap2 = new JBitSet(this.numTablesInQuery);
                        baseTableNumbersVisitor = new BaseTableNumbersVisitor(tableMap2);
                    }
                    final int tableNumber = ((FromTable)optimizable).getTableNumber();
                    tableMap2.clearAll();
                    baseTableNumbersVisitor.setTableMap(tableMap2);
                    ((FromTable)optimizable).accept(baseTableNumbersVisitor);
                    if (tableNumber >= 0) {
                        tableMap2.set(tableNumber);
                    }
                    baseTableNumbersVisitor.setTableMap(tableMap);
                    predicate.accept(baseTableNumbersVisitor);
                    tableMap.and(tableMap2);
                    if (tableMap.getFirstSetBit() == -1) {
                        n = 0;
                    }
                }
                if (n != 0 && optimizable.pushOptPredicate(predicate)) {
                    this.predicateList.removeOptPredicate(i);
                }
            }
        }
    }
    
    public boolean getNextDecoratedPermutation() throws StandardException {
        final Optimizable optimizable = this.optimizableList.getOptimizable(this.proposedJoinOrder[this.joinPosition]);
        double rowCount = 0.0;
        final boolean nextAccessPath = optimizable.nextAccessPath(this, null, this.currentRowOrdering);
        if (optimizable.getBestAccessPath().getCostEstimate() != null && optimizable.getCurrentAccessPath().getCostEstimate() != null) {
            if (optimizable.getBestAccessPath().getCostEstimate().compare(optimizable.getCurrentAccessPath().getCostEstimate()) != 0.0) {
                optimizable.updateBestPlanMap((short)2, optimizable);
            }
            else if (optimizable.getBestAccessPath().getCostEstimate().rowCount() < optimizable.getCurrentAccessPath().getCostEstimate().rowCount()) {
                optimizable.updateBestPlanMap((short)2, optimizable);
            }
        }
        optimizable.updateBestPlanMap((short)0, optimizable);
        final CostEstimate costEstimate = optimizable.getBestAccessPath().getCostEstimate();
        if (!nextAccessPath && costEstimate != null) {
            this.currentCost.setCost(this.currentCost.getEstimatedCost() + costEstimate.getEstimatedCost(), costEstimate.rowCount(), costEstimate.singleScanRowCount());
            if (optimizable.considerSortAvoidancePath() && this.requiredRowOrdering != null) {
                final CostEstimate costEstimate2 = optimizable.getBestSortAvoidancePath().getCostEstimate();
                this.currentSortAvoidanceCost.setCost(this.currentSortAvoidanceCost.getEstimatedCost() + costEstimate2.getEstimatedCost(), costEstimate2.rowCount(), costEstimate2.singleScanRowCount());
            }
            if (this.optimizerTrace) {
                this.trace(13, 0, 0, 0.0, null);
                if (optimizable.considerSortAvoidancePath()) {
                    this.trace(14, 0, 0, 0.0, null);
                }
            }
            if (this.joinPosition == this.numOptimizables - 1) {
                if (this.optimizerTrace) {
                    this.trace(4, 0, 0, 0.0, null);
                }
                if (this.requiredRowOrdering != null) {
                    boolean b = false;
                    if (this.sortCost == null) {
                        this.sortCost = this.newCostEstimate();
                    }
                    else if (this.requiredRowOrdering.getSortNeeded()) {
                        if (this.bestCost.rowCount() > this.currentCost.rowCount()) {
                            this.requiredRowOrdering.estimateCost(this.bestCost.rowCount(), this.bestRowOrdering, this.sortCost);
                            final double estimatedCost = this.sortCost.getEstimatedCost();
                            this.requiredRowOrdering.estimateCost(this.currentCost.rowCount(), this.bestRowOrdering, this.sortCost);
                            b = true;
                            this.bestCost.setCost(this.bestCost.getEstimatedCost() - estimatedCost + this.sortCost.getEstimatedCost(), this.sortCost.rowCount(), this.currentCost.singleScanRowCount());
                        }
                        else if (this.bestCost.rowCount() < this.currentCost.rowCount()) {
                            this.currentCost.setCost(this.currentCost.getEstimatedCost(), this.bestCost.rowCount(), this.currentCost.singleScanRowCount());
                        }
                    }
                    if (!b) {
                        this.requiredRowOrdering.estimateCost(this.currentCost.rowCount(), this.bestRowOrdering, this.sortCost);
                    }
                    rowCount = this.currentCost.rowCount();
                    this.currentCost.setCost(this.currentCost.getEstimatedCost() + this.sortCost.getEstimatedCost(), this.sortCost.rowCount(), this.currentCost.singleScanRowCount());
                    if (this.optimizerTrace) {
                        this.trace(5, 0, 0, 0.0, null);
                        this.trace(15, 0, 0, 0.0, null);
                    }
                }
                if (!this.foundABestPlan || this.currentCost.compare(this.bestCost) < 0.0 || this.bestCost.isUninitialized()) {
                    this.rememberBestCost(this.currentCost, 1);
                    this.reloadBestPlan = false;
                }
                else {
                    this.reloadBestPlan = true;
                }
                if (this.requiredRowOrdering != null) {
                    double n = this.currentCost.getEstimatedCost() - this.sortCost.getEstimatedCost();
                    if (n < 0.0) {
                        n = 0.0;
                    }
                    this.currentCost.setCost(n, rowCount, this.currentCost.singleScanRowCount());
                }
                if (this.requiredRowOrdering != null && optimizable.considerSortAvoidancePath() && this.requiredRowOrdering.sortRequired(this.bestRowOrdering, this.optimizableList, this.proposedJoinOrder) == 3) {
                    if (this.optimizerTrace) {
                        this.trace(16, 0, 0, 0.0, null);
                    }
                    if (this.currentSortAvoidanceCost.compare(this.bestCost) <= 0.0 || this.bestCost.isUninitialized()) {
                        this.rememberBestCost(this.currentSortAvoidanceCost, 2);
                    }
                }
            }
        }
        return nextAccessPath;
    }
    
    private void rememberBestCost(final CostEstimate cost, final int n) throws StandardException {
        this.foundABestPlan = true;
        if (this.optimizerTrace) {
            this.trace(17, 0, 0, 0.0, null);
            this.trace(18, n, 0, 0.0, null);
            this.trace(19, 0, 0, 0.0, null);
        }
        this.bestCost.setCost(cost);
        if (this.bestCost.getEstimatedCost() < this.timeLimit) {
            this.timeLimit = this.bestCost.getEstimatedCost();
        }
        this.bestJoinOrderUsedPredsFromAbove = this.usingPredsPushedFromAbove;
        for (int i = 0; i < this.numOptimizables; ++i) {
            this.bestJoinOrder[i] = this.proposedJoinOrder[i];
        }
        for (int j = 0; j < this.numOptimizables; ++j) {
            this.optimizableList.getOptimizable(this.bestJoinOrder[j]).rememberAsBest(n, this);
        }
        if (this.requiredRowOrdering != null) {
            if (n == 2) {
                this.requiredRowOrdering.sortNotNeeded();
            }
            else {
                this.requiredRowOrdering.sortNeeded();
            }
        }
        if (this.optimizerTrace) {
            if (this.requiredRowOrdering != null) {
                this.trace(20, n, 0, 0.0, null);
            }
            this.trace(21, 0, 0, 0.0, null);
        }
    }
    
    public void costPermutation() throws StandardException {
        CostEstimate costEstimate;
        if (this.joinPosition == 0) {
            costEstimate = this.outermostCostEstimate;
        }
        else {
            costEstimate = this.optimizableList.getOptimizable(this.proposedJoinOrder[this.joinPosition - 1]).getBestAccessPath().getCostEstimate();
        }
        final Optimizable optimizable = this.optimizableList.getOptimizable(this.proposedJoinOrder[this.joinPosition]);
        if (!optimizable.feasibleJoinStrategy(this.predicateList, this)) {
            return;
        }
        optimizable.optimizeIt(this, this.predicateList, costEstimate, this.currentRowOrdering);
    }
    
    public void costOptimizable(final Optimizable optimizable, final TableDescriptor tableDescriptor, final ConglomerateDescriptor conglomerateDescriptor, final OptimizablePredicateList list, final CostEstimate costEstimate) throws StandardException {
        if (!optimizable.feasibleJoinStrategy(list, this)) {
            return;
        }
        if (this.ruleBasedOptimization) {
            this.ruleBasedCostOptimizable(optimizable, tableDescriptor, conglomerateDescriptor, list, costEstimate);
        }
        else {
            this.costBasedCostOptimizable(optimizable, tableDescriptor, conglomerateDescriptor, list, costEstimate);
        }
    }
    
    private void ruleBasedCostOptimizable(final Optimizable optimizable, final TableDescriptor tableDescriptor, final ConglomerateDescriptor conglomerateDescriptor, final OptimizablePredicateList list, final CostEstimate costEstimate) throws StandardException {
        final AccessPath bestAccessPath = optimizable.getBestAccessPath();
        optimizable.getCurrentAccessPath().getLockMode();
        if (list != null && list.useful(optimizable, conglomerateDescriptor)) {
            final boolean coveringIndex = optimizable.isCoveringIndex(conglomerateDescriptor);
            if (!bestAccessPath.getCoveringIndexScan() || bestAccessPath.getNonMatchingIndexScan() || coveringIndex) {
                bestAccessPath.setCostEstimate(this.estimateTotalCost(list, conglomerateDescriptor, costEstimate, optimizable));
                bestAccessPath.setConglomerateDescriptor(conglomerateDescriptor);
                bestAccessPath.setNonMatchingIndexScan(false);
                bestAccessPath.setCoveringIndexScan(coveringIndex);
                bestAccessPath.setLockMode(optimizable.getCurrentAccessPath().getLockMode());
                optimizable.rememberJoinStrategyAsBest(bestAccessPath);
            }
            return;
        }
        if (optimizable.isCoveringIndex(conglomerateDescriptor)) {
            bestAccessPath.setCostEstimate(this.estimateTotalCost(list, conglomerateDescriptor, costEstimate, optimizable));
            bestAccessPath.setConglomerateDescriptor(conglomerateDescriptor);
            bestAccessPath.setNonMatchingIndexScan(true);
            bestAccessPath.setCoveringIndexScan(true);
            bestAccessPath.setLockMode(optimizable.getCurrentAccessPath().getLockMode());
            optimizable.rememberJoinStrategyAsBest(bestAccessPath);
            return;
        }
        if (!bestAccessPath.getCoveringIndexScan() && bestAccessPath.getNonMatchingIndexScan() && !conglomerateDescriptor.isIndex()) {
            bestAccessPath.setCostEstimate(this.estimateTotalCost(list, conglomerateDescriptor, costEstimate, optimizable));
            bestAccessPath.setConglomerateDescriptor(conglomerateDescriptor);
            bestAccessPath.setLockMode(optimizable.getCurrentAccessPath().getLockMode());
            optimizable.rememberJoinStrategyAsBest(bestAccessPath);
            return;
        }
        if (bestAccessPath.getConglomerateDescriptor() == null) {
            bestAccessPath.setCostEstimate(this.estimateTotalCost(list, conglomerateDescriptor, costEstimate, optimizable));
            bestAccessPath.setConglomerateDescriptor(conglomerateDescriptor);
            bestAccessPath.setCoveringIndexScan(false);
            bestAccessPath.setNonMatchingIndexScan(conglomerateDescriptor.isIndex());
            bestAccessPath.setLockMode(optimizable.getCurrentAccessPath().getLockMode());
            optimizable.rememberJoinStrategyAsBest(bestAccessPath);
        }
    }
    
    private void costBasedCostOptimizable(final Optimizable optimizable, final TableDescriptor tableDescriptor, final ConglomerateDescriptor conglomerateDescriptor, final OptimizablePredicateList list, final CostEstimate costEstimate) throws StandardException {
        final CostEstimate estimateTotalCost = this.estimateTotalCost(list, conglomerateDescriptor, costEstimate, optimizable);
        optimizable.getCurrentAccessPath().setCostEstimate(estimateTotalCost);
        if (!optimizable.memoryUsageOK(estimateTotalCost.rowCount() / costEstimate.rowCount(), this.maxMemoryPerTable)) {
            if (this.optimizerTrace) {
                this.trace(22, 0, 0, 0.0, null);
            }
            return;
        }
        final AccessPath bestAccessPath = optimizable.getBestAccessPath();
        final CostEstimate costEstimate2 = bestAccessPath.getCostEstimate();
        if (costEstimate2 == null || costEstimate2.isUninitialized() || estimateTotalCost.compare(costEstimate2) < 0.0) {
            bestAccessPath.setConglomerateDescriptor(conglomerateDescriptor);
            bestAccessPath.setCostEstimate(estimateTotalCost);
            bestAccessPath.setCoveringIndexScan(optimizable.isCoveringIndex(conglomerateDescriptor));
            bestAccessPath.setNonMatchingIndexScan(list == null || !list.useful(optimizable, conglomerateDescriptor));
            bestAccessPath.setLockMode(optimizable.getCurrentAccessPath().getLockMode());
            optimizable.rememberJoinStrategyAsBest(bestAccessPath);
        }
        if (this.requiredRowOrdering != null && (this.joinPosition == 0 || this.optimizableList.getOptimizable(this.proposedJoinOrder[this.joinPosition - 1]).considerSortAvoidancePath()) && this.requiredRowOrdering.sortRequired(this.currentRowOrdering, this.assignedTableMap, this.optimizableList, this.proposedJoinOrder) == 3) {
            final AccessPath bestSortAvoidancePath = optimizable.getBestSortAvoidancePath();
            final CostEstimate costEstimate3 = bestSortAvoidancePath.getCostEstimate();
            if (costEstimate3 == null || costEstimate3.isUninitialized() || estimateTotalCost.compare(costEstimate3) < 0.0) {
                bestSortAvoidancePath.setConglomerateDescriptor(conglomerateDescriptor);
                bestSortAvoidancePath.setCostEstimate(estimateTotalCost);
                bestSortAvoidancePath.setCoveringIndexScan(optimizable.isCoveringIndex(conglomerateDescriptor));
                bestSortAvoidancePath.setNonMatchingIndexScan(list == null || !list.useful(optimizable, conglomerateDescriptor));
                bestSortAvoidancePath.setLockMode(optimizable.getCurrentAccessPath().getLockMode());
                optimizable.rememberJoinStrategyAsBest(bestSortAvoidancePath);
                optimizable.rememberSortAvoidancePath();
                this.currentRowOrdering.copy(this.bestRowOrdering);
            }
        }
    }
    
    public void considerCost(final Optimizable optimizable, final OptimizablePredicateList list, final CostEstimate costEstimate, final CostEstimate costEstimate2) throws StandardException {
        if (!optimizable.feasibleJoinStrategy(list, this)) {
            return;
        }
        optimizable.getCurrentAccessPath().setCostEstimate(costEstimate);
        if (!optimizable.memoryUsageOK(costEstimate.rowCount() / costEstimate2.rowCount(), this.maxMemoryPerTable)) {
            if (this.optimizerTrace) {
                this.trace(22, 0, 0, 0.0, null);
            }
            return;
        }
        final AccessPath bestAccessPath = optimizable.getBestAccessPath();
        final CostEstimate costEstimate3 = bestAccessPath.getCostEstimate();
        if (costEstimate3 == null || costEstimate3.isUninitialized() || costEstimate.compare(costEstimate3) <= 0.0) {
            bestAccessPath.setCostEstimate(costEstimate);
            optimizable.rememberJoinStrategyAsBest(bestAccessPath);
        }
        if (this.requiredRowOrdering != null && (this.joinPosition == 0 || this.optimizableList.getOptimizable(this.proposedJoinOrder[this.joinPosition - 1]).considerSortAvoidancePath()) && this.requiredRowOrdering.sortRequired(this.currentRowOrdering, this.assignedTableMap, this.optimizableList, this.proposedJoinOrder) == 3) {
            final AccessPath bestSortAvoidancePath = optimizable.getBestSortAvoidancePath();
            final CostEstimate costEstimate4 = bestSortAvoidancePath.getCostEstimate();
            if (costEstimate4 == null || costEstimate4.isUninitialized() || costEstimate.compare(costEstimate4) < 0.0) {
                bestSortAvoidancePath.setCostEstimate(costEstimate);
                optimizable.rememberJoinStrategyAsBest(bestSortAvoidancePath);
                optimizable.rememberSortAvoidancePath();
                this.currentRowOrdering.copy(this.bestRowOrdering);
            }
        }
    }
    
    public DataDictionary getDataDictionary() {
        return this.dDictionary;
    }
    
    public void modifyAccessPaths() throws StandardException {
        if (this.optimizerTrace) {
            this.trace(7, 0, 0, 0.0, null);
        }
        if (!this.foundABestPlan) {
            if (this.optimizerTrace) {
                this.trace(6, 0, 0, 0.0, null);
            }
            throw StandardException.newException("42Y69");
        }
        this.optimizableList.reOrder(this.bestJoinOrder);
        final JBitSet set = new JBitSet(this.numOptimizables);
        for (int i = 0; i < this.numOptimizables; ++i) {
            final Optimizable optimizable = this.optimizableList.getOptimizable(i);
            set.or(optimizable.getReferencedTableMap());
            this.pushPredicates(optimizable, set);
            this.optimizableList.setOptimizable(i, optimizable.modifyAccessPath(set));
        }
    }
    
    public CostEstimate newCostEstimate() {
        return new CostEstimateImpl();
    }
    
    public CostEstimate getOptimizedCost() {
        return this.bestCost;
    }
    
    public CostEstimate getFinalCost() {
        if (this.finalCostEstimate != null) {
            return this.finalCostEstimate;
        }
        this.finalCostEstimate = this.getNewCostEstimate(0.0, 0.0, 0.0);
        for (int i = 0; i < this.bestJoinOrder.length; ++i) {
            final CostEstimate costEstimate = this.optimizableList.getOptimizable(this.bestJoinOrder[i]).getTrulyTheBestAccessPath().getCostEstimate();
            this.finalCostEstimate.setCost(this.finalCostEstimate.getEstimatedCost() + costEstimate.getEstimatedCost(), costEstimate.rowCount(), costEstimate.singleScanRowCount());
        }
        return this.finalCostEstimate;
    }
    
    public void setOuterRows(final double n) {
        this.outermostCostEstimate.setCost(this.outermostCostEstimate.getEstimatedCost(), n, this.outermostCostEstimate.singleScanRowCount());
    }
    
    public int tableLockThreshold() {
        return this.tableLockThreshold;
    }
    
    public int getNumberOfJoinStrategies() {
        return this.joinStrategies.length;
    }
    
    public JoinStrategy getJoinStrategy(final int n) {
        return this.joinStrategies[n];
    }
    
    public JoinStrategy getJoinStrategy(final String s) {
        JoinStrategy joinStrategy = null;
        final String sqlToUpperCase = StringUtil.SQLToUpperCase(s);
        for (int i = 0; i < this.joinStrategies.length; ++i) {
            if (sqlToUpperCase.equals(this.joinStrategies[i].getName())) {
                joinStrategy = this.joinStrategies[i];
            }
        }
        return joinStrategy;
    }
    
    public double uniqueJoinWithOuterTable(final OptimizablePredicateList list) throws StandardException {
        double n = -1.0;
        double n2 = 1.0;
        final double rowCount = this.currentCost.rowCount();
        if (list != null) {
            for (int i = this.joinPosition - 1; i >= 0; --i) {
                final Optimizable optimizable = this.optimizableList.getOptimizable(this.proposedJoinOrder[i]);
                if (optimizable.uniqueJoin(list) > 0.0) {
                    n2 *= optimizable.uniqueJoin(list);
                }
            }
        }
        if (n2 != 1.0) {
            n = n2 / rowCount;
        }
        return n;
    }
    
    private boolean isPushable(final OptimizablePredicate optimizablePredicate) {
        return !optimizablePredicate.hasSubquery();
    }
    
    private CostEstimate estimateTotalCost(final OptimizablePredicateList list, final ConglomerateDescriptor conglomerateDescriptor, final CostEstimate costEstimate, final Optimizable optimizable) throws StandardException {
        return optimizable.estimateCost(list, conglomerateDescriptor, costEstimate, this, this.currentRowOrdering);
    }
    
    public int getLevel() {
        return 1;
    }
    
    public CostEstimateImpl getNewCostEstimate(final double n, final double n2, final double n3) {
        return new CostEstimateImpl(n, n2, n3);
    }
    
    public void trace(final int n, final int n2, final int n3, final double n4, final Object o) {
    }
    
    public boolean useStatistics() {
        return this.useStatistics && this.optimizableList.useStatistics();
    }
    
    protected void updateBestPlanMaps(final short n, final Object o) throws StandardException {
        if (this.numOptimizables > 1) {
            int[] value = null;
            if (n == 0) {
                if (this.savedJoinOrders != null) {
                    this.savedJoinOrders.remove(o);
                    if (this.savedJoinOrders.size() == 0) {
                        this.savedJoinOrders = null;
                    }
                }
            }
            else if (n == 1) {
                if (this.savedJoinOrders == null) {
                    this.savedJoinOrders = new HashMap();
                }
                else {
                    value = this.savedJoinOrders.get(o);
                }
                if (value == null) {
                    value = new int[this.numOptimizables];
                }
                for (int i = 0; i < this.bestJoinOrder.length; ++i) {
                    value[i] = this.bestJoinOrder[i];
                }
                this.savedJoinOrders.put(o, value);
            }
            else if (this.savedJoinOrders != null) {
                final int[] array = this.savedJoinOrders.get(o);
                if (array != null) {
                    for (int j = 0; j < array.length; ++j) {
                        this.bestJoinOrder[j] = array[j];
                    }
                }
            }
        }
        for (int k = this.optimizableList.size() - 1; k >= 0; --k) {
            this.optimizableList.getOptimizable(k).updateBestPlanMap(n, o);
        }
    }
    
    protected void addScopedPredicatesToList(final PredicateList list) throws StandardException {
        if (list == null || list == this.predicateList) {
            return;
        }
        if (this.predicateList == null) {
            this.predicateList = new PredicateList();
        }
        for (int i = this.predicateList.size() - 1; i >= 0; --i) {
            if (((Predicate)this.predicateList.getOptPredicate(i)).isScopedForPush()) {
                this.predicateList.removeOptPredicate(i);
            }
        }
        for (int j = list.size() - 1; j >= 0; --j) {
            final Predicate predicate = (Predicate)list.getOptPredicate(j);
            if (predicate.isScopedToSourceResultSet()) {
                predicate.clearScanFlags();
                this.predicateList.addOptPredicate(predicate);
                list.removeOptPredicate(j);
            }
        }
    }
}
