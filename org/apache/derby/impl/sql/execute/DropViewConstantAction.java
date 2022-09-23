// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;

class DropViewConstantAction extends DDLConstantAction
{
    private String fullTableName;
    private String tableName;
    private SchemaDescriptor sd;
    
    DropViewConstantAction(final String fullTableName, final String tableName, final SchemaDescriptor sd) {
        this.fullTableName = fullTableName;
        this.tableName = tableName;
        this.sd = sd;
    }
    
    public String toString() {
        return "DROP VIEW " + this.fullTableName;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        dataDictionary.startWriting(languageConnectionContext);
        final TableDescriptor tableDescriptor = dataDictionary.getTableDescriptor(this.tableName, this.sd, languageConnectionContext.getTransactionExecute());
        if (tableDescriptor == null) {
            throw StandardException.newException("X0X05.S", this.fullTableName);
        }
        if (tableDescriptor.getTableType() != 2) {
            throw StandardException.newException("X0Y16.S", this.fullTableName);
        }
        dataDictionary.getViewDescriptor(tableDescriptor).drop(languageConnectionContext, this.sd, tableDescriptor);
    }
}
