// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.shaded.com.google.common.collect;

import org.apache.curator.shaded.com.google.common.annotations.GwtCompatible;
import java.util.Iterator;

@GwtCompatible
public abstract class UnmodifiableIterator<E> implements Iterator<E>
{
    protected UnmodifiableIterator() {
    }
    
    @Deprecated
    @Override
    public final void remove() {
        throw new UnsupportedOperationException();
    }
}
