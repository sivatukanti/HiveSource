// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;

public interface ScanController extends GenericScanController
{
    public static final int GE = 1;
    public static final int GT = -1;
    public static final int NA = 0;
    
    boolean delete() throws StandardException;
    
    void didNotQualify() throws StandardException;
    
    boolean doesCurrentPositionQualify() throws StandardException;
    
    boolean isHeldAfterCommit() throws StandardException;
    
    void fetch(final DataValueDescriptor[] p0) throws StandardException;
    
    void fetchWithoutQualify(final DataValueDescriptor[] p0) throws StandardException;
    
    boolean fetchNext(final DataValueDescriptor[] p0) throws StandardException;
    
    void fetchLocation(final RowLocation p0) throws StandardException;
    
    boolean isCurrentPositionDeleted() throws StandardException;
    
    boolean next() throws StandardException;
    
    boolean positionAtRowLocation(final RowLocation p0) throws StandardException;
    
    boolean replace(final DataValueDescriptor[] p0, final FormatableBitSet p1) throws StandardException;
}
