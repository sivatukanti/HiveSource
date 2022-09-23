// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.channel.socket.Worker;

public interface WorkerPool<E extends Worker> extends NioSelectorPool
{
    E nextWorker();
}
