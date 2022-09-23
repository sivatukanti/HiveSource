// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.execution;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import java.util.concurrent.Executor;
import org.jboss.netty.util.EstimatableObjectWrapper;

public abstract class ChannelEventRunnable implements Runnable, EstimatableObjectWrapper
{
    protected static final ThreadLocal<Executor> PARENT;
    protected final ChannelHandlerContext ctx;
    protected final ChannelEvent e;
    int estimatedSize;
    private final Executor executor;
    
    protected ChannelEventRunnable(final ChannelHandlerContext ctx, final ChannelEvent e, final Executor executor) {
        this.ctx = ctx;
        this.e = e;
        this.executor = executor;
    }
    
    public ChannelHandlerContext getContext() {
        return this.ctx;
    }
    
    public ChannelEvent getEvent() {
        return this.e;
    }
    
    public Object unwrap() {
        return this.e;
    }
    
    public final void run() {
        this.doRun();
    }
    
    protected abstract void doRun();
    
    static {
        PARENT = new ThreadLocal<Executor>();
    }
}
