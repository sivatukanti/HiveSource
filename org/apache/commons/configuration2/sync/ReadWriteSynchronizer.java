// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.sync;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;

public class ReadWriteSynchronizer implements Synchronizer
{
    private final ReadWriteLock lock;
    
    public ReadWriteSynchronizer(final ReadWriteLock l) {
        this.lock = ((l != null) ? l : createDefaultLock());
    }
    
    public ReadWriteSynchronizer() {
        this(null);
    }
    
    @Override
    public void beginRead() {
        this.lock.readLock().lock();
    }
    
    @Override
    public void endRead() {
        this.lock.readLock().unlock();
    }
    
    @Override
    public void beginWrite() {
        this.lock.writeLock().lock();
    }
    
    @Override
    public void endWrite() {
        this.lock.writeLock().unlock();
    }
    
    private static ReadWriteLock createDefaultLock() {
        return new ReentrantReadWriteLock();
    }
}
