// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.replication.slave;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.log.LogFactory;
import org.apache.derby.iapi.store.raw.RawStoreFactory;

public interface SlaveFactory
{
    public static final String MODULE = "org.apache.derby.iapi.store.replication.slave.SlaveFactory";
    public static final String SLAVE_DB = "replication.slave.dbname";
    public static final String REPLICATION_MODE = "replication.slave.mode";
    public static final String SLAVE_MODE = "slavemode";
    public static final String SLAVE_PRE_MODE = "slavepremode";
    
    void startSlave(final RawStoreFactory p0, final LogFactory p1) throws StandardException;
    
    void stopSlave(final boolean p0) throws StandardException;
    
    void failover() throws StandardException;
    
    boolean isStarted();
}
