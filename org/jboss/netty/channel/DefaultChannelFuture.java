// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import org.jboss.netty.logging.InternalLoggerFactory;
import java.util.Collection;
import java.util.Iterator;
import org.jboss.netty.util.internal.DeadLockProofWorker;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;
import org.jboss.netty.logging.InternalLogger;

public class DefaultChannelFuture implements ChannelFuture
{
    private static final InternalLogger logger;
    private static final Throwable CANCELLED;
    private static volatile boolean useDeadLockChecker;
    private static boolean disabledDeadLockCheckerOnce;
    private final Channel channel;
    private final boolean cancellable;
    private ChannelFutureListener firstListener;
    private List<ChannelFutureListener> otherListeners;
    private List<ChannelFutureProgressListener> progressListeners;
    private boolean done;
    private Throwable cause;
    private int waiters;
    
    public static boolean isUseDeadLockChecker() {
        return DefaultChannelFuture.useDeadLockChecker;
    }
    
    public static void setUseDeadLockChecker(final boolean useDeadLockChecker) {
        if (!useDeadLockChecker && !DefaultChannelFuture.disabledDeadLockCheckerOnce) {
            DefaultChannelFuture.disabledDeadLockCheckerOnce = true;
            if (DefaultChannelFuture.logger.isDebugEnabled()) {
                DefaultChannelFuture.logger.debug("The dead lock checker in " + DefaultChannelFuture.class.getSimpleName() + " has been disabled as requested at your own risk.");
            }
        }
        DefaultChannelFuture.useDeadLockChecker = useDeadLockChecker;
    }
    
    public DefaultChannelFuture(final Channel channel, final boolean cancellable) {
        this.channel = channel;
        this.cancellable = cancellable;
    }
    
    public Channel getChannel() {
        return this.channel;
    }
    
    public synchronized boolean isDone() {
        return this.done;
    }
    
    public synchronized boolean isSuccess() {
        return this.done && this.cause == null;
    }
    
    public synchronized Throwable getCause() {
        if (this.cause != DefaultChannelFuture.CANCELLED) {
            return this.cause;
        }
        return null;
    }
    
    public synchronized boolean isCancelled() {
        return this.cause == DefaultChannelFuture.CANCELLED;
    }
    
    public void addListener(final ChannelFutureListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener");
        }
        boolean notifyNow = false;
        synchronized (this) {
            if (this.done) {
                notifyNow = true;
            }
            else {
                if (this.firstListener == null) {
                    this.firstListener = listener;
                }
                else {
                    if (this.otherListeners == null) {
                        this.otherListeners = new ArrayList<ChannelFutureListener>(1);
                    }
                    this.otherListeners.add(listener);
                }
                if (listener instanceof ChannelFutureProgressListener) {
                    if (this.progressListeners == null) {
                        this.progressListeners = new ArrayList<ChannelFutureProgressListener>(1);
                    }
                    this.progressListeners.add((ChannelFutureProgressListener)listener);
                }
            }
        }
        if (notifyNow) {
            this.notifyListener(listener);
        }
    }
    
    public void removeListener(final ChannelFutureListener listener) {
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
                if (listener instanceof ChannelFutureProgressListener) {
                    this.progressListeners.remove(listener);
                }
            }
        }
    }
    
    public ChannelFuture sync() throws InterruptedException {
        this.await();
        this.rethrowIfFailed0();
        return this;
    }
    
    public ChannelFuture syncUninterruptibly() {
        this.awaitUninterruptibly();
        this.rethrowIfFailed0();
        return this;
    }
    
    private void rethrowIfFailed0() {
        final Throwable cause = this.getCause();
        if (cause == null) {
            return;
        }
        if (cause instanceof RuntimeException) {
            throw (RuntimeException)cause;
        }
        if (cause instanceof Error) {
            throw (Error)cause;
        }
        throw new ChannelException(cause);
    }
    
    public ChannelFuture await() throws InterruptedException {
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
    
    public ChannelFuture awaitUninterruptibly() {
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
        if (isUseDeadLockChecker() && DeadLockProofWorker.PARENT.get() != null) {
            throw new IllegalStateException("await*() in I/O thread causes a dead lock or sudden performance drop. Use addListener() instead or call await*() from a different thread.");
        }
    }
    
    public boolean setSuccess() {
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
    
    public boolean setFailure(final Throwable cause) {
        if (cause == null) {
            throw new NullPointerException("cause");
        }
        synchronized (this) {
            if (this.done) {
                return false;
            }
            this.cause = cause;
            this.done = true;
            if (this.waiters > 0) {
                this.notifyAll();
            }
        }
        this.notifyListeners();
        return true;
    }
    
    public boolean cancel() {
        if (!this.cancellable) {
            return false;
        }
        synchronized (this) {
            if (this.done) {
                return false;
            }
            this.cause = DefaultChannelFuture.CANCELLED;
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
                for (final ChannelFutureListener l : this.otherListeners) {
                    this.notifyListener(l);
                }
                this.otherListeners = null;
            }
        }
    }
    
    private void notifyListener(final ChannelFutureListener l) {
        try {
            l.operationComplete(this);
        }
        catch (Throwable t) {
            if (DefaultChannelFuture.logger.isWarnEnabled()) {
                DefaultChannelFuture.logger.warn("An exception was thrown by " + ChannelFutureListener.class.getSimpleName() + '.', t);
            }
        }
    }
    
    public boolean setProgress(final long amount, final long current, final long total) {
        final ChannelFutureProgressListener[] plisteners;
        synchronized (this) {
            if (this.done) {
                return false;
            }
            final Collection<ChannelFutureProgressListener> progressListeners = this.progressListeners;
            if (progressListeners == null || progressListeners.isEmpty()) {
                return true;
            }
            plisteners = progressListeners.toArray(new ChannelFutureProgressListener[progressListeners.size()]);
        }
        for (final ChannelFutureProgressListener pl : plisteners) {
            this.notifyProgressListener(pl, amount, current, total);
        }
        return true;
    }
    
    private void notifyProgressListener(final ChannelFutureProgressListener l, final long amount, final long current, final long total) {
        try {
            l.operationProgressed(this, amount, current, total);
        }
        catch (Throwable t) {
            if (DefaultChannelFuture.logger.isWarnEnabled()) {
                DefaultChannelFuture.logger.warn("An exception was thrown by " + ChannelFutureProgressListener.class.getSimpleName() + '.', t);
            }
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(DefaultChannelFuture.class);
        CANCELLED = new Throwable();
        DefaultChannelFuture.useDeadLockChecker = true;
    }
}
