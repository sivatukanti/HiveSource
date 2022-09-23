// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.apache.hadoop.util.concurrent.HadoopExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.LoggerFactory;
import java.util.Comparator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashSet;
import org.apache.hadoop.conf.Configuration;
import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.Future;
import java.util.Iterator;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public final class ShutdownHookManager
{
    private static final ShutdownHookManager MGR;
    private static final Logger LOG;
    public static final long TIMEOUT_MINIMUM = 1L;
    public static final TimeUnit TIME_UNIT_DEFAULT;
    private static final ExecutorService EXECUTOR;
    private final Set<HookEntry> hooks;
    private AtomicBoolean shutdownInProgress;
    
    @InterfaceAudience.Private
    @VisibleForTesting
    static int executeShutdown() {
        int timeouts = 0;
        for (final HookEntry entry : ShutdownHookManager.MGR.getShutdownHooksInOrder()) {
            final Future<?> future = ShutdownHookManager.EXECUTOR.submit(entry.getHook());
            try {
                future.get(entry.getTimeout(), entry.getTimeUnit());
            }
            catch (TimeoutException ex) {
                ++timeouts;
                future.cancel(true);
                ShutdownHookManager.LOG.warn("ShutdownHook '" + entry.getHook().getClass().getSimpleName() + "' timeout, " + ex.toString(), ex);
            }
            catch (Throwable ex2) {
                ShutdownHookManager.LOG.warn("ShutdownHook '" + entry.getHook().getClass().getSimpleName() + "' failed, " + ex2.toString(), ex2);
            }
        }
        return timeouts;
    }
    
    private static void shutdownExecutor(final Configuration conf) {
        try {
            ShutdownHookManager.EXECUTOR.shutdown();
            final long shutdownTimeout = getShutdownTimeout(conf);
            if (!ShutdownHookManager.EXECUTOR.awaitTermination(shutdownTimeout, ShutdownHookManager.TIME_UNIT_DEFAULT)) {
                ShutdownHookManager.LOG.error("ShutdownHookManger shutdown forcefully after {} seconds.", (Object)shutdownTimeout);
                ShutdownHookManager.EXECUTOR.shutdownNow();
            }
            ShutdownHookManager.LOG.debug("ShutdownHookManger completed shutdown.");
        }
        catch (InterruptedException ex) {
            ShutdownHookManager.LOG.error("ShutdownHookManger interrupted while waiting for termination.", ex);
            ShutdownHookManager.EXECUTOR.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    @InterfaceAudience.Public
    public static ShutdownHookManager get() {
        return ShutdownHookManager.MGR;
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    static long getShutdownTimeout(final Configuration conf) {
        long duration = conf.getTimeDuration("hadoop.service.shutdown.timeout", 30L, ShutdownHookManager.TIME_UNIT_DEFAULT);
        if (duration < 1L) {
            duration = 1L;
        }
        return duration;
    }
    
    private ShutdownHookManager() {
        this.hooks = Collections.synchronizedSet(new HashSet<HookEntry>());
        this.shutdownInProgress = new AtomicBoolean(false);
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    List<HookEntry> getShutdownHooksInOrder() {
        final List<HookEntry> list;
        synchronized (ShutdownHookManager.MGR.hooks) {
            list = new ArrayList<HookEntry>(ShutdownHookManager.MGR.hooks);
        }
        Collections.sort(list, new Comparator<HookEntry>() {
            @Override
            public int compare(final HookEntry o1, final HookEntry o2) {
                return o2.priority - o1.priority;
            }
        });
        return list;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public void addShutdownHook(final Runnable shutdownHook, final int priority) {
        if (shutdownHook == null) {
            throw new IllegalArgumentException("shutdownHook cannot be NULL");
        }
        if (this.shutdownInProgress.get()) {
            throw new IllegalStateException("Shutdown in progress, cannot add a shutdownHook");
        }
        this.hooks.add(new HookEntry(shutdownHook, priority));
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public void addShutdownHook(final Runnable shutdownHook, final int priority, final long timeout, final TimeUnit unit) {
        if (shutdownHook == null) {
            throw new IllegalArgumentException("shutdownHook cannot be NULL");
        }
        if (this.shutdownInProgress.get()) {
            throw new IllegalStateException("Shutdown in progress, cannot add a shutdownHook");
        }
        this.hooks.add(new HookEntry(shutdownHook, priority, timeout, unit));
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public boolean removeShutdownHook(final Runnable shutdownHook) {
        if (this.shutdownInProgress.get()) {
            throw new IllegalStateException("Shutdown in progress, cannot remove a shutdownHook");
        }
        return this.hooks.remove(new HookEntry(shutdownHook, 0));
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public boolean hasShutdownHook(final Runnable shutdownHook) {
        return this.hooks.contains(new HookEntry(shutdownHook, 0));
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public boolean isShutdownInProgress() {
        return this.shutdownInProgress.get();
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public void clearShutdownHooks() {
        this.hooks.clear();
    }
    
    static {
        MGR = new ShutdownHookManager();
        LOG = LoggerFactory.getLogger(ShutdownHookManager.class);
        TIME_UNIT_DEFAULT = TimeUnit.SECONDS;
        EXECUTOR = HadoopExecutors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("shutdown-hook-%01d").build());
        try {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    if (ShutdownHookManager.MGR.shutdownInProgress.getAndSet(true)) {
                        ShutdownHookManager.LOG.info("Shutdown process invoked a second time: ignoring");
                        return;
                    }
                    final long started = System.currentTimeMillis();
                    final int timeoutCount = ShutdownHookManager.executeShutdown();
                    final long ended = System.currentTimeMillis();
                    ShutdownHookManager.LOG.debug(String.format("Completed shutdown in %.3f seconds; Timeouts: %d", (ended - started) / 1000.0, timeoutCount));
                    shutdownExecutor(new Configuration());
                }
            });
        }
        catch (IllegalStateException ex) {
            ShutdownHookManager.LOG.warn("Failed to add the ShutdownHook", ex);
        }
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    static class HookEntry
    {
        private final Runnable hook;
        private final int priority;
        private final long timeout;
        private final TimeUnit unit;
        
        HookEntry(final Runnable hook, final int priority) {
            this(hook, priority, ShutdownHookManager.getShutdownTimeout(new Configuration()), ShutdownHookManager.TIME_UNIT_DEFAULT);
        }
        
        HookEntry(final Runnable hook, final int priority, final long timeout, final TimeUnit unit) {
            this.hook = hook;
            this.priority = priority;
            this.timeout = timeout;
            this.unit = unit;
        }
        
        @Override
        public int hashCode() {
            return this.hook.hashCode();
        }
        
        @Override
        public boolean equals(final Object obj) {
            boolean eq = false;
            if (obj != null && obj instanceof HookEntry) {
                eq = (this.hook == ((HookEntry)obj).hook);
            }
            return eq;
        }
        
        Runnable getHook() {
            return this.hook;
        }
        
        int getPriority() {
            return this.priority;
        }
        
        long getTimeout() {
            return this.timeout;
        }
        
        TimeUnit getTimeUnit() {
            return this.unit;
        }
    }
}
