// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.catalog.UUID;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.TriggerDescriptor;
import org.apache.derby.iapi.sql.dictionary.CheckConstraintDescriptor;
import java.util.List;
import org.apache.derby.iapi.sql.dictionary.GenericDescriptorList;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptorList;
import java.util.ArrayList;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.util.ReuseFactory;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptorList;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.impl.sql.execute.FKInfo;

public final class UpdateNode extends DMLModStatementNode
{
    public int[] changedColumnIds;
    public boolean deferred;
    public ValueNode checkConstraints;
    public FKInfo fkInfo;
    protected FromTable targetTable;
    protected FormatableBitSet readColsBitSet;
    protected boolean positionedUpdate;
    public static final String COLUMNNAME = "###RowLocationToUpdate";
    
    public void init(final Object o, final Object o2) {
        super.init(o2);
        this.targetTableName = (TableName)o;
    }
    
    public String toString() {
        return "";
    }
    
    public String statementToString() {
        return "UPDATE";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public void bindStatement() throws StandardException {
        this.getCompilerContext().pushCurrentPrivType(0);
        final FromList list = (FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager());
        TableName baseCursorTargetTableName = null;
        CurrentOfNode currentOfNode = null;
        ResultColumnList list2 = null;
        final DataDictionary dataDictionary = this.getDataDictionary();
        if (this.targetTableName != null) {
            final TableName resolveTableToSynonym = this.resolveTableToSynonym(this.targetTableName);
            if (resolveTableToSynonym != null) {
                this.synonymTableName = this.targetTableName;
                this.targetTableName = resolveTableToSynonym;
            }
        }
        this.bindTables(dataDictionary);
        final SelectNode selectNode = (SelectNode)this.resultSet;
        this.targetTable = (FromTable)selectNode.fromList.elementAt(0);
        if (this.targetTable instanceof CurrentOfNode) {
            this.positionedUpdate = true;
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
            throw StandardException.newException("42X29", this.targetTableName, currentOfNode.getCursorName());
        }
        this.verifyTargetTable();
        final ColumnDescriptorList list3 = new ColumnDescriptorList();
        final ColumnDescriptorList list4 = new ColumnDescriptorList();
        this.addGeneratedColumns(this.targetTableDescriptor, this.resultSet, list4, list3);
        this.resultSet.getResultColumns().markUpdated();
        this.resultSet.getFromList();
        if (this.synonymTableName != null) {
            this.normalizeSynonymColumns(this.resultSet.resultColumns, this.targetTable);
        }
        this.normalizeCorrelatedColumns(this.resultSet.resultColumns, this.targetTable);
        this.getCompilerContext().pushCurrentPrivType(this.getPrivType());
        this.resultSet.bindResultColumns(this.targetTableDescriptor, this.targetVTI, this.resultSet.resultColumns, this, list);
        this.getCompilerContext().popCurrentPrivType();
        this.forbidGenerationOverrides(this.resultSet.getResultColumns(), list3);
        if (!this.getLanguageConnectionContext().getAutoincrementUpdate()) {
            this.resultSet.getResultColumns().forbidOverrides(null);
        }
        boolean b = false;
        if (this.targetTable instanceof FromBaseTable) {
            ((FromBaseTable)this.targetTable).markUpdated(this.resultSet.getResultColumns());
        }
        else if (this.targetTable instanceof FromVTI) {
            this.resultColumnList = this.resultSet.getResultColumns();
        }
        else {
            final String[] updateColumns = currentOfNode.getCursorStatement().getUpdateColumns();
            if (updateColumns == null || updateColumns.length == 0) {
                this.getResultColumnList();
                list2 = this.resultSet.getResultColumns().expandToAll(this.targetTableDescriptor, this.targetTable.getTableName());
                this.getAffectedIndexes(this.targetTableDescriptor, null, null);
                b = true;
            }
            else {
                this.resultSet.getResultColumns().checkColumnUpdateability(updateColumns, currentOfNode.getCursorName());
            }
        }
        this.changedColumnIds = this.getChangedColumnIds(this.resultSet.getResultColumns());
        if (!b && this.targetVTI == null) {
            this.getCompilerContext().pushCurrentPrivType(-1);
            try {
                this.readColsBitSet = new FormatableBitSet();
                final FromBaseTable resultColumnList = this.getResultColumnList(this.resultSet.getResultColumns());
                list2 = this.resultSet.getResultColumns().copyListAndObjects();
                this.readColsBitSet = this.getReadMap(dataDictionary, this.targetTableDescriptor, list2, list4);
                list2 = resultColumnList.addColsToList(list2, this.readColsBitSet);
                this.resultColumnList = resultColumnList.addColsToList(this.resultColumnList, this.readColsBitSet);
                int n;
                int maxColumnID;
                for (n = 1, maxColumnID = this.targetTableDescriptor.getMaxColumnID(); n <= maxColumnID && this.readColsBitSet.get(n); ++n) {}
                if (n > maxColumnID) {
                    this.readColsBitSet = null;
                }
            }
            finally {
                this.getCompilerContext().popCurrentPrivType();
            }
        }
        ValueNode valueNode;
        if (this.targetVTI == null) {
            this.resultColumnList.appendResultColumns(list2, false);
            valueNode = (ValueNode)this.getNodeFactory().getNode(2, this.getContextManager());
        }
        else {
            valueNode = (ValueNode)this.getNodeFactory().getNode(70, ReuseFactory.getInteger(0), this.getContextManager());
        }
        final ResultColumn resultColumn = (ResultColumn)this.getNodeFactory().getNode(80, "###RowLocationToUpdate", valueNode, this.getContextManager());
        resultColumn.markGenerated();
        this.resultColumnList.addResultColumn(resultColumn);
        this.checkTableNameAndScrubResultColumns(this.resultColumnList);
        this.resultSet.setResultColumns(this.resultColumnList);
        this.getCompilerContext().pushCurrentPrivType(this.getPrivType());
        super.bindExpressions();
        this.getCompilerContext().popCurrentPrivType();
        this.resultSet.getResultColumns().bindUntypedNullsToResultColumns(this.resultColumnList);
        if (null != resultColumn) {
            resultColumn.bindResultColumnToExpression();
        }
        this.resultColumnList.checkStorableExpressions();
        if (!this.resultColumnList.columnTypesAndLengthsMatch()) {
            this.resultSet = (ResultSetNode)this.getNodeFactory().getNode(122, this.resultSet, this.resultColumnList, null, Boolean.TRUE, this.getContextManager());
            if (this.hasCheckConstraints(dataDictionary, this.targetTableDescriptor) || this.hasGenerationClauses(this.targetTableDescriptor)) {
                final int size = list2.size();
                list2 = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
                final ResultColumnList resultColumns = this.resultSet.getResultColumns();
                for (int i = 0; i < size; ++i) {
                    list2.addElement(resultColumns.elementAt(i + size));
                }
            }
        }
        if (null != this.targetVTI) {
            this.deferred = VTIDeferModPolicy.deferIt(2, this.targetVTI, this.resultColumnList.getColumnNames(), selectNode.getWhereClause());
        }
        else {
            final ResultColumnList list5 = (this.getAllRelevantTriggers(dataDictionary, this.targetTableDescriptor, this.changedColumnIds, true).size() > 0) ? this.resultColumnList : list2;
            this.parseAndBindGenerationClauses(dataDictionary, this.targetTableDescriptor, list2, this.resultColumnList, true, this.resultSet);
            this.checkConstraints = this.bindConstraints(dataDictionary, this.getNodeFactory(), this.targetTableDescriptor, null, list5, this.changedColumnIds, this.readColsBitSet, false, true);
            if (this.resultSet.subqueryReferencesTarget(this.targetTableDescriptor.getName(), true) || this.requiresDeferredProcessing()) {
                this.deferred = true;
            }
        }
        this.getCompilerContext().popCurrentPrivType();
    }
    
    int getPrivType() {
        return 1;
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        return this.resultSet.referencesSessionSchema();
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        if (!this.deferred) {
            final ConglomerateDescriptor conglomerateDescriptor = this.targetTable.getTrulyTheBestAccessPath().getConglomerateDescriptor();
            if (conglomerateDescriptor != null && conglomerateDescriptor.isIndex() && this.resultSet.getResultColumns().updateOverlaps(conglomerateDescriptor.getIndexDescriptor().baseColumnPositions())) {
                this.deferred = true;
            }
        }
        if (null == this.targetTableDescriptor) {
            return this.getGenericConstantActionFactory().getUpdatableVTIConstantAction(2, this.deferred, this.changedColumnIds);
        }
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
        return this.getGenericConstantActionFactory().getUpdateConstantAction(heapConglomerateId, this.targetTableDescriptor.getTableType(), transactionCompile.getStaticCompiledConglomInfo(heapConglomerateId), this.indicesToMaintain, this.indexConglomerateNumbers, array, this.indexNames, this.deferred, this.targetTableDescriptor.getUUID(), updateTargetLockMode, false, this.changedColumnIds, null, null, this.getFKInfo(), this.getTriggerInfo(), (this.readColsBitSet == null) ? ((FormatableBitSet)null) : new FormatableBitSet(this.readColsBitSet), DMLModStatementNode.getReadColMap(this.targetTableDescriptor.getNumberOfColumns(), this.readColsBitSet), this.resultColumnList.getStreamStorableColIds(this.targetTableDescriptor.getNumberOfColumns()), (this.readColsBitSet == null) ? this.targetTableDescriptor.getNumberOfColumns() : this.readColsBitSet.getNumBitsSet(), this.positionedUpdate, this.resultSet.isOneRowResultSet());
    }
    
    protected void setDeferredForUpdateOfIndexColumn() {
        if (!this.deferred) {
            final ConglomerateDescriptor conglomerateDescriptor = this.targetTable.getTrulyTheBestAccessPath().getConglomerateDescriptor();
            if (conglomerateDescriptor != null && conglomerateDescriptor.isIndex() && this.resultSet.getResultColumns().updateOverlaps(conglomerateDescriptor.getIndexDescriptor().baseColumnPositions())) {
                this.deferred = true;
            }
        }
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.generateCodeForTemporaryTable(activationClassBuilder);
        if (!this.isDependentTable) {
            this.generateParameterValueSet(activationClassBuilder);
        }
        activationClassBuilder.newFieldDeclaration(2, "org.apache.derby.iapi.sql.execute.CursorResultSet", activationClassBuilder.newRowLocationScanResultSetName());
        activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        this.resultSet.generate(activationClassBuilder, methodBuilder);
        if (null != this.targetVTI) {
            this.targetVTI.assignCostEstimate(this.resultSet.getNewCostEstimate());
            methodBuilder.callMethod((short)185, null, "getUpdateVTIResultSet", "org.apache.derby.iapi.sql.ResultSet", 1);
        }
        else {
            this.generateGenerationClauses(this.resultColumnList, this.resultSet.getResultSetNumber(), true, activationClassBuilder, methodBuilder);
            this.generateCheckConstraints(this.checkConstraints, activationClassBuilder, methodBuilder);
            if (this.isDependentTable) {
                methodBuilder.push(activationClassBuilder.addItem(this.makeConstantAction()));
                methodBuilder.push(activationClassBuilder.addItem(this.makeResultDescription()));
                methodBuilder.callMethod((short)185, null, "getDeleteCascadeUpdateResultSet", "org.apache.derby.iapi.sql.ResultSet", 5);
            }
            else {
                methodBuilder.callMethod((short)185, null, "getUpdateResultSet", "org.apache.derby.iapi.sql.ResultSet", 3);
            }
        }
    }
    
    protected final int getStatementType() {
        return 3;
    }
    
    public FormatableBitSet getReadMap(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final ResultColumnList list, final ColumnDescriptorList list2) throws StandardException {
        final boolean[] array = { this.requiresDeferredProcessing() };
        final ArrayList list3 = new ArrayList();
        this.relevantCdl = new ConstraintDescriptorList();
        this.relevantTriggers = new GenericDescriptorList();
        final FormatableBitSet updateReadMap = getUpdateReadMap(dataDictionary, tableDescriptor, list, list3, this.relevantCdl, this.relevantTriggers, array, list2);
        this.markAffectedIndexes(list3);
        this.adjustDeferredFlag(array[0]);
        return updateReadMap;
    }
    
    private int[] getChangedColumnIds(final ResultColumnList list) {
        if (list == null) {
            return null;
        }
        return list.sortMe();
    }
    
    public static FormatableBitSet getUpdateReadMap(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final ResultColumnList list, final List list2, final ConstraintDescriptorList list3, final GenericDescriptorList list4, final boolean[] array, final ColumnDescriptorList list5) throws StandardException {
        final int maxColumnID = tableDescriptor.getMaxColumnID();
        final FormatableBitSet set = new FormatableBitSet(maxColumnID + 1);
        final int[] sortMe = list.sortMe();
        for (int i = 0; i < sortMe.length; ++i) {
            set.set(sortMe[i]);
        }
        DMLModStatementNode.getXAffectedIndexes(tableDescriptor, list, set, list2);
        tableDescriptor.getAllRelevantConstraints(3, false, sortMe, array, list3);
        for (int size = list3.size(), j = 0; j < size; ++j) {
            final ConstraintDescriptor element = list3.elementAt(j);
            if (element.getConstraintType() == 4) {
                final int[] referencedColumns = ((CheckConstraintDescriptor)element).getReferencedColumns();
                for (int k = 0; k < referencedColumns.length; ++k) {
                    set.set(referencedColumns[k]);
                }
            }
        }
        addGeneratedColumnPrecursors(tableDescriptor, list5, set);
        tableDescriptor.getAllRelevantTriggers(3, sortMe, list4);
        if (list4.size() > 0) {
            array[0] = true;
            boolean b = false;
            final boolean checkVersion = dataDictionary.checkVersion(210, null);
            for (final TriggerDescriptor triggerDescriptor : list4) {
                if (checkVersion) {
                    final int[] referencedColsInTriggerAction = triggerDescriptor.getReferencedColsInTriggerAction();
                    final int[] referencedCols = triggerDescriptor.getReferencedCols();
                    if (referencedCols == null || referencedCols.length == 0) {
                        for (int l = 0; l < maxColumnID; ++l) {
                            set.set(l + 1);
                        }
                        break;
                    }
                    if (referencedColsInTriggerAction == null || referencedColsInTriggerAction.length == 0) {
                        if (triggerDescriptor.getReferencingNew() || triggerDescriptor.getReferencingOld()) {
                            b = true;
                            break;
                        }
                        for (int n = 0; n < referencedCols.length; ++n) {
                            set.set(referencedCols[n]);
                        }
                    }
                    else {
                        for (int n2 = 0; n2 < referencedCols.length; ++n2) {
                            set.set(referencedCols[n2]);
                        }
                        for (int n3 = 0; n3 < referencedColsInTriggerAction.length; ++n3) {
                            set.set(referencedColsInTriggerAction[n3]);
                        }
                    }
                }
                else {
                    if (!triggerDescriptor.getReferencingNew() && !triggerDescriptor.getReferencingOld()) {
                        continue;
                    }
                    b = true;
                    break;
                }
            }
            if (b) {
                for (int n4 = 1; n4 <= maxColumnID; ++n4) {
                    set.set(n4);
                }
            }
        }
        return set;
    }
    
    private static void addGeneratedColumnPrecursors(final TableDescriptor tableDescriptor, final ColumnDescriptorList list, final FormatableBitSet set) throws StandardException {
        for (int size = list.size(), i = 0; i < size; ++i) {
            final int[] columnIDs = tableDescriptor.getColumnIDs(list.elementAt(i).getDefaultInfo().getReferencedColumnNames());
            for (int length = columnIDs.length, j = 0; j < length; ++j) {
                set.set(columnIDs[j]);
            }
        }
    }
    
    private void addGeneratedColumns(final TableDescriptor tableDescriptor, final ResultSetNode resultSetNode, final ColumnDescriptorList list, final ColumnDescriptorList list2) throws StandardException {
        final ResultColumnList resultColumns = resultSetNode.getResultColumns();
        final int size = resultColumns.size();
        final ColumnDescriptorList generatedColumns = tableDescriptor.getGeneratedColumns();
        final int size2 = generatedColumns.size();
        tableDescriptor.getMaxColumnID();
        final HashSet set = new HashSet<String>();
        final UUID objectID = tableDescriptor.getObjectID();
        for (int i = 0; i < size; ++i) {
            set.add(((ResultColumn)resultColumns.elementAt(i)).getName());
        }
        for (int j = 0; j < size2; ++j) {
            final ColumnDescriptor element = generatedColumns.elementAt(j);
            final String[] referencedColumnNames = element.getDefaultInfo().getReferencedColumnNames();
            final int length = referencedColumnNames.length;
            if (set.contains(element.getColumnName())) {
                list.add(objectID, element);
            }
            int k = 0;
            while (k < length) {
                if (set.contains(referencedColumnNames[k])) {
                    list.add(objectID, element);
                    if (!set.contains(element.getColumnName())) {
                        list2.add(objectID, element);
                        final ResultColumn resultColumn = (ResultColumn)this.getNodeFactory().getNode(80, element.getType(), this.getNodeFactory().getNode(13, this.getContextManager()), this.getContextManager());
                        resultColumn.setColumnDescriptor(tableDescriptor, element);
                        resultColumn.setName(element.getColumnName());
                        resultColumns.addResultColumn(resultColumn);
                        break;
                    }
                    break;
                }
                else {
                    ++k;
                }
            }
        }
    }
    
    private void normalizeCorrelatedColumns(final ResultColumnList list, final FromTable fromTable) throws StandardException {
        final String correlationName = fromTable.getCorrelationName();
        if (correlationName == null) {
            return;
        }
        TableName tableNameNode;
        if (fromTable instanceof CurrentOfNode) {
            tableNameNode = ((CurrentOfNode)fromTable).getBaseCursorTargetTableName();
        }
        else {
            tableNameNode = this.makeTableName(null, fromTable.getBaseTableName());
        }
        for (int size = list.size(), i = 0; i < size; ++i) {
            final ColumnReference reference = ((ResultColumn)list.elementAt(i)).getReference();
            if (reference != null && correlationName.equals(reference.getTableName())) {
                reference.setTableNameNode(tableNameNode);
            }
        }
    }
    
    private void checkTableNameAndScrubResultColumns(final ResultColumnList list) throws StandardException {
        final int size = list.size();
        final int size2 = ((SelectNode)this.resultSet).fromList.size();
        for (int i = 0; i < size; ++i) {
            boolean b = false;
            final ResultColumn resultColumn = (ResultColumn)list.elementAt(i);
            if (resultColumn.getTableName() != null) {
                for (int j = 0; j < size2; ++j) {
                    final FromTable fromTable = (FromTable)((SelectNode)this.resultSet).fromList.elementAt(j);
                    String anObject;
                    if (fromTable instanceof CurrentOfNode) {
                        anObject = ((CurrentOfNode)fromTable).getBaseCursorTargetTableName().getTableName();
                    }
                    else {
                        anObject = fromTable.getBaseTableName();
                    }
                    if (resultColumn.getTableName().equals(anObject)) {
                        b = true;
                        break;
                    }
                }
                if (!b) {
                    throw StandardException.newException("42X04", resultColumn.getTableName() + "." + resultColumn.getName());
                }
            }
            resultColumn.clearTableName();
        }
    }
    
    private void normalizeSynonymColumns(final ResultColumnList list, final FromTable fromTable) throws StandardException {
        if (fromTable.getCorrelationName() != null) {
            return;
        }
        TableName tableName;
        if (fromTable instanceof CurrentOfNode) {
            tableName = ((CurrentOfNode)fromTable).getBaseCursorTargetTableName();
        }
        else {
            tableName = this.makeTableName(null, fromTable.getBaseTableName());
        }
        super.normalizeSynonymColumns(list, tableName);
    }
    
    private void forbidGenerationOverrides(final ResultColumnList list, final ColumnDescriptorList list2) throws StandardException {
        final int size = list.size();
        final ResultColumnList resultColumns = this.resultSet.getResultColumns();
        for (int i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)list.elementAt(i);
            if (resultColumn.hasGenerationClause() && !(((ResultColumn)resultColumns.elementAt(i)).getExpression() instanceof DefaultNode)) {
                boolean b = false;
                final String columnName = resultColumn.getTableColumnDescriptor().getColumnName();
                for (int size2 = list2.size(), j = 0; j < size2; ++j) {
                    if (columnName.equals(list2.elementAt(j).getColumnName())) {
                        b = true;
                        break;
                    }
                }
                if (!b) {
                    throw StandardException.newException("42XA3", resultColumn.getName());
                }
            }
        }
    }
}
