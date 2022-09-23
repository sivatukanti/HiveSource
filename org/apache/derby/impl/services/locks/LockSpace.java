// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.locks;

import java.util.Map;
import org.apache.derby.iapi.services.locks.Lockable;
import org.apache.derby.iapi.util.Matchable;
import java.util.Iterator;
import org.apache.derby.iapi.services.locks.Latch;
import org.apache.derby.iapi.error.StandardException;
import java.util.Enumeration;
import java.util.Collection;
import java.util.Collections;
import org.apache.derby.iapi.services.locks.Limit;
import org.apache.derby.iapi.services.locks.LockOwner;
import java.util.HashMap;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;

final class LockSpace implements CompatibilitySpace
{
    private final HashMap groups;
    private final LockOwner owner;
    private HashMap[] spareGroups;
    private Object callbackGroup;
    private int limit;
    private int nextLimitCall;
    private Limit callback;
    private boolean inLimit;
    
    LockSpace(final LockOwner owner) {
        this.spareGroups = new HashMap[3];
        this.groups = new HashMap();
        this.owner = owner;
    }
    
    public LockOwner getOwner() {
        return this.owner;
    }
    
    protected synchronized void addLock(final Object key, final Lock key2) throws StandardException {
        Lock copy = null;
        HashMap<Object, Lock> groupMap = this.groups.get(key);
        if (groupMap == null) {
            groupMap = (HashMap<Object, Lock>)this.getGroupMap(key);
        }
        else if (key2.getCount() != 1) {
            copy = groupMap.get(key2);
        }
        if (copy == null) {
            copy = key2.copy();
            groupMap.put(copy, copy);
        }
        final Lock lock = copy;
        ++lock.count;
        if (this.inLimit) {
            return;
        }
        if (!key.equals(this.callbackGroup)) {
            return;
        }
        final int size = groupMap.size();
        if (size > this.nextLimitCall) {
            this.inLimit = true;
            this.callback.reached(this, key, this.limit, new LockList(Collections.enumeration((Collection<K>)groupMap.keySet())), size);
            this.inLimit = false;
            final int size2 = groupMap.size();
            if (size2 < this.limit / 2) {
                this.nextLimitCall = this.limit;
            }
            else if (size2 < this.nextLimitCall / 2) {
                this.nextLimitCall -= this.limit;
            }
            else {
                this.nextLimitCall += this.limit;
            }
        }
    }
    
    synchronized void unlockGroup(final LockTable lockTable, final Object key) {
        final HashMap<Lock, HashMap<Lock, HashMap<Lock, HashMap>>> hashMap = this.groups.remove(key);
        if (hashMap == null) {
            return;
        }
        final Iterator<Lock> iterator = hashMap.keySet().iterator();
        while (iterator.hasNext()) {
            lockTable.unlock(iterator.next(), 0);
        }
        if (this.callbackGroup != null && key.equals(this.callbackGroup)) {
            this.nextLimitCall = this.limit;
        }
        this.saveGroup(hashMap);
    }
    
    private HashMap getGroupMap(final Object key) {
        final HashMap[] spareGroups = this.spareGroups;
        HashMap value = null;
        for (int i = 0; i < 3; ++i) {
            value = spareGroups[i];
            if (value != null) {
                spareGroups[i] = null;
                break;
            }
        }
        if (value == null) {
            value = new HashMap(5, 0.8f);
        }
        this.groups.put(key, value);
        return value;
    }
    
    private void saveGroup(final HashMap hashMap) {
        final HashMap[] spareGroups = this.spareGroups;
        for (int i = 0; i < 3; ++i) {
            if (spareGroups[i] == null) {
                (spareGroups[i] = hashMap).clear();
                break;
            }
        }
    }
    
