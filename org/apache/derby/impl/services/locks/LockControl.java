// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.locks;

import java.util.Map;
import java.util.ListIterator;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import org.apache.derby.iapi.services.locks.Latch;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.apache.derby.iapi.services.locks.Lockable;

final class LockControl implements Control
{
    private final Lockable ref;
    private Lock firstGrant;
    private List granted;
    private List waiting;
    private Lock lastPossibleSkip;
    
    protected LockControl(final Lock firstGrant, final Lockable ref) {
        this.ref = ref;
        this.firstGrant = firstGrant;
    }
    
    private LockControl(final LockControl lockControl) {
        this.ref = lockControl.ref;
        this.firstGrant = lockControl.firstGrant;
        if (lockControl.granted != null) {
            this.granted = new LinkedList(lockControl.granted);
        }
        if (lockControl.waiting != null) {
            this.waiting = new LinkedList(lockControl.waiting);
        }
        this.lastPossibleSkip = lockControl.lastPossibleSkip;
    }
    
    public LockControl getLockControl() {
        return this;
    }
    
    public boolean isEmpty() {
        return this.isUnlocked() && (this.waiting == null || this.waiting.isEmpty());
    }
    
    void grant(final Lock firstGrant) {
        firstGrant.grant();
        final List granted = this.granted;
        if (granted == null) {
            if (this.firstGrant == null) {
                this.firstGrant = firstGrant;
            }
            else {
                final LinkedList granted2 = new LinkedList<Lock>();
                this.granted = granted2;
                final LinkedList<Lock> list = (LinkedList<Lock>)granted2;
                list.add(this.firstGrant);
                list.add(firstGrant);
                this.firstGrant = null;
            }
        }
        else {
            granted.add(firstGrant);
        }
    }
    
    public boolean unlock(final Latch latch, int i) {
        if (i == 0) {
            i = latch.getCount();
        }
        final List granted = this.granted;
        int index = 0;
        while (i > 0) {
            Lock firstGrant;
            if (this.firstGrant != null) {
                firstGrant = this.firstGrant;
            }
            else {
                index = granted.indexOf(latch);
                firstGrant = granted.get(index);
            }
            i -= firstGrant.unlock(i);
            if (firstGrant.getCount() != 0) {
                continue;
            }
            if (this.firstGrant == firstGrant) {
                this.firstGrant = null;
            }
            else {
                granted.remove(index);
            }
        }
        return true;
    }
    
    public boolean isGrantable(final boolean b, final CompatibilitySpace compatibilitySpace, final Object o) {
        if (this.isUnlocked()) {
            return true;
        }
        boolean b2 = false;
        final Lockable ref = this.ref;
        final List granted = this.granted;
        final boolean lockerAlwaysCompatible = ref.lockerAlwaysCompatible();
        int n = 0;
        do {
            final Lock lock = (this.firstGrant == null) ? granted.get(n) : this.firstGrant;
            final boolean b3 = lock.getCompatabilitySpace() == compatibilitySpace;
            if (b3 && lockerAlwaysCompatible) {
                b2 = true;
            }
            else {
                if (!ref.requestCompatible(o, lock.getQualifier())) {
                    b2 = false;
                    break;
                }
                if (!b3 && !b) {
                    continue;
                }
                b2 = true;
            }
        } while (++n < ((this.firstGrant == null) ? granted.size() : 0));
        return b2;
    }
    
    public Lock addLock(final LockTable lockTable, final CompatibilitySpace compatibilitySpace, final Object o) {
        boolean b = false;
        final boolean b2 = this.firstWaiter() != null;
        Lock lock = null;
        final Lockable ref = this.ref;
        boolean b3 = false;
        int n = 0;
        if (!this.isUnlocked()) {
            final boolean lockerAlwaysCompatible = ref.lockerAlwaysCompatible();
            int n2 = 0;
            do {
                final Lock lock2 = (this.firstGrant == null) ? this.granted.get(n2) : this.firstGrant;
                final boolean b4 = lock2.getCompatabilitySpace() == compatibilitySpace;
                if (b4 && lockerAlwaysCompatible) {
                    b3 = true;
                    if (n != 0) {
                        break;
                    }
                    if (o == lock2.getQualifier()) {
                        lock = lock2;
                    }
                    b = true;
                }
                else {
                    if (!ref.requestCompatible(o, lock2.getQualifier())) {
                        b = false;
                        lock = null;
                        if (b3) {
                            break;
                        }
                        n = 1;
                    }
                    if (n != 0 || (!b4 && b2)) {
                        continue;
                    }
                    b = true;
                }
            } while (++n2 < ((this.firstGrant == null) ? this.granted.size() : 0));
        }
        if (lock != null) {
            final Lock lock3 = lock;
            ++lock3.count;
            return lock;
        }
        if (b) {
            final Lock lock4 = new Lock(compatibilitySpace, ref, o);
            this.grant(lock4);
            return lock4;
        }
        final ActiveLock lastPossibleSkip = new ActiveLock(compatibilitySpace, ref, o);
        if (b3) {
            lastPossibleSkip.canSkip = true;
        }
        if (this.waiting == null) {
            this.waiting = new LinkedList();
        }
        this.addWaiter(lastPossibleSkip, lockTable);
        if (lastPossibleSkip.canSkip) {
            this.lastPossibleSkip = lastPossibleSkip;
        }
        return lastPossibleSkip;
    }
    
