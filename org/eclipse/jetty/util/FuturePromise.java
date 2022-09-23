// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.Future;

public class FuturePromise<C> implements Future<C>, Promise<C>
{
    private static Throwable COMPLETED;
    private final AtomicBoolean _done;
    private final CountDownLatch _latch;
    private Throwable _cause;
    private C _result;
    
    public FuturePromise() {
        this._done = new AtomicBoolean(false);
        this._latch = new CountDownLatch(1);
    }
    
    public FuturePromise(final C result) {
        this._done = new AtomicBoolean(false);
        this._latch = new CountDownLatch(1);
        this._cause = FuturePromise.COMPLETED;
        this._result = result;
        this._done.set(true);
        this._latch.countDown();
    }
    
    public FuturePromise(final C ctx, final Throwable failed) {
        this._done = new AtomicBoolean(false);
        this._latch = new CountDownLatch(1);
        this._result = ctx;
        this._cause = failed;
        this._done.set(true);
        this._latch.countDown();
    }
    
    @Override
    public void succeeded(final C result) {
        if (this._done.compareAndSet(false, true)) {
            this._result = result;
            this._cause = FuturePromise.COMPLETED;
            this._latch.countDown();
        }
    }
    
    @Override
    public void failed(final Throwable cause) {
        if (this._done.compareAndSet(false, true)) {
            this._cause = cause;
            this._latch.countDown();
        }
    }
    
    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        if (this._done.compareAndSet(false, true)) {
            this._result = null;
            this._cause = new CancellationException();
            this._latch.countDown();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isCancelled() {
        if (this._done.get()) {
            try {
                this._latch.await();
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return this._cause instanceof CancellationException;
        }
        return false;
    }
    
    @Override
    public boolean isDone() {
        return this._done.get() && this._latch.getCount() == 0L;
    }
    
    @Override
    public C get() throws InterruptedException, ExecutionException {
        this._latch.await();
        if (this._cause == FuturePromise.COMPLETED) {
            return this._result;
        }
        if (this._cause instanceof CancellationException) {
            throw (CancellationException)new CancellationException().initCause(this._cause);
        }
        throw new ExecutionException(this._cause);
    }
    
    @Override
    public C get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (!this._latch.await(timeout, unit)) {
            throw new TimeoutException();
        }
        if (this._cause == FuturePromise.COMPLETED) {
            return this._result;
        }
        if (this._cause instanceof TimeoutException) {
            throw (TimeoutException)this._cause;
        }
        if (this._cause instanceof CancellationException) {
            throw (CancellationException)new CancellationException().initCause(this._cause);
        }
        throw new ExecutionException(this._cause);
    }
    
    public static void rethrow(final ExecutionException e) throws IOException {
        final Throwable cause = e.getCause();
        if (cause instanceof IOException) {
            throw (IOException)cause;
        }
        if (cause instanceof Error) {
            throw (Error)cause;
        }
        if (cause instanceof RuntimeException) {
            throw (RuntimeException)cause;
        }
        throw new RuntimeException(cause);
    }
    
    @Override
    public String toString() {
        return String.format("FutureCallback@%x{%b,%b,%s}", this.hashCode(), this._done.get(), this._cause == FuturePromise.COMPLETED, this._result);
    }
    
    static {
        FuturePromise.COMPLETED = new ConstantThrowable();
    }
}
