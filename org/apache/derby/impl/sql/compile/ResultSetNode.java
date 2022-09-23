// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import java.util.Set;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.sql.compile.RequiredRowOrdering;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.sql.compile.OptimizableList;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.depend.Provider;
import java.util.List;
import org.apache.derby.catalog.types.DefaultInfoImpl;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.Optimizer;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.util.JBitSet;

public abstract class ResultSetNode extends QueryTreeNode
{
    int resultSetNumber;
    JBitSet referencedTableMap;
    ResultColumnList resultColumns;
    boolean statementResultSet;
    boolean cursorTargetTable;
    boolean insertSource;
    CostEstimate costEstimate;
    CostEstimate scratchCostEstimate;
    Optimizer optimizer;
    CostEstimate finalCostEstimate;
    
    public String toString() {
        return "";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public int getResultSetNumber() {
        return this.resultSetNumber;
    }
    
    public CostEstimate getCostEstimate() {
        return this.costEstimate;
    }
    
    public CostEstimate getFinalCostEstimate() throws StandardException {
        return this.finalCostEstimate;
    }
    
    public void assignResultSetNumber() throws StandardException {
        this.resultSetNumber = this.getCompilerContext().getNextResultSetNumber();
        this.resultColumns.setResultSetNumber(this.resultSetNumber);
    }
    
    public ResultSetNode bindNonVTITables(final DataDictionary dataDictionary, final FromList list) throws StandardException {
        return this;
    }
    
    public ResultSetNode bindVTITables(final FromList list) throws StandardException {
        return this;
    }
    
    public void bindExpressions(final FromList list) throws StandardException {
    }
    
    public void bindExpressionsWithTables(final FromList list) throws StandardException {
    }
    
    public void bindTargetExpressions(final FromList list) throws StandardException {
    }
    
    void setTableConstructorTypes(final ResultColumnList list) throws StandardException {
        for (int i = 0; i < this.resultColumns.size(); ++i) {
            final ValueNode expression = ((ResultColumn)this.resultColumns.elementAt(i)).getExpression();
            if (expression != null && expression.requiresTypeFromContext()) {
                expression.setType(((ResultColumn)list.elementAt(i)).getTypeServices());
            }
        }
    }
    
    public void setInsertSource() {
        this.insertSource = true;
    }
    
    public void verifySelectStarSubquery(final FromList list, final int n) throws StandardException {
    }
    
    public ResultColumnList getAllResultColumns(final TableName tableName) throws StandardException {
        return null;
    }
    
    public ResultColumn getMatchingColumn(final ColumnReference columnReference) throws StandardException {
        return null;
    }
    
    public ResultSetNode setResultToBooleanTrueNode(final boolean b) throws StandardException {
        ResultColumn resultColumn;
        if (this.resultColumns.elementAt(0) instanceof AllResultColumn) {
            resultColumn = (ResultColumn)this.getNodeFactory().getNode(80, "", null, this.getContextManager());
        }
        else {
            if (b) {
                return this;
            }
            resultColumn = (ResultColumn)this.resultColumns.elementAt(0);
            if (resultColumn.getExpression().isBooleanTrue() && this.resultColumns.size() == 1) {
                return this;
            }
        }
        final BooleanConstantNode expression = (BooleanConstantNode)this.getNodeFactory().getNode(38, Boolean.TRUE, this.getContextManager());
        resultColumn.setExpression(expression);
        resultColumn.setType(expression.getTypeServices());
        resultColumn.setVirtualColumnId(1);
        this.resultColumns.setElementAt(resultColumn, 0);
        return this;
    }
    
    public FromList getFromList() throws StandardException {
        return (FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager());
    }
    
    public void bindResultColumns(final FromList list) throws StandardException {
        this.resultColumns.bindResultColumnsToExpressions();
    }
    
    public void bindResultColumns(final TableDescriptor tableDescriptor, final FromVTI fromVTI, final ResultColumnList list, final DMLStatementNode dmlStatementNode, final FromList list2) throws StandardException {
        if (this instanceof SelectNode) {
            this.resultColumns.expandAllsAndNameColumns(((SelectNode)this).fromList);
        }
        if (list != null) {
            this.resultColumns.copyResultColumnNames(list);
        }
        if (list != null) {
            if (tableDescriptor != null) {
                this.resultColumns.bindResultColumnsByName(tableDescriptor, dmlStatementNode);
            }
            else {
                this.resultColumns.bindResultColumnsByName(fromVTI.getResultColumns(), fromVTI, dmlStatementNode);
            }
        }
        else {
            this.resultColumns.bindResultColumnsByPosition(tableDescriptor);
        }
    }
    
    public void bindUntypedNullsToResultColumns(final ResultColumnList list) throws StandardException {
    }
    
    public ResultSetNode preprocess(final int n, final GroupByList list, final FromList list2) throws StandardException {
        return null;
    }
    
    void projectResultColumns() throws StandardException {
    }
    
    public ResultSetNode ensurePredicateList(final int n) throws StandardException {
        return null;
    }
    
    public ResultSetNode addNewPredicate(final Predicate predicate) throws StandardException {
        return null;
    }
    
    public boolean flattenableInFromSubquery(final FromList list) {
        return false;
    }
    
    ResultSetNode genProjectRestrictForReordering() throws StandardException {
        final ResultColumnList resultColumns = this.resultColumns;
        resultColumns.genVirtualColumnNodes(this, this.resultColumns = this.resultColumns.copyListAndObjects(), false);
        return (ResultSetNode)this.getNodeFactory().getNode(151, this, resultColumns, null, null, null, null, null, this.getContextManager());
    }
    
    public ResultSetNode optimize(final DataDictionary dataDictionary, final PredicateList list, final double n) throws StandardException {
        return null;
    }
    
    public ResultSetNode modifyAccessPaths() throws StandardException {
        return this;
    }
    
    public ResultSetNode modifyAccessPaths(final PredicateList list) throws StandardException {
        return this.modifyAccessPaths();
    }
    
    ResultColumnDescriptor[] makeResultDescriptors() {
        return this.resultColumns.makeResultDescriptors();
    }
    
    boolean columnTypesAndLengthsMatch() throws StandardException {
        return this.resultColumns.columnTypesAndLengthsMatch();
    }
    
    public void setResultColumns(final ResultColumnList resultColumns) {
        this.resultColumns = resultColumns;
    }
    
    public ResultColumnList getResultColumns() {
        return this.resultColumns;
    }
    
    public void setReferencedTableMap(final JBitSet referencedTableMap) {
        this.referencedTableMap = referencedTableMap;
    }
    
    public JBitSet getReferencedTableMap() {
        return this.referencedTableMap;
    }
    
    public void fillInReferencedTableMap(final JBitSet set) {
    }
    
    public void rejectParameters() throws StandardException {
        if (this.resultColumns != null) {
            this.resultColumns.rejectParameters();
        }
    }
    
    public void rejectXMLValues() throws StandardException {
        if (this.resultColumns != null) {
            this.resultColumns.rejectXMLValues();
        }
    }
    
    public void renameGeneratedResultNames() throws StandardException {
        for (int i = 0; i < this.resultColumns.size(); ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.resultColumns.elementAt(i);
            if (resultColumn.isNameGenerated()) {
                resultColumn.setName(Integer.toString(i + 1));
            }
        }
    }
    
    public void markStatementResultSet() {
        this.statementResultSet = true;
    }
    
    ResultSetNode enhanceRCLForInsert(final InsertNode insertNode, final boolean b, final int[] array) throws StandardException {
        if (!b || this.resultColumns.visibleSize() < insertNode.resultColumnList.size()) {
            return this.generateProjectRestrictForInsert(insertNode, array);
        }
        return this;
    }
    
    ResultColumnList getRCLForInsert(final InsertNode insertNode, final int[] array) throws StandardException {
        final ResultColumnList list = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
        for (int size = insertNode.resultColumnList.size(), i = 0; i < size; ++i) {
            ResultColumn resultColumn;
            if (array[i] != -1) {
                resultColumn = this.resultColumns.getResultColumn(array[i] + 1);
            }
            else {
                resultColumn = this.genNewRCForInsert(insertNode.targetTableDescriptor, insertNode.targetVTI, i + 1, insertNode.getDataDictionary());
            }
            list.addResultColumn(resultColumn);
        }
        return list;
    }
    
    private ResultColumn genNewRCForInsert(final TableDescriptor tableDescriptor, final FromVTI fromVTI, final int n, final DataDictionary dataDictionary) throws StandardException {
        ResultColumn resultColumn;
        if (fromVTI != null) {
            resultColumn = fromVTI.getResultColumns().getResultColumn(n).cloneMe();
            resultColumn.setExpressionToNullNode();
        }
        else {
            final ColumnDescriptor columnDescriptor = tableDescriptor.getColumnDescriptor(n);
            final DataTypeDescriptor type = columnDescriptor.getType();
            final DefaultInfoImpl defaultInfoImpl = (DefaultInfoImpl)columnDescriptor.getDefaultInfo();
            if (defaultInfoImpl != null && !columnDescriptor.isAutoincrement()) {
                if (columnDescriptor.hasGenerationClause()) {
                    resultColumn = this.createGeneratedColumn(tableDescriptor, columnDescriptor);
                }
                else {
                    final ValueNode bindExpression = this.parseDefault(defaultInfoImpl.getDefaultText()).bindExpression(this.getFromList(), null, null);
                    resultColumn = (ResultColumn)this.getNodeFactory().getNode(80, bindExpression.getTypeServices(), bindExpression, this.getContextManager());
                }
                this.getCompilerContext().createDependency(columnDescriptor.getDefaultDescriptor(dataDictionary));
            }
            else if (columnDescriptor.isAutoincrement()) {
                resultColumn = (ResultColumn)this.getNodeFactory().getNode(80, columnDescriptor, null, this.getContextManager());
                resultColumn.setAutoincrementGenerated();
            }
            else {
                resultColumn = (ResultColumn)this.getNodeFactory().getNode(80, type, this.getNullNode(type), this.getContextManager());
            }
        }
        resultColumn.markGeneratedForUnmatchedColumnInInsert();
        return resultColumn;
    }
    
    private ResultSetNode generateProjectRestrictForInsert(final InsertNode insertNode, final int[] array) throws StandardException {
        final ResultColumnList list = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
        for (int size = insertNode.resultColumnList.size(), i = 0; i < size; ++i) {
            ResultColumn genNewRCForInsert;
            if (array[i] != -1) {
                final ResultColumn resultColumn = this.resultColumns.getResultColumn(array[i] + 1);
                final ColumnReference columnReference = (ColumnReference)this.getNodeFactory().getNode(62, resultColumn.getName(), null, this.getContextManager());
                columnReference.setSource(resultColumn);
                columnReference.setType(resultColumn.getType());
                columnReference.setNestingLevel(0);
                columnReference.setSourceLevel(0);
                genNewRCForInsert = (ResultColumn)this.getNodeFactory().getNode(80, resultColumn.getType(), columnReference, this.getContextManager());
            }
            else {
                genNewRCForInsert = this.genNewRCForInsert(insertNode.targetTableDescriptor, insertNode.targetVTI, i + 1, insertNode.getDataDictionary());
            }
            list.addResultColumn(genNewRCForInsert);
        }
        return (ResultSetNode)this.getNodeFactory().getNode(151, this, list, null, null, null, null, null, this.getContextManager());
    }
    
    private ResultColumn createGeneratedColumn(final TableDescriptor tableDescriptor, final ColumnDescriptor columnDescriptor) throws StandardException {
        final ResultColumn resultColumn = (ResultColumn)this.getNodeFactory().getNode(80, columnDescriptor.getType(), this.getNodeFactory().getNode(13, this.getContextManager()), this.getContextManager());
        resultColumn.setColumnDescriptor(tableDescriptor, columnDescriptor);
        return resultColumn;
    }
    
    public ValueNode parseDefault(final String str) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = this.getLanguageConnectionContext();
        this.getCompilerContext();
        final String string = "VALUES " + str;
        final CompilerContext pushCompilerContext = languageConnectionContext.pushCompilerContext();
        final ValueNode expression = ((ResultColumn)((CursorNode)pushCompilerContext.getParser().parseStatement(string)).getResultSetNode().getResultColumns().elementAt(0)).getExpression();
        languageConnectionContext.popCompilerContext(pushCompilerContext);
        return expression;
    }
    
