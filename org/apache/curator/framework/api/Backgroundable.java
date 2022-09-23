// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api;

import java.util.concurrent.Executor;

public interface Backgroundable<T>
{
    T inBackground();
    
    T inBackground(final Object p0);
    
    T inBackground(final BackgroundCallback p0);
    
    T inBackground(final BackgroundCallback p0, final Object p1);
    
    T inBackground(final BackgroundCallback p0, final Executor p1);
    
    T inBackground(final BackgroundCallback p0, final Object p1, final Executor p2);
}
