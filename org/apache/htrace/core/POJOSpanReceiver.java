// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

import java.io.IOException;
import java.util.HashSet;
import java.util.Collection;

public class POJOSpanReceiver extends SpanReceiver
{
    private final Collection<Span> spans;
    
    public POJOSpanReceiver(final HTraceConfiguration conf) {
        this.spans = new HashSet<Span>();
    }
    
    public Collection<Span> getSpans() {
        return this.spans;
    }
    
    @Override
    public void close() throws IOException {
    }
    
    @Override
    public void receiveSpan(final Span span) {
        this.spans.add(span);
    }
}
