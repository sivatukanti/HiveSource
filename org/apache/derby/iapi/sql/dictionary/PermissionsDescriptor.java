// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.depend.Provider;

public abstract class PermissionsDescriptor extends TupleDescriptor implements Cloneable, Provider
{
    protected UUID oid;
    private String grantee;
    private final String grantor;
    
    PermissionsDescriptor(final DataDictionary dataDictionary, final String grantee, final String grantor) {
        super(dataDictionary);
        this.grantee = grantee;
        this.grantor = grantor;
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException ex) {
            return null;
        }
    }
    
    public abstract int getCatalogNumber();
    
    protected boolean keyEquals(final PermissionsDescriptor permissionsDescriptor) {
        return this.grantee.equals(permissionsDescriptor.grantee);
    }
    
    protected int keyHashCode() {
        return this.grantee.hashCode();
    }
    
    public void setGrantee(final String grantee) {
        this.grantee = grantee;
    }
    
    public final String getGrantee() {
        return this.grantee;
    }
    
    public final String getGrantor() {
        return this.grantor;
    }
    
    public UUID getUUID() {
        return this.oid;
    }
    
    public void setUUID(final UUID oid) {
        this.oid = oid;
    }
    
    public abstract boolean checkOwner(final String p0) throws StandardException;
    
    public UUID getObjectID() {
        return this.oid;
    }
    
    public boolean isPersistent() {
        return true;
    }
}