    protected boolean isUnlocked() {
        if (this.firstGrant != null) {
            return false;
        }
        final List granted = this.granted;
        return granted == null || granted.isEmpty();
    }
    
    public ActiveLock firstWaiter() {
        if (this.waiting == null || this.waiting.isEmpty()) {
            return null;
        }
        return this.waiting.get(0);
    }
    
    ActiveLock getNextWaiter(final ActiveLock activeLock, final boolean b, final LockTable lockTable) {
        ActiveLock firstWaiter = null;
        if (b && this.waiting.get(0) == activeLock) {
            this.popFrontWaiter(lockTable);
            firstWaiter = this.firstWaiter();
        }
        else if (this.lastPossibleSkip != null && this.lastPossibleSkip != activeLock) {
            final int index = this.waiting.indexOf(activeLock);
            final int n = b ? index : -1;
            if (index != this.waiting.size() - 1) {
                final ListIterator listIterator = this.waiting.listIterator(index + 1);
                while (listIterator.hasNext()) {
                    final ActiveLock activeLock2 = listIterator.next();
                    if (activeLock2.canSkip) {
                        firstWaiter = activeLock2;
                        break;
                    }
                }
            }
            if (b) {
                this.removeWaiter(n, lockTable);
            }
        }
        else if (b) {
            this.removeWaiter(activeLock, lockTable);
        }
        if (b && activeLock == this.lastPossibleSkip) {
            this.lastPossibleSkip = null;
        }
        if (firstWaiter != null && !firstWaiter.setPotentiallyGranted()) {
            firstWaiter = null;
        }
        return firstWaiter;
    }
    
    public Lockable getLockable() {
        return this.ref;
    }
    
    public Lock getFirstGrant() {
        return this.firstGrant;
    }
    
    public List getGranted() {
        return this.granted;
    }
    
    public List getWaiting() {
        return this.waiting;
    }
    
    protected void giveUpWait(final Object o, final LockTable lockTable) {
        this.removeWaiter(o, lockTable);
        if (o == this.lastPossibleSkip) {
            this.lastPossibleSkip = null;
        }
    }
    
    public void addWaiters(final Map map) {
        if (this.waiting == null || this.waiting.isEmpty()) {
            return;
        }
        Control control = this;
        final ListIterator<ActiveLock> listIterator = (ListIterator<ActiveLock>)this.waiting.listIterator();
        while (listIterator.hasNext()) {
            final ActiveLock activeLock = listIterator.next();
            map.put(activeLock.getCompatabilitySpace(), activeLock);
            map.put(activeLock, control);
            control = activeLock;
        }
    }
    
    List getGrants() {
        LinkedList<Lock> list;
        if (this.firstGrant != null) {
            list = new LinkedList<Lock>();
            list.add(this.firstGrant);
        }
        else {
            list = new LinkedList<Lock>(this.granted);
        }
        return list;
    }
    
    public final Lock getLock(final CompatibilitySpace compatibilitySpace, final Object o) {
        if (this.isUnlocked()) {
            return null;
        }
        final List granted = this.granted;
        int n = 0;
        do {
            final Lock lock = (this.firstGrant == null) ? granted.get(n) : this.firstGrant;
            if (lock.getCompatabilitySpace() != compatibilitySpace) {
                continue;
            }
            if (lock.getQualifier() == o) {
                return lock;
            }
        } while (++n < ((this.firstGrant == null) ? granted.size() : 0));
        return null;
    }
    
    public Control shallowClone() {
        return new LockControl(this);
    }
    
    private void addWaiter(final Lock lock, final LockTable lockTable) {
        this.waiting.add(lock);
        lockTable.oneMoreWaiter();
    }
    
    private Object popFrontWaiter(final LockTable lockTable) {
        return this.removeWaiter(0, lockTable);
    }
    
    private Object removeWaiter(final int n, final LockTable lockTable) {
        lockTable.oneLessWaiter();
        return this.waiting.remove(n);
    }
    
    private int removeWaiter(final Object o, final LockTable lockTable) {
        lockTable.oneLessWaiter();
        return this.waiting.remove(o) ? 1 : 0;
    }
}
