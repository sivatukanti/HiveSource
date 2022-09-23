// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.queue;

public interface QueueSerializer<T>
{
    byte[] serialize(final T p0);
    
    T deserialize(final byte[] p0);
}
