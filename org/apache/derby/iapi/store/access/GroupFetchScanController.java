// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueDescriptor;

public interface GroupFetchScanController extends GenericScanController
{
    int fetchNextGroup(final DataValueDescriptor[][] p0, final RowLocation[] p1) throws StandardException;
    
    int fetchNextGroup(final DataValueDescriptor[][] p0, final RowLocation[] p1, final RowLocation[] p2) throws StandardException;
    
    boolean next() throws StandardException;
}
