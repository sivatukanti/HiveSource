// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.locks;

import java.util.NoSuchElementException;
import java.util.List;
import java.util.Map;
import org.apache.derby.iapi.services.locks.Latch;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Enumeration;

class LockTableVTI implements Enumeration
{
    private final Iterator outerControl;
    private Control control;
    private ListIterator grantedList;
    private ListIterator waitingList;
    private Latch nextLock;
    
    LockTableVTI(final Map map) {
        this.outerControl = map.values().iterator();
    }
    
    public boolean hasMoreElements() {
        if (this.nextLock != null) {
            return true;
        }
        while (true) {
            if (this.control == null) {
                if (!this.outerControl.hasNext()) {
                    return false;
                }
                this.control = this.outerControl.next();
                final List granted = this.control.getGranted();
                if (granted != null) {
                    this.grantedList = granted.listIterator();
                }
                final List waiting = this.control.getWaiting();
                if (waiting != null) {
                    this.waitingList = waiting.listIterator();
                }
                this.nextLock = this.control.getFirstGrant();
                if (this.nextLock == null) {
                    this.nextLock = this.getNextLock(this.control);
                }
            }
            else {
                this.nextLock = this.getNextLock(this.control);
            }
            if (this.nextLock != null) {
                return true;
            }
            this.control = null;
        }
    }
    
    private Latch getNextLock(final Control control) {
        Latch latch = null;
        if (this.grantedList != null) {
            if (this.grantedList.hasNext()) {
                latch = this.grantedList.next();
            }
            else {
                this.grantedList = null;
            }
        }
        if (latch == null && this.waitingList != null) {
            if (this.waitingList.hasNext()) {
                latch = this.waitingList.next();
            }
            else {
                this.waitingList = null;
            }
        }
        return latch;
    }
    
    public Object nextElement() {
        if (!this.hasMoreElements()) {
            throw new NoSuchElementException();
        }
        final Latch nextLock = this.nextLock;
        this.nextLock = null;
        return nextLock;
    }
}
