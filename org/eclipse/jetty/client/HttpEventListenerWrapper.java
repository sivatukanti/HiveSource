// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client;

import java.io.IOException;
import org.eclipse.jetty.io.Buffer;

public class HttpEventListenerWrapper implements HttpEventListener
{
    HttpEventListener _listener;
    boolean _delegatingRequests;
    boolean _delegatingResponses;
    boolean _delegationResult;
    private Buffer _version;
    private int _status;
    private Buffer _reason;
    
    public HttpEventListenerWrapper() {
        this._delegationResult = true;
        this._listener = null;
        this._delegatingRequests = false;
        this._delegatingResponses = false;
    }
    
    public HttpEventListenerWrapper(final HttpEventListener eventListener, final boolean delegating) {
        this._delegationResult = true;
        this._listener = eventListener;
        this._delegatingRequests = delegating;
        this._delegatingResponses = delegating;
    }
    
    public HttpEventListener getEventListener() {
        return this._listener;
    }
    
    public void setEventListener(final HttpEventListener listener) {
        this._listener = listener;
    }
    
    public boolean isDelegatingRequests() {
        return this._delegatingRequests;
    }
    
    public boolean isDelegatingResponses() {
        return this._delegatingResponses;
    }
    
    public void setDelegatingRequests(final boolean delegating) {
        this._delegatingRequests = delegating;
    }
    
    public void setDelegatingResponses(final boolean delegating) {
        this._delegatingResponses = delegating;
    }
    
    public void setDelegationResult(final boolean result) {
        this._delegationResult = result;
    }
    
    public void onConnectionFailed(final Throwable ex) {
        if (this._delegatingRequests) {
            this._listener.onConnectionFailed(ex);
        }
    }
    
    public void onException(final Throwable ex) {
        if (this._delegatingRequests || this._delegatingResponses) {
            this._listener.onException(ex);
        }
    }
    
    public void onExpire() {
        if (this._delegatingRequests || this._delegatingResponses) {
            this._listener.onExpire();
        }
    }
    
    public void onRequestCommitted() throws IOException {
        if (this._delegatingRequests) {
            this._listener.onRequestCommitted();
        }
    }
    
    public void onRequestComplete() throws IOException {
        if (this._delegatingRequests) {
            this._listener.onRequestComplete();
        }
    }
    
    public void onResponseComplete() throws IOException {
        if (this._delegatingResponses) {
            if (!this._delegationResult) {
                this._listener.onResponseStatus(this._version, this._status, this._reason);
            }
            this._listener.onResponseComplete();
        }
    }
    
    public void onResponseContent(final Buffer content) throws IOException {
        if (this._delegatingResponses) {
            this._listener.onResponseContent(content);
        }
    }
    
    public void onResponseHeader(final Buffer name, final Buffer value) throws IOException {
        if (this._delegatingResponses) {
            this._listener.onResponseHeader(name, value);
        }
    }
    
    public void onResponseHeaderComplete() throws IOException {
        if (this._delegatingResponses) {
            this._listener.onResponseHeaderComplete();
        }
    }
    
    public void onResponseStatus(final Buffer version, final int status, final Buffer reason) throws IOException {
        if (this._delegatingResponses) {
            this._listener.onResponseStatus(version, status, reason);
        }
        else {
            this._version = version;
            this._status = status;
            this._reason = reason;
        }
    }
    
    public void onRetry() {
        if (this._delegatingRequests) {
            this._listener.onRetry();
        }
    }
}
