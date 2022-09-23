// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.PrivilegedSQLObject;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.PermDescriptor;
import java.sql.SQLException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.conn.ConnectionUtil;
import org.apache.derby.iapi.sql.dictionary.RoutinePermsDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.dictionary.ColPermsDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.dictionary.TablePermsDescriptor;
import org.apache.derby.iapi.sql.dictionary.PermissionsDescriptor;
import org.apache.derby.iapi.services.cache.Cacheable;

class PermissionsCacheable implements Cacheable
{
    protected final DataDictionaryImpl dd;
    private PermissionsDescriptor permissions;
    
    PermissionsCacheable(final DataDictionaryImpl dd) {
        this.dd = dd;
    }
    
    public Cacheable setIdentity(final Object o) throws StandardException {
        if (o instanceof TablePermsDescriptor) {
            final TablePermsDescriptor tablePermsDescriptor = (TablePermsDescriptor)o;
            this.permissions = this.dd.getUncachedTablePermsDescriptor(tablePermsDescriptor);
            if (this.permissions == null) {
                final SchemaDescriptor schemaDescriptor = this.dd.getTableDescriptor(tablePermsDescriptor.getTableUUID()).getSchemaDescriptor();
                if (schemaDescriptor.isSystemSchema()) {
                    this.permissions = new TablePermsDescriptor(this.dd, tablePermsDescriptor.getGrantee(), null, tablePermsDescriptor.getTableUUID(), "Y", "N", "N", "N", "N", "N");
                    ((TablePermsDescriptor)this.permissions).setUUID(tablePermsDescriptor.getTableUUID());
                }
                else if (tablePermsDescriptor.getGrantee().equals(schemaDescriptor.getAuthorizationId())) {
                    this.permissions = new TablePermsDescriptor(this.dd, tablePermsDescriptor.getGrantee(), "_SYSTEM", tablePermsDescriptor.getTableUUID(), "Y", "Y", "Y", "Y", "Y", "Y");
                }
                else {
                    this.permissions = new TablePermsDescriptor(this.dd, tablePermsDescriptor.getGrantee(), null, tablePermsDescriptor.getTableUUID(), "N", "N", "N", "N", "N", "N");
                }
            }
        }
        else if (o instanceof ColPermsDescriptor) {
            final ColPermsDescriptor colPermsDescriptor = (ColPermsDescriptor)o;
            this.permissions = this.dd.getUncachedColPermsDescriptor(colPermsDescriptor);
            if (this.permissions == null) {
                this.permissions = new ColPermsDescriptor(this.dd, colPermsDescriptor.getGrantee(), null, colPermsDescriptor.getTableUUID(), colPermsDescriptor.getType(), null);
            }
        }
        else if (o instanceof RoutinePermsDescriptor) {
            final RoutinePermsDescriptor routinePermsDescriptor = (RoutinePermsDescriptor)o;
            this.permissions = this.dd.getUncachedRoutinePermsDescriptor(routinePermsDescriptor);
            if (this.permissions == null) {
                try {
                    final SchemaDescriptor schemaDescriptor2 = this.dd.getSchemaDescriptor(this.dd.getAliasDescriptor(routinePermsDescriptor.getRoutineUUID()).getSchemaUUID(), ConnectionUtil.getCurrentLCC().getTransactionExecute());
                    if (schemaDescriptor2.isSystemSchema() && !schemaDescriptor2.isSchemaWithGrantableRoutines()) {
                        this.permissions = new RoutinePermsDescriptor(this.dd, routinePermsDescriptor.getGrantee(), null, routinePermsDescriptor.getRoutineUUID(), true);
                    }
                    else if (routinePermsDescriptor.getGrantee().equals(schemaDescriptor2.getAuthorizationId())) {
                        this.permissions = new RoutinePermsDescriptor(this.dd, routinePermsDescriptor.getGrantee(), "_SYSTEM", routinePermsDescriptor.getRoutineUUID(), true);
                    }
                }
                catch (SQLException ex) {
                    throw StandardException.plainWrapException(ex);
                }
            }
        }
        else {
            if (!(o instanceof PermDescriptor)) {
                return null;
            }
            final PermDescriptor permDescriptor = (PermDescriptor)o;
            this.permissions = this.dd.getUncachedGenericPermDescriptor(permDescriptor);
            if (this.permissions == null) {
                final String objectType = permDescriptor.getObjectType();
                final String permission = permDescriptor.getPermission();
                final PrivilegedSQLObject protectedObject = PermDescriptor.getProtectedObject(this.dd, permDescriptor.getPermObjectId(), objectType);
                if (permDescriptor.getGrantee().equals(protectedObject.getSchemaDescriptor().getAuthorizationId())) {
                    this.permissions = new PermDescriptor(this.dd, null, objectType, protectedObject.getUUID(), permission, "_SYSTEM", permDescriptor.getGrantee(), true);
                }
            }
        }
        if (this.permissions != null) {
            return this;
        }
        return null;
    }
    
    public Cacheable createIdentity(final Object o, final Object o2) throws StandardException {
        if (o == null) {
            return null;
        }
        this.permissions = (PermissionsDescriptor)((PermissionsDescriptor)o).clone();
        return this;
    }
    
    public void clearIdentity() {
        this.permissions = null;
    }
    
    public Object getIdentity() {
        return this.permissions;
    }
    
    public boolean isDirty() {
        return false;
    }
    
    public void clean(final boolean b) throws StandardException {
    }
}
