// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.concurrent.TimeUnit;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.apache.hadoop.util.Shell;
import java.io.File;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import java.io.IOException;

public interface GetSpaceUsed
{
    long getUsed() throws IOException;
    
    public static final class Builder
    {
        static final Logger LOG;
        static final String CLASSNAME_KEY = "fs.getspaceused.classname";
        static final String JITTER_KEY = "fs.getspaceused.jitterMillis";
        static final long DEFAULT_JITTER;
        private Configuration conf;
        private Class<? extends GetSpaceUsed> klass;
        private File path;
        private Long interval;
        private Long jitter;
        private Long initialUsed;
        
        public Builder() {
            this.klass = null;
            this.path = null;
            this.interval = null;
            this.jitter = null;
            this.initialUsed = null;
        }
        
        public Configuration getConf() {
            return this.conf;
        }
        
        public Builder setConf(final Configuration conf) {
            this.conf = conf;
            return this;
        }
        
        public long getInterval() {
            if (this.interval != null) {
                return this.interval;
            }
            final long result = 600000L;
            if (this.conf == null) {
                return result;
            }
            return this.conf.getLong("fs.du.interval", result);
        }
        
        public Builder setInterval(final long interval) {
            this.interval = interval;
            return this;
        }
        
        public Class<? extends GetSpaceUsed> getKlass() {
            if (this.klass != null) {
                return this.klass;
            }
            Class<? extends GetSpaceUsed> result = null;
            if (Shell.WINDOWS) {
                result = WindowsGetSpaceUsed.class;
            }
            else {
                result = DU.class;
            }
            if (this.conf == null) {
                return result;
            }
            return this.conf.getClass("fs.getspaceused.classname", result, GetSpaceUsed.class);
        }
        
        public Builder setKlass(final Class<? extends GetSpaceUsed> klass) {
            this.klass = klass;
            return this;
        }
        
        public File getPath() {
            return this.path;
        }
        
        public Builder setPath(final File path) {
            this.path = path;
            return this;
        }
        
        public long getInitialUsed() {
            if (this.initialUsed == null) {
                return -1L;
            }
            return this.initialUsed;
        }
        
        public Builder setInitialUsed(final long initialUsed) {
            this.initialUsed = initialUsed;
            return this;
        }
        
        public long getJitter() {
            if (this.jitter != null) {
                return this.jitter;
            }
            final Configuration configuration = this.conf;
            if (configuration == null) {
                return Builder.DEFAULT_JITTER;
            }
            return configuration.getLong("fs.getspaceused.jitterMillis", Builder.DEFAULT_JITTER);
        }
        
        public Builder setJitter(final Long jit) {
            this.jitter = jit;
            return this;
        }
        
        public GetSpaceUsed build() throws IOException {
            GetSpaceUsed getSpaceUsed = null;
            try {
                final Constructor<? extends GetSpaceUsed> cons = this.getKlass().getConstructor(Builder.class);
                getSpaceUsed = (GetSpaceUsed)cons.newInstance(this);
            }
            catch (InstantiationException e) {
                Builder.LOG.warn("Error trying to create an instance of " + this.getKlass(), e);
            }
            catch (IllegalAccessException e2) {
                Builder.LOG.warn("Error trying to create " + this.getKlass(), e2);
            }
            catch (InvocationTargetException e3) {
                Builder.LOG.warn("Error trying to create " + this.getKlass(), e3);
            }
            catch (NoSuchMethodException e4) {
                Builder.LOG.warn("Doesn't look like the class " + this.getKlass() + " have the needed constructor", e4);
            }
            if (getSpaceUsed == null) {
                if (Shell.WINDOWS) {
                    getSpaceUsed = new WindowsGetSpaceUsed(this);
                }
                else {
                    getSpaceUsed = new DU(this);
                }
            }
            if (getSpaceUsed instanceof CachingGetSpaceUsed) {
                ((CachingGetSpaceUsed)getSpaceUsed).init();
            }
            return getSpaceUsed;
        }
        
        static {
            LOG = LoggerFactory.getLogger(Builder.class);
            DEFAULT_JITTER = TimeUnit.MINUTES.toMillis(1L);
        }
    }
}
