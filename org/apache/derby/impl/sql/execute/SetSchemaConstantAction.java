// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.ConstantAction;

class SetSchemaConstantAction implements ConstantAction
{
    private final String schemaName;
    private final int type;
    
    SetSchemaConstantAction(final String schemaName, final int type) {
        this.schemaName = schemaName;
        this.type = type;
    }
    
    public String toString() {
        return "SET SCHEMA " + ((this.type == 1) ? "USER" : ((this.type == 2 && this.schemaName == null) ? "?" : this.schemaName));
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        String s = this.schemaName;
        if (this.type == 2) {
            s = activation.getParameterValueSet().getParameter(0).getString();
            if (s == null || s.length() > 128) {
                throw StandardException.newException("42815.S.713", "CURRENT SCHEMA");
            }
        }
        else if (this.type == 1) {
            s = languageConnectionContext.getCurrentUserId(activation);
        }
        languageConnectionContext.setDefaultSchema(activation, dataDictionary.getSchemaDescriptor(s, languageConnectionContext.getTransactionExecute(), true));
    }
}
