// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import java.util.List;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import java.util.Enumeration;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.compile.OptimizablePredicate;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.compile.AccessPath;
import org.apache.derby.iapi.sql.compile.JoinStrategy;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.compile.RowOrdering;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.sql.compile.Optimizer;
import java.util.HashMap;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import java.util.Properties;
import org.apache.derby.iapi.sql.compile.Optimizable;

abstract class FromTable extends ResultSetNode implements Optimizable
{
    Properties tableProperties;
    String correlationName;
    TableName corrTableName;
    int tableNumber;
    int level;
    int[] hashKeyColumns;
    int initialCapacity;
    float loadFactor;
    int maxCapacity;
    AccessPathImpl currentAccessPath;
    AccessPathImpl bestAccessPath;
    AccessPathImpl bestSortAvoidancePath;
    AccessPathImpl trulyTheBestAccessPath;
    private int joinStrategyNumber;
    protected String userSpecifiedJoinStrategy;
    protected CostEstimate bestCostEstimate;
    private double perRowUsage;
    private boolean considerSortAvoidancePath;
    private HashMap bestPlanMap;
    protected static final short REMOVE_PLAN = 0;
    protected static final short ADD_PLAN = 1;
    protected static final short LOAD_PLAN = 2;
    protected TableName origTableName;
    
    FromTable() {
        this.initialCapacity = -1;
        this.loadFactor = -1.0f;
        this.maxCapacity = -1;
        this.perRowUsage = -1.0;
    }
    
    public void init(final Object o, final Object o2) {
        this.correlationName = (String)o;
        this.tableProperties = (Properties)o2;
        this.tableNumber = -1;
        this.bestPlanMap = null;
    }
    
    public String getCorrelationName() {
        return this.correlationName;
    }
    
    public CostEstimate optimizeIt(final Optimizer optimizer, final OptimizablePredicateList list, final CostEstimate costEstimate, final RowOrdering rowOrdering) throws StandardException {
        this.updateBestPlanMap((short)1, this);
        final CostEstimate estimateCost = this.estimateCost(list, null, costEstimate, optimizer, rowOrdering);
        this.getCostEstimate(optimizer);
        this.setCostEstimate(estimateCost);
        this.optimizeSubqueries(this.getDataDictionary(), this.costEstimate.rowCount());
        this.getCurrentAccessPath().getJoinStrategy().estimateCost(this, list, null, costEstimate, optimizer, this.getCostEstimate());
        optimizer.considerCost(this, list, this.getCostEstimate(), costEstimate);
        return this.getCostEstimate();
    }
    
    public boolean nextAccessPath(final Optimizer optimizer, final OptimizablePredicateList list, final RowOrdering rowOrdering) throws StandardException {
        final int numberOfJoinStrategies = optimizer.getNumberOfJoinStrategies();
        boolean b = false;
        final AccessPath currentAccessPath = this.getCurrentAccessPath();
        if (this.userSpecifiedJoinStrategy != null) {
            if (currentAccessPath.getJoinStrategy() != null) {
                currentAccessPath.setJoinStrategy(null);
                b = false;
            }
            else {
                currentAccessPath.setJoinStrategy(optimizer.getJoinStrategy(this.userSpecifiedJoinStrategy));
                if (currentAccessPath.getJoinStrategy() == null) {
                    throw StandardException.newException("42Y56", this.userSpecifiedJoinStrategy, this.getBaseTableName());
                }
                b = true;
            }
        }
        else if (this.joinStrategyNumber < numberOfJoinStrategies) {
            currentAccessPath.setJoinStrategy(optimizer.getJoinStrategy(this.joinStrategyNumber));
            ++this.joinStrategyNumber;
            b = true;
            optimizer.trace(28, this.tableNumber, 0, 0.0, currentAccessPath.getJoinStrategy());
        }
        this.tellRowOrderingAboutConstantColumns(rowOrdering, list);
        return b;
    }
    
    protected boolean canBeOrdered() {
        return false;
    }
    
    public AccessPath getCurrentAccessPath() {
        return this.currentAccessPath;
    }
    
