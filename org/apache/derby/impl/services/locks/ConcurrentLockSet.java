// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.locks;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import java.util.Map;
import java.util.Enumeration;
import org.apache.derby.iapi.services.locks.Latch;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import org.apache.derby.iapi.services.locks.Lockable;
import java.util.concurrent.ConcurrentHashMap;

final class ConcurrentLockSet implements LockTable
{
    private final AbstractPool factory;
    private final ConcurrentHashMap<Lockable, Entry> locks;
    private ArrayList<Entry> seenByDeadlockDetection;
    private int deadlockTimeout;
    private int waitTimeout;
    private boolean deadlockTrace;
    private final AtomicInteger blockCount;
    
    ConcurrentLockSet(final AbstractPool factory) {
        this.deadlockTimeout = 20000;
        this.waitTimeout = 60000;
        this.factory = factory;
        this.blockCount = new AtomicInteger();
        this.locks = new ConcurrentHashMap<Lockable, Entry>();
    }
    
    private Entry getEntry(final Lockable lockable) {
        Entry value = this.locks.get(lockable);
        while (true) {
            if (value != null) {
                value.lock();
                if (value.control != null) {
                    return value;
                }
            }
            else {
                value = new Entry();
                value.lock();
            }
            final Entry entry = this.locks.putIfAbsent(lockable, value);
            if (entry == null) {
                return value;
            }
            value.unlock();
            value = entry;
        }
    }
    
    private Object[] checkDeadlock(final Entry entry, final ActiveLock activeLock, final byte b) {
        final LockControl lockControl = (LockControl)entry.control;
        entry.enterDeadlockDetection();
        synchronized (Deadlock.class) {
            try {
                return Deadlock.look(this.factory, this, lockControl, activeLock, b);
            }
            finally {
                final Iterator<Entry> iterator = this.seenByDeadlockDetection.iterator();
                while (iterator.hasNext()) {
                    iterator.next().unlock();
                }
                this.seenByDeadlockDetection = null;
                entry.exitDeadlockDetection();
            }
        }
    }
    
    public Lock lockObject(final CompatibilitySpace compatibilitySpace, final Lockable lockable, final Object o, int waitTimeout) throws StandardException {
        final Entry entry = this.getEntry(lockable);
        LockControl lockControl;
        Lock addLock;
        try {
            final Control control = entry.control;
            if (control == null) {
                final Lock control2 = new Lock(compatibilitySpace, lockable, o);
                control2.grant();
                return (Lock)(entry.control = control2);
            }
            lockControl = control.getLockControl();
            if (lockControl != control) {
                entry.control = lockControl;
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
        finally {
            entry.unlock();
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
            Object[] checkDeadlock = null;
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
                entry.lock();
                boolean b;
                try {
                    if (lockControl.isGrantable(lockControl.firstWaiter() == activeLock, compatibilitySpace, o)) {
                        lockControl.grant(activeLock);
                        activeLock2 = lockControl.getNextWaiter(activeLock, true, this);
                        return activeLock;
                    }
                    activeLock.clearPotentiallyGranted();
                    b = (waitForGrant != 1);
                    if ((waitForGrant == 0 && n != 0) || waitForGrant == 2) {
                        checkDeadlock = this.checkDeadlock(entry, activeLock, waitForGrant);
                        if (checkDeadlock == null) {
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
                }
                finally {
                    entry.unlock();
                }
                if (b) {
                    if (this.deadlockTrace && checkDeadlock == null) {
                        currentTimeMillis = System.currentTimeMillis();
                        virtualLockTable = this.factory.makeVirtualLockTable();
                    }
                    if (checkDeadlock != null) {
                        throw Deadlock.buildException(this.factory, checkDeadlock);
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
        final Entry entry = this.locks.get(latch.getLockable());
        entry.lock();
        try {
            this.unlock(entry, latch, n);
        }
        finally {
            entry.unlock();
        }
    }
    
    private void unlock(final Entry entry, final Latch latch, final int n) {
        ActiveLock firstWaiter = null;
        final Control control = entry.control;
        final boolean unlock = control.unlock(latch, n);
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
                entry.control = null;
            }
            return;
        }
        if (unlock && firstWaiter != null) {
            firstWaiter.wakeUp((byte)1);
        }
    }
    
    public Lock unlockReference(final CompatibilitySpace compatibilitySpace, final Lockable key, final Object o, final Map map) {
        final Entry entry = this.locks.get(key);
        if (entry == null) {
            return null;
        }
        entry.lock();
        try {
            final Control control = entry.control;
            if (control == null) {
                return null;
            }
            final Lock lock = control.getLock(compatibilitySpace, o);
            if (lock == null) {
                return null;
            }
            final Lock lock2 = map.remove(lock);
            if (lock2 != null) {
                this.unlock(entry, lock2, 1);
            }
            return lock2;
        }
        finally {
            entry.unlock();
        }
    }
    
    public boolean zeroDurationLockObject(final CompatibilitySpace compatibilitySpace, final Lockable key, final Object o, final int n) throws StandardException {
        final Entry entry = this.locks.get(key);
        if (entry == null) {
            return true;
        }
        entry.lock();
        try {
            final Control control = entry.control;
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
        finally {
            entry.unlock();
        }
        this.unlock(this.lockObject(compatibilitySpace, key, o, n), 1);
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
    
    private String toDebugString() {
        return null;
    }
    
    public void addWaiters(final Map map) {
        this.seenByDeadlockDetection = new ArrayList<Entry>(this.locks.size());
        for (final Entry e : this.locks.values()) {
            this.seenByDeadlockDetection.add(e);
            e.lockForDeadlockDetection();
            if (e.control != null) {
                e.control.addWaiters(map);
            }
        }
    }
    
    public Map<Lockable, Control> shallowClone() {
        final HashMap<Lockable, Control> hashMap = new HashMap<Lockable, Control>();
        for (final Entry entry : this.locks.values()) {
            entry.lock();
            try {
                final Control control = entry.control;
                if (control == null) {
                    continue;
                }
                hashMap.put(control.getLockable(), control.shallowClone());
            }
            finally {
                entry.unlock();
            }
        }
        return hashMap;
    }
    
    public void oneMoreWaiter() {
        this.blockCount.incrementAndGet();
    }
    
    public void oneLessWaiter() {
        this.blockCount.decrementAndGet();
    }
    
    public boolean anyoneBlocked() {
        return this.blockCount.get() != 0;
    }
    
    private static final class Entry
    {
        Control control;
        private final ReentrantLock mutex;
        private Condition deadlockDetection;
        
        private Entry() {
            this.mutex = new ReentrantLock();
        }
        
        void lock() {
            this.mutex.lock();
            while (this.deadlockDetection != null) {
                this.deadlockDetection.awaitUninterruptibly();
            }
        }
        
        void unlock() {
            this.mutex.unlock();
        }
        
        void lockForDeadlockDetection() {
            this.mutex.lock();
        }
        
        void enterDeadlockDetection() {
            this.deadlockDetection = this.mutex.newCondition();
            this.mutex.unlock();
        }
        
        void exitDeadlockDetection() {
            this.mutex.lock();
            this.deadlockDetection.signalAll();
            this.deadlockDetection = null;
        }
    }
}
