// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;
import java.util.ListIterator;

public abstract class AbstractQueryResultIterator implements ListIterator
{
    protected static final Localiser LOCALISER;
    
    @Override
    public void add(final Object arg0) {
        throw new UnsupportedOperationException(AbstractQueryResultIterator.LOCALISER.msg("052603"));
    }
    
    @Override
    public abstract boolean hasNext();
    
    @Override
    public abstract boolean hasPrevious();
    
    @Override
    public abstract Object next();
    
    @Override
    public abstract int nextIndex();
    
    @Override
    public abstract Object previous();
    
    @Override
    public abstract int previousIndex();
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException(AbstractQueryResultIterator.LOCALISER.msg("052603"));
    }
    
    @Override
    public void set(final Object arg0) {
        throw new UnsupportedOperationException(AbstractQueryResultIterator.LOCALISER.msg("052603"));
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
