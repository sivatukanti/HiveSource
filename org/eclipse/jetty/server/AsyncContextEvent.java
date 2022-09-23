// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.eclipse.jetty.util.thread.Scheduler;
import javax.servlet.ServletContext;
import org.eclipse.jetty.server.handler.ContextHandler;
import javax.servlet.AsyncEvent;

public class AsyncContextEvent extends AsyncEvent implements Runnable
{
    private final ContextHandler.Context _context;
    private final AsyncContextState _asyncContext;
    private volatile HttpChannelState _state;
    private ServletContext _dispatchContext;
    private String _dispatchPath;
    private volatile Scheduler.Task _timeoutTask;
    private Throwable _throwable;
    
    public AsyncContextEvent(final ContextHandler.Context context, final AsyncContextState asyncContext, final HttpChannelState state, final Request baseRequest, final ServletRequest request, final ServletResponse response) {
        super(null, request, response, null);
        this._context = context;
        this._asyncContext = asyncContext;
        this._state = state;
        if (baseRequest.getAttribute("javax.servlet.async.request_uri") == null) {
            final String uri = (String)baseRequest.getAttribute("javax.servlet.forward.request_uri");
            if (uri != null) {
                baseRequest.setAttribute("javax.servlet.async.request_uri", uri);
                baseRequest.setAttribute("javax.servlet.async.context_path", baseRequest.getAttribute("javax.servlet.forward.context_path"));
                baseRequest.setAttribute("javax.servlet.async.servlet_path", baseRequest.getAttribute("javax.servlet.forward.servlet_path"));
                baseRequest.setAttribute("javax.servlet.async.path_info", baseRequest.getAttribute("javax.servlet.forward.path_info"));
                baseRequest.setAttribute("javax.servlet.async.query_string", baseRequest.getAttribute("javax.servlet.forward.query_string"));
            }
            else {
                baseRequest.setAttribute("javax.servlet.async.request_uri", baseRequest.getRequestURI());
                baseRequest.setAttribute("javax.servlet.async.context_path", baseRequest.getContextPath());
                baseRequest.setAttribute("javax.servlet.async.servlet_path", baseRequest.getServletPath());
                baseRequest.setAttribute("javax.servlet.async.path_info", baseRequest.getPathInfo());
                baseRequest.setAttribute("javax.servlet.async.query_string", baseRequest.getQueryString());
            }
        }
    }
    
    public ServletContext getSuspendedContext() {
        return this._context;
    }
    
    public ContextHandler.Context getContext() {
        return this._context;
    }
    
    public ServletContext getDispatchContext() {
        return this._dispatchContext;
    }
    
    public ServletContext getServletContext() {
        return (this._dispatchContext == null) ? this._context : this._dispatchContext;
    }
    
    public String getPath() {
        return this._dispatchPath;
    }
    
    public void setTimeoutTask(final Scheduler.Task task) {
        this._timeoutTask = task;
    }
    
    public void cancelTimeoutTask() {
        final Scheduler.Task task = this._timeoutTask;
        this._timeoutTask = null;
        if (task != null) {
            task.cancel();
        }
    }
    
    @Override
    public AsyncContext getAsyncContext() {
        return this._asyncContext;
    }
    
    @Override
    public Throwable getThrowable() {
        return this._throwable;
    }
    
    public void setDispatchContext(final ServletContext context) {
        this._dispatchContext = context;
    }
    
    public void setDispatchPath(final String path) {
        this._dispatchPath = path;
    }
    
    public void completed() {
        this._timeoutTask = null;
        this._asyncContext.reset();
    }
    
    public HttpChannelState getHttpChannelState() {
        return this._state;
    }
    
    @Override
    public void run() {
        final Scheduler.Task task = this._timeoutTask;
        this._timeoutTask = null;
        if (task != null) {
            this._state.getHttpChannel().execute(() -> this._state.onTimeout());
        }
    }
    
    public void addThrowable(final Throwable e) {
        if (this._throwable == null) {
            this._throwable = e;
        }
        else {
            this._throwable.addSuppressed(e);
        }
    }
}
