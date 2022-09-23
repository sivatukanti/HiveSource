// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.InternalLogger;

public class ThreadRenamingRunnable implements Runnable
{
    private static final InternalLogger logger;
    private static volatile ThreadNameDeterminer threadNameDeterminer;
    private final ThreadNameDeterminer determiner;
    private final Runnable runnable;
    private final String proposedThreadName;
    
    public static ThreadNameDeterminer getThreadNameDeterminer() {
        return ThreadRenamingRunnable.threadNameDeterminer;
    }
    
    public static void setThreadNameDeterminer(final ThreadNameDeterminer threadNameDeterminer) {
        if (threadNameDeterminer == null) {
            throw new NullPointerException("threadNameDeterminer");
        }
        ThreadRenamingRunnable.threadNameDeterminer = threadNameDeterminer;
    }
    
    public ThreadRenamingRunnable(final Runnable runnable, final String proposedThreadName, final ThreadNameDeterminer determiner) {
        if (runnable == null) {
            throw new NullPointerException("runnable");
        }
        if (proposedThreadName == null) {
            throw new NullPointerException("proposedThreadName");
        }
        this.runnable = runnable;
        this.determiner = determiner;
        this.proposedThreadName = proposedThreadName;
    }
    
    public ThreadRenamingRunnable(final Runnable runnable, final String proposedThreadName) {
        this(runnable, proposedThreadName, null);
    }
    
    public void run() {
        final Thread currentThread = Thread.currentThread();
        final String oldThreadName = currentThread.getName();
        final String newThreadName = this.getNewThreadName(oldThreadName);
        boolean renamed = false;
        if (!oldThreadName.equals(newThreadName)) {
            try {
                currentThread.setName(newThreadName);
                renamed = true;
            }
            catch (SecurityException e) {
                ThreadRenamingRunnable.logger.debug("Failed to rename a thread due to security restriction.", e);
            }
        }
        try {
            this.runnable.run();
        }
        finally {
            if (renamed) {
                currentThread.setName(oldThreadName);
            }
        }
    }
    
    private String getNewThreadName(final String currentThreadName) {
        String newThreadName = null;
        try {
            ThreadNameDeterminer nameDeterminer = this.determiner;
            if (nameDeterminer == null) {
                nameDeterminer = getThreadNameDeterminer();
            }
            newThreadName = nameDeterminer.determineThreadName(currentThreadName, this.proposedThreadName);
        }
        catch (Throwable t) {
            ThreadRenamingRunnable.logger.warn("Failed to determine the thread name", t);
        }
        return (newThreadName == null) ? currentThreadName : newThreadName;
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(ThreadRenamingRunnable.class);
        ThreadRenamingRunnable.threadNameDeterminer = ThreadNameDeterminer.PROPOSED;
    }
}
