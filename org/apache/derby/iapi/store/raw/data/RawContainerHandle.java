// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw.data;

import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.store.raw.Page;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.ContainerHandle;

public interface RawContainerHandle extends ContainerHandle
{
    public static final int NORMAL = 1;
    public static final int DROPPED = 2;
    public static final int COMMITTED_DROP = 4;
    
    int getContainerStatus() throws StandardException;
    
    void removeContainer(final LogInstant p0) throws StandardException;
    
    void dropContainer(final LogInstant p0, final boolean p1) throws StandardException;
    
    long getContainerVersion() throws StandardException;
    
    Page getAnyPage(final long p0) throws StandardException;
    
    Page reCreatePageForRedoRecovery(final int p0, final long p1, final long p2) throws StandardException;
    
    ByteArray logCreateContainerInfo() throws StandardException;
    
    void preDirty(final boolean p0) throws StandardException;
    
    void encryptOrDecryptContainer(final String p0, final boolean p1) throws StandardException;
}