    public AccessPath getBestAccessPath() {
        return this.bestAccessPath;
    }
    
    public AccessPath getBestSortAvoidancePath() {
        return this.bestSortAvoidancePath;
    }
    
    public AccessPath getTrulyTheBestAccessPath() {
        return this.trulyTheBestAccessPath;
    }
    
    public void rememberSortAvoidancePath() {
        this.considerSortAvoidancePath = true;
    }
    
    public boolean considerSortAvoidancePath() {
        return this.considerSortAvoidancePath;
    }
    
    public void rememberJoinStrategyAsBest(final AccessPath accessPath) {
        final Optimizer optimizer = accessPath.getOptimizer();
        accessPath.setJoinStrategy(this.getCurrentAccessPath().getJoinStrategy());
        optimizer.trace(49, this.tableNumber, 0, 0.0, this.getCurrentAccessPath().getJoinStrategy());
        if (accessPath == this.bestAccessPath) {
            optimizer.trace(50, this.tableNumber, 0, 0.0, accessPath);
        }
        else if (accessPath == this.bestSortAvoidancePath) {
            optimizer.trace(51, this.tableNumber, 0, 0.0, accessPath);
        }
        else {
            optimizer.trace(52, this.tableNumber, 0, 0.0, accessPath);
        }
    }
    
    public TableDescriptor getTableDescriptor() {
        return null;
    }
    
    public boolean pushOptPredicate(final OptimizablePredicate optimizablePredicate) throws StandardException {
        return false;
    }
    
    public void pullOptPredicates(final OptimizablePredicateList list) throws StandardException {
    }
    
    public Optimizable modifyAccessPath(final JBitSet set) throws StandardException {
        return this;
    }
    
    public boolean isCoveringIndex(final ConglomerateDescriptor conglomerateDescriptor) throws StandardException {
        return false;
    }
    
    public Properties getProperties() {
        return this.tableProperties;
    }
    
    public void setProperties(final Properties tableProperties) {
        this.tableProperties = tableProperties;
    }
    
    public void verifyProperties(final DataDictionary dataDictionary) throws StandardException {
        if (this.tableProperties == null) {
            return;
        }
        final Enumeration<Object> keys = this.tableProperties.keys();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            final String s = (String)this.tableProperties.get(key);
            if (key.equals("joinStrategy")) {
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
            else {
                if (!key.equals("hashMaxCapacity")) {
                    throw StandardException.newException("42Y44", key, "joinStrategy");
                }
                this.maxCapacity = this.getIntProperty(s, key);
                if (this.maxCapacity <= 0) {
                    throw StandardException.newException("42Y61", String.valueOf(this.maxCapacity));
                }
                continue;
            }
        }
    }
    
    public String getName() throws StandardException {
        return this.getExposedName();
    }
    
    public String getBaseTableName() {
        return "";
    }
    
    public int convertAbsoluteToRelativeColumnPosition(final int n) {
        return n;
    }
    
    public void updateBestPlanMap(final short n, final Object o) throws StandardException {
        if (n == 0) {
            if (this.bestPlanMap != null) {
                this.bestPlanMap.remove(o);
                if (this.bestPlanMap.size() == 0) {
                    this.bestPlanMap = null;
                }
            }
            return;
        }
        final AccessPath trulyTheBestAccessPath = this.getTrulyTheBestAccessPath();
        AccessPathImpl value = null;
        if (n == 1) {
            if (trulyTheBestAccessPath == null) {
                return;
            }
            if (this.bestPlanMap == null) {
                this.bestPlanMap = new HashMap();
            }
            else {
                value = this.bestPlanMap.get(o);
            }
            if (value == null) {
                if (o instanceof Optimizer) {
                    value = new AccessPathImpl((Optimizer)o);
                }
                else {
                    value = new AccessPathImpl(null);
                }
            }
            value.copy(trulyTheBestAccessPath);
            this.bestPlanMap.put(o, value);
        }
        else {
            if (this.bestPlanMap == null) {
                return;
            }
            final AccessPathImpl accessPathImpl = this.bestPlanMap.get(o);
            if (accessPathImpl == null || accessPathImpl.getCostEstimate() == null) {
                return;
            }
            trulyTheBestAccessPath.copy(accessPathImpl);
        }
    }
    