    synchronized void unlockGroup(final LockTable lockTable, final Object o, final Matchable matchable) {
        final HashMap<Lock, HashMap<Lock, HashMap<Lock, HashMap>>> hashMap = this.groups.get(o);
        if (hashMap == null) {
            return;
        }
        boolean b = true;
        final Iterator<Lock> iterator = hashMap.keySet().iterator();
        while (iterator.hasNext()) {
            final Lock lock = iterator.next();
            if (!matchable.match(lock.getLockable())) {
                b = false;
            }
            else {
                lockTable.unlock(lock, 0);
                iterator.remove();
            }
        }
        if (b) {
            this.groups.remove(o);
            this.saveGroup(hashMap);
            if (this.callbackGroup != null && o.equals(this.callbackGroup)) {
                this.nextLimitCall = this.limit;
            }
        }
    }
    
    synchronized void transfer(final Object key, final Object key2) {
        final HashMap hashMap = this.groups.get(key);
        if (hashMap == null) {
            return;
        }
        final HashMap hashMap2 = this.groups.get(key2);
        if (hashMap2 == null) {
            this.groups.put(key2, hashMap);
            this.clearLimit(key);
            this.groups.remove(key);
            return;
        }
        if (hashMap2.size() < hashMap.size()) {
            this.mergeGroups(hashMap2, hashMap);
            this.groups.put(key2, hashMap);
        }
        else {
            this.mergeGroups(hashMap, hashMap2);
        }
        this.clearLimit(key);
        this.groups.remove(key);
    }
    
    private void mergeGroups(final HashMap hashMap, final HashMap hashMap2) {
        for (final Lock next : hashMap.keySet()) {
            final Lock value = hashMap2.get(next);
            if (value == null) {
                hashMap2.put(next, next);
            }
            else {
                final Lock lock = next;
                final Lock lock2 = value;
                lock2.count += lock.getCount();
            }
        }
    }
    
    synchronized int unlockReference(final LockTable lockTable, final Lockable lockable, final Object o, final Object o2) {
        final HashMap<Lock, Lock> hashMap = this.groups.get(o2);
        if (hashMap == null) {
            return 0;
        }
        final Lock unlockReference = lockTable.unlockReference(this, lockable, o, hashMap);
        if (unlockReference == null) {
            return 0;
        }
        if (unlockReference.getCount() == 1) {
            if (hashMap.isEmpty()) {
                this.groups.remove(o2);
                this.saveGroup(hashMap);
                if (this.callbackGroup != null && o2.equals(this.callbackGroup)) {
                    this.nextLimitCall = this.limit;
                }
            }
            return 1;
        }
        final Lock lock = unlockReference;
        --lock.count;
        hashMap.put(unlockReference, unlockReference);
        return 1;
    }
    
    synchronized boolean areLocksHeld(final Object key) {
        return this.groups.containsKey(key);
    }
    
    synchronized boolean areLocksHeld() {
        return !this.groups.isEmpty();
    }
    
    synchronized boolean isLockHeld(final Object key, final Lockable lockable, final Object o) {
        final HashMap<Object, Object> hashMap = this.groups.get(key);
        return hashMap != null && hashMap.get(new Lock(this, lockable, o)) != null;
    }
    
    synchronized void setLimit(final Object callbackGroup, final int n, final Limit callback) {
        this.callbackGroup = callbackGroup;
        this.limit = n;
        this.nextLimitCall = n;
        this.callback = callback;
    }
    
    synchronized void clearLimit(final Object o) {
        if (o.equals(this.callbackGroup)) {
            this.callbackGroup = null;
            final int n = Integer.MAX_VALUE;
            this.limit = n;
            this.nextLimitCall = n;
            this.callback = null;
        }
    }
    
    synchronized int deadlockCount(final int n) {
        int n2 = 0;
        final Iterator<HashMap<Lock, HashMap<Lock, HashMap<Lock, HashMap>>>> iterator = this.groups.values().iterator();
        while (iterator.hasNext()) {
            final Iterator<Lock> iterator2 = iterator.next().keySet().iterator();
            while (iterator2.hasNext()) {
                n2 += iterator2.next().getCount();
                if (n2 > n) {
                    return n2;
                }
            }
        }
        return n2;
    }
}
