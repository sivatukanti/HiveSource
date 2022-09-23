// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import java.util.Iterator;
import org.apache.derby.iapi.sql.dictionary.TriggerDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.compile.NodeFactory;
import java.util.Properties;
import org.apache.derby.iapi.services.io.FormatableProperties;
import org.apache.derby.iapi.util.ReuseFactory;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptorList;
import java.util.List;
import org.apache.derby.iapi.sql.dictionary.GenericDescriptorList;
import java.util.ArrayList;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import java.util.HashSet;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.impl.sql.execute.FKInfo;

public class DeleteNode extends DMLModStatementNode
{
    private static final String COLUMNNAME = "###RowLocationToDelete";
    protected boolean deferred;
    protected FromTable targetTable;
    protected FKInfo fkInfo;
    protected FormatableBitSet readColsBitSet;
    private ConstantAction[] dependentConstantActions;
    private boolean cascadeDelete;
    private StatementNode[] dependentNodes;
    
    public void init(final Object o, final Object o2) {
        super.init(o2);
        this.targetTableName = (TableName)o;
    }
    
    public String statementToString() {
        return "DELETE";
    }
    
    public void bindStatement() throws StandardException {
        this.getCompilerContext().pushCurrentPrivType(0);
        try {
            final FromList list = (FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager());
            ResultColumn resultColumn = null;
            TableName baseCursorTargetTableName = null;
            CurrentOfNode currentOfNode = null;
            final DataDictionary dataDictionary = this.getDataDictionary();
            super.bindTables(dataDictionary);
            final SelectNode selectNode = (SelectNode)this.resultSet;
            this.targetTable = (FromTable)selectNode.fromList.elementAt(0);
            if (this.targetTable instanceof CurrentOfNode) {
                currentOfNode = (CurrentOfNode)this.targetTable;
                baseCursorTargetTableName = currentOfNode.getBaseCursorTargetTableName();
            }
            if (this.targetTable instanceof FromVTI) {
                (this.targetVTI = (FromVTI)this.targetTable).setTarget();
            }
            else if (this.targetTableName == null) {
                this.targetTableName = baseCursorTargetTableName;
            }
            else if (baseCursorTargetTableName != null && !this.targetTableName.equals(baseCursorTargetTableName)) {
                throw StandardException.newException("42X28", this.targetTableName, currentOfNode.getCursorName());
            }
            this.verifyTargetTable();
            if (this.targetTable instanceof FromVTI) {
                this.getResultColumnList();
                this.resultColumnList = this.targetTable.getResultColumnsForList(null, this.resultColumnList, null);
                this.resultSet.setResultColumns(this.resultColumnList);
            }
            else {
                this.resultColumnList = new ResultColumnList();
                final FromBaseTable resultColumnList = this.getResultColumnList(this.resultColumnList);
                this.readColsBitSet = this.getReadMap(dataDictionary, this.targetTableDescriptor);
                this.resultColumnList = resultColumnList.addColsToList(this.resultColumnList, this.readColsBitSet);
                int n;
                int maxColumnID;
                for (n = 1, maxColumnID = this.targetTableDescriptor.getMaxColumnID(); n <= maxColumnID && this.readColsBitSet.get(n); ++n) {}
                if (n > maxColumnID) {
                    this.readColsBitSet = null;
                }
                resultColumn = (ResultColumn)this.getNodeFactory().getNode(80, "###RowLocationToDelete", this.getNodeFactory().getNode(2, this.getContextManager()), this.getContextManager());
                resultColumn.markGenerated();
                this.resultColumnList.addResultColumn(resultColumn);
                this.correlateAddedColumns(this.resultColumnList, this.targetTable);
                this.resultSet.setResultColumns(this.resultColumnList);
            }
            super.bindExpressions();
            this.resultSet.getResultColumns().bindUntypedNullsToResultColumns(this.resultColumnList);
            if (!(this.targetTable instanceof FromVTI)) {
                resultColumn.bindResultColumnToExpression();
                this.bindConstraints(dataDictionary, this.getNodeFactory(), this.targetTableDescriptor, null, this.resultColumnList, null, this.readColsBitSet, false, true);
                if (this.resultSet.subqueryReferencesTarget(this.targetTableDescriptor.getName(), true) || this.requiresDeferredProcessing()) {
                    this.deferred = true;
                }
            }
            else {
                this.deferred = VTIDeferModPolicy.deferIt(3, this.targetVTI, null, selectNode.getWhereClause());
            }
            if (this.fkTableNames != null) {
                final String string = this.targetTableDescriptor.getSchemaName() + "." + this.targetTableDescriptor.getName();
                if (!this.isDependentTable) {
                    this.dependentTables = new HashSet();
                }
                if (this.dependentTables.add(string)) {
                    this.cascadeDelete = true;
                    final int length = this.fkTableNames.length;
                    this.dependentNodes = new StatementNode[length];
                    for (int i = 0; i < length; ++i) {
                        (this.dependentNodes[i] = this.getDependentTableNode(this.fkTableNames[i], this.fkRefActions[i], this.fkColDescriptors[i])).bindStatement();
                    }
                }
            }
            else if (this.isDependentTable) {
                this.dependentTables.add(this.targetTableDescriptor.getSchemaName() + "." + this.targetTableDescriptor.getName());
            }
            if (this.isPrivilegeCollectionRequired()) {
                this.getCompilerContext().pushCurrentPrivType(this.getPrivType());
                this.getCompilerContext().addRequiredTablePriv(this.targetTableDescriptor);
                this.getCompilerContext().popCurrentPrivType();
            }
        }
        finally {
            this.getCompilerContext().popCurrentPrivType();
        }
    }
    
