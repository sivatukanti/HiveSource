// 
// Decompiled by Procyon v0.5.36
// 

package org.iq80.leveldb;

import java.io.Closeable;
import java.util.Map;

public interface DB extends Iterable<Map.Entry<byte[], byte[]>>, Closeable
{
    byte[] get(final byte[] p0) throws DBException;
    
    byte[] get(final byte[] p0, final ReadOptions p1) throws DBException;
    
    DBIterator iterator();
    
    DBIterator iterator(final ReadOptions p0);
    
    void put(final byte[] p0, final byte[] p1) throws DBException;
    
    void delete(final byte[] p0) throws DBException;
    
    void write(final WriteBatch p0) throws DBException;
    
    WriteBatch createWriteBatch();
    
    Snapshot put(final byte[] p0, final byte[] p1, final WriteOptions p2) throws DBException;
    
    Snapshot delete(final byte[] p0, final WriteOptions p1) throws DBException;
    
    Snapshot write(final WriteBatch p0, final WriteOptions p1) throws DBException;
    
    Snapshot getSnapshot();
    
    long[] getApproximateSizes(final Range... p0);
    
    String getProperty(final String p0);
    
    void suspendCompactions() throws InterruptedException;
    
    void resumeCompactions();
    
    void compactRange(final byte[] p0, final byte[] p1) throws DBException;
}
