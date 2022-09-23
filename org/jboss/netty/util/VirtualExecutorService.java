// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.AbstractExecutorService;

public class VirtualExecutorService extends AbstractExecutorService
{
    private final Executor e;
    private final ExecutorService s;
    final Object startStopLock;
    volatile boolean shutdown;
    final Set<Thread> activeThreads;
    
    public VirtualExecutorService(final Executor parent) {
        this.startStopLock = new Object();
        this.activeThreads = new MapBackedSet<Thread>(new IdentityHashMap<Thread, Boolean>());
        if (parent == null) {
            throw new NullPointerException("parent");
        }
        if (parent instanceof ExecutorService) {
            this.e = null;
            this.s = (ExecutorService)parent;
        }
        else {
            this.e = parent;
            this.s = null;
        }
    }
    
    public boolean isShutdown() {
        synchronized (this.startStopLock) {
            return this.shutdown;
        }
    }
    
    public boolean isTerminated() {
        synchronized (this.startStopLock) {
            return this.shutdown && this.activeThreads.isEmpty();
        }
    }
    
    public void shutdown() {
        synchronized (this.startStopLock) {
            if (this.shutdown) {
                return;
            }
            this.shutdown = true;
        }
    }
    
    public List<Runnable> shutdownNow() {
        synchronized (this.startStopLock) {
            if (!this.isTerminated()) {
                this.shutdown();
                for (final Thread t : this.activeThreads) {
                    t.interrupt();
                }
            }
        }
        return Collections.emptyList();
    }
    
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        synchronized (this.startStopLock) {
            if (!this.isTerminated()) {
                this.startStopLock.wait(TimeUnit.MILLISECONDS.convert(timeout, unit));
            }
            return this.isTerminated();
        }
    }
    
    public void execute(final Runnable command) {
        if (command == null) {
            throw new NullPointerException("command");
        }
        if (this.shutdown) {
            throw new RejectedExecutionException();
        }
        if (this.s != null) {
            this.s.execute(new ChildExecutorRunnable(command));
        }
        else {
            this.e.execute(new ChildExecutorRunnable(command));
        }
    }
    
    private class ChildExecutorRunnable implements Runnable
    {
        private final Runnable runnable;
        
        ChildExecutorRunnable(final Runnable runnable) {
            this.runnable = runnable;
        }
        
        public void run() {
            final Thread thread = Thread.currentThread();
            synchronized (VirtualExecutorService.this.startStopLock) {
                VirtualExecutorService.this.activeThreads.add(thread);
            }
            try {
                this.runnable.run();
            }
            finally {
                synchronized (VirtualExecutorService.this.startStopLock) {
                    final boolean removed = VirtualExecutorService.this.activeThreads.remove(thread);
                    assert removed;
                    if (VirtualExecutorService.this.isTerminated()) {
                        VirtualExecutorService.this.startStopLock.notifyAll();
                    }
                }
            }
        }
    }
}
