// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

class NullScope extends TraceScope
{
    NullScope(final Tracer tracer) {
        super(tracer, null, null);
    }
    
    @Override
    public SpanId getSpanId() {
        return SpanId.INVALID;
    }
    
    @Override
    public void detach() {
        if (this.detached) {
            Tracer.throwClientError("Can't detach this TraceScope  because it is already detached.");
        }
        this.detached = true;
    }
    
    @Override
    public void reattach() {
        if (!this.detached) {
            Tracer.throwClientError("Can't reattach this TraceScope  because it is not detached.");
        }
        this.detached = false;
    }
    
    @Override
    public void close() {
        this.tracer.popNullScope();
    }
    
    @Override
    public String toString() {
        return "NullScope";
    }
    
    @Override
    public void addKVAnnotation(final String key, final String value) {
    }
    
    @Override
    public void addTimelineAnnotation(final String msg) {
    }
}
