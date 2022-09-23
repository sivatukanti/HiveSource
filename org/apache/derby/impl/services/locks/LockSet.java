// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.locks;

import java.util.Iterator;
import java.util.Map;
import java.util.Enumeration;
import org.apache.derby.iapi.services.locks.Latch;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.locks.Lockable;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import java.util.HashMap;

final class LockSet implements LockTable
{
    private final SinglePool factory;
    private final HashMap locks;
    private int deadlockTimeout;
    private int waitTimeout;
    private boolean deadlockTrace;
    private int blockCount;
    
    protected LockSet(final SinglePool factory) {
        this.deadlockTimeout = 20000;
        this.waitTimeout = 60000;
        this.factory = factory;
        this.locks = new HashMap();
    }
    
    public Lock lockObject(final CompatibilitySpace compatibilitySpace, final Lockable lockable, final Object o, int waitTimeout) throws StandardException {
        final LockControl lockControl;
        final Lock addLock;
        synchronized (this) {
            final Control control = this.getControl(lockable);
            if (control == null) {
                final Lock value = new Lock(compatibilitySpace, lockable, o);
                value.grant();
                this.locks.put(lockable, value);
                return value;
            }
            lockControl = control.getLockControl();
            if (lockControl != control) {
                this.locks.put(lockable, lockControl);
            }
            addLock = lockControl.addLock(this, compatibilitySpace, o);
            if (addLock.getCount() != 0) {
                return addLock;
            }
            if (AbstractPool.noLockWait(waitTimeout, compatibilitySpace)) {
                lockControl.giveUpWait(addLock, this);
                return null;
            }
        }
        int n = 0;
        int n2;
        if (waitTimeout == -1) {
            n = 1;
            if ((n2 = this.deadlockTimeout) == -1) {
                n2 = 20000;
            }
        }
        else {
            if (waitTimeout == -2) {
                n2 = (waitTimeout = this.waitTimeout);
            }
            else {
                n2 = waitTimeout;
            }
            if (this.deadlockTimeout >= 0) {
                if (n2 < 0) {
                    n = 1;
                    n2 = this.deadlockTimeout;
                }
                else if (this.deadlockTimeout < n2) {
                    n = 1;
                    n2 = this.deadlockTimeout;
                    waitTimeout -= this.deadlockTimeout;
                }
            }
        }
        final ActiveLock activeLock = (ActiveLock)addLock;
        int n3 = 0;
        long n4 = 0L;
        while (true) {
            byte waitForGrant = 0;
            ActiveLock activeLock2 = null;
            Object[] look = null;
            try {
                try {
                    waitForGrant = activeLock.waitForGrant(n2);
                }
                catch (StandardException ex) {
                    activeLock2 = lockControl.getNextWaiter(activeLock, true, this);
                    throw ex;
                }
                Enumeration virtualLockTable = null;
                long currentTimeMillis = 0L;
                boolean b;
                synchronized (this) {
                    if (lockControl.isGrantable(lockControl.firstWaiter() == activeLock, compatibilitySpace, o)) {
                        lockControl.grant(activeLock);
                        activeLock2 = lockControl.getNextWaiter(activeLock, true, this);
                        return activeLock;
                    }
                    activeLock.clearPotentiallyGranted();
                    b = (waitForGrant != 1);
                    if ((waitForGrant == 0 && n != 0) || waitForGrant == 2) {
                        look = Deadlock.look(this.factory, this, lockControl, activeLock, waitForGrant);
                        if (look == null) {
                            n = 0;
                            n2 = waitTimeout;
                            n4 = 0L;
                            b = false;
                        }
                        else {
                            b = true;
                        }
                    }
                    activeLock2 = lockControl.getNextWaiter(activeLock, b, this);
                    if (b && this.deadlockTrace && look == null) {
                        currentTimeMillis = System.currentTimeMillis();
                        virtualLockTable = this.factory.makeVirtualLockTable();
                    }
                }
                if (b) {
                    if (look != null) {
                        throw Deadlock.buildException(this.factory, look);
                    }
                    if (waitForGrant == 3) {
                        throw StandardException.newException("08000");
                    }
                    if (this.deadlockTrace) {
                        throw Timeout.buildException(activeLock, virtualLockTable, currentTimeMillis);
                    }
                    throw StandardException.newException("40XL1");
                }
            }
            finally {
                if (activeLock2 != null) {
                    activeLock2.wakeUp((byte)1);
                }
            }
            if (n2 != -1) {
                if (waitForGrant != 0) {
                    ++n3;
                }
                if (n3 <= 5) {
                    continue;
                }
                final long currentTimeMillis2 = System.currentTimeMillis();
                if (n4 != 0L) {
                    n2 -= (int)(currentTimeMillis2 - n4);
                }
                n4 = currentTimeMillis2;
            }
        }
    }
    
