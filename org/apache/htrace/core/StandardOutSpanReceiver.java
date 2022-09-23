// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

import org.apache.htrace.shaded.commons.logging.LogFactory;
import java.io.IOException;
import org.apache.htrace.shaded.commons.logging.Log;

public class StandardOutSpanReceiver extends SpanReceiver
{
    private static final Log LOG;
    
    public StandardOutSpanReceiver(final HTraceConfiguration conf) {
        StandardOutSpanReceiver.LOG.trace("Created new StandardOutSpanReceiver.");
    }
    
    @Override
    public void receiveSpan(final Span span) {
        System.out.println(span);
    }
    
    @Override
    public void close() throws IOException {
    }
    
    static {
        LOG = LogFactory.getLog(StandardOutSpanReceiver.class);
    }
}
