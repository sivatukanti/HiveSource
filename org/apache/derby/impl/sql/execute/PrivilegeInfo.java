// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;
import org.apache.derby.iapi.sql.Activation;

public abstract class PrivilegeInfo
{
    public abstract void executeGrantRevoke(final Activation p0, final boolean p1, final List p2) throws StandardException;
    
    protected void checkOwnership(final String s, final TupleDescriptor tupleDescriptor, final SchemaDescriptor schemaDescriptor, final DataDictionary dataDictionary) throws StandardException {
        if (!s.equals(schemaDescriptor.getAuthorizationId()) && !s.equals(dataDictionary.getAuthorizationDatabaseOwner())) {
            throw StandardException.newException("42506", s, tupleDescriptor.getDescriptorType(), schemaDescriptor.getSchemaName(), tupleDescriptor.getDescriptorName());
        }
    }
    
    protected void addWarningIfPrivilegeNotRevoked(final Activation activation, final boolean b, final boolean b2, final String s) {
        if (!b && !b2) {
            activation.addWarning(StandardException.newWarning("01006", s));
        }
    }
}
