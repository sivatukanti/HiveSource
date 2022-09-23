// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

import java.util.concurrent.Callable;

public class TraceCallable<V> implements Callable<V>
{
    private final Tracer tracer;
    private final Callable<V> impl;
    private final TraceScope parent;
    private final String description;
    
    TraceCallable(final Tracer tracer, final TraceScope parent, final Callable<V> impl, final String description) {
        this.tracer = tracer;
        this.impl = impl;
        this.parent = parent;
        if (description == null) {
            this.description = Thread.currentThread().getName();
        }
        else {
            this.description = description;
        }
    }
    
    @Override
    public V call() throws Exception {
        final TraceScope chunk = this.tracer.newScope(this.description, this.parent.getSpan().getSpanId());
        try {
            return this.impl.call();
        }
        finally {
            chunk.close();
        }
    }
    
    public Callable<V> getImpl() {
        return this.impl;
    }
}
