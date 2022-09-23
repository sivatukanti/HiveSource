// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import java.util.Arrays;
import java.io.IOException;

public class Options
{
    public static <base, T extends base> T getOption(final Class<T> cls, final base[] opts) throws IOException {
        for (final base o : opts) {
            if (o.getClass() == cls) {
                return (T)o;
            }
        }
        return null;
    }
    
    public static <T> T[] prependOptions(final T[] oldOpts, final T... newOpts) {
        final T[] result = Arrays.copyOf(newOpts, newOpts.length + oldOpts.length);
        System.arraycopy(oldOpts, 0, result, newOpts.length, oldOpts.length);
        return result;
    }
    
    public abstract static class StringOption
    {
        private final String value;
        
        protected StringOption(final String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
    }
    
    public abstract static class ClassOption
    {
        private final Class<?> value;
        
        protected ClassOption(final Class<?> value) {
            this.value = value;
        }
        
        public Class<?> getValue() {
            return this.value;
        }
    }
    
    public abstract static class BooleanOption
    {
        private final boolean value;
        
        protected BooleanOption(final boolean value) {
            this.value = value;
        }
        
        public boolean getValue() {
            return this.value;
        }
    }
    
    public abstract static class IntegerOption
    {
        private final int value;
        
        protected IntegerOption(final int value) {
            this.value = value;
        }
        
        public int getValue() {
            return this.value;
        }
    }
    
    public abstract static class LongOption
    {
        private final long value;
        
        protected LongOption(final long value) {
            this.value = value;
        }
        
        public long getValue() {
            return this.value;
        }
    }
    
    public abstract static class PathOption
    {
        private final Path value;
        
        protected PathOption(final Path value) {
            this.value = value;
        }
        
        public Path getValue() {
            return this.value;
        }
    }
    
    public abstract static class FSDataInputStreamOption
    {
        private final FSDataInputStream value;
        
        protected FSDataInputStreamOption(final FSDataInputStream value) {
            this.value = value;
        }
        
        public FSDataInputStream getValue() {
            return this.value;
        }
    }
    
    public abstract static class FSDataOutputStreamOption
    {
        private final FSDataOutputStream value;
        
        protected FSDataOutputStreamOption(final FSDataOutputStream value) {
            this.value = value;
        }
        
        public FSDataOutputStream getValue() {
            return this.value;
        }
    }
    
    public abstract static class ProgressableOption
    {
        private final Progressable value;
        
        protected ProgressableOption(final Progressable value) {
            this.value = value;
        }
        
        public Progressable getValue() {
            return this.value;
        }
    }
}
