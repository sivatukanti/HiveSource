// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.impl.services.daemon.IndexStatisticsDaemonImpl;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.sql.compile.RequiredRowOrdering;
import java.util.HashSet;
import java.util.Set;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptorList;
import org.apache.derby.iapi.sql.compile.ExpressionClassBuilderInterface;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.services.io.FormatableArrayHolder;
import org.apache.derby.iapi.services.io.FormatableIntHolder;
import org.apache.derby.iapi.util.PropertyUtil;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.util.ReuseFactory;
import java.util.Collections;
import org.apache.derby.iapi.services.context.ContextManager;
import java.util.Iterator;
import org.apache.derby.iapi.sql.dictionary.ViewDescriptor;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.store.access.TransactionController;
import java.util.Properties;
import java.util.List;
import org.apache.derby.iapi.store.access.StoreCostController;
import org.apache.derby.iapi.sql.compile.JoinStrategy;
import org.apache.derby.iapi.store.access.StoreCostResult;
import org.apache.derby.iapi.types.DataValueDescriptor;
import java.util.Enumeration;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.compile.OptimizablePredicate;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import org.apache.derby.iapi.sql.compile.AccessPath;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.sql.compile.RowOrdering;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.sql.compile.Optimizer;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;

public class FromBaseTable extends FromTable
{
    static final int UNSET = -1;
    private boolean hasCheckedIndexStats;
    TableName tableName;
    TableDescriptor tableDescriptor;
    ConglomerateDescriptor baseConglomerateDescriptor;
    ConglomerateDescriptor[] conglomDescs;
    int updateOrDelete;
    int bulkFetch;
    boolean bulkFetchTurnedOff;
    boolean multiProbing;
    private double singleScanRowCount;
    private FormatableBitSet referencedCols;
    private ResultColumnList templateColumns;
    private String[] columnNames;
    private boolean specialMaxScan;
    private boolean distinctScan;
    private boolean raDependentScan;
    private String raParentResultSetId;
    private long fkIndexConglomId;
    private int[] fkColArray;
    PredicateList baseTableRestrictionList;
    PredicateList nonBaseTableRestrictionList;
    PredicateList restrictionList;
    PredicateList storeRestrictionList;
    PredicateList nonStoreRestrictionList;
    PredicateList requalificationRestrictionList;
    public static final int UPDATE = 1;
    public static final int DELETE = 2;
    private boolean existsBaseTable;
    private boolean isNotExists;
    private JBitSet dependencyMap;
    private boolean getUpdateLocks;
    private boolean authorizeSYSUSERS;
    private boolean gotRowCount;
    private long rowCount;
    
    public FromBaseTable() {
        this.bulkFetch = -1;
        this.multiProbing = false;
        this.gotRowCount = false;
        this.rowCount = 0L;
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4) {
        if (o3 instanceof Integer) {
            this.init(o2, null);
            this.tableName = (TableName)o;
            this.updateOrDelete = (int)o3;
            this.resultColumns = (ResultColumnList)o4;
        }
        else {
            this.init(o2, o4);
            this.tableName = (TableName)o;
            this.resultColumns = (ResultColumnList)o3;
        }
        this.setOrigTableName(this.tableName);
        this.templateColumns = this.resultColumns;
    }
    
    public boolean LOJ_reorderable(final int n) throws StandardException {
        return false;
    }
    
    public JBitSet LOJgetReferencedTables(final int n) throws StandardException {
        final JBitSet set = new JBitSet(n);
        this.fillInReferencedTableMap(set);
        return set;
    }
    
    public boolean nextAccessPath(final Optimizer optimizer, final OptimizablePredicateList list, final RowOrdering rowOrdering) throws StandardException {
        final String userSpecifiedIndexName = this.getUserSpecifiedIndexName();
        final AccessPath currentAccessPath = this.getCurrentAccessPath();
        ConglomerateDescriptor conglomerateDescriptor = currentAccessPath.getConglomerateDescriptor();
        optimizer.trace(42, (list == null) ? 0 : list.size(), 0, 0.0, this.getExposedName());
        rowOrdering.removeOptimizable(this.getTableNumber());
        if (userSpecifiedIndexName != null) {
            if (conglomerateDescriptor != null) {
                if (!super.nextAccessPath(optimizer, list, rowOrdering)) {
                    conglomerateDescriptor = null;
                }
            }
            else {
                optimizer.trace(39, this.tableNumber, 0, 0.0, userSpecifiedIndexName);
                if (StringUtil.SQLToUpperCase(userSpecifiedIndexName).equals("NULL")) {
                    conglomerateDescriptor = this.tableDescriptor.getConglomerateDescriptor(this.tableDescriptor.getHeapConglomerateId());
                }
                else {
                    this.getConglomDescs();
                    for (int i = 0; i < this.conglomDescs.length; ++i) {
                        conglomerateDescriptor = this.conglomDescs[i];
                        final String conglomerateName = conglomerateDescriptor.getConglomerateName();
                        if (conglomerateName != null && conglomerateName.equals(userSpecifiedIndexName)) {
                            break;
                        }
                    }
                }
                if (!super.nextAccessPath(optimizer, list, rowOrdering)) {}
            }
        }
        else if (conglomerateDescriptor != null) {
            if (!super.nextAccessPath(optimizer, list, rowOrdering)) {
                conglomerateDescriptor = this.getNextConglom(conglomerateDescriptor);
                this.resetJoinStrategies(optimizer);
                if (!super.nextAccessPath(optimizer, list, rowOrdering)) {}
            }
        }
        else {
            conglomerateDescriptor = this.getFirstConglom();
            if (!super.nextAccessPath(optimizer, list, rowOrdering)) {}
        }
        if (conglomerateDescriptor == null) {
            optimizer.trace(30, this.tableNumber, 0, 0.0, null);
        }
        else {
            conglomerateDescriptor.setColumnNames(this.columnNames);
            optimizer.trace(31, this.tableNumber, 0, 0.0, conglomerateDescriptor);
        }
        if (conglomerateDescriptor != null) {
            if (!conglomerateDescriptor.isIndex()) {
                if (!this.isOneRowResultSet(list)) {
                    optimizer.trace(33, (list == null) ? 0 : list.size(), 0, 0.0, null);
                    rowOrdering.addUnorderedOptimizable(this);
                }
                else {
                    optimizer.trace(32, 0, 0, 0.0, null);
                }
            }
            else {
                final IndexRowGenerator indexDescriptor = conglomerateDescriptor.getIndexDescriptor();
                final int[] baseColumnPositions = indexDescriptor.baseColumnPositions();
                final boolean[] ascending = indexDescriptor.isAscending();
                for (int j = 0; j < baseColumnPositions.length; ++j) {
                    if (!rowOrdering.orderedOnColumn(ascending[j] ? 1 : 2, this.getTableNumber(), baseColumnPositions[j])) {
                        rowOrdering.nextOrderPosition(ascending[j] ? 1 : 2);
                        rowOrdering.addOrderedColumn(ascending[j] ? 1 : 2, this.getTableNumber(), baseColumnPositions[j]);
                    }
                }
            }
        }
        currentAccessPath.setConglomerateDescriptor(conglomerateDescriptor);
        return conglomerateDescriptor != null;
    }
    
    protected boolean canBeOrdered() {
        return true;
    }
    
    public CostEstimate optimizeIt(final Optimizer optimizer, final OptimizablePredicateList list, final CostEstimate costEstimate, final RowOrdering rowOrdering) throws StandardException {
        optimizer.costOptimizable(this, this.tableDescriptor, this.getCurrentAccessPath().getConglomerateDescriptor(), list, costEstimate);
        return this.getCurrentAccessPath().getCostEstimate();
    }
    
    public TableDescriptor getTableDescriptor() {
        return this.tableDescriptor;
    }
    
    public boolean isMaterializable() throws StandardException {
        return true;
    }
    
    public boolean pushOptPredicate(final OptimizablePredicate optimizablePredicate) throws StandardException {
        this.restrictionList.addPredicate((Predicate)optimizablePredicate);
        return true;
    }
    
    public void pullOptPredicates(final OptimizablePredicateList list) throws StandardException {
        for (int i = this.restrictionList.size() - 1; i >= 0; --i) {
            list.addOptPredicate(this.restrictionList.getOptPredicate(i));
            this.restrictionList.removeOptPredicate(i);
        }
    }
    
    public boolean isCoveringIndex(final ConglomerateDescriptor conglomerateDescriptor) throws StandardException {
        boolean b = true;
        if (!conglomerateDescriptor.isIndex()) {
            return false;
        }
        final int[] baseColumnPositions = conglomerateDescriptor.getIndexDescriptor().baseColumnPositions();
        for (int size = this.resultColumns.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.resultColumns.elementAt(i);
            if (resultColumn.isReferenced()) {
                if (!(resultColumn.getExpression() instanceof ConstantNode)) {
                    b = false;
                    final int columnPosition = resultColumn.getColumnPosition();
                    for (int j = 0; j < baseColumnPositions.length; ++j) {
                        if (columnPosition == baseColumnPositions[j]) {
                            b = true;
                            break;
                        }
                    }
                    if (!b) {
                        break;
                    }
                }
            }
        }
        return b;
    }
    
