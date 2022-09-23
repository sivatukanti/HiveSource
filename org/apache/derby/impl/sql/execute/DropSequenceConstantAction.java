// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.dictionary.SequenceDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;

class DropSequenceConstantAction extends DDLConstantAction
{
    private final String sequenceName;
    private final SchemaDescriptor schemaDescriptor;
    
    DropSequenceConstantAction(final SchemaDescriptor schemaDescriptor, final String sequenceName) {
        this.sequenceName = sequenceName;
        this.schemaDescriptor = schemaDescriptor;
    }
    
    public String toString() {
        return "DROP SEQUENCE " + this.sequenceName;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        languageConnectionContext.getTransactionExecute();
        dataDictionary.startWriting(languageConnectionContext);
        dataDictionary.clearSequenceCaches();
        final SequenceDescriptor sequenceDescriptor = dataDictionary.getSequenceDescriptor(this.schemaDescriptor, this.sequenceName);
        if (sequenceDescriptor == null) {
            throw StandardException.newException("X0X81.S", "SEQUENCE", this.schemaDescriptor.getObjectName() + "." + this.sequenceName);
        }
        sequenceDescriptor.drop(languageConnectionContext);
    }
}
