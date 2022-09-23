// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator;

import java.util.concurrent.TimeUnit;
import org.apache.curator.drivers.EventTrace;
import org.apache.zookeeper.KeeperException;
import org.slf4j.LoggerFactory;
import org.apache.curator.utils.ThreadUtils;
import java.util.concurrent.Callable;
import org.apache.curator.drivers.TracerDriver;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;

public class RetryLoop
{
    private boolean isDone;
    private int retryCount;
    private final Logger log;
    private final long startTimeMs;
    private final RetryPolicy retryPolicy;
    private final AtomicReference<TracerDriver> tracer;
    private static final RetrySleeper sleeper;
    
    public static RetrySleeper getDefaultRetrySleeper() {
        return RetryLoop.sleeper;
    }
    
    public static <T> T callWithRetry(final CuratorZookeeperClient client, final Callable<T> proc) throws Exception {
        T result = null;
        final RetryLoop retryLoop = client.newRetryLoop();
        while (retryLoop.shouldContinue()) {
            try {
                client.internalBlockUntilConnectedOrTimedOut();
                result = proc.call();
                retryLoop.markComplete();
            }
            catch (Exception e) {
                ThreadUtils.checkInterrupted(e);
                retryLoop.takeException(e);
            }
        }
        return result;
    }
    
    RetryLoop(final RetryPolicy retryPolicy, final AtomicReference<TracerDriver> tracer) {
        this.isDone = false;
        this.retryCount = 0;
        this.log = LoggerFactory.getLogger(this.getClass());
        this.startTimeMs = System.currentTimeMillis();
        this.retryPolicy = retryPolicy;
        this.tracer = tracer;
    }
    
    public boolean shouldContinue() {
        return !this.isDone;
    }
    
    public void markComplete() {
        this.isDone = true;
    }
    
    public static boolean shouldRetry(final int rc) {
        return rc == KeeperException.Code.CONNECTIONLOSS.intValue() || rc == KeeperException.Code.OPERATIONTIMEOUT.intValue() || rc == KeeperException.Code.SESSIONMOVED.intValue() || rc == KeeperException.Code.SESSIONEXPIRED.intValue();
    }
    
    public static boolean isRetryException(final Throwable exception) {
        if (exception instanceof KeeperException) {
            final KeeperException keeperException = (KeeperException)exception;
            return shouldRetry(keeperException.code().intValue());
        }
        return false;
    }
    
    public void takeException(final Exception exception) throws Exception {
        boolean rethrow = true;
        if (isRetryException(exception)) {
            if (!Boolean.getBoolean("curator-dont-log-connection-problems")) {
                this.log.debug("Retry-able exception received", exception);
            }
            if (this.retryPolicy.allowRetry(this.retryCount++, System.currentTimeMillis() - this.startTimeMs, RetryLoop.sleeper)) {
                new EventTrace("retries-allowed", this.tracer.get()).commit();
                if (!Boolean.getBoolean("curator-dont-log-connection-problems")) {
                    this.log.debug("Retrying operation");
                }
                rethrow = false;
            }
            else {
                new EventTrace("retries-disallowed", this.tracer.get()).commit();
                if (!Boolean.getBoolean("curator-dont-log-connection-problems")) {
                    this.log.debug("Retry policy not allowing retry");
                }
            }
        }
        if (rethrow) {
            throw exception;
        }
    }
    
    static {
        sleeper = new RetrySleeper() {
            @Override
            public void sleepFor(final long time, final TimeUnit unit) throws InterruptedException {
                unit.sleep(time);
            }
        };
    }
}
