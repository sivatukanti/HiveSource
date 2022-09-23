// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;

class DropStatisticsConstantAction extends DDLConstantAction
{
    private final String objectName;
    private final boolean forTable;
    private final SchemaDescriptor sd;
    private final String fullTableName;
    
    DropStatisticsConstantAction(final SchemaDescriptor sd, final String fullTableName, final String objectName, final boolean forTable) {
        this.objectName = objectName;
        this.sd = sd;
        this.forTable = forTable;
        this.fullTableName = fullTableName;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        ConglomerateDescriptor conglomerateDescriptor = null;
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        dataDictionary.startWriting(languageConnectionContext);
        TableDescriptor tableDescriptor;
        if (this.forTable) {
            tableDescriptor = dataDictionary.getTableDescriptor(this.objectName, this.sd, transactionExecute);
        }
        else {
            conglomerateDescriptor = dataDictionary.getConglomerateDescriptor(this.objectName, this.sd, false);
            tableDescriptor = dataDictionary.getTableDescriptor(conglomerateDescriptor.getTableID());
        }
        dependencyManager.invalidateFor(tableDescriptor, 39, languageConnectionContext);
        dataDictionary.dropStatisticsDescriptors(tableDescriptor.getUUID(), (conglomerateDescriptor != null) ? conglomerateDescriptor.getUUID() : null, transactionExecute);
    }
    
    public String toString() {
        return "DROP STATISTICS FOR " + (this.forTable ? "table " : "index ") + this.fullTableName;
    }
}
