// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.sql.dictionary.PermissionsDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.types.SQLVarchar;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;

abstract class PermissionsCatalogRowFactory extends CatalogRowFactory
{
    PermissionsCatalogRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
    }
    
    DataValueDescriptor getAuthorizationID(final String s) {
        return new SQLVarchar(s);
    }
    
    DataValueDescriptor getNullAuthorizationID() {
        return new SQLVarchar();
    }
    
    String getAuthorizationID(final ExecRow execRow, final int n) throws StandardException {
        return execRow.getColumn(n).getString();
    }
    
    abstract ExecIndexRow buildIndexKeyRow(final int p0, final PermissionsDescriptor p1) throws StandardException;
    
    abstract int orPermissions(final ExecRow p0, final PermissionsDescriptor p1, final boolean[] p2) throws StandardException;
    
    abstract int removePermissions(final ExecRow p0, final PermissionsDescriptor p1, final boolean[] p2) throws StandardException;
    
    abstract void setUUIDOfThePassedDescriptor(final ExecRow p0, final PermissionsDescriptor p1) throws StandardException;
}
