// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import javax.servlet.ServletContext;
import org.eclipse.jetty.server.handler.ContextHandler;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.AsyncEvent;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.AsyncListener;
import javax.servlet.AsyncContext;

public class AsyncContextState implements AsyncContext
{
    private final HttpChannel _channel;
    volatile HttpChannelState _state;
    
    public AsyncContextState(final HttpChannelState state) {
        this._state = state;
        this._channel = this._state.getHttpChannel();
    }
    
    public HttpChannel getHttpChannel() {
        return this._channel;
    }
    
    HttpChannelState state() {
        final HttpChannelState state = this._state;
        if (state == null) {
            throw new IllegalStateException("AsyncContext completed and/or Request lifecycle recycled");
        }
        return state;
    }
    
    @Override
    public void addListener(final AsyncListener listener, final ServletRequest request, final ServletResponse response) {
        final AsyncListener wrap = new AsyncListener() {
            @Override
            public void onTimeout(final AsyncEvent event) throws IOException {
                listener.onTimeout(new AsyncEvent(event.getAsyncContext(), request, response, event.getThrowable()));
            }
            
            @Override
            public void onStartAsync(final AsyncEvent event) throws IOException {
                listener.onStartAsync(new AsyncEvent(event.getAsyncContext(), request, response, event.getThrowable()));
            }
            
            @Override
            public void onError(final AsyncEvent event) throws IOException {
                listener.onError(new AsyncEvent(event.getAsyncContext(), request, response, event.getThrowable()));
            }
            
            @Override
            public void onComplete(final AsyncEvent event) throws IOException {
                listener.onComplete(new AsyncEvent(event.getAsyncContext(), request, response, event.getThrowable()));
            }
        };
        this.state().addListener(wrap);
    }
    
    @Override
    public void addListener(final AsyncListener listener) {
        this.state().addListener(listener);
    }
    
    @Override
    public void complete() {
        this.state().complete();
    }
    
    @Override
    public <T extends AsyncListener> T createListener(final Class<T> clazz) throws ServletException {
        final ContextHandler contextHandler = this.state().getContextHandler();
        if (contextHandler != null) {
            return contextHandler.getServletContext().createListener(clazz);
        }
        try {
            return clazz.newInstance();
        }
        catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
    @Override
    public void dispatch() {
        this.state().dispatch(null, null);
    }
    
    @Override
    public void dispatch(final String path) {
        this.state().dispatch(null, path);
    }
    
    @Override
    public void dispatch(final ServletContext context, final String path) {
        this.state().dispatch(context, path);
    }
    
    @Override
    public ServletRequest getRequest() {
        return this.state().getAsyncContextEvent().getSuppliedRequest();
    }
    
    @Override
    public ServletResponse getResponse() {
        return this.state().getAsyncContextEvent().getSuppliedResponse();
    }
    
    @Override
    public long getTimeout() {
        return this.state().getTimeout();
    }
    
    @Override
    public boolean hasOriginalRequestAndResponse() {
        final HttpChannel channel = this.state().getHttpChannel();
        return channel.getRequest() == this.getRequest() && channel.getResponse() == this.getResponse();
    }
    
    @Override
    public void setTimeout(final long arg0) {
        this.state().setTimeout(arg0);
    }
    
    @Override
    public void start(final Runnable task) {
        final HttpChannel channel = this.state().getHttpChannel();
        channel.execute(new Runnable() {
            @Override
            public void run() {
                AsyncContextState.this.state().getAsyncContextEvent().getContext().getContextHandler().handle(channel.getRequest(), task);
            }
        });
    }
    
    public void reset() {
        this._state = null;
    }
    
    public HttpChannelState getHttpChannelState() {
        return this.state();
    }
}
