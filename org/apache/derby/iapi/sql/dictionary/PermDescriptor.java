// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.depend.Provider;

public class PermDescriptor extends PermissionsDescriptor implements Provider
{
    public static final String SEQUENCE_TYPE = "SEQUENCE";
    public static final String UDT_TYPE = "TYPE";
    public static final String AGGREGATE_TYPE = "DERBY AGGREGATE";
    public static final String USAGE_PRIV = "USAGE";
    private String objectType;
    private UUID permObjectId;
    private String permission;
    private boolean grantable;
    
    public PermDescriptor(final DataDictionary dataDictionary, final UUID uuid, final String objectType, final UUID permObjectId, final String permission, final String s, final String s2, final boolean grantable) {
        super(dataDictionary, s2, s);
        this.setUUID(uuid);
        this.objectType = objectType;
        this.permObjectId = permObjectId;
        this.permission = permission;
        this.grantable = grantable;
    }
    
    public PermDescriptor(final DataDictionary dataDictionary, final UUID uuid) throws StandardException {
        this(dataDictionary, uuid, null, null, null, null, null, false);
    }
    
    public String getObjectType() {
        return this.objectType;
    }
    
    public UUID getPermObjectId() {
        return this.permObjectId;
    }
    
    public String getPermission() {
        return this.permission;
    }
    
    public boolean isGrantable() {
        return this.grantable;
    }
    
    public int getCatalogNumber() {
        return 21;
    }
    
    public String toString() {
        return "";
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof PermDescriptor)) {
            return false;
        }
        final PermDescriptor permDescriptor = (PermDescriptor)o;
        return super.keyEquals(permDescriptor) && this.permObjectId.equals(permDescriptor.permObjectId);
    }
    
    public int hashCode() {
        return super.keyHashCode() + this.permObjectId.hashCode();
    }
    
    public boolean checkOwner(final String anObject) throws StandardException {
        return getProtectedObject(this.getDataDictionary(), this.permObjectId, this.objectType).getSchemaDescriptor().getAuthorizationId().equals(anObject);
    }
    
    public static PrivilegedSQLObject getProtectedObject(final DataDictionary dataDictionary, final UUID uuid, final String anObject) throws StandardException {
        if ("SEQUENCE".equals(anObject)) {
            return dataDictionary.getSequenceDescriptor(uuid);
        }
        if ("DERBY AGGREGATE".equals(anObject)) {
            return dataDictionary.getAliasDescriptor(uuid);
        }
        if ("TYPE".equals(anObject)) {
            return dataDictionary.getAliasDescriptor(uuid);
        }
        throw StandardException.newException("XSCB3.S");
    }
    
    public String getObjectName() {
        try {
            return getProtectedObject(this.getDataDictionary(), this.permObjectId, this.objectType).getName();
        }
        catch (StandardException ex) {
            return this.objectType;
        }
    }
    
    public String getClassType() {
        return "Perm";
    }
    
    public DependableFinder getDependableFinder() {
        return this.getDependableFinder(473);
    }
}
