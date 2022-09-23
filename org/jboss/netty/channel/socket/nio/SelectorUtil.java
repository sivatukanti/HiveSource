// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import java.util.concurrent.TimeUnit;
import org.jboss.netty.util.internal.SystemPropertyUtil;
import org.jboss.netty.logging.InternalLoggerFactory;
import java.nio.channels.CancelledKeyException;
import java.io.IOException;
import java.nio.channels.Selector;
import org.jboss.netty.logging.InternalLogger;

final class SelectorUtil
{
    private static final InternalLogger logger;
    static final int DEFAULT_IO_THREADS;
    static final long DEFAULT_SELECT_TIMEOUT = 500L;
    static final long SELECT_TIMEOUT;
    static final long SELECT_TIMEOUT_NANOS;
    static final boolean EPOLL_BUG_WORKAROUND;
    
    static Selector open() throws IOException {
        return Selector.open();
    }
    
    static int select(final Selector selector) throws IOException {
        try {
            return selector.select(SelectorUtil.SELECT_TIMEOUT);
        }
        catch (CancelledKeyException e) {
            if (SelectorUtil.logger.isDebugEnabled()) {
                SelectorUtil.logger.debug(CancelledKeyException.class.getSimpleName() + " raised by a Selector - JDK bug?", e);
            }
            return -1;
        }
    }
    
    private SelectorUtil() {
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(SelectorUtil.class);
        DEFAULT_IO_THREADS = Runtime.getRuntime().availableProcessors() * 2;
        SELECT_TIMEOUT = SystemPropertyUtil.getLong("org.jboss.netty.selectTimeout", 500L);
        SELECT_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(SelectorUtil.SELECT_TIMEOUT);
        EPOLL_BUG_WORKAROUND = SystemPropertyUtil.getBoolean("org.jboss.netty.epollBugWorkaround", false);
        final String key = "sun.nio.ch.bugLevel";
        try {
            final String buglevel = System.getProperty(key);
            if (buglevel == null) {
                System.setProperty(key, "");
            }
        }
        catch (SecurityException e) {
            if (SelectorUtil.logger.isDebugEnabled()) {
                SelectorUtil.logger.debug("Unable to get/set System Property '" + key + '\'', e);
            }
        }
        if (SelectorUtil.logger.isDebugEnabled()) {
            SelectorUtil.logger.debug("Using select timeout of " + SelectorUtil.SELECT_TIMEOUT);
            SelectorUtil.logger.debug("Epoll-bug workaround enabled = " + SelectorUtil.EPOLL_BUG_WORKAROUND);
        }
    }
}
