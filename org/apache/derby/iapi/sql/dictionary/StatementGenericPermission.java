// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.catalog.UUID;

public final class StatementGenericPermission extends StatementPermission
{
    private UUID _objectID;
    private String _objectType;
    private String _privilege;
    
    public StatementGenericPermission(final UUID objectID, final String objectType, final String privilege) {
        this._objectID = objectID;
        this._objectType = objectType;
        this._privilege = privilege;
    }
    
    public UUID getObjectID() {
        return this._objectID;
    }
    
    public String getPrivilege() {
        return this._privilege;
    }
    
    public String getObjectType() {
        return this._objectType;
    }
    
    public void check(final LanguageConnectionContext languageConnectionContext, final boolean b, final Activation activation) throws StandardException {
        this.genericCheck(languageConnectionContext, b, activation, this._privilege);
    }
    
    public boolean isCorrectPermission(final PermissionsDescriptor permissionsDescriptor) {
        if (permissionsDescriptor == null || !(permissionsDescriptor instanceof PermDescriptor)) {
            return false;
        }
        final PermDescriptor permDescriptor = (PermDescriptor)permissionsDescriptor;
        return permDescriptor.getPermObjectId().equals(this._objectID) && permDescriptor.getObjectType().equals(this._objectType) && permDescriptor.getPermission().equals(this._privilege);
    }
    
    public PrivilegedSQLObject getPrivilegedObject(final DataDictionary dataDictionary) throws StandardException {
        if ("TYPE".equals(this._objectType)) {
            return dataDictionary.getAliasDescriptor(this._objectID);
        }
        if ("DERBY AGGREGATE".equals(this._objectType)) {
            return dataDictionary.getAliasDescriptor(this._objectID);
        }
        if ("SEQUENCE".equals(this._objectType)) {
            return dataDictionary.getSequenceDescriptor(this._objectID);
        }
        throw StandardException.newException("XSCB3.S");
    }
    
    public PermissionsDescriptor getPermissionDescriptor(final String s, final DataDictionary dataDictionary) throws StandardException {
        return dataDictionary.getGenericPermissions(this._objectID, this._objectType, this._privilege, s);
    }
    
    public String toString() {
        return "StatementGenericPermission( " + this._objectID + ", " + this._objectType + ", " + this._privilege + " )";
    }
}
