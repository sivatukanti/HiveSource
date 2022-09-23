// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util.internal;

import org.jboss.netty.logging.InternalLoggerFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.jboss.netty.logging.InternalLogger;

public class SharedResourceMisuseDetector
{
    private static final int MAX_ACTIVE_INSTANCES = 256;
    private static final InternalLogger logger;
    private final Class<?> type;
    private final AtomicLong activeInstances;
    private final AtomicBoolean logged;
    
    public SharedResourceMisuseDetector(final Class<?> type) {
        this.activeInstances = new AtomicLong();
        this.logged = new AtomicBoolean();
        if (type == null) {
            throw new NullPointerException("type");
        }
        this.type = type;
    }
    
    public void increase() {
        if (this.activeInstances.incrementAndGet() > 256L && SharedResourceMisuseDetector.logger.isWarnEnabled() && this.logged.compareAndSet(false, true)) {
            SharedResourceMisuseDetector.logger.warn("You are creating too many " + this.type.getSimpleName() + " instances.  " + this.type.getSimpleName() + " is a shared resource that must be reused across the" + " application, so that only a few instances are created.");
        }
    }
    
    public void decrease() {
        this.activeInstances.decrementAndGet();
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(SharedResourceMisuseDetector.class);
    }
}
