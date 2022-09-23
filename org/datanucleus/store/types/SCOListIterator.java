// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types;

import java.util.Iterator;
import java.util.ArrayList;
import org.datanucleus.store.scostore.ListStore;
import org.datanucleus.state.ObjectProvider;
import java.util.List;
import java.util.ListIterator;

public class SCOListIterator implements ListIterator
{
    private final ListIterator iter;
    private final List ownerSCO;
    private boolean reverse;
    
    public SCOListIterator(final List sco, final ObjectProvider sm, final List theDelegate, final ListStore theStore, final boolean useDelegate, final int startIndex) {
        this.ownerSCO = sco;
        final ArrayList entries = new ArrayList();
        Iterator i = null;
        if (useDelegate) {
            i = theDelegate.iterator();
        }
        else if (theStore != null) {
            i = theStore.iterator(sm);
        }
        else {
            i = theDelegate.iterator();
        }
        while (i.hasNext()) {
            entries.add(i.next());
        }
        if (startIndex >= 0) {
            this.iter = entries.listIterator(startIndex);
        }
        else {
            this.iter = entries.listIterator();
        }
    }
    
    @Override
    public void add(final Object o) {
        this.iter.add(o);
        this.ownerSCO.add(this.iter.previousIndex(), o);
    }
    
    @Override
    public boolean hasNext() {
        return this.iter.hasNext();
    }
    
    @Override
    public boolean hasPrevious() {
        return this.iter.hasPrevious();
    }
    
    @Override
    public Object next() {
        final Object result = this.iter.next();
        this.reverse = false;
        return result;
    }
    
    @Override
    public int nextIndex() {
        return this.iter.nextIndex();
    }
    
    @Override
    public Object previous() {
        final Object result = this.iter.previous();
        this.reverse = true;
        return result;
    }
    
    @Override
    public int previousIndex() {
        return this.iter.previousIndex();
    }
    
    @Override
    public void remove() {
        this.iter.remove();
        this.ownerSCO.remove(this.iter.nextIndex());
    }
    
    @Override
    public void set(final Object o) {
        this.iter.set(o);
        this.ownerSCO.set(this.reverse ? this.iter.nextIndex() : this.iter.previousIndex(), o);
    }
}
