// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptorList;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDescriptorGenerator;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.store.access.ColumnOrdering;
import org.apache.derby.iapi.sql.Activation;
import java.util.Properties;

class CreateTableConstantAction extends DDLConstantAction
{
    private char lockGranularity;
    private boolean onCommitDeleteRows;
    private boolean onRollbackDeleteRows;
    private String tableName;
    private String schemaName;
    private int tableType;
    private ColumnInfo[] columnInfo;
    private CreateConstraintConstantAction[] constraintActions;
    private Properties properties;
    
    CreateTableConstantAction(final String schemaName, final String tableName, final int tableType, final ColumnInfo[] columnInfo, final CreateConstraintConstantAction[] constraintActions, final Properties properties, final char lockGranularity, final boolean onCommitDeleteRows, final boolean onRollbackDeleteRows) {
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.tableType = tableType;
        this.columnInfo = columnInfo;
        this.constraintActions = constraintActions;
        this.properties = properties;
        this.lockGranularity = lockGranularity;
        this.onCommitDeleteRows = onCommitDeleteRows;
        this.onRollbackDeleteRows = onRollbackDeleteRows;
    }
    
    public String toString() {
        if (this.tableType == 3) {
            return this.constructToString("DECLARE GLOBAL TEMPORARY TABLE ", this.tableName);
        }
        return this.constructToString("CREATE TABLE ", this.tableName);
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        dataDictionary.getDependencyManager();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        activation.setForCreateTable();
        final ExecRow emptyValueRow = RowUtil.getEmptyValueRow(this.columnInfo.length, languageConnectionContext);
        final int[] array = new int[this.columnInfo.length];
        for (int i = 0; i < this.columnInfo.length; ++i) {
            final ColumnInfo columnInfo = this.columnInfo[i];
            if (columnInfo.defaultValue != null) {
                emptyValueRow.setColumn(i + 1, columnInfo.defaultValue);
            }
            else {
                emptyValueRow.setColumn(i + 1, columnInfo.dataType.getNull());
            }
            array[i] = columnInfo.dataType.getCollationType();
        }
        final long conglomerate = transactionExecute.createConglomerate("heap", emptyValueRow.getRowArray(), null, array, this.properties, (this.tableType == 3) ? 3 : 0);
        if (this.tableType != 3) {
            dataDictionary.startWriting(languageConnectionContext);
        }
        SchemaDescriptor schemaDescriptor;
        if (this.tableType == 3) {
            schemaDescriptor = dataDictionary.getSchemaDescriptor(this.schemaName, transactionExecute, true);
        }
        else {
            schemaDescriptor = DDLConstantAction.getSchemaDescriptorForCreate(dataDictionary, activation, this.schemaName);
        }
        final DataDescriptorGenerator dataDescriptorGenerator = dataDictionary.getDataDescriptorGenerator();
        TableDescriptor ddlTableDescriptor;
        if (this.tableType != 3) {
            ddlTableDescriptor = dataDescriptorGenerator.newTableDescriptor(this.tableName, schemaDescriptor, this.tableType, this.lockGranularity);
            dataDictionary.addDescriptor(ddlTableDescriptor, schemaDescriptor, 1, false, transactionExecute);
        }
        else {
            ddlTableDescriptor = dataDescriptorGenerator.newTableDescriptor(this.tableName, schemaDescriptor, this.tableType, this.onCommitDeleteRows, this.onRollbackDeleteRows);
            ddlTableDescriptor.setUUID(dataDictionary.getUUIDFactory().createUUID());
        }
        final UUID uuid = ddlTableDescriptor.getUUID();
        activation.setDDLTableDescriptor(ddlTableDescriptor);
        int n = 1;
        final ColumnDescriptor[] array2 = new ColumnDescriptor[this.columnInfo.length];
        for (int j = 0; j < this.columnInfo.length; ++j) {
            UUID uuid2 = this.columnInfo[j].newDefaultUUID;
            if (this.columnInfo[j].defaultInfo != null && uuid2 == null) {
                uuid2 = dataDictionary.getUUIDFactory().createUUID();
            }
            ColumnDescriptor columnDescriptor;
            if (this.columnInfo[j].autoincInc != 0L) {
                columnDescriptor = new ColumnDescriptor(this.columnInfo[j].name, n++, this.columnInfo[j].dataType, this.columnInfo[j].defaultValue, this.columnInfo[j].defaultInfo, ddlTableDescriptor, uuid2, this.columnInfo[j].autoincStart, this.columnInfo[j].autoincInc, this.columnInfo[j].autoinc_create_or_modify_Start_Increment);
            }
            else {
                columnDescriptor = new ColumnDescriptor(this.columnInfo[j].name, n++, this.columnInfo[j].dataType, this.columnInfo[j].defaultValue, this.columnInfo[j].defaultInfo, ddlTableDescriptor, uuid2, this.columnInfo[j].autoincStart, this.columnInfo[j].autoincInc);
            }
            array2[j] = columnDescriptor;
        }
        if (this.tableType != 3) {
            dataDictionary.addDescriptorArray(array2, ddlTableDescriptor, 2, false, transactionExecute);
        }
        final ColumnDescriptorList columnDescriptorList = ddlTableDescriptor.getColumnDescriptorList();
        for (int k = 0; k < array2.length; ++k) {
            columnDescriptorList.add(array2[k]);
        }
        final ConglomerateDescriptor conglomerateDescriptor = dataDescriptorGenerator.newConglomerateDescriptor(conglomerate, null, false, null, false, null, uuid, schemaDescriptor.getUUID());
        if (this.tableType != 3) {
            dataDictionary.addDescriptor(conglomerateDescriptor, schemaDescriptor, 0, false, transactionExecute);
        }
        ddlTableDescriptor.getConglomerateDescriptorList().add(conglomerateDescriptor);
        if (this.constraintActions != null) {
            for (int l = 0; l < this.constraintActions.length; ++l) {
                if (!this.constraintActions[l].isForeignKeyConstraint()) {
                    this.constraintActions[l].executeConstantAction(activation);
                }
            }
            for (int n2 = 0; n2 < this.constraintActions.length; ++n2) {
                if (this.constraintActions[n2].isForeignKeyConstraint()) {
                    this.constraintActions[n2].executeConstantAction(activation);
                }
            }
        }
        for (int n3 = 0; n3 < this.columnInfo.length; ++n3) {
            this.addColumnDependencies(languageConnectionContext, dataDictionary, ddlTableDescriptor, this.columnInfo[n3]);
        }
        this.adjustUDTDependencies(languageConnectionContext, dataDictionary, ddlTableDescriptor, this.columnInfo, false);
        if (this.tableType == 3) {
            languageConnectionContext.addDeclaredGlobalTempTable(ddlTableDescriptor);
        }
        dataDictionary.getDependencyManager().addDependency(activation.getPreparedStatement(), ddlTableDescriptor, languageConnectionContext.getContextManager());
    }
}
