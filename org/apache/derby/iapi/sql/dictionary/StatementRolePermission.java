// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;

public class StatementRolePermission extends StatementPermission
{
    private String roleName;
    private int privType;
    
    public StatementRolePermission(final String roleName, final int privType) {
        this.roleName = roleName;
        this.privType = privType;
    }
    
    public void check(final LanguageConnectionContext languageConnectionContext, final boolean b, final Activation activation) throws StandardException {
        languageConnectionContext.getDataDictionary();
        languageConnectionContext.getTransactionExecute();
        switch (this.privType) {
            case 19: {
                throw StandardException.newException("4251A", "CREATE ROLE");
            }
            case 20: {
                throw StandardException.newException("4251A", "DROP ROLE");
            }
            default: {}
        }
    }
    
    public PermissionsDescriptor getPermissionDescriptor(final String s, final DataDictionary dataDictionary) throws StandardException {
        return null;
    }
    
    private String getPrivName() {
        switch (this.privType) {
            case 19: {
                return "CREATE_ROLE";
            }
            case 20: {
                return "DROP_ROLE";
            }
            default: {
                return "?";
            }
        }
    }
    
    public String toString() {
        return "StatementRolePermission: " + this.roleName + " " + this.getPrivName();
    }
}
