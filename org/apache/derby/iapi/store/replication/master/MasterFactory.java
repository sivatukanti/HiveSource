// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.replication.master;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.log.LogFactory;
import org.apache.derby.iapi.store.raw.data.DataFactory;
import org.apache.derby.iapi.store.raw.RawStoreFactory;

public interface MasterFactory
{
    public static final String MODULE = "org.apache.derby.iapi.store.replication.master.MasterFactory";
    public static final String REPLICATION_MODE = "derby.__rt.replication.master.mode";
    public static final String ASYNCHRONOUS_MODE = "derby.__rt.asynch";
    
    void startMaster(final RawStoreFactory p0, final DataFactory p1, final LogFactory p2, final String p3, final int p4, final String p5) throws StandardException;
    
    void stopMaster() throws StandardException;
    
    void startFailover() throws StandardException;
    
    void appendLog(final long p0, final byte[] p1, final int p2, final int p3);
    
    void flushedTo(final long p0);
    
    void workToDo();
}
