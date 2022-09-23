// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.retry;

import java.util.Iterator;
import java.util.Map;
import java.io.IOException;

public class MultiException extends IOException
{
    private final Map<String, Exception> exes;
    
    public MultiException(final Map<String, Exception> exes) {
        this.exes = exes;
    }
    
    public Map<String, Exception> getExceptions() {
        return this.exes;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        for (final Exception e : this.exes.values()) {
            sb.append(e.toString()).append(", ");
        }
        sb.append("}");
        return "MultiException[" + sb.toString() + "]";
    }
}
