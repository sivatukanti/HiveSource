// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

import org.apache.htrace.shaded.commons.logging.LogFactory;
import java.lang.reflect.Constructor;
import org.apache.htrace.shaded.commons.logging.Log;
import java.util.concurrent.atomic.AtomicLong;
import java.io.Closeable;

public abstract class SpanReceiver implements Closeable
{
    private final long id;
    private static final AtomicLong HIGHEST_SPAN_RECEIVER_ID;
    
    public final long getId() {
        return this.id;
    }
    
    protected SpanReceiver() {
        this.id = SpanReceiver.HIGHEST_SPAN_RECEIVER_ID.incrementAndGet();
    }
    
    public abstract void receiveSpan(final Span p0);
    
    static {
        HIGHEST_SPAN_RECEIVER_ID = new AtomicLong(0L);
    }
    
    public static class Builder
    {
        private static final Log LOG;
        static final String DEFAULT_PACKAGE = "org.apache.htrace.core";
        private final HTraceConfiguration conf;
        private boolean logErrors;
        private String className;
        private ClassLoader classLoader;
        
        public Builder(final HTraceConfiguration conf) {
            this.classLoader = Builder.class.getClassLoader();
            this.conf = conf;
            this.reset();
        }
        
        public Builder reset() {
            this.logErrors = true;
            this.className = null;
            return this;
        }
        
        public Builder className(final String className) {
            this.className = className;
            return this;
        }
        
        public Builder logErrors(final boolean logErrors) {
            this.logErrors = logErrors;
            return this;
        }
        
        public Builder classLoader(final ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }
        
        private void throwError(final String errorStr) {
            if (this.logErrors) {
                Builder.LOG.error(errorStr);
            }
            throw new RuntimeException(errorStr);
        }
        
        private void throwError(final String errorStr, final Throwable e) {
            if (this.logErrors) {
                Builder.LOG.error(errorStr, e);
            }
            throw new RuntimeException(errorStr, e);
        }
        
        public SpanReceiver build() {
            final SpanReceiver spanReceiver = this.newSpanReceiver();
            if (Builder.LOG.isTraceEnabled()) {
                Builder.LOG.trace("Created new span receiver of type " + spanReceiver.getClass().getName());
            }
            return spanReceiver;
        }
        
        private SpanReceiver newSpanReceiver() {
            if (this.className == null || this.className.isEmpty()) {
                this.throwError("No span receiver class specified.");
            }
            String str = this.className;
            if (!str.contains(".")) {
                str = "org.apache.htrace.core." + str;
            }
            Class cls = null;
            try {
                cls = this.classLoader.loadClass(str);
            }
            catch (ClassNotFoundException e2) {
                this.throwError("Cannot find SpanReceiver class " + str);
            }
            Constructor<SpanReceiver> ctor = null;
            try {
                ctor = cls.getConstructor(HTraceConfiguration.class);
            }
            catch (NoSuchMethodException e3) {
                this.throwError("Cannot find a constructor for class " + str + "which takes an HTraceConfiguration.");
            }
            SpanReceiver receiver = null;
            try {
                Builder.LOG.debug("Creating new instance of " + str + "...");
                receiver = ctor.newInstance(this.conf);
            }
            catch (ReflectiveOperationException e) {
                this.throwError("Reflection error when constructing " + str + ".", e);
            }
            catch (Throwable t) {
                this.throwError("NewInstance error when constructing " + str + ".", t);
            }
            return receiver;
        }
        
        static {
            LOG = LogFactory.getLog(Builder.class);
        }
    }
}
