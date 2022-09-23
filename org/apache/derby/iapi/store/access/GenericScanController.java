// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.error.StandardException;

public interface GenericScanController extends RowCountable
{
    void close() throws StandardException;
    
    ScanInfo getScanInfo() throws StandardException;
    
    boolean isKeyed();
    
    boolean isTableLocked();
    
    RowLocation newRowLocationTemplate() throws StandardException;
    
    void reopenScan(final DataValueDescriptor[] p0, final int p1, final Qualifier[][] p2, final DataValueDescriptor[] p3, final int p4) throws StandardException;
    
    void reopenScanByRowLocation(final RowLocation p0, final Qualifier[][] p1) throws StandardException;
}
