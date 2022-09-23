// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.UUID;

public class RoutinePermsDescriptor extends PermissionsDescriptor
{
    private UUID routineUUID;
    private String routineName;
    private boolean hasExecutePermission;
    
    public RoutinePermsDescriptor(final DataDictionary dataDictionary, final String s, final String s2, final UUID routineUUID, final boolean hasExecutePermission) throws StandardException {
        super(dataDictionary, s, s2);
        this.routineUUID = routineUUID;
        this.hasExecutePermission = hasExecutePermission;
        if (routineUUID != null) {
            this.routineName = dataDictionary.getAliasDescriptor(routineUUID).getObjectName();
        }
    }
    
    public RoutinePermsDescriptor(final DataDictionary dataDictionary, final String s, final String s2, final UUID uuid) throws StandardException {
        this(dataDictionary, s, s2, uuid, true);
    }
    
    public RoutinePermsDescriptor(final DataDictionary dataDictionary, final String s, final String s2) throws StandardException {
        this(dataDictionary, s, s2, null);
    }
    
    public RoutinePermsDescriptor(final DataDictionary dataDictionary, final UUID oid) throws StandardException {
        this(dataDictionary, null, null, null, true);
        this.oid = oid;
    }
    
    public int getCatalogNumber() {
        return 18;
    }
    
    public UUID getRoutineUUID() {
        return this.routineUUID;
    }
    
    public boolean getHasExecutePermission() {
        return this.hasExecutePermission;
    }
    
    public String toString() {
        return "routinePerms: grantee=" + this.getGrantee() + ",routinePermsUUID=" + this.getUUID() + ",grantor=" + this.getGrantor() + ",routineUUID=" + this.getRoutineUUID();
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof RoutinePermsDescriptor)) {
            return false;
        }
        final RoutinePermsDescriptor routinePermsDescriptor = (RoutinePermsDescriptor)o;
        return super.keyEquals(routinePermsDescriptor) && this.routineUUID.equals(routinePermsDescriptor.routineUUID);
    }
    
    public int hashCode() {
        return super.keyHashCode() + this.routineUUID.hashCode();
    }
    
    public boolean checkOwner(final String anObject) throws StandardException {
        return this.getDataDictionary().getSchemaDescriptor(this.getDataDictionary().getAliasDescriptor(this.routineUUID).getSchemaUUID(), null).getAuthorizationId().equals(anObject);
    }
    
    public String getObjectName() {
        return "Routine Privilege on " + this.routineName;
    }
    
    public String getClassType() {
        return "RoutinePrivilege";
    }
    
    public DependableFinder getDependableFinder() {
        return this.getDependableFinder(461);
    }
}
