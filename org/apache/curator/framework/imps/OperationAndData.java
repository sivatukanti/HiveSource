// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import java.util.concurrent.TimeUnit;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.curator.framework.api.BackgroundCallback;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.curator.RetrySleeper;
import java.util.concurrent.Delayed;

class OperationAndData<T> implements Delayed, RetrySleeper
{
    private static final AtomicLong nextOrdinal;
    private final BackgroundOperation<T> operation;
    private final T data;
    private final BackgroundCallback callback;
    private final long startTimeMs;
    private final ErrorCallback<T> errorCallback;
    private final AtomicInteger retryCount;
    private final AtomicLong sleepUntilTimeMs;
    private final AtomicLong ordinal;
    private final Object context;
    
    OperationAndData(final BackgroundOperation<T> operation, final T data, final BackgroundCallback callback, final ErrorCallback<T> errorCallback, final Object context) {
        this.startTimeMs = System.currentTimeMillis();
        this.retryCount = new AtomicInteger(0);
        this.sleepUntilTimeMs = new AtomicLong(0L);
        this.ordinal = new AtomicLong();
        this.operation = operation;
        this.data = data;
        this.callback = callback;
        this.errorCallback = errorCallback;
        this.context = context;
        this.reset();
    }
    
    void reset() {
        this.retryCount.set(0);
        this.ordinal.set(OperationAndData.nextOrdinal.getAndIncrement());
    }
    
    Object getContext() {
        return this.context;
    }
    
    void callPerformBackgroundOperation() throws Exception {
        this.operation.performBackgroundOperation(this);
    }
    
    T getData() {
        return this.data;
    }
    
    long getElapsedTimeMs() {
        return System.currentTimeMillis() - this.startTimeMs;
    }
    
    int getThenIncrementRetryCount() {
        return this.retryCount.getAndIncrement();
    }
    
    BackgroundCallback getCallback() {
        return this.callback;
    }
    
    ErrorCallback<T> getErrorCallback() {
        return this.errorCallback;
    }
    
    @VisibleForTesting
    BackgroundOperation<T> getOperation() {
        return this.operation;
    }
    
    @Override
    public void sleepFor(final long time, final TimeUnit unit) throws InterruptedException {
        this.sleepUntilTimeMs.set(System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(time, unit));
    }
    
    @Override
    public long getDelay(final TimeUnit unit) {
        return unit.convert(this.sleepUntilTimeMs.get() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }
    
    @Override
    public int compareTo(final Delayed o) {
        if (o == this) {
            return 0;
        }
        long diff = this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
        if (diff == 0L && o instanceof OperationAndData) {
            diff = this.ordinal.get() - ((OperationAndData)o).ordinal.get();
        }
        return (diff < 0L) ? -1 : ((diff > 0L) ? 1 : 0);
    }
    
    static {
        nextOrdinal = new AtomicLong();
    }
    
    interface ErrorCallback<T>
    {
        void retriesExhausted(final OperationAndData<T> p0);
    }
}
