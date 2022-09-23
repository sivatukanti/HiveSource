// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;

public interface Row
{
    int nColumns();
    
    DataValueDescriptor getColumn(final int p0) throws StandardException;
    
    void setColumn(final int p0, final DataValueDescriptor p1);
}
