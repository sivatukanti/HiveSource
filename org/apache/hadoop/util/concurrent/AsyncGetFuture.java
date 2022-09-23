// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util.concurrent;

import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import com.google.common.util.concurrent.AbstractFuture;

public class AsyncGetFuture<T, E extends Throwable> extends AbstractFuture<T>
{
    public static final Logger LOG;
    private final AtomicBoolean called;
    private final AsyncGet<T, E> asyncGet;
    
    public AsyncGetFuture(final AsyncGet<T, E> asyncGet) {
        this.called = new AtomicBoolean(false);
        this.asyncGet = asyncGet;
    }
    
    private void callAsyncGet(final long timeout, final TimeUnit unit) {
        if (!this.isCancelled() && this.called.compareAndSet(false, true)) {
            try {
                this.set(this.asyncGet.get(timeout, unit));
            }
            catch (TimeoutException te) {
                AsyncGetFuture.LOG.trace("TRACE", te);
                this.called.compareAndSet(true, false);
            }
            catch (Throwable e) {
                AsyncGetFuture.LOG.trace("TRACE", e);
                this.setException(e);
            }
        }
    }
    
    @Override
    public T get() throws InterruptedException, ExecutionException {
        this.callAsyncGet(-1L, TimeUnit.MILLISECONDS);
        return super.get();
    }
    
    @Override
    public T get(final long timeout, final TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException {
        this.callAsyncGet(timeout, unit);
        return super.get(0L, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public boolean isDone() {
        this.callAsyncGet(0L, TimeUnit.MILLISECONDS);
        return super.isDone();
    }
    
    static {
        LOG = LoggerFactory.getLogger(AsyncGetFuture.class);
    }
}
