// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

import org.apache.htrace.shaded.commons.logging.LogFactory;
import java.util.Arrays;
import java.util.HashSet;
import org.apache.htrace.shaded.commons.logging.Log;

public class TracerPool
{
    private static final Log LOG;
    static final TracerPool GLOBAL;
    private final String name;
    private volatile SpanReceiver[] curReceivers;
    private SpanReceiverShutdownHook shutdownHook;
    private final HashSet<Tracer> curTracers;
    
    public static TracerPool getGlobalTracerPool() {
        return TracerPool.GLOBAL;
    }
    
    public TracerPool(final String name) {
        this.name = name;
        this.shutdownHook = null;
        this.curTracers = new HashSet<Tracer>();
        this.curReceivers = new SpanReceiver[0];
    }
    
    public String getName() {
        return this.name;
    }
    
    public SpanReceiver[] getReceivers() {
        return this.curReceivers;
    }
    
    public synchronized boolean addReceiver(final SpanReceiver receiver) {
        final SpanReceiver[] receivers = this.curReceivers;
        for (int i = 0; i < receivers.length; ++i) {
            if (receivers[i] == receiver) {
                TracerPool.LOG.trace(this.toString() + ": can't add receiver " + receiver.toString() + " since it is already in this pool.");
                return false;
            }
        }
        final SpanReceiver[] newReceivers = Arrays.copyOf(receivers, receivers.length + 1);
        newReceivers[receivers.length] = receiver;
        this.registerShutdownHookIfNeeded();
        this.curReceivers = newReceivers;
        TracerPool.LOG.trace(this.toString() + ": added receiver " + receiver.toString());
        return true;
    }
    
    private synchronized void registerShutdownHookIfNeeded() {
        if (this.shutdownHook != null) {
            return;
        }
        this.shutdownHook = new SpanReceiverShutdownHook();
        Runtime.getRuntime().addShutdownHook(this.shutdownHook);
        TracerPool.LOG.trace(this.toString() + ": registered shutdown hook.");
    }
    
    public synchronized boolean removeReceiver(final SpanReceiver receiver) {
        final SpanReceiver[] receivers = this.curReceivers;
        for (int i = 0; i < receivers.length; ++i) {
            if (receivers[i] == receiver) {
                final SpanReceiver[] newReceivers = new SpanReceiver[receivers.length - 1];
                System.arraycopy(receivers, 0, newReceivers, 0, i);
                System.arraycopy(receivers, i + 1, newReceivers, i, receivers.length - i - 1);
                this.curReceivers = newReceivers;
                TracerPool.LOG.trace(this.toString() + ": removed receiver " + receiver.toString());
                return true;
            }
        }
        TracerPool.LOG.trace(this.toString() + ": can't remove receiver " + receiver.toString() + " since it's not currently in this pool.");
        return false;
    }
    
    public boolean removeAndCloseReceiver(final SpanReceiver receiver) {
        if (!this.removeReceiver(receiver)) {
            return false;
        }
        try {
            TracerPool.LOG.trace(this.toString() + ": closing receiver " + receiver.toString());
            receiver.close();
        }
        catch (Throwable t) {
            TracerPool.LOG.error(this.toString() + ": error closing " + receiver.toString(), t);
        }
        return true;
    }
    
    private synchronized void removeAndCloseAllSpanReceivers() {
        final SpanReceiver[] receivers = this.curReceivers;
        this.curReceivers = new SpanReceiver[0];
        for (final SpanReceiver receiver : receivers) {
            try {
                TracerPool.LOG.trace(this.toString() + ": closing receiver " + receiver.toString());
                receiver.close();
            }
            catch (Throwable t) {
                TracerPool.LOG.error(this.toString() + ": error closing " + receiver.toString(), t);
            }
        }
    }
    
    public synchronized SpanReceiver loadReceiverType(final String className, final HTraceConfiguration conf, final ClassLoader classLoader) {
        final String receiverClass = className.contains(".") ? className : ("org.apache.htrace.core." + className);
        final SpanReceiver[] arr$;
        final SpanReceiver[] receivers = arr$ = this.curReceivers;
        for (final SpanReceiver receiver : arr$) {
            if (receiver.getClass().getName().equals(receiverClass)) {
                TracerPool.LOG.trace(this.toString() + ": returning a reference to receiver " + receiver.toString());
                return receiver;
            }
        }
        TracerPool.LOG.trace(this.toString() + ": creating a new SpanReceiver of type " + className);
        final SpanReceiver receiver2 = new SpanReceiver.Builder(conf).className(className).classLoader(classLoader).build();
        this.addReceiver(receiver2);
        return receiver2;
    }
    
    public synchronized Tracer[] getTracers() {
        return this.curTracers.toArray(new Tracer[this.curTracers.size()]);
    }
    
    synchronized void addTracer(final Tracer tracer) {
        if (this.curTracers.add(tracer)) {
            TracerPool.LOG.trace(this.toString() + ": adding tracer " + tracer.toString());
        }
    }
    
    synchronized void removeTracer(final Tracer tracer) {
        if (this.curTracers.remove(tracer)) {
            TracerPool.LOG.trace(this.toString() + ": removing tracer " + tracer.toString());
            if (this.curTracers.size() == 0) {
                this.removeAndCloseAllSpanReceivers();
            }
        }
    }
    
    @Override
    public String toString() {
        return "TracerPool(" + this.name + ")";
    }
    
    static {
        LOG = LogFactory.getLog(TracerPool.class);
        GLOBAL = new TracerPool("Global");
    }
    
    private class SpanReceiverShutdownHook extends Thread
    {
        SpanReceiverShutdownHook() {
            this.setName("SpanReceiverShutdownHook");
            this.setDaemon(false);
        }
        
        @Override
        public void run() {
            TracerPool.this.removeAndCloseAllSpanReceivers();
        }
    }
}
