// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.queue;

import org.apache.curator.framework.state.ConnectionStateListener;

public interface QueueConsumer<T> extends ConnectionStateListener
{
    void consumeMessage(final T p0) throws Exception;
}
