// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlets;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.continuation.ContinuationSupport;
import java.util.concurrent.TimeUnit;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.servlet.FilterConfig;
import org.eclipse.jetty.continuation.ContinuationListener;
import org.eclipse.jetty.continuation.Continuation;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import javax.servlet.ServletContext;
import javax.servlet.Filter;

public class QoSFilter implements Filter
{
    static final int __DEFAULT_MAX_PRIORITY = 10;
    static final int __DEFAULT_PASSES = 10;
    static final int __DEFAULT_WAIT_MS = 50;
    static final long __DEFAULT_TIMEOUT_MS = -1L;
    static final String MANAGED_ATTR_INIT_PARAM = "managedAttr";
    static final String MAX_REQUESTS_INIT_PARAM = "maxRequests";
    static final String MAX_PRIORITY_INIT_PARAM = "maxPriority";
    static final String MAX_WAIT_INIT_PARAM = "waitMs";
    static final String SUSPEND_INIT_PARAM = "suspendMs";
    ServletContext _context;
    protected long _waitMs;
    protected long _suspendMs;
    protected int _maxRequests;
    private Semaphore _passes;
    private Queue<Continuation>[] _queue;
    private ContinuationListener[] _listener;
    private String _suspended;
    
    public QoSFilter() {
        this._suspended = "QoSFilter@" + this.hashCode();
    }
    
    public void init(final FilterConfig filterConfig) {
        this._context = filterConfig.getServletContext();
        int max_priority = 10;
        if (filterConfig.getInitParameter("maxPriority") != null) {
            max_priority = Integer.parseInt(filterConfig.getInitParameter("maxPriority"));
        }
        this._queue = (Queue<Continuation>[])new Queue[max_priority + 1];
        this._listener = new ContinuationListener[max_priority + 1];
        for (int p = 0; p < this._queue.length; ++p) {
            this._queue[p] = new ConcurrentLinkedQueue<Continuation>();
            final int priority = p;
            this._listener[p] = new ContinuationListener() {
                public void onComplete(final Continuation continuation) {
                }
                
                public void onTimeout(final Continuation continuation) {
                    QoSFilter.this._queue[priority].remove(continuation);
                }
            };
        }
        int maxRequests = 10;
        if (filterConfig.getInitParameter("maxRequests") != null) {
            maxRequests = Integer.parseInt(filterConfig.getInitParameter("maxRequests"));
        }
        this._passes = new Semaphore(maxRequests, true);
        this._maxRequests = maxRequests;
        long wait = 50L;
        if (filterConfig.getInitParameter("waitMs") != null) {
            wait = Integer.parseInt(filterConfig.getInitParameter("waitMs"));
        }
        this._waitMs = wait;
        long suspend = -1L;
        if (filterConfig.getInitParameter("suspendMs") != null) {
            suspend = Integer.parseInt(filterConfig.getInitParameter("suspendMs"));
        }
        this._suspendMs = suspend;
        if (this._context != null && Boolean.parseBoolean(filterConfig.getInitParameter("managedAttr"))) {
            this._context.setAttribute(filterConfig.getFilterName(), this);
        }
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        boolean accepted = false;
        try {
            if (request.getAttribute(this._suspended) == null) {
                accepted = this._passes.tryAcquire(this._waitMs, TimeUnit.MILLISECONDS);
                if (!accepted) {
                    request.setAttribute(this._suspended, Boolean.TRUE);
                    final int priority = this.getPriority(request);
                    final Continuation continuation = ContinuationSupport.getContinuation(request);
                    if (this._suspendMs > 0L) {
                        continuation.setTimeout(this._suspendMs);
                    }
                    continuation.suspend();
                    continuation.addContinuationListener(this._listener[priority]);
                    this._queue[priority].add(continuation);
                    return;
                }
                request.setAttribute(this._suspended, Boolean.FALSE);
            }
            else {
                final Boolean suspended = (Boolean)request.getAttribute(this._suspended);
                if (suspended) {
                    request.setAttribute(this._suspended, Boolean.FALSE);
                    if (request.getAttribute("javax.servlet.resumed") == Boolean.TRUE) {
                        this._passes.acquire();
                        accepted = true;
                    }
                    else {
                        accepted = this._passes.tryAcquire(this._waitMs, TimeUnit.MILLISECONDS);
                    }
                }
                else {
                    this._passes.acquire();
                    accepted = true;
                }
            }
            if (accepted) {
                chain.doFilter(request, response);
            }
            else {
                ((HttpServletResponse)response).sendError(503);
            }
        }
        catch (InterruptedException e) {
            this._context.log("QoS", e);
            ((HttpServletResponse)response).sendError(503);
        }
        finally {
            if (accepted) {
                int p = this._queue.length;
                while (p-- > 0) {
                    final Continuation continutaion = this._queue[p].poll();
                    if (continutaion != null && continutaion.isSuspended()) {
                        continutaion.resume();
                        break;
                    }
                }
                this._passes.release();
            }
        }
    }
    
    protected int getPriority(final ServletRequest request) {
        final HttpServletRequest baseRequest = (HttpServletRequest)request;
        if (baseRequest.getUserPrincipal() != null) {
            return 2;
        }
        final HttpSession session = baseRequest.getSession(false);
        if (session != null && !session.isNew()) {
            return 1;
        }
        return 0;
    }
    
    public void destroy() {
    }
    
    public long getWaitMs() {
        return this._waitMs;
    }
    
    public void setWaitMs(final long value) {
        this._waitMs = value;
    }
    
    public long getSuspendMs() {
        return this._suspendMs;
    }
    
    public void setSuspendMs(final long value) {
        this._suspendMs = value;
    }
    
    public int getMaxRequests() {
        return this._maxRequests;
    }
    
    public void setMaxRequests(final int value) {
        this._passes = new Semaphore(value - this._maxRequests + this._passes.availablePermits(), true);
        this._maxRequests = value;
    }
}
