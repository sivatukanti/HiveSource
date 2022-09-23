// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.common.util;

import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Set;
import org.apache.commons.logging.Log;

public class ShutdownHookManager
{
    private static final ShutdownHookManager MGR;
    private static final Log LOG;
    private final Set<HookEntry> hooks;
    private final AtomicBoolean shutdownInProgress;
    
    private ShutdownHookManager() {
        this.hooks = Collections.synchronizedSet(new HashSet<HookEntry>());
        this.shutdownInProgress = new AtomicBoolean(false);
    }
    
    static List<Runnable> getShutdownHooksInOrder() {
        return ShutdownHookManager.MGR.getShutdownHooksInOrderInternal();
    }
    
    List<Runnable> getShutdownHooksInOrderInternal() {
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
        final List<Runnable> ordered = new ArrayList<Runnable>();
        for (final HookEntry entry : list) {
            ordered.add(entry.hook);
        }
        return ordered;
    }
    
    public static void addShutdownHook(final Runnable shutdownHook, final int priority) {
        ShutdownHookManager.MGR.addShutdownHookInternal(shutdownHook, priority);
    }
    
    private void addShutdownHookInternal(final Runnable shutdownHook, final int priority) {
        if (shutdownHook == null) {
            throw new IllegalArgumentException("shutdownHook cannot be NULL");
        }
        if (this.shutdownInProgress.get()) {
            throw new IllegalStateException("Shutdown in progress, cannot add a shutdownHook");
        }
        this.hooks.add(new HookEntry(shutdownHook, priority));
    }
    
    public static boolean removeShutdownHook(final Runnable shutdownHook) {
        return ShutdownHookManager.MGR.removeShutdownHookInternal(shutdownHook);
    }
    
    private boolean removeShutdownHookInternal(final Runnable shutdownHook) {
        if (this.shutdownInProgress.get()) {
            throw new IllegalStateException("Shutdown in progress, cannot remove a shutdownHook");
        }
        return this.hooks.remove(new HookEntry(shutdownHook, 0));
    }
    
    public static boolean hasShutdownHook(final Runnable shutdownHook) {
        return ShutdownHookManager.MGR.hasShutdownHookInternal(shutdownHook);
    }
    
    public boolean hasShutdownHookInternal(final Runnable shutdownHook) {
        return this.hooks.contains(new HookEntry(shutdownHook, 0));
    }
    
    public static boolean isShutdownInProgress() {
        return ShutdownHookManager.MGR.isShutdownInProgressInternal();
    }
    
    private boolean isShutdownInProgressInternal() {
        return this.shutdownInProgress.get();
    }
    
    static {
        MGR = new ShutdownHookManager();
        LOG = LogFactory.getLog(ShutdownHookManager.class);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                ShutdownHookManager.MGR.shutdownInProgress.set(true);
                ShutdownHookManager.MGR;
                for (final Runnable hook : ShutdownHookManager.getShutdownHooksInOrder()) {
                    try {
                        hook.run();
                    }
                    catch (Throwable ex) {
                        ShutdownHookManager.LOG.warn("ShutdownHook '" + hook.getClass().getSimpleName() + "' failed, " + ex.toString(), ex);
                    }
                }
            }
        });
    }
    
    private static class HookEntry
    {
        Runnable hook;
        int priority;
        
        public HookEntry(final Runnable hook, final int priority) {
            this.hook = hook;
            this.priority = priority;
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
    }
}
