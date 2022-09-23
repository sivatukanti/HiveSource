// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

import org.apache.htrace.shaded.commons.logging.LogFactory;
import java.lang.reflect.Constructor;
import org.apache.htrace.shaded.commons.logging.Log;

public abstract class Sampler
{
    public static final Sampler ALWAYS;
    public static final Sampler NEVER;
    
    public abstract boolean next();
    
    static {
        ALWAYS = AlwaysSampler.INSTANCE;
        NEVER = NeverSampler.INSTANCE;
    }
    
    public static class Builder
    {
        private static final Log LOG;
        private static final String DEFAULT_PACKAGE = "org.apache.htrace.core";
        private final HTraceConfiguration conf;
        private String className;
        private ClassLoader classLoader;
        
        public Builder(final HTraceConfiguration conf) {
            this.classLoader = Builder.class.getClassLoader();
            this.conf = conf;
            this.reset();
        }
        
        public Builder reset() {
            this.className = null;
            return this;
        }
        
        public Builder className(final String className) {
            this.className = className;
            return this;
        }
        
        public Builder classLoader(final ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }
        
        private void throwError(final String errorStr) {
            Builder.LOG.error(errorStr);
            throw new RuntimeException(errorStr);
        }
        
        private void throwError(final String errorStr, final Throwable e) {
            Builder.LOG.error(errorStr, e);
            throw new RuntimeException(errorStr, e);
        }
        
        public Sampler build() {
            final Sampler sampler = this.newSampler();
            if (Builder.LOG.isTraceEnabled()) {
                Builder.LOG.trace("Created new sampler of type " + sampler.getClass().getName(), new Exception());
            }
            return sampler;
        }
        
        private Sampler newSampler() {
            if (this.className == null || this.className.isEmpty()) {
                this.throwError("No sampler class specified.");
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
                this.throwError("Cannot find Sampler class " + str);
            }
            Constructor<Sampler> ctor = null;
            try {
                ctor = cls.getConstructor(HTraceConfiguration.class);
            }
            catch (NoSuchMethodException e3) {
                this.throwError("Cannot find a constructor for class " + str + "which takes an HTraceConfiguration.");
            }
            Sampler sampler = null;
            try {
                Builder.LOG.debug("Creating new instance of " + str + "...");
                sampler = ctor.newInstance(this.conf);
            }
            catch (ReflectiveOperationException e) {
                this.throwError("Reflection error when constructing " + str + ".", e);
            }
            catch (Throwable t) {
                this.throwError("NewInstance error when constructing " + str + ".", t);
            }
            return sampler;
        }
        
        static {
            LOG = LogFactory.getLog(Builder.class);
        }
    }
}
