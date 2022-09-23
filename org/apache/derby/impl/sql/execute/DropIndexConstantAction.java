// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.catalog.UUID;

class DropIndexConstantAction extends IndexConstantAction
{
    private String fullIndexName;
    private long tableConglomerateId;
    
    DropIndexConstantAction(final String fullIndexName, final String s, final String s2, final String s3, final UUID uuid, final long tableConglomerateId) {
        super(uuid, s, s2, s3);
        this.fullIndexName = fullIndexName;
        this.tableConglomerateId = tableConglomerateId;
    }
    
    public String toString() {
        return "DROP INDEX " + this.fullIndexName;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        dataDictionary.startWriting(languageConnectionContext);
        if (this.tableConglomerateId == 0L) {
            final TableDescriptor tableDescriptor = dataDictionary.getTableDescriptor(this.tableId);
            if (tableDescriptor == null) {
                throw StandardException.newException("X0X05.S", this.tableName);
            }
            this.tableConglomerateId = tableDescriptor.getHeapConglomerateId();
        }
        this.lockTableForDDL(transactionExecute, this.tableConglomerateId, true);
        final TableDescriptor tableDescriptor2 = dataDictionary.getTableDescriptor(this.tableId);
        if (tableDescriptor2 == null) {
            throw StandardException.newException("X0X05.S", this.tableName);
        }
        final ConglomerateDescriptor conglomerateDescriptor = dataDictionary.getConglomerateDescriptor(this.indexName, dataDictionary.getSchemaDescriptor(this.schemaName, transactionExecute, true), true);
        if (conglomerateDescriptor == null) {
            throw StandardException.newException("X0X99.S", this.fullIndexName);
        }
        this.dropConglomerate(conglomerateDescriptor, tableDescriptor2, activation, languageConnectionContext);
    }
}
