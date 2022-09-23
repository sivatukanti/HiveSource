// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.AsyncEvent;
import org.eclipse.jetty.server.HttpChannel;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;

public class IdleTimeoutHandler extends HandlerWrapper
{
    private long _idleTimeoutMs;
    private boolean _applyToAsync;
    
    public IdleTimeoutHandler() {
        this._idleTimeoutMs = 1000L;
        this._applyToAsync = false;
    }
    
    public boolean isApplyToAsync() {
        return this._applyToAsync;
    }
    
    public void setApplyToAsync(final boolean applyToAsync) {
        this._applyToAsync = applyToAsync;
    }
    
    public long getIdleTimeoutMs() {
        return this._idleTimeoutMs;
    }
    
    public void setIdleTimeoutMs(final long idleTimeoutMs) {
        this._idleTimeoutMs = idleTimeoutMs;
    }
    
    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final HttpChannel channel = baseRequest.getHttpChannel();
        final long idle_timeout = baseRequest.getHttpChannel().getIdleTimeout();
        channel.setIdleTimeout(this._idleTimeoutMs);
        try {
            super.handle(target, baseRequest, request, response);
        }
        finally {
            if (this._applyToAsync && request.isAsyncStarted()) {
                request.getAsyncContext().addListener(new AsyncListener() {
                    @Override
                    public void onTimeout(final AsyncEvent event) throws IOException {
                    }
                    
                    @Override
                    public void onStartAsync(final AsyncEvent event) throws IOException {
                    }
                    
                    @Override
                    public void onError(final AsyncEvent event) throws IOException {
                        channel.setIdleTimeout(idle_timeout);
                    }
                    
                    @Override
                    public void onComplete(final AsyncEvent event) throws IOException {
                        channel.setIdleTimeout(idle_timeout);
                    }
                });
            }
            else {
                channel.setIdleTimeout(idle_timeout);
            }
        }
    }
}
