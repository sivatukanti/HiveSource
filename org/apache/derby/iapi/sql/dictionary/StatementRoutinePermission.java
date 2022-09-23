// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.catalog.UUID;

public final class StatementRoutinePermission extends StatementPermission
{
    private UUID routineUUID;
    
    public StatementRoutinePermission(final UUID routineUUID) {
        this.routineUUID = routineUUID;
    }
    
    public UUID getRoutineUUID() {
        return this.routineUUID;
    }
    
    public void check(final LanguageConnectionContext languageConnectionContext, final boolean b, final Activation activation) throws StandardException {
        this.genericCheck(languageConnectionContext, b, activation, "EXECUTE");
    }
    
    public boolean isCorrectPermission(final PermissionsDescriptor permissionsDescriptor) {
        return permissionsDescriptor != null && permissionsDescriptor instanceof RoutinePermsDescriptor && ((RoutinePermsDescriptor)permissionsDescriptor).getHasExecutePermission();
    }
    
    public PrivilegedSQLObject getPrivilegedObject(final DataDictionary dataDictionary) throws StandardException {
        return dataDictionary.getAliasDescriptor(this.routineUUID);
    }
    
    public PermissionsDescriptor getPermissionDescriptor(final String s, final DataDictionary dataDictionary) throws StandardException {
        return dataDictionary.getRoutinePermissions(this.routineUUID, s);
    }
    
    public String getObjectType() {
        return "ROUTINE";
    }
    
    public String toString() {
        return "StatementRoutinePermission: " + this.routineUUID;
    }
}
