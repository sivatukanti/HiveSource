// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.thread.Timeout;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.AsyncEndPoint;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletContext;
import java.util.ArrayList;
import org.eclipse.jetty.continuation.ContinuationListener;
import java.util.List;
import org.eclipse.jetty.continuation.ContinuationThrowable;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.continuation.Continuation;

public class AsyncContinuation implements AsyncContext, Continuation
{
    private static final Logger LOG;
    private static final long DEFAULT_TIMEOUT = 30000L;
    private static final ContinuationThrowable __exception;
    private static final int __IDLE = 0;
    private static final int __DISPATCHED = 1;
    private static final int __ASYNCSTARTED = 2;
    private static final int __REDISPATCHING = 3;
    private static final int __ASYNCWAIT = 4;
    private static final int __REDISPATCH = 5;
    private static final int __REDISPATCHED = 6;
    private static final int __COMPLETING = 7;
    private static final int __UNCOMPLETED = 8;
    private static final int __COMPLETED = 9;
    protected AbstractHttpConnection _connection;
    private List<ContinuationListener> _continuationListeners;
    private int _state;
    private boolean _initial;
    private boolean _resumed;
    private boolean _expired;
    private volatile boolean _responseWrapped;
    private long _timeoutMs;
    private AsyncEventState _event;
    private volatile long _expireAt;
    
    protected AsyncContinuation() {
        this._timeoutMs = 30000L;
        this._state = 0;
        this._initial = true;
    }
    
    protected void setConnection(final AbstractHttpConnection connection) {
        synchronized (this) {
            this._connection = connection;
        }
    }
    
    public void addContinuationListener(final ContinuationListener listener) {
        synchronized (this) {
            if (this._continuationListeners == null) {
                this._continuationListeners = new ArrayList<ContinuationListener>();
            }
            this._continuationListeners.add(listener);
        }
    }
    
    public void setTimeout(final long ms) {
        synchronized (this) {
            this._timeoutMs = ms;
        }
    }
    
    public long getTimeout() {
        synchronized (this) {
            return this._timeoutMs;
        }
    }
    
    public AsyncEventState getAsyncEventState() {
        synchronized (this) {
            return this._event;
        }
    }
    
    public boolean isResponseWrapped() {
        return this._responseWrapped;
    }
    
    public boolean isInitial() {
        synchronized (this) {
            return this._initial;
        }
    }
    