    public void verifyProperties(final DataDictionary dataDictionary) throws StandardException {
        if (this.tableProperties == null) {
            return;
        }
        boolean b = false;
        int n = 0;
        ConstraintDescriptor constraintDescriptorByName = null;
        final Enumeration<Object> keys = this.tableProperties.keys();
        StringUtil.SQLEqualsIgnoreCase(this.tableDescriptor.getSchemaName(), "SYS");
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            final String s = (String)this.tableProperties.get(key);
            if (key.equals("index")) {
                if (n != 0) {
                    throw StandardException.newException("42Y50", this.getBaseTableName());
                }
                b = true;
                if (StringUtil.SQLToUpperCase(s).equals("NULL")) {
                    continue;
                }
                ConglomerateDescriptor conglomerateDescriptor = null;
                final ConglomerateDescriptor[] conglomerateDescriptors = this.tableDescriptor.getConglomerateDescriptors();
                for (int i = 0; i < conglomerateDescriptors.length; ++i) {
                    conglomerateDescriptor = conglomerateDescriptors[i];
                    final String conglomerateName = conglomerateDescriptor.getConglomerateName();
                    if (conglomerateName != null && conglomerateName.equals(s)) {
                        break;
                    }
                    conglomerateDescriptor = null;
                }
                if (conglomerateDescriptor == null) {
                    throw StandardException.newException("42Y46", s, this.getBaseTableName());
                }
                this.getCompilerContext().createDependency(conglomerateDescriptor);
            }
            else if (key.equals("constraint")) {
                if (b) {
                    throw StandardException.newException("42Y50", this.getBaseTableName());
                }
                n = 1;
                if (StringUtil.SQLToUpperCase(s).equals("NULL")) {
                    continue;
                }
                constraintDescriptorByName = dataDictionary.getConstraintDescriptorByName(this.tableDescriptor, null, s, false);
                if (constraintDescriptorByName == null || !constraintDescriptorByName.hasBackingIndex()) {
                    throw StandardException.newException("42Y48", s, this.getBaseTableName());
                }
                this.getCompilerContext().createDependency(constraintDescriptorByName);
            }
            else if (key.equals("joinStrategy")) {
                this.userSpecifiedJoinStrategy = StringUtil.SQLToUpperCase(s);
            }
            else if (key.equals("hashInitialCapacity")) {
                this.initialCapacity = this.getIntProperty(s, key);
                if (this.initialCapacity <= 0) {
                    throw StandardException.newException("42Y59", String.valueOf(this.initialCapacity));
                }
                continue;
            }
            else if (key.equals("hashLoadFactor")) {
                try {
                    this.loadFactor = Float.parseFloat(s);
                }
                catch (NumberFormatException ex) {
                    throw StandardException.newException("42Y58", s, key);
                }
                if (this.loadFactor <= 0.0 || this.loadFactor > 1.0) {
                    throw StandardException.newException("42Y60", s);
                }
                continue;
            }
            else if (key.equals("hashMaxCapacity")) {
                this.maxCapacity = this.getIntProperty(s, key);
                if (this.maxCapacity <= 0) {
                    throw StandardException.newException("42Y61", String.valueOf(this.maxCapacity));
                }
                continue;
            }
            else {
                if (!key.equals("bulkFetch")) {
                    throw StandardException.newException("42Y44", key, "index, constraint, joinStrategy");
                }
                this.bulkFetch = this.getIntProperty(s, key);
                if (this.bulkFetch <= 0) {
                    throw StandardException.newException("42Y64", String.valueOf(this.bulkFetch));
                }
                if (this.forUpdate()) {
                    throw StandardException.newException("42Y66");
                }
                continue;
            }
        }
        if (n != 0 && constraintDescriptorByName != null) {
            final String conglomerateName2 = dataDictionary.getConglomerateDescriptor(constraintDescriptorByName.getConglomerateId()).getConglomerateName();
            this.tableProperties.remove("constraint");
            this.tableProperties.put("index", conglomerateName2);
        }
    }
    
    public String getBaseTableName() {
        return this.tableName.getTableName();
    }
    
    public void startOptimizing(final Optimizer optimizer, final RowOrdering rowOrdering) {
        final AccessPath currentAccessPath = this.getCurrentAccessPath();
        final AccessPath bestAccessPath = this.getBestAccessPath();
        final AccessPath bestSortAvoidancePath = this.getBestSortAvoidancePath();
        currentAccessPath.setConglomerateDescriptor(null);
        bestAccessPath.setConglomerateDescriptor(null);
        bestSortAvoidancePath.setConglomerateDescriptor(null);
        currentAccessPath.setCoveringIndexScan(false);
        bestAccessPath.setCoveringIndexScan(false);
        bestSortAvoidancePath.setCoveringIndexScan(false);
        currentAccessPath.setLockMode(0);
        bestAccessPath.setLockMode(0);
        bestSortAvoidancePath.setLockMode(0);
        final CostEstimate costEstimate = this.getCostEstimate(optimizer);
        currentAccessPath.setCostEstimate(costEstimate);
        costEstimate.setCost(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        super.startOptimizing(optimizer, rowOrdering);
    }
    
    public int convertAbsoluteToRelativeColumnPosition(final int n) {
        return this.mapAbsoluteToRelativeColumnPosition(n);
    }
    
    public CostEstimate estimateCost(final OptimizablePredicateList list, final ConglomerateDescriptor conglomerateDescriptor, final CostEstimate costEstimate, final Optimizer optimizer, final RowOrdering rowOrdering) throws StandardException {
        boolean statisticsExist = false;
        boolean statisticsExist2 = false;
        PredicateList list2 = null;
        if (optimizer.useStatistics() && list != null) {
            statisticsExist2 = this.tableDescriptor.statisticsExist(conglomerateDescriptor);
            statisticsExist = this.tableDescriptor.statisticsExist(null);
            list2 = new PredicateList();
            list.copyPredicatesToOtherList(list2);
            if (!this.hasCheckedIndexStats) {
                this.hasCheckedIndexStats = true;
                if (this.qualifiesForStatisticsUpdateCheck(this.tableDescriptor)) {
                    this.tableDescriptor.markForIndexStatsUpdate(this.baseRowCount());
                }
            }
        }
        final AccessPath currentAccessPath = this.getCurrentAccessPath();
        final JoinStrategy joinStrategy = currentAccessPath.getJoinStrategy();
        optimizer.trace(38, this.tableNumber, 0, 0.0, conglomerateDescriptor);
        final double uniqueJoinWithOuterTable = optimizer.uniqueJoinWithOuterTable(list);
        final boolean oneRowResultSet = this.isOneRowResultSet(list);
        this.baseTableRestrictionList.removeAllElements();
        joinStrategy.getBasePredicates(list, this.baseTableRestrictionList, this);
        final StoreCostController storeCostController = this.getStoreCostController(conglomerateDescriptor);
        final CostEstimate scratchCostEstimate = this.getScratchCostEstimate(optimizer);
        if (this.isOneRowResultSet(conglomerateDescriptor, this.baseTableRestrictionList)) {
            rowOrdering.optimizableAlwaysOrdered(this);
            this.singleScanRowCount = 1.0;
            final double fetchFromFullKeyCost = storeCostController.getFetchFromFullKeyCost(null, 0);
            optimizer.trace(40, this.tableNumber, 0, fetchFromFullKeyCost, null);
            scratchCostEstimate.setCost(fetchFromFullKeyCost, 1.0, 1.0);
            double estimatedCost = scratchCostEstimate.getEstimatedCost();
            if (joinStrategy.multiplyBaseCostByOuterRows()) {
                estimatedCost *= costEstimate.rowCount();
            }
            scratchCostEstimate.setCost(estimatedCost, scratchCostEstimate.rowCount() * costEstimate.rowCount(), scratchCostEstimate.singleScanRowCount());
            boolean b = true;
            for (int i = 0; i < list.size(); ++i) {
                final OptimizablePredicate optPredicate = list.getOptPredicate(i);
                if (!optPredicate.isStartKey() && !optPredicate.isStopKey()) {
                    break;
                }
                if (!optPredicate.getReferencedMap().hasSingleBitSet()) {
                    b = false;
                    break;
                }
            }
            if (b) {
                currentAccessPath.setLockMode(6);
                optimizer.trace(37, 0, 0, 0.0, null);
            }
            else {
                this.setLockingBasedOnThreshold(optimizer, scratchCostEstimate.rowCount());
            }
            optimizer.trace(23, this.tableNumber, 0, costEstimate.rowCount(), scratchCostEstimate);
            if (conglomerateDescriptor.isIndex() && !this.isCoveringIndex(conglomerateDescriptor)) {
                final double n = this.getBaseCostController().getFetchFromRowLocationCost(null, 0) * scratchCostEstimate.rowCount();
                scratchCostEstimate.setEstimatedCost(scratchCostEstimate.getEstimatedCost() + n);
                optimizer.trace(36, this.tableNumber, 0, n, null);
            }
        }
        else {
            double n2 = 1.0;
            double n3 = 1.0;
            double n4 = 1.0;
            double n5 = 1.0;
            double selectivityForConglomerate = 1.0;
            double selectivity = 1.0;
            final int n6 = 0;
            int n7 = 0;
            int n8 = 0;
            int n9 = 0;
            int n10 = 0;
            int n11 = 0;
            int n12 = 0;
            boolean b2 = true;
            boolean b3 = false;
            int n13 = 0;
            int n14 = 0;
            int size;
            if (list != null) {
                size = this.baseTableRestrictionList.size();
            }
            else {
                size = 0;
            }
            int n15 = 0;
            ColumnReference columnOperand = null;
            for (int j = 0; j < size; ++j) {
                final OptimizablePredicate optPredicate2 = this.baseTableRestrictionList.getOptPredicate(j);
                final boolean startKey = optPredicate2.isStartKey();
                final boolean stopKey = optPredicate2.isStopKey();
                if (startKey || stopKey) {
                    b3 = true;
                    if (!optPredicate2.getReferencedMap().hasSingleBitSet()) {
                        b2 = false;
                    }
                    final boolean compareWithKnownConstant = optPredicate2.compareWithKnownConstant(this, true);
                    if (startKey) {
                        if (compareWithKnownConstant && n10 == 0) {
                            ++n13;
                            if (list2 != null) {
                                list2.removeOptPredicate(optPredicate2);
                            }
                        }
                        else {
                            n10 = 1;
                        }
                    }
                    if (stopKey) {
                        if (compareWithKnownConstant && n11 == 0) {
                            ++n14;
                            if (list2 != null) {
                                list2.removeOptPredicate(optPredicate2);
                            }
                        }
                        else {
                            n11 = 1;
                        }
                    }
                    if (n10 != 0 || n11 != 0) {
                        if (!this.baseTableRestrictionList.isRedundantPredicate(j)) {
                            if (startKey && stopKey) {
                                ++n15;
                            }
                            if (optPredicate2.getIndexPosition() == 0) {
                                n2 *= optPredicate2.selectivity(this);
                                if (n12 == 0) {
                                    final ValueNode leftOperand = ((Predicate)optPredicate2).getAndNode().getLeftOperand();
                                    if (leftOperand instanceof BinaryRelationalOperatorNode) {
                                        columnOperand = ((BinaryRelationalOperatorNode)leftOperand).getColumnOperand(this);
                                    }
                                    n12 = 1;
                                }
                            }
                            else {
                                n3 *= optPredicate2.selectivity(this);
                                ++n7;
                            }
                        }
                    }
                }
                else if (!this.baseTableRestrictionList.isRedundantPredicate(j)) {
                    if (optPredicate2 instanceof Predicate) {
                        final ValueNode leftOperand2 = ((Predicate)optPredicate2).getAndNode().getLeftOperand();
                        if (columnOperand != null && leftOperand2 instanceof LikeEscapeOperatorNode) {
                            final LikeEscapeOperatorNode likeEscapeOperatorNode = (LikeEscapeOperatorNode)leftOperand2;
                            if (likeEscapeOperatorNode.getLeftOperand().requiresTypeFromContext()) {
                                final ValueNode receiver = likeEscapeOperatorNode.getReceiver();
                                if (receiver instanceof ColumnReference) {
                                    final ColumnReference columnReference = (ColumnReference)receiver;
                                    if (columnReference.getTableNumber() == columnOperand.getTableNumber() && columnReference.getColumnNumber() == columnOperand.getColumnNumber()) {
                                        n2 *= 0.2;
                                    }
                                }
                            }
                        }
                    }
                    if (optPredicate2.isQualifier()) {
                        n4 *= optPredicate2.selectivity(this);
                        ++n8;
                    }
                    else {
                        n5 *= optPredicate2.selectivity(this);
                        ++n9;
                    }
                    n10 = 1;
                    n11 = 1;
                }
            }
            if (list2 != null) {
                selectivity = list2.selectivity(this);
                if (selectivity == -1.0) {
                    selectivity = 1.0;
                }
            }
            if (n12 != 0 && n15 > 0) {
                if (statisticsExist2) {
                    selectivityForConglomerate = this.tableDescriptor.selectivityForConglomerate(conglomerateDescriptor, n15);
                }
                else if (conglomerateDescriptor.isIndex()) {
                    final IndexRowGenerator indexDescriptor = conglomerateDescriptor.getIndexDescriptor();
                    if (indexDescriptor.isUnique() && indexDescriptor.numberOfOrderedColumns() == 1 && n15 == 1) {
                        selectivityForConglomerate = 1.0 / this.baseRowCount();
                    }
                }
            }
            final double n16 = n5 * joinStrategy.nonBasePredicateSelectivity(this, list);
            DataValueDescriptor[] array;
            if (n13 > 0) {
                array = new DataValueDescriptor[n13];
            }
            else {
                array = null;
            }
            DataValueDescriptor[] array2;
            if (n14 > 0) {
                array2 = new DataValueDescriptor[n14];
            }
            else {
                array2 = null;
            }
            int n17 = 0;
            int n18 = 0;
            int n19 = 0;
            int n20 = 0;
            BinaryListOperatorNode sourceInList = null;
            for (int k = 0; k < size; ++k) {
                final OptimizablePredicate optPredicate3 = this.baseTableRestrictionList.getOptPredicate(k);
                final boolean startKey2 = optPredicate3.isStartKey();
                final boolean stopKey2 = optPredicate3.isStopKey();
                if (startKey2 || stopKey2) {
                    sourceInList = ((Predicate)optPredicate3).getSourceInList(true);
                    final boolean compareWithKnownConstant2 = optPredicate3.compareWithKnownConstant(this, true);
                    if (startKey2) {
                        if (compareWithKnownConstant2 && n19 == 0) {
                            array[n17] = optPredicate3.getCompareValue(this);
                            ++n17;
                        }
                        else {
                            n19 = 1;
                        }
                    }
                    if (stopKey2) {
                        if (compareWithKnownConstant2 && n20 == 0) {
                            array2[n18] = optPredicate3.getCompareValue(this);
                            ++n18;
                        }
                        else {
                            n20 = 1;
                        }
                    }
                }
                else {
                    n19 = 1;
                    n20 = 1;
                }
            }
            int startOperator;
            int stopOperator;
            if (this.baseTableRestrictionList != null) {
                startOperator = this.baseTableRestrictionList.startOperator(this);
                stopOperator = this.baseTableRestrictionList.stopOperator(this);
            }
            else {
                startOperator = 0;
                stopOperator = 0;
            }
            storeCostController.getScanCost(joinStrategy.scanCostType(), (array != null || array2 != null) ? this.baseRowCount() : (this.baseRowCount() + 5L), 1, this.forUpdate(), null, this.getRowTemplate(conglomerateDescriptor, this.getBaseCostController()), array, startOperator, array2, stopOperator, false, 0, scratchCostEstimate);
            double fetchFromFullKeyCost2 = 0.0;
            if (conglomerateDescriptor.isIndex()) {
                fetchFromFullKeyCost2 = storeCostController.getFetchFromFullKeyCost(null, 0);
                if (oneRowResultSet && scratchCostEstimate.rowCount() <= 1.0) {
                    scratchCostEstimate.setCost(scratchCostEstimate.getEstimatedCost() * 2.0, scratchCostEstimate.rowCount() + 2.0, scratchCostEstimate.singleScanRowCount() + 2.0);
                }
            }
            optimizer.trace(53, this.tableNumber, 0, 0.0, conglomerateDescriptor);
            optimizer.trace(54, this.tableNumber, 0, 0.0, scratchCostEstimate);
            optimizer.trace(55, n6, 0, n2, null);
            optimizer.trace(56, n7, 0, n3, null);
            optimizer.trace(59, n15, 0, selectivityForConglomerate, null);
            optimizer.trace(57, n8, 0, n4, null);
            optimizer.trace(58, n9, 0, n16, null);
            final double rowCount = scratchCostEstimate.rowCount();
            if (selectivityForConglomerate != 1.0) {
                scratchCostEstimate.setCost(this.scanCostAfterSelectivity(scratchCostEstimate.getEstimatedCost(), fetchFromFullKeyCost2, selectivityForConglomerate, oneRowResultSet), scratchCostEstimate.rowCount() * selectivityForConglomerate, scratchCostEstimate.singleScanRowCount() * selectivityForConglomerate);
                optimizer.trace(62, this.tableNumber, 0, 0.0, scratchCostEstimate);
            }
            else {
                if (n2 != 1.0) {
                    scratchCostEstimate.setCost(this.scanCostAfterSelectivity(scratchCostEstimate.getEstimatedCost(), fetchFromFullKeyCost2, n2, oneRowResultSet), scratchCostEstimate.rowCount() * n2, scratchCostEstimate.singleScanRowCount() * n2);
                    optimizer.trace(41, this.tableNumber, 0, 0.0, scratchCostEstimate);
                }
                if (n3 != 1.0) {
                    scratchCostEstimate.setCost(scratchCostEstimate.getEstimatedCost(), scratchCostEstimate.rowCount() * n3, scratchCostEstimate.singleScanRowCount() * n3);
                    optimizer.trace(45, this.tableNumber, 0, 0.0, scratchCostEstimate);
                }
            }
            if (sourceInList != null) {
                final int size2 = sourceInList.getRightOperandList().size();
                final double n21 = scratchCostEstimate.rowCount() * size2;
                final double n22 = scratchCostEstimate.singleScanRowCount() * size2;
                scratchCostEstimate.setCost(scratchCostEstimate.getEstimatedCost() * size2, (n21 > rowCount) ? rowCount : n21, (n22 > rowCount) ? rowCount : n22);
            }
            if (!b3) {
                currentAccessPath.setLockMode(7);
                optimizer.trace(35, 0, 0, 0.0, null);
            }
            else {
                double rowCount2 = scratchCostEstimate.rowCount();
                if (!b2 && joinStrategy.multiplyBaseCostByOuterRows()) {
                    final double n23 = (double)this.baseRowCount();
                    if (n23 > 0.0) {
                        rowCount2 = n23 * (1.0 - Math.pow(1.0 - scratchCostEstimate.rowCount() / n23, costEstimate.rowCount()));
                    }
                    else {
                        rowCount2 = optimizer.tableLockThreshold() + 1;
                    }
                }
                this.setLockingBasedOnThreshold(optimizer, rowCount2);
            }
            if (conglomerateDescriptor.isIndex() && !this.isCoveringIndex(conglomerateDescriptor)) {
                final double fetchFromRowLocationCost = this.getBaseCostController().getFetchFromRowLocationCost(null, 0);
                double b4 = scratchCostEstimate.rowCount();
                if (oneRowResultSet) {
                    b4 = Math.max(1.0, b4);
                }
                scratchCostEstimate.setEstimatedCost(scratchCostEstimate.getEstimatedCost() + fetchFromRowLocationCost * b4);
                optimizer.trace(48, this.tableNumber, 0, 0.0, scratchCostEstimate);
            }
            if (n4 != 1.0) {
                scratchCostEstimate.setCost(scratchCostEstimate.getEstimatedCost(), scratchCostEstimate.rowCount() * n4, scratchCostEstimate.singleScanRowCount() * n4);
                optimizer.trace(46, this.tableNumber, 0, 0.0, scratchCostEstimate);
            }
            this.singleScanRowCount = scratchCostEstimate.singleScanRowCount();
            double estimatedCost2 = scratchCostEstimate.getEstimatedCost();
            final double rowCount3 = scratchCostEstimate.rowCount();
            if (joinStrategy.multiplyBaseCostByOuterRows()) {
                estimatedCost2 *= costEstimate.rowCount();
            }
            double rowCount4 = rowCount3 * costEstimate.rowCount();
            final double n24 = rowCount * costEstimate.rowCount();
            if (oneRowResultSet && costEstimate.rowCount() < rowCount4) {
                rowCount4 = costEstimate.rowCount();
            }
            if (conglomerateDescriptor.isIndex() && b3 && !b2) {
                final double uniqueJoinWithOuterTable2 = optimizer.uniqueJoinWithOuterTable(this.baseTableRestrictionList);
                if (uniqueJoinWithOuterTable2 > 0.0) {
                    final double n25 = this.baseRowCount() / uniqueJoinWithOuterTable2;
                    if (rowCount4 > n25) {
                        estimatedCost2 *= n25 / rowCount4;
                    }
                }
            }
            if (uniqueJoinWithOuterTable > 0.0) {
                final double n26 = this.baseRowCount() / uniqueJoinWithOuterTable;
                if (rowCount4 > n26) {
                    rowCount4 = n26;
                }
            }
            scratchCostEstimate.setCost(estimatedCost2, rowCount4, scratchCostEstimate.singleScanRowCount());
            optimizer.trace(23, this.tableNumber, 0, costEstimate.rowCount(), scratchCostEstimate);
            double n27 = -1.0;
            double n28 = -1.0;
            if (this.existsBaseTable) {
                n28 = (n27 = 1.0);
            }
            else if (n16 != 1.0) {
                n27 = (oneRowResultSet ? scratchCostEstimate.rowCount() : (scratchCostEstimate.rowCount() * n16));
                n28 = scratchCostEstimate.singleScanRowCount() * n16;
            }
            if (n27 != -1.0) {
                scratchCostEstimate.setCost(scratchCostEstimate.getEstimatedCost(), n27, n28);
                optimizer.trace(47, this.tableNumber, 0, 0.0, scratchCostEstimate);
            }
            if (statisticsExist && !oneRowResultSet && selectivity != 1.0) {
                final double n29 = n24 * selectivity;
                optimizer.trace(61, 0, 0, selectivity, null);
                if (uniqueJoinWithOuterTable <= 0.0 || n29 <= this.baseRowCount() * uniqueJoinWithOuterTable) {
                    scratchCostEstimate.setCost(scratchCostEstimate.getEstimatedCost(), n29, this.existsBaseTable ? 1.0 : (n29 / costEstimate.rowCount()));
                    optimizer.trace(60, this.tableNumber, 0, 0.0, scratchCostEstimate);
                }
            }
        }
        joinStrategy.putBasePredicates(list, this.baseTableRestrictionList);
        return scratchCostEstimate;
    }
    
    private double scanCostAfterSelectivity(final double n, final double n2, double n3, final boolean b) throws StandardException {
        if (b) {
            final double n4 = (double)this.baseRowCount();
            if (n4 > 0.0) {
                final double n5 = 2.0 / n4;
                if (n5 > n3) {
                    n3 = n5;
                }
            }
        }
        double n6 = (n - n2) * n3;
        if (n6 < 0.0) {
            n6 = 0.0;
        }
        return n2 + n6;
    }
    
    private void setLockingBasedOnThreshold(final Optimizer optimizer, final double n) {
        this.getCurrentAccessPath().setLockMode(6);
    }
    
    public boolean isBaseTable() {
        return true;
    }
    
    public boolean forUpdate() {
        return this.updateOrDelete != 0 || this.cursorTargetTable || this.getUpdateLocks;
    }
    
    public int initialCapacity() {
        return this.initialCapacity;
    }
    
    public float loadFactor() {
        return this.loadFactor;
    }
    
    public boolean memoryUsageOK(final double n, final int n2) throws StandardException {
        return super.memoryUsageOK(this.singleScanRowCount, n2);
    }
    
    public boolean isTargetTable() {
        return this.updateOrDelete != 0;
    }
    
    public double uniqueJoin(final OptimizablePredicateList list) throws StandardException {
        double singleScanRowCount = -1.0;
        final PredicateList list2 = (PredicateList)list;
        final int numberOfColumns = this.getTableDescriptor().getNumberOfColumns();
        final int tableNumber = this.getTableNumber();
        final int[] array = new int[0];
        final JBitSet[] array2 = { new JBitSet(numberOfColumns + 1) };
        list2.checkTopPredicatesForEqualsConditions(tableNumber, null, array, array2, false);
        if (this.supersetOfUniqueIndex(array2)) {
            singleScanRowCount = this.getBestAccessPath().getCostEstimate().singleScanRowCount();
        }
        return singleScanRowCount;
    }
    
    public boolean isOneRowScan() throws StandardException {
        return !this.existsBaseTable && super.isOneRowScan();
    }
    
    public boolean legalJoinOrder(final JBitSet set) {
        return !this.existsBaseTable || set.contains(this.dependencyMap);
    }
    
    public String toString() {
        return "";
    }
    
    boolean getExistsBaseTable() {
        return this.existsBaseTable;
    }
    
    void setExistsBaseTable(final boolean existsBaseTable, final JBitSet dependencyMap, final boolean isNotExists) {
        this.existsBaseTable = existsBaseTable;
        this.isNotExists = isNotExists;
        if (existsBaseTable) {
            this.dependencyMap = dependencyMap;
        }
        else {
            this.dependencyMap = null;
        }
    }
    
    void clearDependency(final List list) {
        if (this.dependencyMap != null) {
            for (int i = 0; i < list.size(); ++i) {
                this.dependencyMap.clear(list.get(i));
            }
        }
    }
    
    public void setTableProperties(final Properties tableProperties) {
        this.tableProperties = tableProperties;
    }
    
    public ResultSetNode bindNonVTITables(final DataDictionary dataDictionary, final FromList list) throws StandardException {
        final TableDescriptor bindTableDescriptor = this.bindTableDescriptor();
        if (bindTableDescriptor.getTableType() == 5) {
            return this.mapTableAsVTI(bindTableDescriptor, this.getCorrelationName(), this.resultColumns, this.getProperties(), this.getContextManager()).bindNonVTITables(dataDictionary, list);
        }
        final ResultColumnList resultColumns = this.resultColumns;
        this.restrictionList = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
        this.baseTableRestrictionList = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
        final CompilerContext compilerContext = this.getCompilerContext();
        this.resultColumns = this.genResultColList();
        this.templateColumns = this.resultColumns;
        if (bindTableDescriptor.getTableType() == 2) {
            final ViewDescriptor viewDescriptor = dataDictionary.getViewDescriptor(bindTableDescriptor);
            final SchemaDescriptor schemaDescriptor = dataDictionary.getSchemaDescriptor(viewDescriptor.getCompSchemaId(), null);
            compilerContext.pushCompilationSchema(schemaDescriptor);
            try {
                compilerContext.createDependency(viewDescriptor);
                final CreateViewNode createViewNode = (CreateViewNode)this.parseStatement(viewDescriptor.getViewText(), false);
                final ResultSetNode parsedQueryExpression = createViewNode.getParsedQueryExpression();
                if (parsedQueryExpression.getResultColumns().containsAllResultColumn()) {
                    this.resultColumns.setCountMismatchAllowed(true);
                }
                for (int i = 0; i < this.resultColumns.size(); ++i) {
                    final ResultColumn resultColumn = (ResultColumn)this.resultColumns.elementAt(i);
                    if (resultColumn.isPrivilegeCollectionRequired()) {
                        compilerContext.addRequiredColumnPriv(resultColumn.getTableColumnDescriptor());
                    }
                }
                final FromSubquery fromSubquery = (FromSubquery)this.getNodeFactory().getNode(136, parsedQueryExpression, createViewNode.getOrderByList(), createViewNode.getOffset(), createViewNode.getFetchFirst(), createViewNode.hasJDBClimitClause(), (this.correlationName != null) ? this.correlationName : this.getOrigTableName().getTableName(), this.resultColumns, this.tableProperties, this.getContextManager());
                fromSubquery.setLevel(this.level);
                final CollectNodesVisitor collectNodesVisitor = new CollectNodesVisitor(QueryTreeNode.class, null);
                fromSubquery.accept(collectNodesVisitor);
                final Iterator iterator = collectNodesVisitor.getList().iterator();
                while (iterator.hasNext()) {
                    iterator.next().disablePrivilegeCollection();
                }
                fromSubquery.setOrigTableName(this.getOrigTableName());
                fromSubquery.setOrigCompilationSchema(schemaDescriptor);
                final ResultSetNode bindNonVTITables = fromSubquery.bindNonVTITables(dataDictionary, list);
                if (resultColumns != null) {
                    bindNonVTITables.getResultColumns().propagateDCLInfo(resultColumns, this.origTableName.getFullTableName());
                }
                return bindNonVTITables;
            }
            finally {
                compilerContext.popCompilationSchema();
            }
        }
        compilerContext.createDependency(bindTableDescriptor);
        this.baseConglomerateDescriptor = bindTableDescriptor.getConglomerateDescriptor(bindTableDescriptor.getHeapConglomerateId());
        if (this.baseConglomerateDescriptor == null) {
            throw StandardException.newException("XSAI2.S", new Long(bindTableDescriptor.getHeapConglomerateId()));
        }
        this.columnNames = this.resultColumns.getColumnNames();
        if (resultColumns != null) {
            this.resultColumns.propagateDCLInfo(resultColumns, this.origTableName.getFullTableName());
        }
        if (this.tableNumber == -1) {
            this.tableNumber = compilerContext.getNextTableNumber();
        }
        this.authorizeSYSUSERS = (dataDictionary.usesSqlAuthorization() && bindTableDescriptor.getUUID().toString().equals("9810800c-0134-14a5-40c1-000004f61f90"));
        if (this.authorizeSYSUSERS && !dataDictionary.getAuthorizationDatabaseOwner().equals(this.getLanguageConnectionContext().getStatementContext().getSQLSessionContext().getCurrentUser())) {
            throw StandardException.newException("4251D");
        }
        return this;
    }
    
    private ResultSetNode mapTableAsVTI(final TableDescriptor tableDescriptor, final String s, final ResultColumnList list, final Properties properties, final ContextManager contextManager) throws StandardException {
        final QueryTreeNode queryTreeNode = (QueryTreeNode)this.getNodeFactory().getNode(133, null, tableDescriptor, Collections.EMPTY_LIST, Boolean.FALSE, contextManager);
        QueryTreeNode queryTreeNode2;
        if (s != null) {
            queryTreeNode2 = (QueryTreeNode)this.getNodeFactory().getNode(120, queryTreeNode, s, list, properties, contextManager);
        }
        else {
            queryTreeNode2 = (QueryTreeNode)this.getNodeFactory().getNode(120, queryTreeNode, s, list, properties, queryTreeNode.makeTableName(tableDescriptor.getSchemaName(), tableDescriptor.getDescriptorName()), contextManager);
        }
        return (ResultSetNode)queryTreeNode2;
    }
    
    protected FromTable getFromTableByName(final String str, final String str2, final boolean b) throws StandardException {
        final String schemaName = this.getOrigTableName().getSchemaName();
        final String s = (str2 != null) ? (str2 + '.' + str) : str;
        if (b) {
            if ((str2 != null && schemaName == null) || (str2 == null && schemaName != null)) {
                return null;
            }
            if (this.getExposedName().equals(s)) {
                return this;
            }
            return null;
        }
        else {
            if (this.getExposedName().equals(s)) {
                return this;
            }
            if ((str2 != null && schemaName != null) || (str2 == null && schemaName == null)) {
                return null;
            }
            if (str2 != null && schemaName == null) {
                if (this.tableName.equals(this.origTableName) && !str2.equals(this.tableDescriptor.getSchemaDescriptor().getSchemaName())) {
                    return null;
                }
                if (!this.getExposedName().equals(str)) {
                    return null;
                }
                if (!this.getExposedName().equals(this.getOrigTableName().getTableName())) {
                    return null;
                }
                return this;
            }
            else {
                if (!this.getExposedName().equals(this.getOrigTableName().getSchemaName() + "." + str)) {
                    return null;
                }
                return this;
            }
        }
    }
    
    private TableDescriptor bindTableDescriptor() throws StandardException {
        this.tableDescriptor = this.getTableDescriptor(this.tableName.getTableName(), this.getSchemaDescriptor(this.tableName.getSchemaName()));
        if (this.tableDescriptor == null) {
            final TableName resolveTableToSynonym = this.resolveTableToSynonym(this.tableName);
            if (resolveTableToSynonym == null) {
                throw StandardException.newException("42X05", this.tableName);
            }
            this.tableName = resolveTableToSynonym;
            this.tableDescriptor = this.getTableDescriptor(resolveTableToSynonym.getTableName(), this.getSchemaDescriptor(this.tableName.getSchemaName()));
            if (this.tableDescriptor == null) {
                throw StandardException.newException("42X05", this.tableName);
            }
        }
        return this.tableDescriptor;
    }
    
    public void bindExpressions(final FromList list) throws StandardException {
    }
    
    public void bindResultColumns(final FromList list) throws StandardException {
    }
    
    public ResultColumn getMatchingColumn(final ColumnReference columnReference) throws StandardException {
        ResultColumn resultColumn = null;
        final TableName tableNameNode = columnReference.getTableNameNode();
        if (tableNameNode != null && tableNameNode.getSchemaName() == null && this.correlationName == null) {
            tableNameNode.bind(this.getDataDictionary());
        }
        final TableName exposedTableName = this.getExposedTableName();
        if (exposedTableName.getSchemaName() == null && this.correlationName == null) {
            exposedTableName.bind(this.getDataDictionary());
        }
        if (tableNameNode == null || tableNameNode.equals(exposedTableName)) {
            if (this.resultColumns == null) {
                throw StandardException.newException("42ZB7", columnReference.getColumnName());
            }
            resultColumn = this.resultColumns.getResultColumn(columnReference.getColumnName());
            if (resultColumn != null) {
                columnReference.setTableNumber(this.tableNumber);
                columnReference.setColumnNumber(resultColumn.getColumnPosition());
                if (this.tableDescriptor != null) {
                    FormatableBitSet referencedColumnMap = this.tableDescriptor.getReferencedColumnMap();
                    if (referencedColumnMap == null) {
                        referencedColumnMap = new FormatableBitSet(this.tableDescriptor.getNumberOfColumns() + 1);
                    }
                    referencedColumnMap.set(resultColumn.getColumnPosition());
                    this.tableDescriptor.setReferencedColumnMap(referencedColumnMap);
                }
            }
        }
        return resultColumn;
    }
    
    public ResultSetNode preprocess(final int n, final GroupByList list, final FromList list2) throws StandardException {
        if (this.authorizeSYSUSERS) {
            final int n2 = 3;
            final FormatableBitSet referencedFormatableBitSet = this.resultColumns.getReferencedFormatableBitSet(false, true, false);
            if (referencedFormatableBitSet.getLength() >= n2 && referencedFormatableBitSet.isSet(n2 - 1)) {
                throw StandardException.newException("4251E", "SYSUSERS", "PASSWORD");
            }
        }
        (this.referencedTableMap = new JBitSet(n)).set(this.tableNumber);
        return this.genProjectRestrict(n);
    }
    
    protected ResultSetNode genProjectRestrict(final int n) throws StandardException {
        final ResultColumnList resultColumns = this.resultColumns;
        resultColumns.genVirtualColumnNodes(this, this.resultColumns = this.resultColumns.copyListAndObjects(), false);
        resultColumns.doProjection();
        return (ResultSetNode)this.getNodeFactory().getNode(151, this, resultColumns, null, null, null, null, null, this.getContextManager());
    }
    
    public ResultSetNode changeAccessPath() throws StandardException {
        final AccessPath trulyTheBestAccessPath = this.getTrulyTheBestAccessPath();
        final ConglomerateDescriptor conglomerateDescriptor = trulyTheBestAccessPath.getConglomerateDescriptor();
        final JoinStrategy joinStrategy = trulyTheBestAccessPath.getJoinStrategy();
        trulyTheBestAccessPath.getOptimizer().trace(34, this.tableNumber, 0, 0.0, null);
        if (this.bulkFetch != -1) {
            if (!joinStrategy.bulkFetchOK()) {
                throw StandardException.newException("42Y65", joinStrategy.getName());
            }
            if (joinStrategy.ignoreBulkFetch()) {
                this.disableBulkFetch();
            }
            else if (this.isOneRowResultSet()) {
                this.disableBulkFetch();
            }
        }
        if (this.bulkFetch == 1) {
            this.disableBulkFetch();
        }
        this.restrictionList.removeRedundantPredicates();
        this.storeRestrictionList = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
        this.nonStoreRestrictionList = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
        this.requalificationRestrictionList = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
        joinStrategy.divideUpPredicateLists(this, this.restrictionList, this.storeRestrictionList, this.nonStoreRestrictionList, this.requalificationRestrictionList, this.getDataDictionary());
        for (int i = 0; i < this.restrictionList.size(); ++i) {
            final Predicate predicate = (Predicate)this.restrictionList.elementAt(i);
            if (predicate.isInListProbePredicate() && predicate.isStartKey()) {
                this.disableBulkFetch();
                this.multiProbing = true;
                break;
            }
        }
        if (joinStrategy.bulkFetchOK() && !joinStrategy.ignoreBulkFetch() && !this.bulkFetchTurnedOff && this.bulkFetch == -1 && !this.forUpdate() && !this.isOneRowResultSet() && this.getLevel() == 0) {
            this.bulkFetch = this.getDefaultBulkFetch();
        }
        this.getCompilerContext().createDependency(conglomerateDescriptor);
        if (!conglomerateDescriptor.isIndex()) {
            final boolean equals = this.tableName.equals("SYS", "SYSSTATEMENTS");
            this.templateColumns = this.resultColumns;
            this.referencedCols = this.resultColumns.getReferencedFormatableBitSet(this.cursorTargetTable, equals, false);
            this.resultColumns = this.resultColumns.compactColumns(this.cursorTargetTable, equals);
            return this;
        }
        if (trulyTheBestAccessPath.getCoveringIndexScan() && !this.cursorTargetTable()) {
            this.resultColumns = this.newResultColumns(this.resultColumns, conglomerateDescriptor, this.baseConglomerateDescriptor, false);
            (this.templateColumns = this.newResultColumns(this.resultColumns, conglomerateDescriptor, this.baseConglomerateDescriptor, false)).addRCForRID();
            if (this.forUpdate()) {
                this.resultColumns.addRCForRID();
            }
            this.referencedCols = this.resultColumns.getReferencedFormatableBitSet(this.cursorTargetTable, true, false);
            (this.resultColumns = this.resultColumns.compactColumns(this.cursorTargetTable, true)).setIndexRow(this.baseConglomerateDescriptor.getConglomerateNumber(), this.forUpdate());
            return this;
        }
        this.getCompilerContext().createDependency(this.baseConglomerateDescriptor);
        if (this.bulkFetch != -1) {
            this.restrictionList.copyPredicatesToOtherList(this.requalificationRestrictionList);
        }
        final ResultColumnList resultColumns = this.newResultColumns(this.resultColumns, conglomerateDescriptor, this.baseConglomerateDescriptor, true);
        FormatableBitSet referencedFormatableBitSet = null;
        FormatableBitSet set;
        if (this.bulkFetch == -1 && (this.requalificationRestrictionList == null || this.requalificationRestrictionList.size() == 0)) {
            referencedFormatableBitSet = this.resultColumns.getReferencedFormatableBitSet(this.cursorTargetTable, true, false);
            set = this.resultColumns.getReferencedFormatableBitSet(this.cursorTargetTable, true, true);
            if (set != null) {
                referencedFormatableBitSet.xor(set);
            }
        }
        else {
            set = this.resultColumns.getReferencedFormatableBitSet(this.cursorTargetTable, true, false);
        }
        final ResultSetNode resultSetNode = (ResultSetNode)this.getNodeFactory().getNode(149, this, this.baseConglomerateDescriptor, this.resultColumns.compactColumns(this.cursorTargetTable, false), new Boolean(this.cursorTargetTable), set, referencedFormatableBitSet, this.requalificationRestrictionList, new Boolean(this.forUpdate()), this.tableProperties, this.getContextManager());
        this.resultColumns = resultColumns;
        this.templateColumns = this.newResultColumns(this.resultColumns, conglomerateDescriptor, this.baseConglomerateDescriptor, false);
        if (this.bulkFetch != -1) {
            this.resultColumns.markAllUnreferenced();
            this.storeRestrictionList.markReferencedColumns();
            if (this.nonStoreRestrictionList != null) {
                this.nonStoreRestrictionList.markReferencedColumns();
            }
        }
        this.resultColumns.addRCForRID();
        this.templateColumns.addRCForRID();
        this.referencedCols = this.resultColumns.getReferencedFormatableBitSet(this.cursorTargetTable, false, false);
        (this.resultColumns = this.resultColumns.compactColumns(this.cursorTargetTable, false)).setIndexRow(this.baseConglomerateDescriptor.getConglomerateNumber(), this.forUpdate());
        this.getUpdateLocks = this.cursorTargetTable;
        this.cursorTargetTable = false;
        return resultSetNode;
    }
    
    private ResultColumnList newResultColumns(final ResultColumnList list, final ConglomerateDescriptor conglomerateDescriptor, final ConglomerateDescriptor conglomerateDescriptor2, final boolean b) throws StandardException {
        final int[] baseColumnPositions = conglomerateDescriptor.getIndexDescriptor().baseColumnPositions();
        final ResultColumnList list2 = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
        for (int i = 0; i < baseColumnPositions.length; ++i) {
            final ResultColumn resultColumn = list.getResultColumn(baseColumnPositions[i]);
            ResultColumn cloneMe;
            if (b) {
                cloneMe = resultColumn.cloneMe();
                resultColumn.setExpression((ValueNode)this.getNodeFactory().getNode(107, this, cloneMe, ReuseFactory.getInteger(resultColumn.getVirtualColumnId()), this.getContextManager()));
            }
            else {
                cloneMe = resultColumn;
            }
            list2.addResultColumn(cloneMe);
        }
        list2.setIndexRow(conglomerateDescriptor2.getConglomerateNumber(), this.forUpdate());
        return list2;
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.generateResultSet(activationClassBuilder, methodBuilder);
        if (this.cursorTargetTable) {
            activationClassBuilder.rememberCursorTarget(methodBuilder);
        }
    }
    
    public void generateResultSet(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.assignResultSetNumber();
        if (this.specialMaxScan) {
            this.generateMaxSpecialResultSet(expressionClassBuilder, methodBuilder);
            return;
        }
        if (this.distinctScan) {
            this.generateDistinctScan(expressionClassBuilder, methodBuilder);
            return;
        }
        if (this.raDependentScan) {
            this.generateRefActionDependentTableScan(expressionClassBuilder, methodBuilder);
            return;
        }
        final JoinStrategy joinStrategy = this.getTrulyTheBestAccessPath().getJoinStrategy();
        expressionClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        methodBuilder.callMethod((short)185, null, joinStrategy.resultSetMethodName(this.bulkFetch != -1, this.multiProbing), "org.apache.derby.iapi.sql.execute.NoPutResultSet", this.getScanArguments(expressionClassBuilder, methodBuilder));
        if (this.updateOrDelete == 1 || this.updateOrDelete == 2) {
            methodBuilder.cast("org.apache.derby.iapi.sql.execute.CursorResultSet");
            methodBuilder.putField(expressionClassBuilder.getRowLocationScanResultSetName(), "org.apache.derby.iapi.sql.execute.CursorResultSet");
            methodBuilder.cast("org.apache.derby.iapi.sql.execute.NoPutResultSet");
        }
    }
    
    public CostEstimate getFinalCostEstimate() {
        return this.getTrulyTheBestAccessPath().getCostEstimate();
    }
    
    private void pushIndexName(final ConglomerateDescriptor conglomerateDescriptor, final MethodBuilder methodBuilder) throws StandardException {
        if (conglomerateDescriptor.isConstraint()) {
            methodBuilder.push(this.getDataDictionary().getConstraintDescriptor(this.tableDescriptor, conglomerateDescriptor.getUUID()).getConstraintName());
        }
        else if (conglomerateDescriptor.isIndex()) {
            methodBuilder.push(conglomerateDescriptor.getConglomerateName());
        }
        else {
            methodBuilder.pushNull("java.lang.String");
        }
    }
    
    private void generateMaxSpecialResultSet(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final ConglomerateDescriptor conglomerateDescriptor = this.getTrulyTheBestAccessPath().getConglomerateDescriptor();
        final CostEstimate finalCostEstimate = this.getFinalCostEstimate();
        final int n = (this.referencedCols == null) ? -1 : expressionClassBuilder.addItem(this.referencedCols);
        final boolean b = this.tableDescriptor.getLockGranularity() == 'T';
        expressionClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        expressionClassBuilder.pushThisAsActivation(methodBuilder);
        methodBuilder.push(this.getResultSetNumber());
        methodBuilder.push(expressionClassBuilder.addItem(this.resultColumns.buildRowTemplate(this.referencedCols, false)));
        methodBuilder.push(conglomerateDescriptor.getConglomerateNumber());
        methodBuilder.push(this.tableDescriptor.getName());
        if (this.tableProperties != null) {
            methodBuilder.push(PropertyUtil.sortProperties(this.tableProperties));
        }
        else {
            methodBuilder.pushNull("java.lang.String");
        }
        this.pushIndexName(conglomerateDescriptor, methodBuilder);
        methodBuilder.push(n);
        methodBuilder.push(this.getTrulyTheBestAccessPath().getLockMode());
        methodBuilder.push(b);
        methodBuilder.push(this.getCompilerContext().getScanIsolationLevel());
        methodBuilder.push(finalCostEstimate.singleScanRowCount());
        methodBuilder.push(finalCostEstimate.getEstimatedCost());
        methodBuilder.callMethod((short)185, null, "getLastIndexKeyResultSet", "org.apache.derby.iapi.sql.execute.NoPutResultSet", 13);
    }
    
    private void generateDistinctScan(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final ConglomerateDescriptor conglomerateDescriptor = this.getTrulyTheBestAccessPath().getConglomerateDescriptor();
        final CostEstimate finalCostEstimate = this.getFinalCostEstimate();
        final int n = (this.referencedCols == null) ? -1 : expressionClassBuilder.addItem(this.referencedCols);
        final boolean b = this.tableDescriptor.getLockGranularity() == 'T';
        final int[] array = new int[this.resultColumns.size()];
        if (this.referencedCols == null) {
            for (int i = 0; i < array.length; ++i) {
                array[i] = i;
            }
        }
        else {
            int n2 = 0;
            for (int j = this.referencedCols.anySetBit(); j != -1; j = this.referencedCols.anySetBit(j)) {
                array[n2++] = j;
            }
        }
        final int addItem = expressionClassBuilder.addItem(new FormatableArrayHolder(FormatableIntHolder.getFormatableIntHolders(array)));
        final long conglomerateNumber = conglomerateDescriptor.getConglomerateNumber();
        final StaticCompiledOpenConglomInfo staticCompiledConglomInfo = this.getLanguageConnectionContext().getTransactionCompile().getStaticCompiledConglomInfo(conglomerateNumber);
        expressionClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        expressionClassBuilder.pushThisAsActivation(methodBuilder);
        methodBuilder.push(conglomerateNumber);
        methodBuilder.push(expressionClassBuilder.addItem(staticCompiledConglomInfo));
        methodBuilder.push(expressionClassBuilder.addItem(this.resultColumns.buildRowTemplate(this.referencedCols, false)));
        methodBuilder.push(this.getResultSetNumber());
        methodBuilder.push(addItem);
        methodBuilder.push(this.tableDescriptor.getName());
        if (this.tableProperties != null) {
            methodBuilder.push(PropertyUtil.sortProperties(this.tableProperties));
        }
        else {
            methodBuilder.pushNull("java.lang.String");
        }
        this.pushIndexName(conglomerateDescriptor, methodBuilder);
        methodBuilder.push(conglomerateDescriptor.isConstraint());
        methodBuilder.push(n);
        methodBuilder.push(this.getTrulyTheBestAccessPath().getLockMode());
        methodBuilder.push(b);
        methodBuilder.push(this.getCompilerContext().getScanIsolationLevel());
        methodBuilder.push(finalCostEstimate.singleScanRowCount());
        methodBuilder.push(finalCostEstimate.getEstimatedCost());
        methodBuilder.callMethod((short)185, null, "getDistinctScanResultSet", "org.apache.derby.iapi.sql.execute.NoPutResultSet", 16);
    }
    
    private void generateRefActionDependentTableScan(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        expressionClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        final int scanArguments = this.getScanArguments(expressionClassBuilder, methodBuilder);
        methodBuilder.push(this.raParentResultSetId);
        methodBuilder.push(this.fkIndexConglomId);
        methodBuilder.push(expressionClassBuilder.addItem(this.fkColArray));
        methodBuilder.push(expressionClassBuilder.addItem(this.getDataDictionary().getRowLocationTemplate(this.getLanguageConnectionContext(), this.tableDescriptor)));
        methodBuilder.callMethod((short)185, null, "getRaDependentTableScanResultSet", "org.apache.derby.iapi.sql.execute.NoPutResultSet", scanArguments + 4);
        if (this.updateOrDelete == 1 || this.updateOrDelete == 2) {
            methodBuilder.cast("org.apache.derby.iapi.sql.execute.CursorResultSet");
            methodBuilder.putField(expressionClassBuilder.getRowLocationScanResultSetName(), "org.apache.derby.iapi.sql.execute.CursorResultSet");
            methodBuilder.cast("org.apache.derby.iapi.sql.execute.NoPutResultSet");
        }
    }
    
    private int getScanArguments(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final int addItem = expressionClassBuilder.addItem(this.resultColumns.buildRowTemplate(this.referencedCols, false));
        int addItem2 = -1;
        if (this.referencedCols != null) {
            addItem2 = expressionClassBuilder.addItem(this.referencedCols);
        }
        int addItem3 = -1;
        if (this.cursorTargetTable || this.getUpdateLocks) {
            final ConglomerateDescriptor conglomerateDescriptor = this.getTrulyTheBestAccessPath().getConglomerateDescriptor();
            if (conglomerateDescriptor.isIndex()) {
                final int[] baseColumnPositions = conglomerateDescriptor.getIndexDescriptor().baseColumnPositions();
                final boolean[] ascending = conglomerateDescriptor.getIndexDescriptor().isAscending();
                final int[] array = new int[baseColumnPositions.length];
                for (int i = 0; i < array.length; ++i) {
                    array[i] = (ascending[i] ? baseColumnPositions[i] : (-baseColumnPositions[i]));
                }
                addItem3 = expressionClassBuilder.addItem(array);
            }
        }
        final AccessPath trulyTheBestAccessPath = this.getTrulyTheBestAccessPath();
        return trulyTheBestAccessPath.getJoinStrategy().getScanArgs(this.getLanguageConnectionContext().getTransactionCompile(), methodBuilder, this, this.storeRestrictionList, this.nonStoreRestrictionList, expressionClassBuilder, this.bulkFetch, addItem, addItem2, addItem3, this.getTrulyTheBestAccessPath().getLockMode(), this.tableDescriptor.getLockGranularity() == 'T', this.getCompilerContext().getScanIsolationLevel(), trulyTheBestAccessPath.getOptimizer().getMaxMemoryPerTable(), this.multiProbing);
    }
    
    private int mapAbsoluteToRelativeColumnPosition(final int n) {
        if (this.referencedCols == null) {
            return n;
        }
        int n2 = 0;
        for (int n3 = 0; n3 < this.referencedCols.size() && n3 < n; ++n3) {
            if (this.referencedCols.get(n3)) {
                ++n2;
            }
        }
        return n2;
    }
    
    public String getExposedName() {
        if (this.correlationName != null) {
            return this.correlationName;
        }
        return this.getOrigTableName().getFullTableName();
    }
    
    private TableName getExposedTableName() throws StandardException {
        if (this.correlationName != null) {
            return this.makeTableName(null, this.correlationName);
        }
        return this.getOrigTableName();
    }
    
    public TableName getTableNameField() {
        return this.tableName;
    }
    
    public ResultColumnList getAllResultColumns(final TableName tableName) throws StandardException {
        return this.getResultColumnsForList(tableName, this.resultColumns, this.getOrigTableName());
    }
    
    public ResultColumnList genResultColList() throws StandardException {
        final TableName exposedTableName = this.getExposedTableName();
        final ResultColumnList list = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
        final ColumnDescriptorList columnDescriptorList = this.tableDescriptor.getColumnDescriptorList();
        for (int size = columnDescriptorList.size(), i = 0; i < size; ++i) {
            final ColumnDescriptor element = columnDescriptorList.elementAt(i);
            element.setTableDescriptor(this.tableDescriptor);
            list.addResultColumn((ResultColumn)this.getNodeFactory().getNode(80, element, this.getNodeFactory().getNode(94, element.getColumnName(), exposedTableName, element.getType(), this.getContextManager()), this.getContextManager()));
        }
        return list;
    }
    
    public ResultColumnList addColsToList(final ResultColumnList list, final FormatableBitSet set) throws StandardException {
        final TableName exposedTableName = this.getExposedTableName();
        final ResultColumnList list2 = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
        final ColumnDescriptorList columnDescriptorList = this.tableDescriptor.getColumnDescriptorList();
        for (int size = columnDescriptorList.size(), i = 0; i < size; ++i) {
            final ColumnDescriptor element = columnDescriptorList.elementAt(i);
            final int position = element.getPosition();
            if (set.get(position)) {
                ResultColumn resultColumn;
                if ((resultColumn = list.getResultColumn(position)) == null) {
                    resultColumn = (ResultColumn)this.getNodeFactory().getNode(80, element, this.getNodeFactory().getNode(62, element.getColumnName(), exposedTableName, this.getContextManager()), this.getContextManager());
                }
                list2.addResultColumn(resultColumn);
            }
        }
        return list2;
    }
    
    public TableName getTableName() throws StandardException {
        final TableName tableName = super.getTableName();
        if (tableName != null && tableName.getSchemaName() == null && this.correlationName == null) {
            tableName.bind(this.getDataDictionary());
        }
        return (tableName != null) ? tableName : this.tableName;
    }
    
    public boolean markAsCursorTargetTable() {
        return this.cursorTargetTable = true;
    }
    
    protected boolean cursorTargetTable() {
        return this.cursorTargetTable;
    }
    
    void markUpdated(final ResultColumnList list) {
        this.resultColumns.markUpdated(list);
    }
    
    public boolean referencesTarget(final String s, final boolean b) throws StandardException {
        return b && s.equals(this.getBaseTableName());
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        return this.isSessionSchema(this.tableDescriptor.getSchemaDescriptor());
    }
    
    public boolean isOneRowResultSet() throws StandardException {
        if (this.existsBaseTable) {
            return true;
        }
        if (this.getTrulyTheBestAccessPath().getJoinStrategy().isHashJoin()) {
            final PredicateList list = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
            if (this.storeRestrictionList != null) {
                list.nondestructiveAppend(this.storeRestrictionList);
            }
            if (this.nonStoreRestrictionList != null) {
                list.nondestructiveAppend(this.nonStoreRestrictionList);
            }
            return this.isOneRowResultSet(list);
        }
        return this.isOneRowResultSet(this.getTrulyTheBestAccessPath().getConglomerateDescriptor(), this.restrictionList);
    }
    
    public boolean isNotExists() {
        return this.isNotExists;
    }
    
    public boolean isOneRowResultSet(final OptimizablePredicateList list) throws StandardException {
        final ConglomerateDescriptor[] conglomerateDescriptors = this.tableDescriptor.getConglomerateDescriptors();
        for (int i = 0; i < conglomerateDescriptors.length; ++i) {
            if (this.isOneRowResultSet(conglomerateDescriptors[i], list)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean supersetOfUniqueIndex(final boolean[] array) throws StandardException {
        final ConglomerateDescriptor[] conglomerateDescriptors = this.tableDescriptor.getConglomerateDescriptors();
        for (int i = 0; i < conglomerateDescriptors.length; ++i) {
            final ConglomerateDescriptor conglomerateDescriptor = conglomerateDescriptors[i];
            if (conglomerateDescriptor.isIndex()) {
                final IndexRowGenerator indexDescriptor = conglomerateDescriptor.getIndexDescriptor();
                if (indexDescriptor.isUnique()) {
                    int[] baseColumnPositions;
                    int n;
                    for (baseColumnPositions = indexDescriptor.baseColumnPositions(), n = 0; n < baseColumnPositions.length && array[baseColumnPositions[n]]; ++n) {}
                    if (n == baseColumnPositions.length) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    protected boolean supersetOfUniqueIndex(final JBitSet[] array) throws StandardException {
        final ConglomerateDescriptor[] conglomerateDescriptors = this.tableDescriptor.getConglomerateDescriptors();
        for (int i = 0; i < conglomerateDescriptors.length; ++i) {
            final ConglomerateDescriptor conglomerateDescriptor = conglomerateDescriptors[i];
            if (conglomerateDescriptor.isIndex()) {
                final IndexRowGenerator indexDescriptor = conglomerateDescriptor.getIndexDescriptor();
                if (indexDescriptor.isUnique()) {
                    final int[] baseColumnPositions = indexDescriptor.baseColumnPositions();
                    final int size = array[0].size();
                    final JBitSet set = new JBitSet(size);
                    final JBitSet set2 = new JBitSet(size);
                    for (int j = 0; j < baseColumnPositions.length; ++j) {
                        set.set(baseColumnPositions[j]);
                    }
                    for (int k = 0; k < array.length; ++k) {
                        set2.setTo(array[k]);
                        set2.and(set);
                        if (set.equals(set2)) {
                            array[k].set(0);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public int updateTargetLockMode() {
        if (this.getTrulyTheBestAccessPath().getConglomerateDescriptor().isIndex()) {
            return 6;
        }
        if (this.getLanguageConnectionContext().getCurrentIsolationLevel() != 4 && this.tableDescriptor.getLockGranularity() != 'T') {
            final int lockMode = this.getTrulyTheBestAccessPath().getLockMode();
            int n;
            if (lockMode != 6) {
                n = (lockMode & 0xFF) << 16;
            }
            else {
                n = 0;
            }
            n += 6;
            return n;
        }
        return this.getTrulyTheBestAccessPath().getLockMode();
    }
    
    boolean isOrderedOn(final ColumnReference[] array, final boolean b, final List list) throws StandardException {
        for (int i = 0; i < array.length; ++i) {
            if (array[i].getTableNumber() != this.tableNumber) {
                return false;
            }
        }
        final ConglomerateDescriptor conglomerateDescriptor = this.getTrulyTheBestAccessPath().getConglomerateDescriptor();
        if (!conglomerateDescriptor.isIndex()) {
            return false;
        }
        boolean b2;
        if (b) {
            b2 = this.isOrdered(array, conglomerateDescriptor);
        }
        else {
            b2 = this.isStrictlyOrdered(array, conglomerateDescriptor);
        }
        if (list != null) {
            list.add(this);
        }
        return b2;
    }
    
    void disableBulkFetch() {
        this.bulkFetchTurnedOff = true;
        this.bulkFetch = -1;
    }
    
    void doSpecialMaxScan() {
        this.specialMaxScan = true;
    }
    
    boolean isPossibleDistinctScan(final Set o) {
        if (this.restrictionList != null && this.restrictionList.size() != 0) {
            return false;
        }
        final HashSet set = new HashSet<ValueNode>();
        for (int i = 0; i < this.resultColumns.size(); ++i) {
            set.add(((ResultColumn)this.resultColumns.elementAt(i)).getExpression());
        }
        return set.equals(o);
    }
    
    void markForDistinctScan() {
        this.distinctScan = true;
    }
    
    void adjustForSortElimination() {
    }
    
    void adjustForSortElimination(final RequiredRowOrdering requiredRowOrdering) throws StandardException {
        if (this.restrictionList != null) {
            this.restrictionList.adjustForSortElimination(requiredRowOrdering);
        }
    }
    
    private boolean isOrdered(final ColumnReference[] array, final ConglomerateDescriptor conglomerateDescriptor) throws StandardException {
        final boolean[] array2 = new boolean[array.length];
        int i;
        int[] baseColumnPositions;
        boolean b;
        int j;
        for (i = 0, baseColumnPositions = conglomerateDescriptor.getIndexDescriptor().baseColumnPositions(); i < baseColumnPositions.length; ++i) {
            b = false;
            for (j = 0; j < array.length; ++j) {
                if (array[j].getColumnNumber() == baseColumnPositions[i]) {
                    array2[j] = true;
                    b = true;
                    break;
                }
            }
            if (!b) {
                if (!this.storeRestrictionList.hasOptimizableEqualityPredicate(this, baseColumnPositions[i], true)) {
                    break;
                }
            }
        }
        int n = 0;
        for (int k = 0; k < array2.length; ++k) {
            if (array2[k]) {
                ++n;
            }
        }
        return n == array2.length || (i == baseColumnPositions.length && conglomerateDescriptor.getIndexDescriptor().isUnique());
    }
    
    private boolean isStrictlyOrdered(final ColumnReference[] array, final ConglomerateDescriptor conglomerateDescriptor) throws StandardException {
        int i = 0;
        int n = 0;
        final int[] baseColumnPositions = conglomerateDescriptor.getIndexDescriptor().baseColumnPositions();
        while (i < array.length) {
            if (n == baseColumnPositions.length) {
                if (conglomerateDescriptor.getIndexDescriptor().isUnique()) {
                    break;
                }
                return false;
            }
            else {
                if (array[i].getColumnNumber() == baseColumnPositions[n]) {
                    ++n;
                }
                else {
                    while (array[i].getColumnNumber() != baseColumnPositions[n]) {
                        if (!this.storeRestrictionList.hasOptimizableEqualityPredicate(this, baseColumnPositions[n], true)) {
                            return false;
                        }
                        if (++n != baseColumnPositions.length) {
                            continue;
                        }
                        if (conglomerateDescriptor.getIndexDescriptor().isUnique()) {
                            break;
                        }
                        return false;
                    }
                }
                ++i;
            }
        }
        return true;
    }
    
    private boolean isOneRowResultSet(final ConglomerateDescriptor conglomerateDescriptor, final OptimizablePredicateList list) throws StandardException {
        if (list == null) {
            return false;
        }
        final PredicateList list2 = (PredicateList)list;
        if (!conglomerateDescriptor.isIndex()) {
            return false;
        }
        final IndexRowGenerator indexDescriptor = conglomerateDescriptor.getIndexDescriptor();
        if (!indexDescriptor.isUnique()) {
            return false;
        }
        final int[] baseColumnPositions = indexDescriptor.baseColumnPositions();
        this.getDataDictionary();
        for (int i = 0; i < baseColumnPositions.length; ++i) {
            if (!list2.hasOptimizableEqualityPredicate(this, baseColumnPositions[i], true)) {
                return false;
            }
        }
        return true;
    }
    
    private int getDefaultBulkFetch() throws StandardException {
        final int intProperty = this.getIntProperty(org.apache.derby.iapi.services.property.PropertyUtil.getServiceProperty(this.getLanguageConnectionContext().getTransactionCompile(), "derby.language.bulkFetchDefault", "16"), "derby.language.bulkFetchDefault");
        if (intProperty <= 0) {
            throw StandardException.newException("42Y64", String.valueOf(intProperty));
        }
        return (intProperty <= 1) ? -1 : intProperty;
    }
    
    private String getUserSpecifiedIndexName() {
        String property = null;
        if (this.tableProperties != null) {
            property = this.tableProperties.getProperty("index");
        }
        return property;
    }
    
    private StoreCostController getStoreCostController(final ConglomerateDescriptor conglomerateDescriptor) throws StandardException {
        return this.getCompilerContext().getStoreCostController(conglomerateDescriptor.getConglomerateNumber());
    }
    
    private StoreCostController getBaseCostController() throws StandardException {
        return this.getStoreCostController(this.baseConglomerateDescriptor);
    }
    
    private long baseRowCount() throws StandardException {
        if (!this.gotRowCount) {
            this.rowCount = this.getBaseCostController().getEstimatedRowCount();
            this.gotRowCount = true;
        }
        return this.rowCount;
    }
    
    private DataValueDescriptor[] getRowTemplate(final ConglomerateDescriptor conglomerateDescriptor, final StoreCostController storeCostController) throws StandardException {
        if (!conglomerateDescriptor.isIndex()) {
            return this.templateColumns.buildEmptyRow().getRowArray();
        }
        return this.templateColumns.buildEmptyIndexRow(this.tableDescriptor, conglomerateDescriptor, storeCostController, this.getDataDictionary()).getRowArray();
    }
    
    private ConglomerateDescriptor getFirstConglom() throws StandardException {
        this.getConglomDescs();
        return this.conglomDescs[0];
    }
    
    private ConglomerateDescriptor getNextConglom(final ConglomerateDescriptor conglomerateDescriptor) {
        int n;
        for (n = 0; n < this.conglomDescs.length && conglomerateDescriptor != this.conglomDescs[n]; ++n) {}
        if (n < this.conglomDescs.length - 1) {
            return this.conglomDescs[n + 1];
        }
        return null;
    }
    
    private void getConglomDescs() throws StandardException {
        if (this.conglomDescs == null) {
            this.conglomDescs = this.tableDescriptor.getConglomerateDescriptors();
        }
    }
    
    public void setRefActionInfo(final long fkIndexConglomId, final int[] fkColArray, final String raParentResultSetId, final boolean raDependentScan) {
        this.fkIndexConglomId = fkIndexConglomId;
        this.fkColArray = fkColArray;
        this.raParentResultSetId = raParentResultSetId;
        this.raDependentScan = raDependentScan;
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.nonStoreRestrictionList != null) {
            this.nonStoreRestrictionList.accept(visitor);
        }
        if (this.restrictionList != null) {
            this.restrictionList.accept(visitor);
        }
        if (this.nonBaseTableRestrictionList != null) {
            this.nonBaseTableRestrictionList.accept(visitor);
        }
        if (this.requalificationRestrictionList != null) {
            this.requalificationRestrictionList.accept(visitor);
        }
    }
    
    private boolean qualifiesForStatisticsUpdateCheck(final TableDescriptor tableDescriptor) throws StandardException {
        int n = 0;
        if (tableDescriptor.getTableType() == 0) {
            final IndexStatisticsDaemonImpl indexStatisticsDaemonImpl = (IndexStatisticsDaemonImpl)this.getDataDictionary().getIndexStatsRefresher(false);
            if (indexStatisticsDaemonImpl == null) {
                n = 0;
            }
            else if (indexStatisticsDaemonImpl.skipDisposableStats) {
                n = tableDescriptor.getQualifiedNumberOfIndexes(2, true);
            }
            else {
                n = tableDescriptor.getTotalNumberOfIndexes();
            }
        }
        return n > 0;
    }
}
