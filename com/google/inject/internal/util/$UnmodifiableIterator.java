// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.util.Iterator;

public abstract class $UnmodifiableIterator<E> implements Iterator<E>
{
    public final void remove() {
        throw new UnsupportedOperationException();
    }
}
