// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.retry;

import org.apache.hadoop.ipc.Client;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.util.Daemon;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Iterator;
import org.apache.hadoop.util.Time;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Queue;
import org.slf4j.LoggerFactory;
import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Method;
import com.google.common.base.Preconditions;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.util.concurrent.AsyncGet;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class AsyncCallHandler
{
    public static final Logger LOG;
    private static final ThreadLocal<AsyncGet<?, Exception>> LOWER_LAYER_ASYNC_RETURN;
    private static final ThreadLocal<AsyncGet<Object, Throwable>> ASYNC_RETURN;
    private final AsyncCallQueue asyncCalls;
    private volatile boolean hasSuccessfulCall;
    
    public AsyncCallHandler() {
        this.asyncCalls = new AsyncCallQueue();
        this.hasSuccessfulCall = false;
    }
    
    @InterfaceStability.Unstable
    public static <R, T extends Throwable> AsyncGet<R, T> getAsyncReturn() {
        final AsyncGet<R, T> asyncGet = (AsyncGet<R, T>)AsyncCallHandler.ASYNC_RETURN.get();
        if (asyncGet != null) {
            AsyncCallHandler.ASYNC_RETURN.set(null);
            return asyncGet;
        }
        return (AsyncGet<R, T>)getLowerLayerAsyncReturn();
    }
    
    @InterfaceStability.Unstable
    public static void setLowerLayerAsyncReturn(final AsyncGet<?, Exception> asyncReturn) {
        AsyncCallHandler.LOWER_LAYER_ASYNC_RETURN.set(asyncReturn);
    }
    
    private static AsyncGet<?, Exception> getLowerLayerAsyncReturn() {
        final AsyncGet<?, Exception> asyncGet = AsyncCallHandler.LOWER_LAYER_ASYNC_RETURN.get();
        Preconditions.checkNotNull(asyncGet);
        AsyncCallHandler.LOWER_LAYER_ASYNC_RETURN.set(null);
        return asyncGet;
    }
    
    AsyncCall newAsyncCall(final Method method, final Object[] args, final boolean isRpc, final int callId, final RetryInvocationHandler<?> retryInvocationHandler) {
        return new AsyncCall(method, args, isRpc, callId, retryInvocationHandler, this);
    }
    
    boolean hasSuccessfulCall() {
        return this.hasSuccessfulCall;
    }
    
    private void initAsyncCall(final AsyncCall asyncCall, final AsyncValue<CallReturn> asyncCallReturn) {
        this.asyncCalls.addCall(asyncCall);
        final AsyncGet<Object, Throwable> asyncGet = new AsyncGet<Object, Throwable>() {
            @Override
            public Object get(final long timeout, final TimeUnit unit) throws Throwable {
                final CallReturn c = asyncCallReturn.waitAsyncValue(timeout, unit);
                final Object r = c.getReturnValue();
                AsyncCallHandler.this.hasSuccessfulCall = true;
                return r;
            }
            
            @Override
            public boolean isDone() {
                return asyncCallReturn.isDone();
            }
        };
        AsyncCallHandler.ASYNC_RETURN.set(asyncGet);
    }
    
    @VisibleForTesting
    public static long getGracePeriod() {
        return 3000L;
    }
    
    static {
        LOG = LoggerFactory.getLogger(AsyncCallHandler.class);
        LOWER_LAYER_ASYNC_RETURN = new ThreadLocal<AsyncGet<?, Exception>>();
        ASYNC_RETURN = new ThreadLocal<AsyncGet<Object, Throwable>>();
    }
    
    static class ConcurrentQueue<T>
    {
        private final Queue<T> queue;
        private final AtomicLong emptyStartTime;
        
        ConcurrentQueue() {
            this.queue = new ConcurrentLinkedQueue<T>();
            this.emptyStartTime = new AtomicLong(Time.monotonicNow());
        }
        
        Iterator<T> iterator() {
            return this.queue.iterator();
        }
        
        boolean isEmpty(final long time) {
            return Time.monotonicNow() - this.emptyStartTime.get() > time && this.queue.isEmpty();
        }
        
        void offer(final T c) {
            final boolean added = this.queue.offer(c);
            Preconditions.checkState(added);
        }
        
        void checkEmpty() {
            if (this.queue.isEmpty()) {
                this.emptyStartTime.set(Time.monotonicNow());
            }
        }
    }
    
    class AsyncCallQueue
    {
        private final ConcurrentQueue<AsyncCall> queue;
        private final Processor processor;
        
        AsyncCallQueue() {
            this.queue = new ConcurrentQueue<AsyncCall>();
            this.processor = new Processor();
        }
        
        void addCall(final AsyncCall call) {
            if (AsyncCallHandler.LOG.isDebugEnabled()) {
                AsyncCallHandler.LOG.debug("add " + call);
            }
            this.queue.offer(call);
            this.processor.tryStart();
        }
        
        long checkCalls() {
            final long startTime = Time.monotonicNow();
            long minWaitTime = 100L;
            final Iterator<AsyncCall> i = this.queue.iterator();
            while (i.hasNext()) {
                final AsyncCall c = i.next();
                if (c.isDone()) {
                    i.remove();
                    this.queue.checkEmpty();
                }
                else {
                    final Long waitTime = c.getWaitTime(startTime);
                    if (waitTime == null || waitTime <= 0L || waitTime >= minWaitTime) {
                        continue;
                    }
                    minWaitTime = waitTime;
                }
            }
            return minWaitTime;
        }
        
        private class Processor
        {
            static final long GRACE_PERIOD = 3000L;
            static final long MAX_WAIT_PERIOD = 100L;
            private final AtomicReference<Thread> running;
            
            private Processor() {
                this.running = new AtomicReference<Thread>();
            }
            
            boolean isRunning(final Daemon d) {
                return d == this.running.get();
            }
            
            void tryStart() {
                final Thread current = Thread.currentThread();
                if (this.running.compareAndSet(null, current)) {
                    final Daemon daemon = new Daemon() {
                        @Override
                        public void run() {
                            while (Processor.this.isRunning(this)) {
                                final long waitTime = AsyncCallQueue.this.checkCalls();
                                Processor.this.tryStop(this);
                                try {
                                    synchronized (AsyncCallHandler.this) {
                                        AsyncCallHandler.this.wait(waitTime);
                                    }
                                }
                                catch (InterruptedException e) {
                                    Processor.this.kill(this);
                                }
                            }
                        }
                    };
                    final boolean set = this.running.compareAndSet(current, daemon);
                    Preconditions.checkState(set);
                    if (AsyncCallHandler.LOG.isDebugEnabled()) {
                        AsyncCallHandler.LOG.debug("Starting AsyncCallQueue.Processor " + daemon);
                    }
                    daemon.start();
                }
            }
            
            void tryStop(final Daemon d) {
                if (AsyncCallQueue.this.queue.isEmpty(3000L)) {
                    this.kill(d);
                }
            }
            
            void kill(final Daemon d) {
                if (AsyncCallHandler.LOG.isDebugEnabled()) {
                    AsyncCallHandler.LOG.debug("Killing " + d);
                }
                final boolean set = this.running.compareAndSet(d, null);
                Preconditions.checkState(set);
            }
        }
    }
    
    static class AsyncValue<V>
    {
        private V value;
        
        synchronized V waitAsyncValue(final long timeout, final TimeUnit unit) throws InterruptedException, TimeoutException {
            if (this.value != null) {
                return this.value;
            }
            AsyncGet.Util.wait(this, timeout, unit);
            if (this.value != null) {
                return this.value;
            }
            throw new TimeoutException("waitCallReturn timed out " + timeout + " " + unit);
        }
        
        synchronized void set(final V v) {
            Preconditions.checkNotNull(v);
            Preconditions.checkState(this.value == null);
            this.value = v;
            this.notify();
        }
        
        synchronized boolean isDone() {
            return this.value != null;
        }
    }
    
    static class AsyncCall extends RetryInvocationHandler.Call
    {
        private final AsyncCallHandler asyncCallHandler;
        private final AsyncValue<CallReturn> asyncCallReturn;
        private AsyncGet<?, Exception> lowerLayerAsyncGet;
        
        AsyncCall(final Method method, final Object[] args, final boolean isRpc, final int callId, final RetryInvocationHandler<?> retryInvocationHandler, final AsyncCallHandler asyncCallHandler) {
            super(method, args, isRpc, callId, retryInvocationHandler);
            this.asyncCallReturn = new AsyncValue<CallReturn>();
            this.asyncCallHandler = asyncCallHandler;
        }
        
        boolean isDone() {
            final CallReturn r = this.invokeOnce();
            AsyncCallHandler.LOG.debug("#{}: {}", (Object)this.getCallId(), r.getState());
            switch (r.getState()) {
                case RETURNED:
                case EXCEPTION: {
                    this.asyncCallReturn.set(r);
                    return true;
                }
                case RETRY: {
                    this.invokeOnce();
                    break;
                }
                case WAIT_RETRY:
                case ASYNC_CALL_IN_PROGRESS:
                case ASYNC_INVOKED: {
                    break;
                }
                default: {
                    Preconditions.checkState(false);
                    break;
                }
            }
            return false;
        }
        
        @Override
        CallReturn processWaitTimeAndRetryInfo() {
            final Long waitTime = this.getWaitTime(Time.monotonicNow());
            AsyncCallHandler.LOG.trace("#{} processRetryInfo: waitTime={}", (Object)this.getCallId(), waitTime);
            if (waitTime != null && waitTime > 0L) {
                return CallReturn.WAIT_RETRY;
            }
            this.processRetryInfo();
            return CallReturn.RETRY;
        }
        
        @Override
        CallReturn invoke() throws Throwable {
            AsyncCallHandler.LOG.debug("{}.invoke {}", this.getClass().getSimpleName(), this);
            if (this.lowerLayerAsyncGet != null) {
                final boolean isDone = this.lowerLayerAsyncGet.isDone();
                AsyncCallHandler.LOG.trace("#{} invoke: lowerLayerAsyncGet.isDone()? {}", (Object)this.getCallId(), isDone);
                if (!isDone) {
                    return CallReturn.ASYNC_CALL_IN_PROGRESS;
                }
                try {
                    return new CallReturn(this.lowerLayerAsyncGet.get(0L, TimeUnit.SECONDS));
                }
                finally {
                    this.lowerLayerAsyncGet = null;
                }
            }
            AsyncCallHandler.LOG.trace("#{} invoke: ASYNC_INVOKED", (Object)this.getCallId());
            final boolean mode = Client.isAsynchronousMode();
            try {
                Client.setAsynchronousMode(true);
                final Object r = this.invokeMethod();
                Preconditions.checkState(r == null);
                this.lowerLayerAsyncGet = getLowerLayerAsyncReturn();
                if (this.getCounters().isZeros()) {
                    AsyncCallHandler.LOG.trace("#{} invoke: initAsyncCall", (Object)this.getCallId());
                    this.asyncCallHandler.initAsyncCall(this, this.asyncCallReturn);
                }
                return CallReturn.ASYNC_INVOKED;
            }
            finally {
                Client.setAsynchronousMode(mode);
            }
        }
    }
}
