// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.UUID;

public class TablePermsDescriptor extends PermissionsDescriptor
{
    private UUID tableUUID;
    private String tableName;
    private String selectPriv;
    private String deletePriv;
    private String insertPriv;
    private String updatePriv;
    private String referencesPriv;
    private String triggerPriv;
    
    public TablePermsDescriptor(final DataDictionary dataDictionary, final String s, final String s2, final UUID tableUUID, final String selectPriv, final String deletePriv, final String insertPriv, final String updatePriv, final String referencesPriv, final String triggerPriv) throws StandardException {
        super(dataDictionary, s, s2);
        this.tableUUID = tableUUID;
        this.selectPriv = selectPriv;
        this.deletePriv = deletePriv;
        this.insertPriv = insertPriv;
        this.updatePriv = updatePriv;
        this.referencesPriv = referencesPriv;
        this.triggerPriv = triggerPriv;
        if (tableUUID != null) {
            this.tableName = dataDictionary.getTableDescriptor(tableUUID).getName();
        }
    }
    
    public TablePermsDescriptor(final DataDictionary dataDictionary, final String s, final String s2, final UUID uuid) throws StandardException {
        this(dataDictionary, s, s2, uuid, null, null, null, null, null, null);
    }
    
    public TablePermsDescriptor(final DataDictionary dataDictionary, final UUID oid) throws StandardException {
        this(dataDictionary, null, null, null, null, null, null, null, null, null);
        this.oid = oid;
    }
    
    public int getCatalogNumber() {
        return 16;
    }
    
    public UUID getTableUUID() {
        return this.tableUUID;
    }
    
    public String getSelectPriv() {
        return this.selectPriv;
    }
    
    public String getDeletePriv() {
        return this.deletePriv;
    }
    
    public String getInsertPriv() {
        return this.insertPriv;
    }
    
    public String getUpdatePriv() {
        return this.updatePriv;
    }
    
    public String getReferencesPriv() {
        return this.referencesPriv;
    }
    
    public String getTriggerPriv() {
        return this.triggerPriv;
    }
    
    public String toString() {
        return "tablePerms: grantee=" + this.getGrantee() + ",tablePermsUUID=" + this.getUUID() + ",grantor=" + this.getGrantor() + ",tableUUID=" + this.getTableUUID() + ",selectPriv=" + this.getSelectPriv() + ",deletePriv=" + this.getDeletePriv() + ",insertPriv=" + this.getInsertPriv() + ",updatePriv=" + this.getUpdatePriv() + ",referencesPriv=" + this.getReferencesPriv() + ",triggerPriv=" + this.getTriggerPriv();
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof TablePermsDescriptor)) {
            return false;
        }
        final TablePermsDescriptor tablePermsDescriptor = (TablePermsDescriptor)o;
        return super.keyEquals(tablePermsDescriptor) && this.tableUUID.equals(tablePermsDescriptor.tableUUID);
    }
    
    public int hashCode() {
        return super.keyHashCode() + this.tableUUID.hashCode();
    }
    
    public boolean checkOwner(final String anObject) throws StandardException {
        return this.getDataDictionary().getTableDescriptor(this.tableUUID).getSchemaDescriptor().getAuthorizationId().equals(anObject);
    }
    
    public String getObjectName() {
        return "Table Privilege on " + this.tableName;
    }
    
    public String getClassType() {
        return "TablePrivilege";
    }
    
    public DependableFinder getDependableFinder() {
        return this.getDependableFinder(462);
    }
}
