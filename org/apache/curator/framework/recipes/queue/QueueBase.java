// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.queue;

import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.listen.ListenerContainer;
import java.io.Closeable;

public interface QueueBase<T> extends Closeable
{
    void start() throws Exception;
    
    ListenerContainer<QueuePutListener<T>> getPutListenerContainer();
    
    void setErrorMode(final ErrorMode p0);
    
    boolean flushPuts(final long p0, final TimeUnit p1) throws InterruptedException;
    
    int getLastMessageCount();
}
