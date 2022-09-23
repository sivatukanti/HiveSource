// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.queue;

public interface QueuePutListener<T>
{
    void putCompleted(final T p0);
    
    void putMultiCompleted(final MultiItem<T> p0);
}
