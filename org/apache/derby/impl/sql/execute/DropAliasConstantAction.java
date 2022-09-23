// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.AliasDescriptor;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;

class DropAliasConstantAction extends DDLConstantAction
{
    private SchemaDescriptor sd;
    private final String aliasName;
    private final char nameSpace;
    
    DropAliasConstantAction(final SchemaDescriptor sd, final String aliasName, final char nameSpace) {
        this.sd = sd;
        this.aliasName = aliasName;
        this.nameSpace = nameSpace;
    }
    
    public String toString() {
        return "DROP ALIAS " + this.aliasName;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        dataDictionary.startWriting(languageConnectionContext);
        final AliasDescriptor aliasDescriptor = dataDictionary.getAliasDescriptor(this.sd.getUUID().toString(), this.aliasName, this.nameSpace);
        if (aliasDescriptor == null) {
            throw StandardException.newException("42X94", AliasDescriptor.getAliasType(this.nameSpace), this.aliasName);
        }
        this.adjustUDTDependencies(languageConnectionContext, dataDictionary, aliasDescriptor, false);
        aliasDescriptor.drop(languageConnectionContext);
    }
}
