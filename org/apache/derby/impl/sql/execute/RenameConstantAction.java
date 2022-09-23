// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptorList;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.sql.dictionary.ReferencedKeyConstraintDescriptor;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.catalog.UUID;

class RenameConstantAction extends DDLSingleTableConstantAction
{
    private String fullTableName;
    private String tableName;
    private String newTableName;
    private String oldObjectName;
    private String newObjectName;
    private UUID schemaId;
    private SchemaDescriptor sd;
    private boolean usedAlterTable;
    private int renamingWhat;
    
    public RenameConstantAction(final String fullTableName, final String tableName, final String oldObjectName, final String newObjectName, final SchemaDescriptor sd, final UUID uuid, final boolean usedAlterTable, final int renamingWhat) {
        super(uuid);
        this.fullTableName = fullTableName;
        this.tableName = tableName;
        this.sd = sd;
        this.usedAlterTable = usedAlterTable;
        switch (this.renamingWhat = renamingWhat) {
            case 1: {
                this.newTableName = newObjectName;
                this.oldObjectName = null;
                this.newObjectName = newObjectName;
                break;
            }
            case 2:
            case 3: {
                this.oldObjectName = oldObjectName;
                this.newObjectName = newObjectName;
                break;
            }
        }
    }
    
    public String toString() {
        String str;
        if (this.usedAlterTable) {
            str = "ALTER TABLE ";
        }
        else {
            str = "RENAME ";
        }
        switch (this.renamingWhat) {
            case 1: {
                if (this.usedAlterTable) {
                    str = str + this.fullTableName + " RENAME TO " + this.newTableName;
                    break;
                }
                str = str + " TABLE " + this.fullTableName + " TO " + this.newTableName;
                break;
            }
            case 2: {
                if (this.usedAlterTable) {
                    str = str + this.fullTableName + " RENAME " + this.oldObjectName + " TO " + this.newObjectName;
                    break;
                }
                str = str + " COLUMN " + this.fullTableName + "." + this.oldObjectName + " TO " + this.newObjectName;
                break;
            }
            case 3: {
                str = str + " INDEX " + this.oldObjectName + " TO " + this.newObjectName;
                break;
            }
        }
        return str;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        dataDictionary.getDependencyManager();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        dataDictionary.startWriting(languageConnectionContext);
        final TableDescriptor tableDescriptor = dataDictionary.getTableDescriptor(this.tableId);
        if (tableDescriptor == null) {
            throw StandardException.newException("X0X05.S", this.fullTableName);
        }
        if (this.sd == null) {
            this.sd = DDLConstantAction.getAndCheckSchemaDescriptor(dataDictionary, this.schemaId, "RENAME TABLE");
        }
        this.lockTableForDDL(transactionExecute, tableDescriptor.getHeapConglomerateId(), true);
        final TableDescriptor tableDescriptor2 = dataDictionary.getTableDescriptor(this.tableId);
        if (tableDescriptor2 == null) {
            throw StandardException.newException("X0X05.S", this.fullTableName);
        }
        switch (this.renamingWhat) {
            case 1: {
                this.execGutsRenameTable(tableDescriptor2, activation);
                break;
            }
            case 2: {
                this.execGutsRenameColumn(tableDescriptor2, activation);
                break;
            }
            case 3: {
                this.execGutsRenameIndex(tableDescriptor2, activation);
                break;
            }
        }
    }
    
    private void execGutsRenameTable(final TableDescriptor tableDescriptor, final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        dependencyManager.invalidateFor(tableDescriptor, 34, languageConnectionContext);
        final ConstraintDescriptorList constraintDescriptors = dataDictionary.getConstraintDescriptors(tableDescriptor);
        for (int i = 0; i < constraintDescriptors.size(); ++i) {
            final ConstraintDescriptor element = constraintDescriptors.elementAt(i);
            if (element instanceof ReferencedKeyConstraintDescriptor) {
                dependencyManager.invalidateFor(element, 34, languageConnectionContext);
            }
        }
        dataDictionary.dropTableDescriptor(tableDescriptor, this.sd, transactionExecute);
        tableDescriptor.setTableName(this.newTableName);
        dataDictionary.addDescriptor(tableDescriptor, this.sd, 1, false, transactionExecute);
    }
    
    private void execGutsRenameColumn(TableDescriptor tableDescriptor, final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        final ColumnDescriptor columnDescriptor = tableDescriptor.getColumnDescriptor(this.oldObjectName);
        if (columnDescriptor.isAutoincrement()) {
            columnDescriptor.setAutoinc_create_or_modify_Start_Increment(0);
        }
        final int position = columnDescriptor.getPosition();
        final FormatableBitSet referencedColumnMap = new FormatableBitSet(tableDescriptor.getColumnDescriptorList().size() + 1);
        referencedColumnMap.set(position);
        tableDescriptor.setReferencedColumnMap(referencedColumnMap);
        dependencyManager.invalidateFor(tableDescriptor, 34, languageConnectionContext);
        final ConstraintDescriptorList constraintDescriptors = dataDictionary.getConstraintDescriptors(tableDescriptor);
        for (int i = 0; i < constraintDescriptors.size(); ++i) {
            final ConstraintDescriptor element = constraintDescriptors.elementAt(i);
            final int[] referencedColumns = element.getReferencedColumns();
            for (int length = referencedColumns.length, j = 0; j < length; ++j) {
                if (referencedColumns[j] == position && element instanceof ReferencedKeyConstraintDescriptor) {
                    dependencyManager.invalidateFor(element, 34, languageConnectionContext);
                }
            }
        }
        dataDictionary.dropColumnDescriptor(tableDescriptor.getUUID(), this.oldObjectName, transactionExecute);
        columnDescriptor.setColumnName(this.newObjectName);
        dataDictionary.addDescriptor(columnDescriptor, tableDescriptor, 2, false, transactionExecute);
        tableDescriptor = dataDictionary.getTableDescriptor(tableDescriptor.getObjectID());
    }
    
    private void execGutsRenameIndex(final TableDescriptor tableDescriptor, final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        dependencyManager.invalidateFor(tableDescriptor, 41, languageConnectionContext);
        final ConglomerateDescriptor conglomerateDescriptor = dataDictionary.getConglomerateDescriptor(this.oldObjectName, this.sd, true);
        if (conglomerateDescriptor == null) {
            throw StandardException.newException("X0X99.S", this.oldObjectName);
        }
        dataDictionary.dropConglomerateDescriptor(conglomerateDescriptor, transactionExecute);
        conglomerateDescriptor.setConglomerateName(this.newObjectName);
        dataDictionary.addDescriptor(conglomerateDescriptor, this.sd, 0, false, transactionExecute);
    }
    
    public String getTableName() {
        return this.tableName;
    }
}
