// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.Activation;

class DropSchemaConstantAction extends DDLConstantAction
{
    private final String schemaName;
    
    DropSchemaConstantAction(final String schemaName) {
        this.schemaName = schemaName;
    }
    
    public String toString() {
        return "DROP SCHEMA " + this.schemaName;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        dataDictionary.startWriting(languageConnectionContext);
        dataDictionary.getSchemaDescriptor(this.schemaName, transactionExecute, true).drop(languageConnectionContext, activation);
    }
}