    public void rememberAsBest(final int n, final Optimizer optimizer) throws StandardException {
        AccessPath accessPath = null;
        switch (n) {
            case 1: {
                accessPath = this.getBestAccessPath();
                break;
            }
            case 2: {
                accessPath = this.getBestSortAvoidancePath();
                break;
            }
        }
        this.getTrulyTheBestAccessPath().copy(accessPath);
        if (!(this instanceof ProjectRestrictNode)) {
            this.updateBestPlanMap((short)1, optimizer);
        }
        else if (!(((ProjectRestrictNode)this).getChildResult() instanceof Optimizable)) {
            this.updateBestPlanMap((short)1, optimizer);
        }
        if (this.isBaseTable()) {
            this.getTrulyTheBestAccessPath().initializeAccessPathName(this.getDataDictionary(), this.getTableDescriptor());
        }
        this.setCostEstimate(accessPath.getCostEstimate());
        accessPath.getOptimizer().trace(29, this.tableNumber, n, 0.0, accessPath);
    }
    
    public void startOptimizing(final Optimizer optimizer, final RowOrdering rowOrdering) {
        this.resetJoinStrategies(optimizer);
        this.considerSortAvoidancePath = false;
        final CostEstimate costEstimate = this.getBestAccessPath().getCostEstimate();
        if (costEstimate != null) {
            costEstimate.setCost(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        }
        final CostEstimate costEstimate2 = this.getBestSortAvoidancePath().getCostEstimate();
        if (costEstimate2 != null) {
            costEstimate2.setCost(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        }
        if (!this.canBeOrdered()) {
            rowOrdering.addUnorderedOptimizable(this);
        }
    }
    
    protected void resetJoinStrategies(final Optimizer optimizer) {
        this.joinStrategyNumber = 0;
        this.getCurrentAccessPath().setJoinStrategy(null);
    }
    
    public CostEstimate estimateCost(final OptimizablePredicateList list, final ConglomerateDescriptor conglomerateDescriptor, final CostEstimate costEstimate, final Optimizer optimizer, final RowOrdering rowOrdering) throws StandardException {
        return null;
    }
    
    public CostEstimate getFinalCostEstimate() throws StandardException {
        if (this.finalCostEstimate != null) {
            return this.finalCostEstimate;
        }
        if (this.getTrulyTheBestAccessPath() == null) {
            this.finalCostEstimate = this.costEstimate;
        }
        else {
            this.finalCostEstimate = this.getTrulyTheBestAccessPath().getCostEstimate();
        }
        return this.finalCostEstimate;
    }
    
    public boolean isBaseTable() {
        return false;
    }
    
    public boolean hasLargeObjectColumns() {
        for (int i = 0; i < this.resultColumns.size(); ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.resultColumns.elementAt(i);
            if (resultColumn.isReferenced()) {
                final DataTypeDescriptor type = resultColumn.getType();
                if (type != null && type.getTypeId().isLOBTypeId()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isMaterializable() throws StandardException {
        final HasCorrelatedCRsVisitor hasCorrelatedCRsVisitor = new HasCorrelatedCRsVisitor();
        this.accept(hasCorrelatedCRsVisitor);
        return !hasCorrelatedCRsVisitor.hasCorrelatedCRs();
    }
    
    public boolean supportsMultipleInstantiations() {
        return true;
    }
    
    public int getTableNumber() {
        return this.tableNumber;
    }
    
    public boolean hasTableNumber() {
        return this.tableNumber >= 0;
    }
    
    public boolean forUpdate() {
        return false;
    }
    
    public int initialCapacity() {
        return 0;
    }
    
    public float loadFactor() {
        return 0.0f;
    }
    
    public int maxCapacity(final JoinStrategy joinStrategy, final int n) throws StandardException {
        return joinStrategy.maxCapacity(this.maxCapacity, n, this.getPerRowUsage());
    }
    
    private double getPerRowUsage() throws StandardException {
        if (this.perRowUsage < 0.0) {
            final FormatableBitSet referencedFormatableBitSet = this.resultColumns.getReferencedFormatableBitSet(this.cursorTargetTable(), true, false);
            this.perRowUsage = 0.0;
            for (int i = 0; i < referencedFormatableBitSet.size(); ++i) {
                if (referencedFormatableBitSet.isSet(i)) {
                    final DataTypeDescriptor typeServices = ((ResultColumn)this.resultColumns.elementAt(i)).getExpression().getTypeServices();
                    if (typeServices != null) {
                        this.perRowUsage += typeServices.estimatedMemoryUsage();
                    }
                }
            }
            final ConglomerateDescriptor conglomerateDescriptor = this.getCurrentAccessPath().getConglomerateDescriptor();
            if (conglomerateDescriptor != null && conglomerateDescriptor.isIndex() && !this.isCoveringIndex(conglomerateDescriptor)) {
                this.perRowUsage += 12.0;
            }
        }
        return this.perRowUsage;
    }
    
    public int[] hashKeyColumns() {
        return this.hashKeyColumns;
    }
    
    public void setHashKeyColumns(final int[] hashKeyColumns) {
        this.hashKeyColumns = hashKeyColumns;
    }
    
    public boolean feasibleJoinStrategy(final OptimizablePredicateList list, final Optimizer optimizer) throws StandardException {
        return this.getCurrentAccessPath().getJoinStrategy().feasible(this, list, optimizer);
    }
    
    public boolean memoryUsageOK(final double n, final int n2) throws StandardException {
        return this.userSpecifiedJoinStrategy != null || ((n > 2.147483647E9) ? Integer.MAX_VALUE : ((int)n)) <= this.maxCapacity(this.getCurrentAccessPath().getJoinStrategy(), n2);
    }
    
    public void isJoinColumnForRightOuterJoin(final ResultColumn resultColumn) {
    }
    
    public boolean legalJoinOrder(final JBitSet set) {
        return true;
    }
    
    public int getNumColumnsReturned() {
        return this.resultColumns.size();
    }
    
    public boolean isTargetTable() {
        return false;
    }
    
    public boolean isOneRowScan() throws StandardException {
        return this.isOneRowResultSet();
    }
    
    public void initAccessPaths(final Optimizer optimizer) {
        if (this.currentAccessPath == null) {
            this.currentAccessPath = new AccessPathImpl(optimizer);
        }
        if (this.bestAccessPath == null) {
            this.bestAccessPath = new AccessPathImpl(optimizer);
        }
        if (this.bestSortAvoidancePath == null) {
            this.bestSortAvoidancePath = new AccessPathImpl(optimizer);
        }
        if (this.trulyTheBestAccessPath == null) {
            this.trulyTheBestAccessPath = new AccessPathImpl(optimizer);
        }
    }
    
    public double uniqueJoin(final OptimizablePredicateList list) throws StandardException {
        return -1.0;
    }
    
    String getUserSpecifiedJoinStrategy() {
        if (this.tableProperties == null) {
            return null;
        }
        return this.tableProperties.getProperty("joinStrategy");
    }
    
    protected boolean cursorTargetTable() {
        return false;
    }
    
    protected CostEstimate getCostEstimate(final Optimizer optimizer) {
        if (this.costEstimate == null) {
            this.costEstimate = optimizer.newCostEstimate();
        }
        return this.costEstimate;
    }
    
    protected CostEstimate getScratchCostEstimate(final Optimizer optimizer) {
        if (this.scratchCostEstimate == null) {
            this.scratchCostEstimate = optimizer.newCostEstimate();
        }
        return this.scratchCostEstimate;
    }
    
    protected void setCostEstimate(final CostEstimate cost) {
        (this.costEstimate = this.getCostEstimate()).setCost(cost);
    }
    
    protected void assignCostEstimate(final CostEstimate costEstimate) {
        this.costEstimate = costEstimate;
    }
    
    public String toString() {
        return "";
    }
    
    public ResultColumnList getResultColumnsForList(final TableName tableName, final ResultColumnList list, final TableName tableName2) throws StandardException {
        TableName tableName3;
        if (this.correlationName == null) {
            tableName3 = tableName2;
        }
        else if (tableName != null) {
            tableName3 = this.makeTableName(tableName.getSchemaName(), this.correlationName);
        }
        else {
            tableName3 = this.makeTableName(null, this.correlationName);
        }
        if (tableName != null && !tableName.equals(tableName3)) {
            return null;
        }
        TableName tableName4;
        if (this.correlationName == null) {
            tableName4 = tableName2;
        }
        else {
            tableName4 = this.makeTableName(null, this.correlationName);
        }
        final ResultColumnList list2 = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
        for (int size = list.size(), i = 0; i < size; ++i) {
            final String name = ((ResultColumn)list.elementAt(i)).getName();
            list2.addResultColumn((ResultColumn)this.getNodeFactory().getNode(80, name, this.getNodeFactory().getNode(62, name, tableName4, this.getContextManager()), this.getContextManager()));
        }
        return list2;
    }
    
    void pushExpressions(final PredicateList list) throws StandardException {
    }
    
    public String getExposedName() throws StandardException {
        return null;
    }
    
    public void setTableNumber(final int tableNumber) {
        this.tableNumber = tableNumber;
    }
    
    public TableName getTableName() throws StandardException {
        if (this.correlationName == null) {
            return null;
        }
        if (this.corrTableName == null) {
            this.corrTableName = this.makeTableName(null, this.correlationName);
        }
        return this.corrTableName;
    }
    
    public void setLevel(final int level) {
        this.level = level;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    void decrementLevel(final int n) {
        if (this.level > 0) {
            this.level -= n;
        }
    }
    
    public SchemaDescriptor getSchemaDescriptor() throws StandardException {
        return this.getSchemaDescriptor(this.corrTableName);
    }
    
    public SchemaDescriptor getSchemaDescriptor(final TableName tableName) throws StandardException {
        return this.getSchemaDescriptor(tableName.getSchemaName());
    }
    
    protected FromTable getFromTableByName(final String anObject, final String s, final boolean b) throws StandardException {
        if (s != null) {
            return null;
        }
        if (this.getExposedName().equals(anObject)) {
            return this;
        }
        return null;
    }
    
    public boolean isFlattenableJoinNode() {
        return false;
    }
    
    public boolean LOJ_reorderable(final int n) throws StandardException {
        return false;
    }
    
    public FromTable transformOuterJoins(final ValueNode valueNode, final int n) throws StandardException {
        return this;
    }
    
    public void fillInReferencedTableMap(final JBitSet set) {
        if (this.tableNumber != -1) {
            set.set(this.tableNumber);
        }
    }
    
    protected void markUpdatableByCursor(final List list) {
        this.resultColumns.markUpdatableByCursor(list);
    }
    
    public FromList flatten(final ResultColumnList list, final PredicateList list2, final SubqueryList list3, final GroupByList list4, final ValueNode valueNode) throws StandardException {
        return null;
    }
    
    void optimizeSubqueries(final DataDictionary dataDictionary, final double n) throws StandardException {
    }
    
    protected void tellRowOrderingAboutConstantColumns(final RowOrdering rowOrdering, final OptimizablePredicateList list) {
        if (list != null) {
            for (int i = 0; i < list.size(); ++i) {
                final Predicate predicate = (Predicate)list.getOptPredicate(i);
                if (predicate.equalsComparisonWithConstantExpression(this)) {
                    final ColumnReference columnOperand = predicate.getRelop().getColumnOperand(this);
                    if (columnOperand != null) {
                        rowOrdering.columnAlwaysOrdered(this, columnOperand.getColumnNumber());
                    }
                }
            }
        }
    }
    
    public boolean needsSpecialRCLBinding() {
        return false;
    }
    
    public void setOrigTableName(final TableName origTableName) {
        this.origTableName = origTableName;
    }
    
    public TableName getOrigTableName() {
        return this.origTableName;
    }
}
