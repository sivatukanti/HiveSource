// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;

public class StatementSchemaPermission extends StatementPermission
{
    private String schemaName;
    private String aid;
    private int privType;
    
    public StatementSchemaPermission(final String schemaName, final String aid, final int privType) {
        this.schemaName = schemaName;
        this.aid = aid;
        this.privType = privType;
    }
    
    public void check(final LanguageConnectionContext languageConnectionContext, final boolean b, final Activation activation) throws StandardException {
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        final String currentUserId = languageConnectionContext.getCurrentUserId(activation);
        switch (this.privType) {
            case 17:
            case 18: {
                final SchemaDescriptor schemaDescriptor = dataDictionary.getSchemaDescriptor(this.schemaName, transactionExecute, false);
                if (schemaDescriptor == null) {
                    return;
                }
                if (!currentUserId.equals(schemaDescriptor.getAuthorizationId())) {
                    throw StandardException.newException("42507", currentUserId, this.schemaName);
                }
                break;
            }
            case 16: {
                if (!this.schemaName.equals(currentUserId) || (this.aid != null && !this.aid.equals(currentUserId))) {
                    throw StandardException.newException("42508", currentUserId, this.schemaName);
                }
                break;
            }
        }
    }
    
    public PermissionsDescriptor getPermissionDescriptor(final String s, final DataDictionary dataDictionary) throws StandardException {
        return null;
    }
    
    private String getPrivName() {
        switch (this.privType) {
            case 16: {
                return "CREATE_SCHEMA";
            }
            case 17: {
                return "MODIFY_SCHEMA";
            }
            case 18: {
                return "DROP_SCHEMA";
            }
            default: {
                return "?";
            }
        }
    }
    
    public String toString() {
        return "StatementSchemaPermission: " + this.schemaName + " owner:" + this.aid + " " + this.getPrivName();
    }
}
