// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator;

import java.util.Map;
import org.apache.curator.shaded.com.google.common.collect.Sets;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.apache.zookeeper.WatchedEvent;
import org.apache.curator.utils.ThreadUtils;
import java.util.concurrent.Callable;
import java.util.Set;
import org.apache.zookeeper.Watcher;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.Closeable;

public class SessionFailRetryLoop implements Closeable
{
    private final CuratorZookeeperClient client;
    private final Mode mode;
    private final Thread ourThread;
    private final AtomicBoolean sessionHasFailed;
    private final AtomicBoolean isDone;
    private final RetryLoop retryLoop;
    private final Watcher watcher;
    private static final Set<Thread> failedSessionThreads;
    
    public static <T> T callWithRetry(final CuratorZookeeperClient client, final Mode mode, final Callable<T> proc) throws Exception {
        T result = null;
        final SessionFailRetryLoop retryLoop = client.newSessionFailRetryLoop(mode);
        retryLoop.start();
        try {
            while (retryLoop.shouldContinue()) {
                try {
                    result = proc.call();
                }
                catch (Exception e) {
                    ThreadUtils.checkInterrupted(e);
                    retryLoop.takeException(e);
                }
            }
        }
        finally {
            retryLoop.close();
        }
        return result;
    }
    
    SessionFailRetryLoop(final CuratorZookeeperClient client, final Mode mode) {
        this.ourThread = Thread.currentThread();
        this.sessionHasFailed = new AtomicBoolean(false);
        this.isDone = new AtomicBoolean(false);
        this.watcher = new Watcher() {
            @Override
            public void process(final WatchedEvent event) {
                if (event.getState() == Event.KeeperState.Expired) {
                    SessionFailRetryLoop.this.sessionHasFailed.set(true);
                    SessionFailRetryLoop.failedSessionThreads.add(SessionFailRetryLoop.this.ourThread);
                }
            }
        };
        this.client = client;
        this.mode = mode;
        this.retryLoop = client.newRetryLoop();
    }
    
    static boolean sessionForThreadHasFailed() {
        return SessionFailRetryLoop.failedSessionThreads.size() > 0 && SessionFailRetryLoop.failedSessionThreads.contains(Thread.currentThread());
    }
    
    public void start() {
        Preconditions.checkState(Thread.currentThread().equals(this.ourThread), (Object)"Not in the correct thread");
        this.client.addParentWatcher(this.watcher);
    }
    
    public boolean shouldContinue() {
        final boolean localIsDone = this.isDone.getAndSet(true);
        return !localIsDone;
    }
    
    @Override
    public void close() {
        Preconditions.checkState(Thread.currentThread().equals(this.ourThread), (Object)"Not in the correct thread");
        SessionFailRetryLoop.failedSessionThreads.remove(this.ourThread);
        this.client.removeParentWatcher(this.watcher);
    }
    
    public void takeException(final Exception exception) throws Exception {
        Preconditions.checkState(Thread.currentThread().equals(this.ourThread), (Object)"Not in the correct thread");
        boolean passUp = true;
        if (this.sessionHasFailed.get()) {
            switch (this.mode) {
                case RETRY: {
                    this.sessionHasFailed.set(false);
                    SessionFailRetryLoop.failedSessionThreads.remove(this.ourThread);
                    if (exception instanceof SessionFailedException) {
                        this.isDone.set(false);
                        passUp = false;
                        break;
                    }
                    break;
                }
            }
        }
        if (passUp) {
            this.retryLoop.takeException(exception);
        }
    }
    
    static {
        failedSessionThreads = Sets.newSetFromMap((Map<Thread, Boolean>)Maps.newConcurrentMap());
    }
    
    public static class SessionFailedException extends Exception
    {
        private static final long serialVersionUID = 1L;
    }
    
    public enum Mode
    {
        RETRY, 
        FAIL;
    }
}
