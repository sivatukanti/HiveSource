// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.listen;

import java.util.concurrent.Executor;

public interface Listenable<T>
{
    void addListener(final T p0);
    
    void addListener(final T p0, final Executor p1);
    
    void removeListener(final T p0);
}
