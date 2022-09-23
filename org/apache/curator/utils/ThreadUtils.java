// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.utils;

import org.slf4j.LoggerFactory;
import org.apache.curator.shaded.com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.curator.shaded.com.google.common.base.Throwables;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;

public class ThreadUtils
{
    private static final Logger log;
    
    public static void checkInterrupted(final Throwable e) {
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
    }
    
    public static ExecutorService newSingleThreadExecutor(final String processName) {
        return Executors.newSingleThreadExecutor(newThreadFactory(processName));
    }
    
    public static ExecutorService newFixedThreadPool(final int qty, final String processName) {
        return Executors.newFixedThreadPool(qty, newThreadFactory(processName));
    }
    
    public static ScheduledExecutorService newSingleThreadScheduledExecutor(final String processName) {
        return Executors.newSingleThreadScheduledExecutor(newThreadFactory(processName));
    }
    
    public static ScheduledExecutorService newFixedThreadScheduledPool(final int qty, final String processName) {
        return Executors.newScheduledThreadPool(qty, newThreadFactory(processName));
    }
    
    public static ThreadFactory newThreadFactory(final String processName) {
        return newGenericThreadFactory("Curator-" + processName);
    }
    
    public static ThreadFactory newGenericThreadFactory(final String processName) {
        final Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread t, final Throwable e) {
                ThreadUtils.log.error("Unexpected exception in thread: " + t, e);
                Throwables.propagate(e);
            }
        };
        return new ThreadFactoryBuilder().setNameFormat(processName + "-%d").setDaemon(true).setUncaughtExceptionHandler(uncaughtExceptionHandler).build();
    }
    
    public static String getProcessName(final Class<?> clazz) {
        if (clazz.isAnonymousClass()) {
            return getProcessName(clazz.getEnclosingClass());
        }
        return clazz.getSimpleName();
    }
    
    static {
        log = LoggerFactory.getLogger(ThreadUtils.class);
    }
}
