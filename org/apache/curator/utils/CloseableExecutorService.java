// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.utils;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Callable;
import java.util.Iterator;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import java.util.Map;
import org.apache.curator.shaded.com.google.common.collect.Sets;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.slf4j.LoggerFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.Set;
import org.slf4j.Logger;
import java.io.Closeable;

public class CloseableExecutorService implements Closeable
{
    private final Logger log;
    private final Set<Future<?>> futures;
    private final ExecutorService executorService;
    private final boolean shutdownOnClose;
    protected final AtomicBoolean isOpen;
    
    public CloseableExecutorService(final ExecutorService executorService) {
        this(executorService, false);
    }
    
    public CloseableExecutorService(final ExecutorService executorService, final boolean shutdownOnClose) {
        this.log = LoggerFactory.getLogger(CloseableExecutorService.class);
        this.futures = Sets.newSetFromMap((Map<Future<?>, Boolean>)Maps.newConcurrentMap());
        this.isOpen = new AtomicBoolean(true);
        this.executorService = Preconditions.checkNotNull(executorService, (Object)"executorService cannot be null");
        this.shutdownOnClose = shutdownOnClose;
    }
    
    public boolean isShutdown() {
        return !this.isOpen.get();
    }
    
    @VisibleForTesting
    int size() {
        return this.futures.size();
    }
    
    @Override
    public void close() {
        this.isOpen.set(false);
        final Iterator<Future<?>> iterator = this.futures.iterator();
        while (iterator.hasNext()) {
            final Future<?> future = iterator.next();
            iterator.remove();
            if (!future.isDone() && !future.isCancelled() && !future.cancel(true)) {
                this.log.warn("Could not cancel " + future);
            }
        }
        if (this.shutdownOnClose) {
            this.executorService.shutdownNow();
        }
    }
    
    public <V> Future<V> submit(final Callable<V> task) {
        Preconditions.checkState(this.isOpen.get(), (Object)"CloseableExecutorService is closed");
        final InternalFutureTask<V> futureTask = new InternalFutureTask<V>(new FutureTask<V>(task));
        this.executorService.execute(futureTask);
        return futureTask;
    }
    
    public Future<?> submit(final Runnable task) {
        Preconditions.checkState(this.isOpen.get(), (Object)"CloseableExecutorService is closed");
        final InternalFutureTask<Void> futureTask = new InternalFutureTask<Void>(new FutureTask<Void>(task, null));
        this.executorService.execute(futureTask);
        return futureTask;
    }
    
    protected class InternalScheduledFutureTask implements Future<Void>
    {
        private final ScheduledFuture<?> scheduledFuture;
        
        public InternalScheduledFutureTask(final ScheduledFuture<?> scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
            CloseableExecutorService.this.futures.add(scheduledFuture);
        }
        
        @Override
        public boolean cancel(final boolean mayInterruptIfRunning) {
            CloseableExecutorService.this.futures.remove(this.scheduledFuture);
            return this.scheduledFuture.cancel(mayInterruptIfRunning);
        }
        
        @Override
        public boolean isCancelled() {
            return this.scheduledFuture.isCancelled();
        }
        
        @Override
        public boolean isDone() {
            return this.scheduledFuture.isDone();
        }
        
        @Override
        public Void get() throws InterruptedException, ExecutionException {
            return null;
        }
        
        @Override
        public Void get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }
    }
    
    protected class InternalFutureTask<T> extends FutureTask<T>
    {
        private final RunnableFuture<T> task;
        
        InternalFutureTask(final RunnableFuture<T> task) {
            super(task, null);
            this.task = task;
            CloseableExecutorService.this.futures.add(task);
        }
        
        @Override
        protected void done() {
            CloseableExecutorService.this.futures.remove(this.task);
        }
    }
}
