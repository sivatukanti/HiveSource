// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types;

import java.util.ArrayList;
import org.datanucleus.store.scostore.CollectionStore;
import org.datanucleus.state.ObjectProvider;
import java.util.Collection;
import java.util.Iterator;

public class SCOCollectionIterator implements Iterator
{
    private final Iterator iter;
    private Object last;
    private Collection ownerSCO;
    
    public SCOCollectionIterator(final Collection sco, final ObjectProvider sm, final Collection theDelegate, final CollectionStore backingStore, final boolean useDelegate) {
        this.last = null;
        this.ownerSCO = sco;
        final ArrayList entries = new ArrayList();
        Iterator i = null;
        if (useDelegate) {
            i = theDelegate.iterator();
        }
        else if (backingStore != null) {
            i = backingStore.iterator(sm);
        }
        else {
            i = theDelegate.iterator();
        }
        while (i.hasNext()) {
            entries.add(i.next());
        }
        this.iter = entries.iterator();
    }
    
    @Override
    public boolean hasNext() {
        return this.iter.hasNext();
    }
    
    @Override
    public Object next() {
        return this.last = this.iter.next();
    }
    
    @Override
    public void remove() {
        if (this.last == null) {
            throw new IllegalStateException();
        }
        this.ownerSCO.remove(this.last);
        this.last = null;
    }
}
