// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.core;

import java.util.ArrayList;
import com.sun.jersey.server.impl.application.WebApplicationContext;
import java.util.List;

public class TraceInformation
{
    private final List<String> traces;
    private final WebApplicationContext c;
    
    public TraceInformation(final WebApplicationContext c) {
        this.traces = new ArrayList<String>();
        this.c = c;
    }
    
    public void trace(final String message) {
        this.traces.add(message);
    }
    
    public void addTraceHeaders() {
        this.addTraceHeaders(new TraceHeaderListener() {
            @Override
            public void onHeader(final String name, final String value) {
                TraceInformation.this.c.getContainerResponse().getHttpHeaders().add(name, value);
            }
        });
    }
    
    public void addTraceHeaders(final TraceHeaderListener x) {
        for (int i = 0; i < this.traces.size(); ++i) {
            x.onHeader(String.format("X-Jersey-Trace-%03d", i), this.traces.get(i));
        }
        this.traces.clear();
    }
    
    public interface TraceHeaderListener
    {
        void onHeader(final String p0, final String p1);
    }
}
