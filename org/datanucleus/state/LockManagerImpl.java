// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.state;

import java.util.HashMap;
import java.util.Map;

public class LockManagerImpl implements LockManager
{
    Map<Object, Short> requiredLockModesById;
    
    public LockManagerImpl() {
        this.requiredLockModesById = null;
    }
    
    @Override
    public void close() {
        this.clear();
        this.requiredLockModesById = null;
    }
    
    @Override
    public void lock(final Object id, final short lockMode) {
        if (this.requiredLockModesById == null) {
            this.requiredLockModesById = new HashMap<Object, Short>();
        }
        this.requiredLockModesById.put(id, lockMode);
    }
    
    @Override
    public short getLockMode(final Object id) {
        if (this.requiredLockModesById != null) {
            final Short lockMode = this.requiredLockModesById.get(id);
            if (lockMode != null) {
                return lockMode;
            }
        }
        return 0;
    }
    
    @Override
    public void clear() {
        if (this.requiredLockModesById != null) {
            this.requiredLockModesById.clear();
            this.requiredLockModesById = null;
        }
    }
    
    @Override
    public void lock(final ObjectProvider sm, final short lockMode) {
        sm.lock(lockMode);
        if (lockMode == 3 || lockMode == 4) {
            sm.locate();
        }
    }
    
    @Override
    public void unlock(final ObjectProvider sm) {
        sm.unlock();
    }
    
    @Override
    public short getLockMode(final ObjectProvider sm) {
        return sm.getLockMode();
    }
}
