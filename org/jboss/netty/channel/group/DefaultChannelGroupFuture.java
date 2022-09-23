// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.group;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.internal.DeadLockProofWorker;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import org.jboss.netty.channel.Channel;
import java.util.Iterator;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Collection;
import org.jboss.netty.channel.ChannelFutureListener;
import java.util.List;
import org.jboss.netty.channel.ChannelFuture;
import java.util.Map;
import org.jboss.netty.logging.InternalLogger;

public class DefaultChannelGroupFuture implements ChannelGroupFuture
{
    private static final InternalLogger logger;
    private final ChannelGroup group;
    final Map<Integer, ChannelFuture> futures;
    private ChannelGroupFutureListener firstListener;
    private List<ChannelGroupFutureListener> otherListeners;
    private boolean done;
    int successCount;
    int failureCount;
    private int waiters;
    private final ChannelFutureListener childListener;
    
    public DefaultChannelGroupFuture(final ChannelGroup group, final Collection<ChannelFuture> futures) {
        this.childListener = new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture future) throws Exception {
                final boolean success = future.isSuccess();
                final boolean callSetDone;
                synchronized (DefaultChannelGroupFuture.this) {
                    if (success) {
                        final DefaultChannelGroupFuture this$0 = DefaultChannelGroupFuture.this;
                        ++this$0.successCount;
                    }
                    else {
                        final DefaultChannelGroupFuture this$2 = DefaultChannelGroupFuture.this;
                        ++this$2.failureCount;
                    }
                    callSetDone = (DefaultChannelGroupFuture.this.successCount + DefaultChannelGroupFuture.this.failureCount == DefaultChannelGroupFuture.this.futures.size());
                    assert DefaultChannelGroupFuture.this.successCount + DefaultChannelGroupFuture.this.failureCount <= DefaultChannelGroupFuture.this.futures.size();
                }
                if (callSetDone) {
                    DefaultChannelGroupFuture.this.setDone();
                }
            }
        };
        if (group == null) {
            throw new NullPointerException("group");
        }
        if (futures == null) {
            throw new NullPointerException("futures");
        }
        this.group = group;
        final Map<Integer, ChannelFuture> futureMap = new LinkedHashMap<Integer, ChannelFuture>();
        for (final ChannelFuture f : futures) {
            futureMap.put(f.getChannel().getId(), f);
        }
        this.futures = Collections.unmodifiableMap((Map<? extends Integer, ? extends ChannelFuture>)futureMap);
        for (final ChannelFuture f : this.futures.values()) {
            f.addListener(this.childListener);
        }
        if (this.futures.isEmpty()) {
            this.setDone();
        }
    }
    
    DefaultChannelGroupFuture(final ChannelGroup group, final Map<Integer, ChannelFuture> futures) {
        this.childListener = new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture future) throws Exception {
                final boolean success = future.isSuccess();
                final boolean callSetDone;
                synchronized (DefaultChannelGroupFuture.this) {
                    if (success) {
                        final DefaultChannelGroupFuture this$0 = DefaultChannelGroupFuture.this;
                        ++this$0.successCount;
                    }
                    else {
                        final DefaultChannelGroupFuture this$2 = DefaultChannelGroupFuture.this;
                        ++this$2.failureCount;
                    }
                    callSetDone = (DefaultChannelGroupFuture.this.successCount + DefaultChannelGroupFuture.this.failureCount == DefaultChannelGroupFuture.this.futures.size());
                    assert DefaultChannelGroupFuture.this.successCount + DefaultChannelGroupFuture.this.failureCount <= DefaultChannelGroupFuture.this.futures.size();
                }
                if (callSetDone) {
                    DefaultChannelGroupFuture.this.setDone();
                }
            }
        };
        this.group = group;
        this.futures = Collections.unmodifiableMap((Map<? extends Integer, ? extends ChannelFuture>)futures);
        for (final ChannelFuture f : this.futures.values()) {
            f.addListener(this.childListener);
        }
        if (this.futures.isEmpty()) {
            this.setDone();
        }
    }
    
    public ChannelGroup getGroup() {
        return this.group;
    }
    
    public ChannelFuture find(final Integer channelId) {
        return this.futures.get(channelId);
    }
    
    public ChannelFuture find(final Channel channel) {
        return this.futures.get(channel.getId());
    }
    
    public Iterator<ChannelFuture> iterator() {
        return this.futures.values().iterator();
    }
    
    public synchronized boolean isDone() {
        return this.done;
    }
    
    public synchronized boolean isCompleteSuccess() {
        return this.successCount == this.futures.size();
    }
    
    public synchronized boolean isPartialSuccess() {
        return this.successCount != 0 && this.successCount != this.futures.size();
    }
    
    public synchronized boolean isPartialFailure() {
        return this.failureCount != 0 && this.failureCount != this.futures.size();
    }
    
    public synchronized boolean isCompleteFailure() {
        final int futureCnt = this.futures.size();
        return futureCnt != 0 && this.failureCount == futureCnt;
    }
    
    public void addListener(final ChannelGroupFutureListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener");
        }
        boolean notifyNow = false;
        synchronized (this) {
            if (this.done) {
                notifyNow = true;
            }
            else if (this.firstListener == null) {
                this.firstListener = listener;
            }
            else {
                if (this.otherListeners == null) {
                    this.otherListeners = new ArrayList<ChannelGroupFutureListener>(1);
                }
                this.otherListeners.add(listener);
            }
        }
        if (notifyNow) {
            this.notifyListener(listener);
        }
    }
    
    public void removeListener(final ChannelGroupFutureListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener");
        }
        synchronized (this) {
            if (!this.done) {
                if (listener == this.firstListener) {
                    if (this.otherListeners != null && !this.otherListeners.isEmpty()) {
                        this.firstListener = this.otherListeners.remove(0);
                    }
                    else {
                        this.firstListener = null;
                    }
                }
                else if (this.otherListeners != null) {
                    this.otherListeners.remove(listener);
                }
            }
        }
    }
    
    public ChannelGroupFuture await() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        synchronized (this) {
            while (!this.done) {
                checkDeadLock();
                ++this.waiters;
                try {
                    this.wait();
                }
                finally {
                    --this.waiters;
                }
            }
        }
        return this;
    }
    
    public boolean await(final long timeout, final TimeUnit unit) throws InterruptedException {
        return this.await0(unit.toNanos(timeout), true);
    }
    
    public boolean await(final long timeoutMillis) throws InterruptedException {
        return this.await0(TimeUnit.MILLISECONDS.toNanos(timeoutMillis), true);
    }
    
    public ChannelGroupFuture awaitUninterruptibly() {
        boolean interrupted = false;
        synchronized (this) {
            while (!this.done) {
                checkDeadLock();
                ++this.waiters;
                try {
                    this.wait();
                }
                catch (InterruptedException e) {
                    interrupted = true;
                }
                finally {
                    --this.waiters;
                }
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        return this;
    }
    
    public boolean awaitUninterruptibly(final long timeout, final TimeUnit unit) {
        try {
            return this.await0(unit.toNanos(timeout), false);
        }
        catch (InterruptedException e) {
            throw new InternalError();
        }
    }
    
    public boolean awaitUninterruptibly(final long timeoutMillis) {
        try {
            return this.await0(TimeUnit.MILLISECONDS.toNanos(timeoutMillis), false);
        }
        catch (InterruptedException e) {
            throw new InternalError();
        }
    }
    
    private boolean await0(final long timeoutNanos, final boolean interruptable) throws InterruptedException {
        if (interruptable && Thread.interrupted()) {
            throw new InterruptedException();
        }
        final long startTime = (timeoutNanos <= 0L) ? 0L : System.nanoTime();
        long waitTime = timeoutNanos;
        boolean interrupted = false;
        try {
            synchronized (this) {
                if (this.done || waitTime <= 0L) {
                    return this.done;
                }
                checkDeadLock();
                ++this.waiters;
                try {
                    while (true) {
                        try {
                            this.wait(waitTime / 1000000L, (int)(waitTime % 1000000L));
                        }
                        catch (InterruptedException e) {
                            if (interruptable) {
                                throw e;
                            }
                            interrupted = true;
                        }
                        if (this.done) {
                            return true;
                        }
                        waitTime = timeoutNanos - (System.nanoTime() - startTime);
                        if (waitTime <= 0L) {
                            return this.done;
                        }
                    }
                }
                finally {
                    --this.waiters;
                }
            }
        }
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private static void checkDeadLock() {
        if (DeadLockProofWorker.PARENT.get() != null) {
            throw new IllegalStateException("await*() in I/O thread causes a dead lock or sudden performance drop. Use addListener() instead or call await*() from a different thread.");
        }
    }
    
    boolean setDone() {
        synchronized (this) {
            if (this.done) {
                return false;
            }
            this.done = true;
            if (this.waiters > 0) {
                this.notifyAll();
            }
        }
        this.notifyListeners();
        return true;
    }
    
    private void notifyListeners() {
        if (this.firstListener != null) {
            this.notifyListener(this.firstListener);
            this.firstListener = null;
            if (this.otherListeners != null) {
                for (final ChannelGroupFutureListener l : this.otherListeners) {
                    this.notifyListener(l);
                }
                this.otherListeners = null;
            }
        }
    }
    
    private void notifyListener(final ChannelGroupFutureListener l) {
        try {
            l.operationComplete(this);
        }
        catch (Throwable t) {
            if (DefaultChannelGroupFuture.logger.isWarnEnabled()) {
                DefaultChannelGroupFuture.logger.warn("An exception was thrown by " + ChannelFutureListener.class.getSimpleName() + '.', t);
            }
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(DefaultChannelGroupFuture.class);
    }
}
