// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.execute.CursorActivation;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.compile.OptimizableList;
import org.apache.derby.iapi.sql.compile.RequiredRowOrdering;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptorList;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.execute.ExecCursorTableReference;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.RowOrdering;
import org.apache.derby.iapi.sql.compile.Optimizer;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.sql.execute.ExecPreparedStatement;

public final class CurrentOfNode extends FromTable
{
    private String cursorName;
    private ExecPreparedStatement preStmt;
    private TableName exposedTableName;
    private TableName baseTableName;
    private CostEstimate singleScanCostEstimate;
    
    public void init(final Object o, final Object o2, final Object o3) {
        super.init(o, o3);
        this.cursorName = (String)o2;
    }
    
    public CostEstimate estimateCost(final OptimizablePredicateList list, final ConglomerateDescriptor conglomerateDescriptor, final CostEstimate costEstimate, final Optimizer optimizer, final RowOrdering rowOrdering) throws StandardException {
        if (this.singleScanCostEstimate == null) {
            this.singleScanCostEstimate = optimizer.newCostEstimate();
        }
        this.singleScanCostEstimate.setCost(0.0, 1.0, 1.0);
        this.getBestAccessPath().setCostEstimate(this.singleScanCostEstimate);
        this.getBestSortAvoidancePath().setCostEstimate(this.singleScanCostEstimate);
        return this.singleScanCostEstimate;
    }
    
    public ResultSetNode bindNonVTITables(final DataDictionary dataDictionary, final FromList list) throws StandardException {
        this.preStmt = this.getCursorStatement();
        if (this.preStmt == null) {
            throw StandardException.newException("42X30", this.cursorName);
        }
        this.preStmt.rePrepare(this.getLanguageConnectionContext());
        if (this.preStmt.getUpdateMode() != 2) {
            throw StandardException.newException("42X23", (this.cursorName == null) ? "" : this.cursorName);
        }
        final ExecCursorTableReference targetTable = this.preStmt.getTargetTable();
        final String schemaName = targetTable.getSchemaName();
        this.exposedTableName = this.makeTableName(null, targetTable.getExposedName());
        this.baseTableName = this.makeTableName(schemaName, targetTable.getBaseName());
        final SchemaDescriptor schemaDescriptor = this.getSchemaDescriptor(targetTable.getSchemaName());
        if (schemaDescriptor == null) {
            throw StandardException.newException("42Y07", targetTable.getSchemaName());
        }
        final TableDescriptor tableDescriptor = this.getTableDescriptor(targetTable.getBaseName(), schemaDescriptor);
        if (tableDescriptor == null) {
            throw StandardException.newException("42X05", targetTable.getBaseName());
        }
        this.resultColumns = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
        final ColumnDescriptorList columnDescriptorList = tableDescriptor.getColumnDescriptorList();
        for (int size = columnDescriptorList.size(), i = 0; i < size; ++i) {
            final ColumnDescriptor element = columnDescriptorList.elementAt(i);
            this.resultColumns.addResultColumn((ResultColumn)this.getNodeFactory().getNode(80, element, this.getNodeFactory().getNode(94, element.getColumnName(), this.exposedTableName, element.getType(), this.getContextManager()), this.getContextManager()));
        }
        if (this.tableNumber == -1) {
            this.tableNumber = this.getCompilerContext().getNextTableNumber();
        }
        return this;
    }
    
    public void bindExpressions(final FromList list) {
    }
    
    public ResultColumn getMatchingColumn(final ColumnReference columnReference) throws StandardException {
        ResultColumn resultColumn = null;
        final TableName tableNameNode = columnReference.getTableNameNode();
        if (tableNameNode != null && tableNameNode.getSchemaName() == null && this.correlationName == null) {
            tableNameNode.bind(this.getDataDictionary());
        }
        if (this.baseTableName != null && this.baseTableName.getSchemaName() == null && this.correlationName == null) {
            this.baseTableName.bind(this.getDataDictionary());
        }
        if (tableNameNode == null || tableNameNode.getFullTableName().equals(this.baseTableName.getFullTableName()) || (this.correlationName != null && this.correlationName.equals(tableNameNode.getTableName()))) {
            resultColumn = this.resultColumns.getResultColumn(columnReference.getColumnName());
            boolean b;
            if (resultColumn != null) {
                columnReference.setTableNumber(this.tableNumber);
                columnReference.setColumnNumber(resultColumn.getColumnPosition());
                b = (resultColumn.updatableByCursor() && !this.foundString(this.preStmt.getUpdateColumns(), columnReference.getColumnName()));
            }
            else {
                b = true;
            }
            if (b) {
                throw StandardException.newException("42X31", columnReference.getColumnName(), (this.cursorName == null) ? "" : this.cursorName);
            }
        }
        return resultColumn;
    }
    
    public ResultSetNode preprocess(final int n, final GroupByList list, final FromList list2) throws StandardException {
        this.referencedTableMap = new JBitSet(n);
        return this;
    }
    
    public ResultSetNode optimize(final DataDictionary dataDictionary, final PredicateList list, final double n) throws StandardException {
        (this.bestCostEstimate = this.getOptimizer((OptimizableList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this, this.getContextManager()), list, dataDictionary, null).newCostEstimate()).setCost(0.0, n, n);
        return this;
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.assignResultSetNumber();
        methodBuilder.pushThis();
        activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        methodBuilder.push(this.cursorName);
        activationClassBuilder.pushThisAsActivation(methodBuilder);
        methodBuilder.push(this.resultSetNumber);
        methodBuilder.callMethod((short)185, null, "getCurrentOfResultSet", "org.apache.derby.iapi.sql.execute.NoPutResultSet", 3);
        methodBuilder.cast("org.apache.derby.iapi.sql.execute.CursorResultSet");
        methodBuilder.putField(null, activationClassBuilder.getRowLocationScanResultSetName(), "org.apache.derby.iapi.sql.execute.CursorResultSet");
        methodBuilder.cast("org.apache.derby.iapi.sql.execute.NoPutResultSet");
        final MethodBuilder startResetMethod = activationClassBuilder.startResetMethod();
        startResetMethod.pushThis();
        startResetMethod.push(this.cursorName);
        startResetMethod.push(this.preStmt.getObjectName());
        startResetMethod.callMethod((short)182, "org.apache.derby.impl.sql.execute.BaseActivation", "checkPositionedStatement", "void", 2);
        startResetMethod.methodReturn();
        startResetMethod.complete();
    }
    
    public void printSubNodes(final int n) {
    }
    
    public String toString() {
        return "";
    }
    
    public String getExposedName() {
        return this.exposedTableName.getFullTableName();
    }
    
    public TableName getExposedTableName() {
        return this.exposedTableName;
    }
    
    public TableName getBaseCursorTargetTableName() {
        return this.baseTableName;
    }
    
    public String getCursorName() {
        return this.cursorName;
    }
    
    ExecPreparedStatement getCursorStatement() {
        final CursorActivation lookupCursorActivation = this.getLanguageConnectionContext().lookupCursorActivation(this.cursorName);
        if (lookupCursorActivation == null) {
            return null;
        }
        return lookupCursorActivation.getPreparedStatement();
    }
    
    public int updateTargetLockMode() {
        return 6;
    }
}
