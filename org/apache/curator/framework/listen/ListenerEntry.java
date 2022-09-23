// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.listen;

import java.util.concurrent.Executor;

public class ListenerEntry<T>
{
    public final T listener;
    public final Executor executor;
    
    public ListenerEntry(final T listener, final Executor executor) {
        this.listener = listener;
        this.executor = executor;
    }
}