    int getPrivType() {
        return 4;
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        return this.resultSet.referencesSessionSchema();
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        if (this.targetTableDescriptor != null) {
            int updateTargetLockMode = this.resultSet.updateTargetLockMode();
            final long heapConglomerateId = this.targetTableDescriptor.getHeapConglomerateId();
            final TransactionController transactionCompile = this.getLanguageConnectionContext().getTransactionCompile();
            final StaticCompiledOpenConglomInfo[] array = new StaticCompiledOpenConglomInfo[this.indexConglomerateNumbers.length];
            for (int i = 0; i < array.length; ++i) {
                array[i] = transactionCompile.getStaticCompiledConglomInfo(this.indexConglomerateNumbers[i]);
            }
            if (this.targetTableDescriptor.getLockGranularity() == 'T') {
                updateTargetLockMode = 7;
            }
            ResultDescription resultDescription = null;
            if (this.isDependentTable) {
                resultDescription = this.makeResultDescription();
            }
            return this.getGenericConstantActionFactory().getDeleteConstantAction(heapConglomerateId, this.targetTableDescriptor.getTableType(), transactionCompile.getStaticCompiledConglomInfo(heapConglomerateId), this.indicesToMaintain, this.indexConglomerateNumbers, array, this.deferred, false, this.targetTableDescriptor.getUUID(), updateTargetLockMode, null, null, null, 0L, null, null, resultDescription, this.getFKInfo(), this.getTriggerInfo(), (this.readColsBitSet == null) ? ((FormatableBitSet)null) : new FormatableBitSet(this.readColsBitSet), DMLModStatementNode.getReadColMap(this.targetTableDescriptor.getNumberOfColumns(), this.readColsBitSet), this.resultColumnList.getStreamStorableColIds(this.targetTableDescriptor.getNumberOfColumns()), (this.readColsBitSet == null) ? this.targetTableDescriptor.getNumberOfColumns() : this.readColsBitSet.getNumBitsSet(), null, this.resultSet.isOneRowResultSet(), this.dependentConstantActions);
        }
        return this.getGenericConstantActionFactory().getUpdatableVTIConstantAction(3, this.deferred);
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.generateCodeForTemporaryTable(activationClassBuilder);
        if (!this.isDependentTable) {
            this.generateParameterValueSet(activationClassBuilder);
        }
        activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        activationClassBuilder.newRowLocationScanResultSetName();
        this.resultSet.generate(activationClassBuilder, methodBuilder);
        String s;
        int n;
        if (this.targetTableDescriptor != null) {
            activationClassBuilder.newFieldDeclaration(2, "org.apache.derby.iapi.sql.execute.CursorResultSet", activationClassBuilder.getRowLocationScanResultSetName());
            if (this.cascadeDelete || this.isDependentTable) {
                s = "getDeleteCascadeResultSet";
                n = 4;
            }
            else {
                s = "getDeleteResultSet";
                n = 1;
            }
        }
        else {
            n = 1;
            s = "getDeleteVTIResultSet";
        }
        if (this.isDependentTable) {
            methodBuilder.push(activationClassBuilder.addItem(this.makeConstantAction()));
        }
        else if (this.cascadeDelete) {
            methodBuilder.push(-1);
        }
        final String s2 = "org.apache.derby.iapi.sql.ResultSet[]";
        if (this.cascadeDelete) {
            final String string = this.targetTableDescriptor.getSchemaName() + "." + this.targetTableDescriptor.getName();
            final LocalField fieldDeclaration = activationClassBuilder.newFieldDeclaration(2, s2);
            methodBuilder.pushNewArray("org.apache.derby.iapi.sql.ResultSet", this.dependentNodes.length);
            methodBuilder.setField(fieldDeclaration);
            for (int i = 0; i < this.dependentNodes.length; ++i) {
                this.dependentNodes[i].setRefActionInfo(this.fkIndexConglomNumbers[i], this.fkColArrays[i], string, true);
                methodBuilder.getField(fieldDeclaration);
                if (methodBuilder.statementNumHitLimit(10)) {
                    final MethodBuilder generatedFun = activationClassBuilder.newGeneratedFun("org.apache.derby.iapi.sql.ResultSet", 2);
                    this.dependentNodes[i].generate(activationClassBuilder, generatedFun);
                    generatedFun.methodReturn();
                    generatedFun.complete();
                    methodBuilder.pushThis();
                    methodBuilder.callMethod((short)182, null, generatedFun.getName(), "org.apache.derby.iapi.sql.ResultSet", 0);
                }
                else {
                    this.dependentNodes[i].generate(activationClassBuilder, methodBuilder);
                }
                methodBuilder.setArrayElement(i);
            }
            methodBuilder.getField(fieldDeclaration);
        }
        else if (this.isDependentTable) {
            methodBuilder.pushNull(s2);
        }
        if (this.cascadeDelete || this.isDependentTable) {
            methodBuilder.push(this.targetTableDescriptor.getSchemaName() + "." + this.targetTableDescriptor.getName());
        }
        methodBuilder.callMethod((short)185, null, s, "org.apache.derby.iapi.sql.ResultSet", n);
        if (!this.isDependentTable && this.cascadeDelete) {
            final int rowCount = activationClassBuilder.getRowCount();
            if (rowCount > 0) {
                final MethodBuilder constructor = activationClassBuilder.getConstructor();
                constructor.pushThis();
                constructor.pushNewArray("org.apache.derby.iapi.sql.execute.CursorResultSet", rowCount);
                constructor.putField("org.apache.derby.impl.sql.execute.BaseActivation", "raParentResultSets", "org.apache.derby.iapi.sql.execute.CursorResultSet[]");
                constructor.endStatement();
            }
        }
    }
    
