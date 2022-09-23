// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.commons.logging.Log;

public class Deadline
{
    private static final Log LOG;
    private long timeout;
    private long startTime;
    private String method;
    private static final ThreadLocal<Deadline> DEADLINE_THREAD_LOCAL;
    
    private Deadline(final long timeout) {
        this.startTime = -1L;
        this.timeout = timeout;
    }
    
    static void setCurrentDeadline(final Deadline deadline) {
        Deadline.DEADLINE_THREAD_LOCAL.set(deadline);
    }
    
    static Deadline getCurrentDeadline() {
        return Deadline.DEADLINE_THREAD_LOCAL.get();
    }
    
    static void removeCurrentDeadline() {
        Deadline.DEADLINE_THREAD_LOCAL.remove();
    }
    
    public static void registerIfNot(final long timeout) {
        if (getCurrentDeadline() == null) {
            setCurrentDeadline(new Deadline(timeout));
        }
    }
    
    public static void resetTimeout(final long timeout) throws MetaException {
        if (timeout <= 0L) {
            throw newMetaException(new DeadlineException("The reset timeout value should be larger than 0: " + timeout));
        }
        final Deadline deadline = getCurrentDeadline();
        if (deadline != null) {
            deadline.timeout = timeout;
            return;
        }
        throw newMetaException(new DeadlineException("The threadlocal Deadline is null, please register it firstly."));
    }
    
    public static boolean isStarted() throws MetaException {
        final Deadline deadline = getCurrentDeadline();
        if (deadline != null) {
            return deadline.startTime >= 0L;
        }
        throw newMetaException(new DeadlineException("The threadlocal Deadline is null, please register it firstly."));
    }
    
    public static void startTimer(final String method) throws MetaException {
        final Deadline deadline = getCurrentDeadline();
        if (deadline != null) {
            deadline.startTime = System.currentTimeMillis();
            deadline.method = method;
            return;
        }
        throw newMetaException(new DeadlineException("The threadlocal Deadline is null, please register it firstly."));
    }
    
    public static void stopTimer() throws MetaException {
        final Deadline deadline = getCurrentDeadline();
        if (deadline != null) {
            deadline.startTime = -1L;
            deadline.method = null;
            return;
        }
        throw newMetaException(new DeadlineException("The threadlocal Deadline is null, please register it firstly."));
    }
    
    public static void clear() {
        removeCurrentDeadline();
    }
    
    public static void checkTimeout() throws MetaException {
        final Deadline deadline = getCurrentDeadline();
        if (deadline != null) {
            deadline.check();
            return;
        }
        throw newMetaException(new DeadlineException("The threadlocal Deadline is null, please register it first."));
    }
    
    private void check() throws MetaException {
        try {
            if (this.startTime < 0L) {
                throw new DeadlineException("Should execute startTimer() method before checkTimeout. Error happens in method: " + this.method);
            }
            if (this.startTime + this.timeout < System.currentTimeMillis()) {
                throw new DeadlineException("Timeout when executing method: " + this.method);
            }
        }
        catch (DeadlineException e) {
            throw newMetaException(e);
        }
    }
    
    private static MetaException newMetaException(final DeadlineException e) {
        final MetaException metaException = new MetaException(e.getMessage());
        metaException.initCause(e);
        return metaException;
    }
    
    static {
        LOG = LogFactory.getLog(Deadline.class.getName());
        DEADLINE_THREAD_LOCAL = new ThreadLocal<Deadline>() {
            @Override
            protected synchronized Deadline initialValue() {
                return null;
            }
        };
    }
}
