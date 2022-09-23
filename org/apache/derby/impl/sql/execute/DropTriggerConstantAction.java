// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.dictionary.TriggerDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;

public class DropTriggerConstantAction extends DDLSingleTableConstantAction
{
    private final String triggerName;
    private final SchemaDescriptor sd;
    
    DropTriggerConstantAction(final SchemaDescriptor sd, final String triggerName, final UUID uuid) {
        super(uuid);
        this.sd = sd;
        this.triggerName = triggerName;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        dataDictionary.startWriting(languageConnectionContext);
        final TableDescriptor tableDescriptor = dataDictionary.getTableDescriptor(this.tableId);
        if (tableDescriptor == null) {
            throw StandardException.newException("X0X05.S", this.tableId.toString());
        }
        this.lockTableForDDL(languageConnectionContext.getTransactionExecute(), tableDescriptor.getHeapConglomerateId(), true);
        if (dataDictionary.getTableDescriptor(this.tableId) == null) {
            throw StandardException.newException("X0X05.S", this.tableId.toString());
        }
        final TriggerDescriptor triggerDescriptor = dataDictionary.getTriggerDescriptor(this.triggerName, this.sd);
        if (triggerDescriptor == null) {
            throw StandardException.newException("X0X81.S", "TRIGGER", this.sd.getSchemaName() + "." + this.triggerName);
        }
        triggerDescriptor.drop(languageConnectionContext);
    }
    
    public String toString() {
        return "DROP TRIGGER " + this.triggerName;
    }
}
