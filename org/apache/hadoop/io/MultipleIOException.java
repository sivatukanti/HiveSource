// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.IOException;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class MultipleIOException extends IOException
{
    private static final long serialVersionUID = 1L;
    private final List<IOException> exceptions;
    
    private MultipleIOException(final List<IOException> exceptions) {
        super(exceptions.size() + " exceptions " + exceptions);
        this.exceptions = exceptions;
    }
    
    public List<IOException> getExceptions() {
        return this.exceptions;
    }
    
    public static IOException createIOException(final List<IOException> exceptions) {
        if (exceptions == null || exceptions.isEmpty()) {
            return null;
        }
        if (exceptions.size() == 1) {
            return exceptions.get(0);
        }
        return new MultipleIOException(exceptions);
    }
    
    public static class Builder
    {
        private List<IOException> exceptions;
        
        public void add(final Throwable t) {
            if (this.exceptions == null) {
                this.exceptions = new ArrayList<IOException>();
            }
            this.exceptions.add((t instanceof IOException) ? ((IOException)t) : new IOException(t));
        }
        
        public IOException build() {
            return MultipleIOException.createIOException(this.exceptions);
        }
        
        public boolean isEmpty() {
            return this.exceptions == null || this.exceptions.isEmpty();
        }
    }
}