    public ResultDescription makeResultDescription() {
        return this.getExecutionFactory().getResultDescription(this.makeResultDescriptors(), null);
    }
    
    boolean isUpdatableCursor(final DataDictionary dataDictionary) throws StandardException {
        return false;
    }
    
    FromTable getCursorTargetTable() {
        return null;
    }
    
    public boolean markAsCursorTargetTable() {
        return false;
    }
    
    void notCursorTargetTable() {
        this.cursorTargetTable = false;
    }
    
    public ResultSetNode genProjectRestrict() throws StandardException {
        final ResultColumnList resultColumns = this.resultColumns;
        resultColumns.genVirtualColumnNodes(this, this.resultColumns = this.resultColumns.copyListAndObjects());
        return (ResultSetNode)this.getNodeFactory().getNode(151, this, resultColumns, null, null, null, null, null, this.getContextManager());
    }
    
    protected ResultSetNode genProjectRestrict(final int n) throws StandardException {
        return this.genProjectRestrict();
    }
    
    public void generateNormalizationResultSet(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder, final int n, final ResultDescription resultDescription) throws StandardException {
        final int addItem = activationClassBuilder.addItem(resultDescription);
        methodBuilder.push(n);
        methodBuilder.push(addItem);
        methodBuilder.push(this.getCostEstimate().rowCount());
        methodBuilder.push(this.getCostEstimate().getEstimatedCost());
        methodBuilder.push(false);
        methodBuilder.callMethod((short)185, null, "getNormalizeResultSet", "org.apache.derby.iapi.sql.execute.NoPutResultSet", 6);
    }
    
