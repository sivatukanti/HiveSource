// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.util.concurrent;

import java.util.logging.Level;
import com.google.common.base.Preconditions;
import javax.annotation.concurrent.GuardedBy;
import java.util.Iterator;
import java.util.concurrent.Executor;
import com.google.common.collect.Queues;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
final class ExecutionQueue
{
    private static final Logger logger;
    private final ConcurrentLinkedQueue<RunnableExecutorPair> queuedListeners;
    private final ReentrantLock lock;
    
    ExecutionQueue() {
        this.queuedListeners = Queues.newConcurrentLinkedQueue();
        this.lock = new ReentrantLock();
    }
    
    void add(final Runnable runnable, final Executor executor) {
        this.queuedListeners.add(new RunnableExecutorPair(runnable, executor));
    }
    
    void execute() {
        final Iterator<RunnableExecutorPair> iterator = this.queuedListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().submit();
            iterator.remove();
        }
    }
    
    static {
        logger = Logger.getLogger(ExecutionQueue.class.getName());
    }
    
    private final class RunnableExecutorPair implements Runnable
    {
        private final Executor executor;
        private final Runnable runnable;
        @GuardedBy("lock")
        private boolean hasBeenExecuted;
        
        RunnableExecutorPair(final Runnable runnable, final Executor executor) {
            this.hasBeenExecuted = false;
            this.runnable = Preconditions.checkNotNull(runnable);
            this.executor = Preconditions.checkNotNull(executor);
        }
        
        private void submit() {
            ExecutionQueue.this.lock.lock();
            try {
                if (!this.hasBeenExecuted) {
                    try {
                        this.executor.execute(this);
                    }
                    catch (Exception e) {
                        ExecutionQueue.logger.log(Level.SEVERE, "Exception while executing listener " + this.runnable + " with executor " + this.executor, e);
                    }
                }
            }
            finally {
                if (ExecutionQueue.this.lock.isHeldByCurrentThread()) {
                    this.hasBeenExecuted = true;
                    ExecutionQueue.this.lock.unlock();
                }
            }
        }
        
        @Override
        public final void run() {
            if (ExecutionQueue.this.lock.isHeldByCurrentThread()) {
                this.hasBeenExecuted = true;
                ExecutionQueue.this.lock.unlock();
            }
            this.runnable.run();
        }
    }
}
