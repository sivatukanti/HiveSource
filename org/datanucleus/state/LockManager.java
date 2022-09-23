// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.state;

public interface LockManager
{
    public static final short LOCK_MODE_NONE = 0;
    public static final short LOCK_MODE_OPTIMISTIC_READ = 1;
    public static final short LOCK_MODE_OPTIMISTIC_WRITE = 2;
    public static final short LOCK_MODE_PESSIMISTIC_READ = 3;
    public static final short LOCK_MODE_PESSIMISTIC_WRITE = 4;
    
    void lock(final Object p0, final short p1);
    
    short getLockMode(final Object p0);
    
    void clear();
    
    void lock(final ObjectProvider p0, final short p1);
    
    void unlock(final ObjectProvider p0);
    
    short getLockMode(final ObjectProvider p0);
    
    void close();
}
