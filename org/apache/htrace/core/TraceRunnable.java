// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

public class TraceRunnable implements Runnable
{
    private final Tracer tracer;
    private final TraceScope parent;
    private final Runnable runnable;
    private final String description;
    
    public TraceRunnable(final Tracer tracer, final TraceScope parent, final Runnable runnable, final String description) {
        this.tracer = tracer;
        this.parent = parent;
        this.runnable = runnable;
        if (description == null) {
            this.description = Thread.currentThread().getName();
        }
        else {
            this.description = description;
        }
    }
    
    @Override
    public void run() {
        final TraceScope chunk = this.tracer.newScope(this.description, this.parent.getSpan().getSpanId());
        try {
            this.runnable.run();
        }
        finally {
            chunk.close();
        }
    }
    
    public Runnable getRunnable() {
        return this.runnable;
    }
}
