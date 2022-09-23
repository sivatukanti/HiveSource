// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class TraceExecutorService implements ExecutorService
{
    private final Tracer tracer;
    private final String scopeName;
    private final ExecutorService impl;
    
    TraceExecutorService(final Tracer tracer, final String scopeName, final ExecutorService impl) {
        this.tracer = tracer;
        this.scopeName = scopeName;
        this.impl = impl;
    }
    
    @Override
    public void execute(final Runnable command) {
        this.impl.execute(this.tracer.wrap(command, this.scopeName));
    }
    
    @Override
    public void shutdown() {
        this.impl.shutdown();
    }
    
    @Override
    public List<Runnable> shutdownNow() {
        return this.impl.shutdownNow();
    }
    
    @Override
    public boolean isShutdown() {
        return this.impl.isShutdown();
    }
    
    @Override
    public boolean isTerminated() {
        return this.impl.isTerminated();
    }
    
    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        return this.impl.awaitTermination(timeout, unit);
    }
    
    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        return this.impl.submit((Callable<T>)this.tracer.wrap((Callable<T>)task, this.scopeName));
    }
    
    @Override
    public <T> Future<T> submit(final Runnable task, final T result) {
        return this.impl.submit(this.tracer.wrap(task, this.scopeName), result);
    }
    
    @Override
    public Future<?> submit(final Runnable task) {
        return this.impl.submit(this.tracer.wrap(task, this.scopeName));
    }
    
    private <T> Collection<? extends Callable<T>> wrapCollection(final Collection<? extends Callable<T>> tasks) {
        final List<Callable<T>> result = new ArrayList<Callable<T>>();
        for (final Callable<T> task : tasks) {
            result.add(this.tracer.wrap(task, this.scopeName));
        }
        return result;
    }
    
    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.impl.invokeAll((Collection<? extends Callable<T>>)this.wrapCollection((Collection<? extends Callable<Object>>)tasks));
    }
    
    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit) throws InterruptedException {
        return this.impl.invokeAll((Collection<? extends Callable<T>>)this.wrapCollection((Collection<? extends Callable<Object>>)tasks), timeout, unit);
    }
    
    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.impl.invokeAny((Collection<? extends Callable<T>>)this.wrapCollection((Collection<? extends Callable<Object>>)tasks));
    }
    
    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.impl.invokeAny((Collection<? extends Callable<T>>)this.wrapCollection((Collection<? extends Callable<Object>>)tasks), timeout, unit);
    }
}
