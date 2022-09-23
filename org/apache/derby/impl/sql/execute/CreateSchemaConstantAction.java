// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDescriptorGenerator;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;

class CreateSchemaConstantAction extends DDLConstantAction
{
    private final String aid;
    private final String schemaName;
    
    CreateSchemaConstantAction(final String schemaName, final String aid) {
        this.schemaName = schemaName;
        this.aid = aid;
    }
    
    public String toString() {
        return "CREATE SCHEMA " + this.schemaName;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        this.executeConstantActionMinion(activation, activation.getLanguageConnectionContext().getTransactionExecute());
    }
    
    public void executeConstantAction(final Activation activation, final TransactionController transactionController) throws StandardException {
        this.executeConstantActionMinion(activation, transactionController);
    }
    
    private void executeConstantActionMinion(final Activation activation, final TransactionController transactionController) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final DataDescriptorGenerator dataDescriptorGenerator = dataDictionary.getDataDescriptorGenerator();
        final SchemaDescriptor schemaDescriptor = dataDictionary.getSchemaDescriptor(this.schemaName, languageConnectionContext.getTransactionExecute(), false);
        if (schemaDescriptor != null && schemaDescriptor.getUUID() != null) {
            throw StandardException.newException("X0Y68.S", "Schema", this.schemaName);
        }
        final UUID uuid = dataDictionary.getUUIDFactory().createUUID();
        String s = this.aid;
        if (s == null) {
            s = languageConnectionContext.getCurrentUserId(activation);
        }
        dataDictionary.startWriting(languageConnectionContext);
        dataDictionary.addDescriptor(dataDescriptorGenerator.newSchemaDescriptor(this.schemaName, s, uuid), null, 3, false, transactionController);
    }
}
