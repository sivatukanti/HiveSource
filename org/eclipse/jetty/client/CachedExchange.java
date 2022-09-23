// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client;

import java.io.IOException;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.http.HttpFields;

public class CachedExchange extends HttpExchange
{
    private final HttpFields _responseFields;
    private volatile int _responseStatus;
    
    public CachedExchange(final boolean cacheHeaders) {
        this._responseFields = (cacheHeaders ? new HttpFields() : null);
    }
    
    public synchronized int getResponseStatus() {
        if (this.getStatus() < 5) {
            throw new IllegalStateException("Response not received yet");
        }
        return this._responseStatus;
    }
    
    public synchronized HttpFields getResponseFields() {
        if (this.getStatus() < 6) {
            throw new IllegalStateException("Headers not completely received yet");
        }
        return this._responseFields;
    }
    
    @Override
    protected synchronized void onResponseStatus(final Buffer version, final int status, final Buffer reason) throws IOException {
        super.onResponseStatus(version, this._responseStatus = status, reason);
    }
    
    @Override
    protected synchronized void onResponseHeader(final Buffer name, final Buffer value) throws IOException {
        if (this._responseFields != null) {
            this._responseFields.add(name, value.asImmutableBuffer());
        }
        super.onResponseHeader(name, value);
    }
}
