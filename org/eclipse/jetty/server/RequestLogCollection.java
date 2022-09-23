// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;

class RequestLogCollection implements RequestLog
{
    private final ArrayList<RequestLog> delegates;
    
    public RequestLogCollection(final RequestLog... requestLogs) {
        this.delegates = new ArrayList<RequestLog>(Arrays.asList(requestLogs));
    }
    
    public void add(final RequestLog requestLog) {
        this.delegates.add(requestLog);
    }
    
    @Override
    public void log(final Request request, final Response response) {
        for (final RequestLog delegate : this.delegates) {
            delegate.log(request, response);
        }
    }
}