    public void unlock(final Latch latch, final int n) {
        boolean unlock = false;
        ActiveLock firstWaiter = null;
        synchronized (this) {
            final Control control = this.getControl(latch.getLockable());
            unlock = control.unlock(latch, n);
            boolean b = true;
            if (unlock) {
                firstWaiter = control.firstWaiter();
                if (firstWaiter != null) {
                    b = false;
                    if (!firstWaiter.setPotentiallyGranted()) {
                        firstWaiter = null;
                    }
                }
            }
            if (b) {
                if (control.isEmpty()) {
                    this.locks.remove(control.getLockable());
                }
                return;
            }
        }
        if (unlock && firstWaiter != null) {
            firstWaiter.wakeUp((byte)1);
        }
    }
    
    public synchronized Lock unlockReference(final CompatibilitySpace compatibilitySpace, final Lockable lockable, final Object o, final Map map) {
        final Control control = this.getControl(lockable);
        if (control == null) {
            return null;
        }
        final Lock lock = control.getLock(compatibilitySpace, o);
        if (lock == null) {
            return null;
        }
        final Lock lock2 = map.remove(lock);
        if (lock2 != null) {
            this.unlock(lock2, 1);
        }
        return lock2;
    }
    
    public boolean zeroDurationLockObject(final CompatibilitySpace compatibilitySpace, final Lockable lockable, final Object o, final int n) throws StandardException {
        synchronized (this) {
            final Control control = this.getControl(lockable);
            if (control == null) {
                return true;
            }
            if (control.isGrantable(true, compatibilitySpace, o)) {
                return true;
            }
            if (AbstractPool.noLockWait(n, compatibilitySpace)) {
                return false;
            }
        }
        this.unlock(this.lockObject(compatibilitySpace, lockable, o, n), 1);
        return true;
    }
    
    public void setDeadlockTimeout(final int deadlockTimeout) {
        this.deadlockTimeout = deadlockTimeout;
    }
    
    public void setWaitTimeout(final int waitTimeout) {
        this.waitTimeout = waitTimeout;
    }
    
    public int getWaitTimeout() {
        return this.waitTimeout;
    }
    
    public void setDeadlockTrace(final boolean deadlockTrace) {
        this.deadlockTrace = deadlockTrace;
    }
    
    public String toDebugString() {
        return null;
    }
    
    public void addWaiters(final Map map) {
        final Iterator<Control> iterator = this.locks.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().addWaiters(map);
        }
    }
    
    public synchronized Map shallowClone() {
        final HashMap<Lockable, Control> hashMap = new HashMap<Lockable, Control>();
        for (final Lockable key : this.locks.keySet()) {
            hashMap.put(key, this.getControl(key).shallowClone());
        }
        return hashMap;
    }
    
    public void oneMoreWaiter() {
        ++this.blockCount;
    }
    
    public void oneLessWaiter() {
        --this.blockCount;
    }
    
    public synchronized boolean anyoneBlocked() {
        return this.blockCount != 0;
    }
    
    private final Control getControl(final Lockable key) {
        return this.locks.get(key);
    }
}
