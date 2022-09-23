// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util.internal;

import java.util.Iterator;

public interface ReusableIterator<E> extends Iterator<E>
{
    void rewind();
}