    protected final int getStatementType() {
        return 4;
    }
    
    public FormatableBitSet getReadMap(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor) throws StandardException {
        final boolean[] array = { this.requiresDeferredProcessing() };
        final ArrayList list = new ArrayList();
        this.relevantTriggers = new GenericDescriptorList();
        final FormatableBitSet deleteReadMap = getDeleteReadMap(tableDescriptor, list, this.relevantTriggers, array);
        this.markAffectedIndexes(list);
        this.adjustDeferredFlag(array[0]);
        return deleteReadMap;
    }
    
    private StatementNode getDependentTableNode(final String s, final int n, final ColumnDescriptorList list) throws StandardException {
        DMLModStatementNode dmlModStatementNode = null;
        final int index = s.indexOf(46);
        final String substring = s.substring(0, index);
        final String substring2 = s.substring(index + 1);
        if (n == 0) {
            dmlModStatementNode = this.getEmptyDeleteNode(substring, substring2);
        }
        if (n == 3) {
            dmlModStatementNode = this.getEmptyUpdateNode(substring, substring2, list);
        }
        if (dmlModStatementNode != null) {
            dmlModStatementNode.isDependentTable = true;
            dmlModStatementNode.dependentTables = this.dependentTables;
        }
        return dmlModStatementNode;
    }
    
