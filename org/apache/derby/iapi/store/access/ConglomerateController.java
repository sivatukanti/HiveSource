// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.error.StandardException;

public interface ConglomerateController extends ConglomPropertyQueryable
{
    public static final int ROWISDUPLICATE = 1;
    public static final int LOCK_READ = 0;
    public static final int LOCK_UPD = 1;
    public static final int LOCK_INS = 2;
    public static final int LOCK_INS_PREVKEY = 4;
    public static final int LOCK_UPDATE_LOCKS = 8;
    
    void close() throws StandardException;
    
    boolean closeForEndTransaction(final boolean p0) throws StandardException;
    
    void checkConsistency() throws StandardException;
    
    boolean delete(final RowLocation p0) throws StandardException;
    
    boolean fetch(final RowLocation p0, final DataValueDescriptor[] p1, final FormatableBitSet p2) throws StandardException;
    
    boolean fetch(final RowLocation p0, final DataValueDescriptor[] p1, final FormatableBitSet p2, final boolean p3) throws StandardException;
    
    int insert(final DataValueDescriptor[] p0) throws StandardException;
    
    void insertAndFetchLocation(final DataValueDescriptor[] p0, final RowLocation p1) throws StandardException;
    
    boolean isKeyed();
    
    boolean lockRow(final RowLocation p0, final int p1, final boolean p2, final int p3) throws StandardException;
    
    boolean lockRow(final long p0, final int p1, final int p2, final boolean p3, final int p4) throws StandardException;
    
    void unlockRowAfterRead(final RowLocation p0, final boolean p1, final boolean p2) throws StandardException;
    
    RowLocation newRowLocationTemplate() throws StandardException;
    
    boolean replace(final RowLocation p0, final DataValueDescriptor[] p1, final FormatableBitSet p2) throws StandardException;
    
    SpaceInfo getSpaceInfo() throws StandardException;
    
    void debugConglomerate() throws StandardException;
}