    public ResultSetNode changeAccessPath() throws StandardException {
        return this;
    }
    
    public boolean referencesTarget(final String s, final boolean b) throws StandardException {
        return false;
    }
    
    boolean subqueryReferencesTarget(final String s, final boolean b) throws StandardException {
        return false;
    }
    
    public boolean isOneRowResultSet() throws StandardException {
        return false;
    }
    
    public boolean isNotExists() {
        return false;
    }
    
    protected Optimizer getOptimizer(final OptimizableList list, final OptimizablePredicateList list2, final DataDictionary dataDictionary, final RequiredRowOrdering requiredRowOrdering) throws StandardException {
        if (this.optimizer == null) {
            this.optimizer = this.getLanguageConnectionContext().getOptimizerFactory().getOptimizer(list, list2, dataDictionary, requiredRowOrdering, this.getCompilerContext().getNumTables(), this.getLanguageConnectionContext());
        }
        this.optimizer.prepForNextRound();
        return this.optimizer;
    }
    
    protected OptimizerImpl getOptimizerImpl() {
        return (OptimizerImpl)this.optimizer;
    }
    
    protected CostEstimate getNewCostEstimate() throws StandardException {
        return this.getLanguageConnectionContext().getOptimizerFactory().getCostEstimate();
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.resultColumns != null) {
            this.resultColumns = (ResultColumnList)this.resultColumns.accept(visitor);
        }
    }
    
    public ResultSetNode considerMaterialization(final JBitSet set) throws StandardException {
        return this;
    }
    
    public boolean performMaterialization(final JBitSet set) throws StandardException {
        return false;
    }
    
    protected FromTable getFromTableByName(final String s, final String s2, final boolean b) throws StandardException {
        return null;
    }
    
    abstract void decrementLevel(final int p0);
    
    void pushOrderByList(final OrderByList list) {
    }
    
    void pushOffsetFetchFirst(final ValueNode valueNode, final ValueNode valueNode2, final boolean b) {
    }
    
    public void generateResultSet(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        System.out.println("I am a " + this.getClass());
    }
    
    public int updateTargetLockMode() {
        return 7;
    }
    
    void notFlattenableJoin() {
    }
    
    boolean isOrderedOn(final ColumnReference[] array, final boolean b, final List list) throws StandardException {
        return false;
    }
    
    boolean returnsAtMostOneRow() {
        return false;
    }
    
    void replaceOrForbidDefaults(final TableDescriptor tableDescriptor, final ResultColumnList list, final boolean b) throws StandardException {
    }
    
    boolean isPossibleDistinctScan(final Set set) {
        return false;
    }
    
    void markForDistinctScan() {
    }
    
    void adjustForSortElimination() {
    }
    
    void adjustForSortElimination(final RequiredRowOrdering requiredRowOrdering) throws StandardException {
        this.adjustForSortElimination();
    }
    
    protected static final int numDistinctAggregates(final List list) {
        int n = 0;
        for (int size = list.size(), i = 0; i < size; ++i) {
            n += (list.get(i).isDistinct() ? 1 : 0);
        }
        return n;
    }
    
    public JBitSet LOJgetReferencedTables(final int n) throws StandardException {
        if (this instanceof FromTable && ((FromTable)this).tableNumber != -1) {
            final JBitSet set = new JBitSet(n);
            set.set(((FromTable)this).tableNumber);
            return set;
        }
        return null;
    }
}
