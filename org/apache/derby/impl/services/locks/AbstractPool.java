// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.locks;

import org.apache.derby.iapi.services.daemon.Serviceable;
import java.io.Serializable;
import org.apache.derby.iapi.services.property.PropertyUtil;
import java.util.Dictionary;
import java.util.Enumeration;
import org.apache.derby.iapi.services.locks.Limit;
import org.apache.derby.iapi.util.Matchable;
import org.apache.derby.iapi.services.locks.LockOwner;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.locks.Lockable;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import org.apache.derby.iapi.services.locks.LockFactory;

abstract class AbstractPool implements LockFactory
{
    protected final LockTable lockTable;
    int deadlockMonitor;
    
    protected AbstractPool() {
        this.lockTable = this.createLockTable();
    }
    
    protected abstract LockTable createLockTable();
    
    public boolean lockObject(final CompatibilitySpace compatibilitySpace, final Object o, final Lockable lockable, final Object o2, final int n) throws StandardException {
        final Lock lockObject = this.lockTable.lockObject(compatibilitySpace, lockable, o2, n);
        if (lockObject != null) {
            ((LockSpace)compatibilitySpace).addLock(o, lockObject);
            return true;
        }
        if (n == -2) {
            throw StandardException.newException("40XL1");
        }
        return false;
    }
    
    public CompatibilitySpace createCompatibilitySpace(final LockOwner lockOwner) {
        return new LockSpace(lockOwner);
    }
    
    public int unlock(final CompatibilitySpace compatibilitySpace, final Object o, final Lockable lockable, final Object o2) {
        return ((LockSpace)compatibilitySpace).unlockReference(this.lockTable, lockable, o2, o);
    }
    
    public void unlockGroup(final CompatibilitySpace compatibilitySpace, final Object o) {
        ((LockSpace)compatibilitySpace).unlockGroup(this.lockTable, o);
    }
    
    public void unlockGroup(final CompatibilitySpace compatibilitySpace, final Object o, final Matchable matchable) {
        ((LockSpace)compatibilitySpace).unlockGroup(this.lockTable, o, matchable);
    }
    
    public void transfer(final CompatibilitySpace compatibilitySpace, final Object o, final Object o2) {
        ((LockSpace)compatibilitySpace).transfer(o, o2);
    }
    
    public boolean anyoneBlocked() {
        return this.lockTable.anyoneBlocked();
    }
    
    public boolean areLocksHeld(final CompatibilitySpace compatibilitySpace, final Object o) {
        return ((LockSpace)compatibilitySpace).areLocksHeld(o);
    }
    
    public boolean areLocksHeld(final CompatibilitySpace compatibilitySpace) {
        return ((LockSpace)compatibilitySpace).areLocksHeld();
    }
    
    public boolean zeroDurationlockObject(final CompatibilitySpace compatibilitySpace, final Lockable lockable, final Object o, final int n) throws StandardException {
        final boolean zeroDurationLockObject = this.lockTable.zeroDurationLockObject(compatibilitySpace, lockable, o, n);
        if (!zeroDurationLockObject && n == -2) {
            throw StandardException.newException("40XL1");
        }
        return zeroDurationLockObject;
    }
    
    public boolean isLockHeld(final CompatibilitySpace compatibilitySpace, final Object o, final Lockable lockable, final Object o2) {
        return ((LockSpace)compatibilitySpace).isLockHeld(o, lockable, o2);
    }
    
    public int getWaitTimeout() {
        return this.lockTable.getWaitTimeout();
    }
    
    public void setLimit(final CompatibilitySpace compatibilitySpace, final Object o, final int n, final Limit limit) {
        ((LockSpace)compatibilitySpace).setLimit(o, n, limit);
    }
    
    public void clearLimit(final CompatibilitySpace compatibilitySpace, final Object o) {
        ((LockSpace)compatibilitySpace).clearLimit(o);
    }
    
    static boolean noLockWait(final int n, final CompatibilitySpace compatibilitySpace) {
        if (n == 0) {
            return true;
        }
        final LockOwner owner = compatibilitySpace.getOwner();
        return owner != null && owner.noWait();
    }
    
    public Enumeration makeVirtualLockTable() {
        return new LockTableVTI(this.lockTable.shallowClone());
    }
    
    public void init(final boolean b, final Dictionary dictionary) {
        this.getAndApply(b, dictionary, "derby.locks.deadlockTimeout");
        this.getAndApply(b, dictionary, "derby.locks.waitTimeout");
        this.getAndApply(b, dictionary, "derby.locks.monitor");
        this.getAndApply(b, dictionary, "derby.locks.deadlockTrace");
    }
    
    private void getAndApply(final boolean b, final Dictionary dictionary, final String s) {
        try {
            final Serializable propertyFromSet = PropertyUtil.getPropertyFromSet(b, dictionary, s);
            if (propertyFromSet != null) {
                this.validate(s, propertyFromSet, dictionary);
                this.apply(s, propertyFromSet, dictionary);
            }
        }
        catch (StandardException ex) {}
    }
    
    public boolean validate(final String s, final Serializable s2, final Dictionary dictionary) throws StandardException {
        if (!s.startsWith("derby.locks.")) {
            return false;
        }
        if (s2 != null) {
            if (s.equals("derby.locks.deadlockTimeout")) {
                getWaitValue((String)s2, 20);
            }
            else if (s.equals("derby.locks.waitTimeout")) {
                getWaitValue((String)s2, 60);
            }
            else if (s.equals("derby.locks.monitor")) {
                PropertyUtil.booleanProperty("derby.locks.monitor", s2, false);
            }
            else if (s.equals("derby.locks.deadlockTrace")) {
                PropertyUtil.booleanProperty("derby.locks.deadlockTrace", s2, false);
            }
        }
        return true;
    }
    
    public Serviceable apply(final String s, Serializable propertyFromSet, final Dictionary dictionary) throws StandardException {
        if (propertyFromSet == null) {
            propertyFromSet = PropertyUtil.getPropertyFromSet(dictionary, s);
        }
        final String s2 = (String)propertyFromSet;
        if (s.equals("derby.locks.deadlockTimeout")) {
            this.lockTable.setDeadlockTimeout(getWaitValue(s2, 20));
        }
        else if (s.equals("derby.locks.waitTimeout")) {
            this.lockTable.setWaitTimeout(getWaitValue(s2, 60));
        }
        else if (s.equals("derby.locks.monitor")) {
            this.deadlockMonitor = (PropertyUtil.booleanProperty("derby.locks.monitor", s2, false) ? 2 : 0);
        }
        else if (s.equals("derby.locks.deadlockTrace")) {
            this.lockTable.setDeadlockTrace(PropertyUtil.booleanProperty("derby.locks.deadlockTrace", s2, false));
        }
        return null;
    }
    
    public Serializable map(final String s, final Serializable s2, final Dictionary dictionary) {
        return null;
    }
    
    private static int getWaitValue(final String s, final int n) {
        final int handleInt = PropertyUtil.handleInt(s, Integer.MIN_VALUE, 2147483, n);
        int n2;
        if (handleInt < 0) {
            n2 = -1;
        }
        else {
            n2 = handleInt * 1000;
        }
        return n2;
    }
}