    public boolean isSuspended() {
        synchronized (this) {
            switch (this._state) {
                case 2:
                case 3:
                case 4:
                case 7: {
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
    }
    
    public boolean isSuspending() {
        synchronized (this) {
            switch (this._state) {
                case 2:
                case 4: {
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
    }
    
    public boolean isDispatchable() {
        synchronized (this) {
            switch (this._state) {
                case 3:
                case 5:
                case 6:
                case 7: {
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
    }
    
    @Override
    public String toString() {
        synchronized (this) {
            return super.toString() + "@" + this.getStatusString();
        }
    }
    
    public String getStatusString() {
        synchronized (this) {
            return ((this._state == 0) ? "IDLE" : ((this._state == 1) ? "DISPATCHED" : ((this._state == 2) ? "ASYNCSTARTED" : ((this._state == 4) ? "ASYNCWAIT" : ((this._state == 3) ? "REDISPATCHING" : ((this._state == 5) ? "REDISPATCH" : ((this._state == 6) ? "REDISPATCHED" : ((this._state == 7) ? "COMPLETING" : ((this._state == 8) ? "UNCOMPLETED" : ((this._state == 9) ? "COMPLETE" : ("UNKNOWN?" + this._state))))))))))) + (this._initial ? ",initial" : "") + (this._resumed ? ",resumed" : "") + (this._expired ? ",expired" : "");
        }
    }
    
    protected boolean handling() {
        synchronized (this) {
            this._responseWrapped = false;
            switch (this._state) {
                case 0: {
                    this._initial = true;
                    this._state = 1;
                    return true;
                }
                case 7: {
                    this._state = 8;
                    return false;
                }
                case 4: {
                    return false;
                }
                case 5: {
                    this._state = 6;
                    return true;
                }
                default: {
                    throw new IllegalStateException(this.getStatusString());
                }
            }
        }
    }
    
    protected void suspend(final ServletContext context, final ServletRequest request, final ServletResponse response) {
        synchronized (this) {
            switch (this._state) {
                case 1:
                case 6: {
                    this._resumed = false;
                    this._expired = false;
                    if (this._event == null || request != this._event.getRequest() || response != this._event.getResponse() || context != this._event.getServletContext()) {
                        this._event = new AsyncEventState(context, request, response);
                    }
                    else {
                        this._event._dispatchContext = null;
                        this._event._path = null;
                    }
                    this._state = 2;
                    break;
                }
                default: {
                    throw new IllegalStateException(this.getStatusString());
                }
            }
        }
    }
    
    protected boolean unhandle() {
        synchronized (this) {
            final List<ContinuationListener> listeners = this._continuationListeners;
            switch (this._state) {
                case 1:
                case 6: {
                    this._state = 8;
                    return true;
                }
                case 0: {
                    throw new IllegalStateException(this.getStatusString());
                }
                case 2: {
                    this._initial = false;
                    this._state = 4;
                    this.scheduleTimeout();
                    if (this._state == 4) {
                        return true;
                    }
                    if (this._state == 7) {
                        this._state = 8;
                        return true;
                    }
                    this._initial = false;
                    this._state = 6;
                    return false;
                }
                case 3: {
                    this._initial = false;
                    this._state = 6;
                    return false;
                }
                case 7: {
                    this._initial = false;
                    this._state = 8;
                    return true;
                }
                default: {
                    throw new IllegalStateException(this.getStatusString());
                }
            }
        }
    }
    
    public void dispatch() {
        boolean dispatch = false;
        synchronized (this) {
            switch (this._state) {
                case 2: {
                    this._state = 3;
                    this._resumed = true;
                    return;
                }
                case 4: {
                    dispatch = !this._expired;
                    this._state = 5;
                    this._resumed = true;
                    break;
                }
                case 5: {
                    return;
                }
                default: {
                    throw new IllegalStateException(this.getStatusString());
                }
            }
        }
        if (dispatch) {
            this.cancelTimeout();
            this.scheduleDispatch();
        }
    }
    
    protected void expired() {
        List<ContinuationListener> listeners = null;
        synchronized (this) {
            switch (this._state) {
                case 2:
                case 4: {
                    listeners = this._continuationListeners;
                    this._expired = true;
                    break;
                }
                default: {
                    listeners = null;
                    return;
                }
            }
        }
        if (listeners != null) {
            for (int i = 0; i < listeners.size(); ++i) {
                final ContinuationListener listener = listeners.get(i);
                try {
                    listener.onTimeout(this);
                }
                catch (Exception e) {
                    AsyncContinuation.LOG.warn(e);
                }
            }
        }
        synchronized (this) {
            switch (this._state) {
                case 2:
                case 4: {
                    this.dispatch();
                    break;
                }
            }
        }
        this.scheduleDispatch();
    }
    
    public void complete() {
        boolean dispatch = false;
        synchronized (this) {
            switch (this._state) {
                case 1:
                case 6: {
                    throw new IllegalStateException(this.getStatusString());
                }
                case 2: {
                    this._state = 7;
                    return;
                }
                case 4: {
                    this._state = 7;
                    dispatch = !this._expired;
                    break;
                }
                default: {
                    throw new IllegalStateException(this.getStatusString());
                }
            }
        }
        if (dispatch) {
            this.cancelTimeout();
            this.scheduleDispatch();
        }
    }
    
    protected void doComplete() {
        List<ContinuationListener> listeners = null;
        synchronized (this) {
            switch (this._state) {
                case 8: {
                    this._state = 9;
                    listeners = this._continuationListeners;
                    break;
                }
                default: {
                    listeners = null;
                    throw new IllegalStateException(this.getStatusString());
                }
            }
        }
        if (listeners != null) {
            for (int i = 0; i < listeners.size(); ++i) {
                try {
                    listeners.get(i).onComplete(this);
                }
                catch (Exception e) {
                    AsyncContinuation.LOG.warn(e);
                }
            }
        }
    }
    
    protected void recycle() {
        synchronized (this) {
            switch (this._state) {
                case 1:
                case 6: {
                    throw new IllegalStateException(this.getStatusString());
                }
                default: {
                    this._state = 0;
                    this._initial = true;
                    this._resumed = false;
                    this._expired = false;
                    this._responseWrapped = false;
                    this.cancelTimeout();
                    this._timeoutMs = 30000L;
                    this._continuationListeners = null;
                    break;
                }
            }
        }
    }
    
    public void cancel() {
        synchronized (this) {
            this.cancelTimeout();
            this._continuationListeners = null;
        }
    }
    
    protected void scheduleDispatch() {
        final EndPoint endp = this._connection.getEndPoint();
        if (!endp.isBlocking()) {
            ((AsyncEndPoint)endp).asyncDispatch();
        }
    }
    
    protected void scheduleTimeout() {
        final EndPoint endp = this._connection.getEndPoint();
        if (this._timeoutMs > 0L) {
            if (endp.isBlocking()) {
                synchronized (this) {
                    this._expireAt = System.currentTimeMillis() + this._timeoutMs;
                    long wait;
                    for (wait = this._timeoutMs; this._expireAt > 0L && wait > 0L && this._connection.getServer().isRunning(); wait = this._expireAt - System.currentTimeMillis()) {
                        try {
                            this.wait(wait);
                        }
                        catch (InterruptedException e) {
                            AsyncContinuation.LOG.ignore(e);
                        }
                    }
                    if (this._expireAt > 0L && wait <= 0L && this._connection.getServer().isRunning()) {
                        this.expired();
                    }
                }
            }
            else {
                ((AsyncEndPoint)endp).scheduleTimeout(this._event, this._timeoutMs);
            }
        }
    }
    
    protected void cancelTimeout() {
        final EndPoint endp = this._connection.getEndPoint();
        if (endp.isBlocking()) {
            synchronized (this) {
                this._expireAt = 0L;
                this.notifyAll();
            }
        }
        else {
            final AsyncEventState event = this._event;
            if (event != null) {
                ((AsyncEndPoint)endp).cancelTimeout(event);
            }
        }
    }
    
    public boolean isCompleting() {
        synchronized (this) {
            return this._state == 7;
        }
    }
    
    boolean isUncompleted() {
        synchronized (this) {
            return this._state == 8;
        }
    }
    
    public boolean isComplete() {
        synchronized (this) {
            return this._state == 9;
        }
    }
    
    public boolean isAsyncStarted() {
        synchronized (this) {
            switch (this._state) {
                case 2:
                case 3:
                case 4:
                case 5: {
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
    }
    
    public boolean isAsync() {
        synchronized (this) {
            switch (this._state) {
                case 0:
                case 1:
                case 8:
                case 9: {
                    return false;
                }
                default: {
                    return true;
                }
            }
        }
    }
    
    public void dispatch(final ServletContext context, final String path) {
        this._event._dispatchContext = context;
        this._event._path = path;
        this.dispatch();
    }
    
    public void dispatch(final String path) {
        this._event._path = path;
        this.dispatch();
    }
    
    public Request getBaseRequest() {
        return this._connection.getRequest();
    }
    
    public ServletRequest getRequest() {
        if (this._event != null) {
            return this._event.getRequest();
        }
        return this._connection.getRequest();
    }
    
    public ServletResponse getResponse() {
        if (this._event != null) {
            return this._event.getResponse();
        }
        return this._connection.getResponse();
    }
    
    public void start(final Runnable run) {
        final AsyncEventState event = this._event;
        if (event != null) {
            this._connection.getServer().getThreadPool().dispatch((Runnable)new Runnable() {
                public void run() {
                    ((ContextHandler.Context)event.getServletContext()).getContextHandler().handle(run);
                }
            });
        }
    }
    
    public boolean hasOriginalRequestAndResponse() {
        synchronized (this) {
            return this._event != null && this._event.getRequest() == this._connection._request && this._event.getResponse() == this._connection._response;
        }
    }
    
    public ContextHandler getContextHandler() {
        final AsyncEventState event = this._event;
        if (event != null) {
            return ((ContextHandler.Context)event.getServletContext()).getContextHandler();
        }
        return null;
    }
    
    public boolean isResumed() {
        synchronized (this) {
            return this._resumed;
        }
    }
    
    public boolean isExpired() {
        synchronized (this) {
            return this._expired;
        }
    }
    
    public void resume() {
        this.dispatch();
    }
    
    public void suspend(final ServletResponse response) {
        this._responseWrapped = !(response instanceof Response);
        this.suspend(this._connection.getRequest().getServletContext(), this._connection.getRequest(), response);
    }
    
    public void suspend() {
        this._responseWrapped = false;
        this.suspend(this._connection.getRequest().getServletContext(), this._connection.getRequest(), this._connection.getResponse());
    }
    
    public ServletResponse getServletResponse() {
        if (this._responseWrapped && this._event != null && this._event.getResponse() != null) {
            return this._event.getResponse();
        }
        return this._connection.getResponse();
    }
    
    public Object getAttribute(final String name) {
        return this._connection.getRequest().getAttribute(name);
    }
    
    public void removeAttribute(final String name) {
        this._connection.getRequest().removeAttribute(name);
    }
    
    public void setAttribute(final String name, final Object attribute) {
        this._connection.getRequest().setAttribute(name, attribute);
    }
    
    public void undispatch() {
        if (!this.isSuspended()) {
            throw new IllegalStateException("!suspended");
        }
        if (AsyncContinuation.LOG.isDebugEnabled()) {
            throw new ContinuationThrowable();
        }
        throw AsyncContinuation.__exception;
    }
    
    static {
        LOG = Log.getLogger(AsyncContinuation.class);
        __exception = new ContinuationThrowable();
    }
    
    public class AsyncEventState extends Timeout.Task implements Runnable
    {
        private final ServletContext _suspendedContext;
        private final ServletRequest _request;
        private final ServletResponse _response;
        private ServletContext _dispatchContext;
        private String _path;
        
        public AsyncEventState(final ServletContext context, final ServletRequest request, final ServletResponse response) {
            this._suspendedContext = context;
            this._request = request;
            this._response = response;
        }
        
        public ServletContext getSuspendedContext() {
            return this._suspendedContext;
        }
        
        public ServletContext getDispatchContext() {
            return this._dispatchContext;
        }
        
        public ServletContext getServletContext() {
            return (this._dispatchContext == null) ? this._suspendedContext : this._dispatchContext;
        }
        
        public ServletRequest getRequest() {
            return this._request;
        }
        
        public ServletResponse getResponse() {
            return this._response;
        }
        
        public String getPath() {
            return this._path;
        }
        
        @Override
        public void expired() {
            AsyncContinuation.this.expired();
        }
        
        public void run() {
            AsyncContinuation.this.expired();
        }
    }
}
