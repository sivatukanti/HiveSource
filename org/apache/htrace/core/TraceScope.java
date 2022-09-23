// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

import java.io.Closeable;

public class TraceScope implements Closeable
{
    final Tracer tracer;
    private final Span span;
    private TraceScope parent;
    boolean detached;
    
    TraceScope(final Tracer tracer, final Span span, final TraceScope parent) {
        this.tracer = tracer;
        this.span = span;
        this.parent = parent;
        this.detached = false;
    }
    
    public Span getSpan() {
        return this.span;
    }
    
    public SpanId getSpanId() {
        return this.span.getSpanId();
    }
    
    TraceScope getParent() {
        return this.parent;
    }
    
    void setParent(final TraceScope parent) {
        this.parent = parent;
    }
    
    public void detach() {
        if (this.detached) {
            Tracer.throwClientError("Can't detach this TraceScope  because it is already detached.");
        }
        this.tracer.detachScope(this);
        this.detached = true;
        this.parent = null;
    }
    
    public void reattach() {
        if (!this.detached) {
            Tracer.throwClientError("Can't reattach this TraceScope  because it is not detached.");
        }
        this.tracer.reattachScope(this);
        this.detached = false;
    }
    
    @Override
    public void close() {
        this.tracer.closeScope(this);
    }
    
    public void addKVAnnotation(final String key, final String value) {
        this.span.addKVAnnotation(key, value);
    }
    
    public void addTimelineAnnotation(final String msg) {
        this.span.addTimelineAnnotation(msg);
    }
    
    @Override
    public String toString() {
        return "TraceScope(tracerId=" + this.tracer.getTracerId() + ", span=" + this.span.toJson() + ", detached=" + this.detached + ")";
    }
}
