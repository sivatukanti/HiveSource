// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw;

import org.apache.derby.iapi.error.StandardException;

public interface LockingPolicy
{
    public static final int MODE_NONE = 0;
    public static final int MODE_RECORD = 1;
    public static final int MODE_CONTAINER = 2;
    
    boolean lockContainer(final Transaction p0, final ContainerHandle p1, final boolean p2, final boolean p3) throws StandardException;
    
    void unlockContainer(final Transaction p0, final ContainerHandle p1);
    
    boolean lockRecordForRead(final Transaction p0, final ContainerHandle p1, final RecordHandle p2, final boolean p3, final boolean p4) throws StandardException;
    
    boolean zeroDurationLockRecordForWrite(final Transaction p0, final RecordHandle p1, final boolean p2, final boolean p3) throws StandardException;
    
    boolean lockRecordForWrite(final Transaction p0, final RecordHandle p1, final boolean p2, final boolean p3) throws StandardException;
    
    void unlockRecordAfterRead(final Transaction p0, final ContainerHandle p1, final RecordHandle p2, final boolean p3, final boolean p4) throws StandardException;
    
    int getMode();
}
