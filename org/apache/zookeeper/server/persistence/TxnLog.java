// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.persistence;

import org.apache.jute.Record;
import org.apache.zookeeper.txn.TxnHeader;
import java.io.IOException;
import org.apache.zookeeper.server.ServerStats;

public interface TxnLog
{
    void setServerStats(final ServerStats p0);
    
    void rollLog() throws IOException;
    
    boolean append(final TxnHeader p0, final Record p1) throws IOException;
    
    TxnIterator read(final long p0) throws IOException;
    
    long getLastLoggedZxid() throws IOException;
    
    boolean truncate(final long p0) throws IOException;
    
    long getDbId() throws IOException;
    
    void commit() throws IOException;
    
    void close() throws IOException;
    
    public interface TxnIterator
    {
        TxnHeader getHeader();
        
        Record getTxn();
        
        boolean next() throws IOException;
        
        void close() throws IOException;
    }
}