    private DeleteNode getEmptyDeleteNode(final String s, final String s2) throws StandardException {
        final Object o = null;
        final TableName tableName = new TableName();
        tableName.init(s, s2);
        final NodeFactory nodeFactory = this.getNodeFactory();
        final FromList list = (FromList)nodeFactory.getNode(37, this.getContextManager());
        final FromTable fromTable = (FromTable)nodeFactory.getNode(135, tableName, null, ReuseFactory.getInteger(2), null, this.getContextManager());
        final FormatableProperties tableProperties = new FormatableProperties();
        tableProperties.put("index", "null");
        ((FromBaseTable)fromTable).setTableProperties(tableProperties);
        list.addFromTable(fromTable);
        return (DeleteNode)nodeFactory.getNode(101, tableName, nodeFactory.getNode(129, null, null, list, o, null, null, null, this.getContextManager()), this.getContextManager());
    }
    
    private UpdateNode getEmptyUpdateNode(final String s, final String s2, final ColumnDescriptorList list) throws StandardException {
        final Object o = null;
        final TableName tableName = new TableName();
        tableName.init(s, s2);
        final NodeFactory nodeFactory = this.getNodeFactory();
        final FromList list2 = (FromList)nodeFactory.getNode(37, this.getContextManager());
        final FromTable fromTable = (FromTable)nodeFactory.getNode(135, tableName, null, ReuseFactory.getInteger(2), null, this.getContextManager());
        final FormatableProperties tableProperties = new FormatableProperties();
        tableProperties.put("index", "null");
        ((FromBaseTable)fromTable).setTableProperties(tableProperties);
        list2.addFromTable(fromTable);
        return (UpdateNode)nodeFactory.getNode(102, tableName, nodeFactory.getNode(129, this.getSetClause(tableName, list), null, list2, o, null, null, null, this.getContextManager()), this.getContextManager());
    }
    
    private ResultColumnList getSetClause(final TableName tableName, final ColumnDescriptorList list) throws StandardException {
        final NodeFactory nodeFactory = this.getNodeFactory();
        final ResultColumnList list2 = (ResultColumnList)nodeFactory.getNode(9, this.getContextManager());
        final ValueNode valueNode = (ValueNode)nodeFactory.getNode(13, this.getContextManager());
        for (int i = 0; i < list.size(); ++i) {
            final ColumnDescriptor element = list.elementAt(i);
            if (element.getType().isNullable()) {
                list2.addResultColumn((ResultColumn)nodeFactory.getNode(80, element, valueNode, this.getContextManager()));
            }
        }
        return list2;
    }
    
    public void optimizeStatement() throws StandardException {
        if (this.cascadeDelete) {
            for (int i = 0; i < this.dependentNodes.length; ++i) {
                this.dependentNodes[i].optimizeStatement();
            }
        }
        super.optimizeStatement();
    }
    
    private static FormatableBitSet getDeleteReadMap(final TableDescriptor tableDescriptor, final List list, final GenericDescriptorList list2, final boolean[] array) throws StandardException {
        final int maxColumnID = tableDescriptor.getMaxColumnID();
        final FormatableBitSet set = new FormatableBitSet(maxColumnID + 1);
        DMLModStatementNode.getXAffectedIndexes(tableDescriptor, null, set, list);
        tableDescriptor.getAllRelevantTriggers(4, null, list2);
        if (list2.size() > 0) {
            array[0] = true;
            boolean b = false;
            for (final TriggerDescriptor triggerDescriptor : list2) {
                if (!triggerDescriptor.getReferencingNew() && !triggerDescriptor.getReferencingOld()) {
                    continue;
                }
                b = true;
                break;
            }
            if (b) {
                for (int i = 1; i <= maxColumnID; ++i) {
                    set.set(i);
                }
            }
        }
        return set;
    }
    
    private void correlateAddedColumns(final ResultColumnList list, final FromTable fromTable) throws StandardException {
        final String correlationName = fromTable.getCorrelationName();
        if (correlationName == null) {
            return;
        }
        final TableName tableName = this.makeTableName(null, correlationName);
        for (int size = list.size(), i = 0; i < size; ++i) {
            final ValueNode expression = ((ResultColumn)list.elementAt(i)).getExpression();
            if (expression != null && expression instanceof ColumnReference) {
                ((ColumnReference)expression).setTableNameNode(tableName);
            }
        }
    }
}
