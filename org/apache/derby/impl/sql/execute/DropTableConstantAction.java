// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptorList;
import org.apache.derby.iapi.sql.dictionary.ReferencedKeyConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptorList;
import java.util.Iterator;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.sql.dictionary.TriggerDescriptor;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;

class DropTableConstantAction extends DDLSingleTableConstantAction
{
    private final long conglomerateNumber;
    private final String fullTableName;
    private final String tableName;
    private final SchemaDescriptor sd;
    private final boolean cascade;
    
    DropTableConstantAction(final String fullTableName, final String tableName, final SchemaDescriptor sd, final long conglomerateNumber, final UUID uuid, final int n) {
        super(uuid);
        this.fullTableName = fullTableName;
        this.tableName = tableName;
        this.sd = sd;
        this.conglomerateNumber = conglomerateNumber;
        this.cascade = (n == 0);
    }
    
    public String toString() {
        return "DROP TABLE " + this.fullTableName;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        if (this.sd != null && this.sd.getSchemaName().equals("SESSION")) {
            TableDescriptor tableDescriptor = languageConnectionContext.getTableDescriptorForDeclaredGlobalTempTable(this.tableName);
            if (tableDescriptor == null) {
                tableDescriptor = dataDictionary.getTableDescriptor(this.tableName, this.sd, transactionExecute);
            }
            if (tableDescriptor == null) {
                throw StandardException.newException("X0X05.S", this.fullTableName);
            }
            if (tableDescriptor.getTableType() == 3) {
                dependencyManager.invalidateFor(tableDescriptor, 1, languageConnectionContext);
                transactionExecute.dropConglomerate(tableDescriptor.getHeapConglomerateId());
                languageConnectionContext.dropDeclaredGlobalTempTable(this.tableName);
                return;
            }
        }
        if (this.conglomerateNumber != 0L) {
            this.lockTableForDDL(transactionExecute, this.conglomerateNumber, true);
        }
        dataDictionary.startWriting(languageConnectionContext);
        final TableDescriptor tableDescriptor2 = dataDictionary.getTableDescriptor(this.tableId);
        if (tableDescriptor2 == null) {
            throw StandardException.newException("X0X05.S", this.fullTableName);
        }
        final long heapConglomerateId = tableDescriptor2.getHeapConglomerateId();
        this.lockTableForDDL(transactionExecute, heapConglomerateId, true);
        final Iterator<Object> iterator = dataDictionary.getTriggerDescriptors(tableDescriptor2).iterator();
        while (iterator.hasNext()) {
            iterator.next().drop(languageConnectionContext);
        }
        final ColumnDescriptorList columnDescriptorList = tableDescriptor2.getColumnDescriptorList();
        for (int size = columnDescriptorList.size(), i = 0; i < size; ++i) {
            final ColumnDescriptor element = columnDescriptorList.elementAt(i);
            if (element.getDefaultInfo() != null) {
                dependencyManager.clearDependencies(languageConnectionContext, element.getDefaultDescriptor(dataDictionary));
            }
        }
        dataDictionary.dropAllColumnDescriptors(this.tableId, transactionExecute);
        dataDictionary.dropAllTableAndColPermDescriptors(this.tableId, transactionExecute);
        this.dropAllConstraintDescriptors(tableDescriptor2, activation);
        final ConglomerateDescriptor[] conglomerateDescriptors = tableDescriptor2.getConglomerateDescriptors();
        final long[] array = new long[conglomerateDescriptors.length - 1];
        int n = 0;
        for (int j = 0; j < conglomerateDescriptors.length; ++j) {
            final ConglomerateDescriptor conglomerateDescriptor = conglomerateDescriptors[j];
            if (conglomerateDescriptor.getConglomerateNumber() != heapConglomerateId) {
                long conglomerateNumber;
                int n2;
                for (conglomerateNumber = conglomerateDescriptor.getConglomerateNumber(), n2 = 0; n2 < n && array[n2] != conglomerateNumber; ++n2) {}
                if (n2 == n) {
                    transactionExecute.dropConglomerate(array[n++] = conglomerateNumber);
                    dataDictionary.dropStatisticsDescriptors(tableDescriptor2.getUUID(), conglomerateDescriptor.getUUID(), transactionExecute);
                }
            }
        }
        dependencyManager.invalidateFor(tableDescriptor2, 1, languageConnectionContext);
        this.adjustUDTDependencies(languageConnectionContext, dataDictionary, tableDescriptor2, null, true);
        dataDictionary.dropTableDescriptor(tableDescriptor2, this.sd, transactionExecute);
        dataDictionary.dropAllConglomerateDescriptors(tableDescriptor2, transactionExecute);
        transactionExecute.dropConglomerate(heapConglomerateId);
    }
    
    private void dropAllConstraintDescriptors(final TableDescriptor tableDescriptor, final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
        languageConnectionContext.getTransactionExecute();
        final ConstraintDescriptorList constraintDescriptors = dataDictionary.getConstraintDescriptors(tableDescriptor);
        int i = 0;
        while (i < constraintDescriptors.size()) {
            final ConstraintDescriptor element = constraintDescriptors.elementAt(i);
            if (element instanceof ReferencedKeyConstraintDescriptor) {
                ++i;
            }
            else {
                dependencyManager.invalidateFor(element, 19, languageConnectionContext);
                this.dropConstraint(element, tableDescriptor, activation, languageConnectionContext, true);
            }
        }
        while (constraintDescriptors.size() > 0) {
            final ConstraintDescriptor element2 = constraintDescriptors.elementAt(0);
            this.dropConstraint(element2, tableDescriptor, activation, languageConnectionContext, false);
            if (this.cascade) {
                final ConstraintDescriptorList foreignKeys = dataDictionary.getForeignKeys(element2.getUUID());
                for (int j = 0; j < foreignKeys.size(); ++j) {
                    final ConstraintDescriptor element3 = foreignKeys.elementAt(j);
                    dependencyManager.invalidateFor(element3, 19, languageConnectionContext);
                    this.dropConstraint(element3, tableDescriptor, activation, languageConnectionContext, true);
                    activation.addWarning(StandardException.newWarning("01500", element3.getConstraintName(), element3.getTableDescriptor().getName()));
                }
            }
            dependencyManager.invalidateFor(element2, 19, languageConnectionContext);
            dependencyManager.clearDependencies(languageConnectionContext, element2);
        }
    }
}
