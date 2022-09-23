// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlets;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionBindingListener;
import org.eclipse.jetty.util.log.Log;
import java.util.StringTokenizer;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.continuation.ContinuationSupport;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.servlet.FilterConfig;
import org.eclipse.jetty.util.thread.Timeout;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.continuation.ContinuationListener;
import org.eclipse.jetty.continuation.Continuation;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import javax.servlet.ServletContext;
import org.eclipse.jetty.util.log.Logger;
import javax.servlet.Filter;

public class DoSFilter implements Filter
{
    private static final Logger LOG;
    static final String __TRACKER = "DoSFilter.Tracker";
    static final String __THROTTLED = "DoSFilter.Throttled";
    static final int __DEFAULT_MAX_REQUESTS_PER_SEC = 25;
    static final int __DEFAULT_DELAY_MS = 100;
    static final int __DEFAULT_THROTTLE = 5;
    static final int __DEFAULT_WAIT_MS = 50;
    static final long __DEFAULT_THROTTLE_MS = 30000L;
    static final long __DEFAULT_MAX_REQUEST_MS_INIT_PARAM = 30000L;
    static final long __DEFAULT_MAX_IDLE_TRACKER_MS_INIT_PARAM = 30000L;
    static final String MANAGED_ATTR_INIT_PARAM = "managedAttr";
    static final String MAX_REQUESTS_PER_S_INIT_PARAM = "maxRequestsPerSec";
    static final String DELAY_MS_INIT_PARAM = "delayMs";
    static final String THROTTLED_REQUESTS_INIT_PARAM = "throttledRequests";
    static final String MAX_WAIT_INIT_PARAM = "maxWaitMs";
    static final String THROTTLE_MS_INIT_PARAM = "throttleMs";
    static final String MAX_REQUEST_MS_INIT_PARAM = "maxRequestMs";
    static final String MAX_IDLE_TRACKER_MS_INIT_PARAM = "maxIdleTrackerMs";
    static final String INSERT_HEADERS_INIT_PARAM = "insertHeaders";
    static final String TRACK_SESSIONS_INIT_PARAM = "trackSessions";
    static final String REMOTE_PORT_INIT_PARAM = "remotePort";
    static final String IP_WHITELIST_INIT_PARAM = "ipWhitelist";
    static final int USER_AUTH = 2;
    static final int USER_SESSION = 2;
    static final int USER_IP = 1;
    static final int USER_UNKNOWN = 0;
    ServletContext _context;
    protected String _name;
    protected long _delayMs;
    protected long _throttleMs;
    protected long _maxWaitMs;
    protected long _maxRequestMs;
    protected long _maxIdleTrackerMs;
    protected boolean _insertHeaders;
    protected boolean _trackSessions;
    protected boolean _remotePort;
    protected int _throttledRequests;
    protected Semaphore _passes;
    protected Queue<Continuation>[] _queue;
    protected ContinuationListener[] _listener;
    protected int _maxRequestsPerSec;
    protected final ConcurrentHashMap<String, RateTracker> _rateTrackers;
    protected String _whitelistStr;
    private final HashSet<String> _whitelist;
    private final Timeout _requestTimeoutQ;
    private final Timeout _trackerTimeoutQ;
    private Thread _timerThread;
    private volatile boolean _running;
    
    public DoSFilter() {
        this._rateTrackers = new ConcurrentHashMap<String, RateTracker>();
        this._whitelist = new HashSet<String>();
        this._requestTimeoutQ = new Timeout();
        this._trackerTimeoutQ = new Timeout();
    }
    
    public void init(final FilterConfig filterConfig) {
        this._context = filterConfig.getServletContext();
        this._queue = (Queue<Continuation>[])new Queue[this.getMaxPriority() + 1];
        this._listener = new ContinuationListener[this.getMaxPriority() + 1];
        for (int p = 0; p < this._queue.length; ++p) {
            this._queue[p] = new ConcurrentLinkedQueue<Continuation>();
            final int priority = p;
            this._listener[p] = new ContinuationListener() {
                public void onComplete(final Continuation continuation) {
                }
                
                public void onTimeout(final Continuation continuation) {
                    DoSFilter.this._queue[priority].remove(continuation);
                }
            };
        }
        this._rateTrackers.clear();
        int baseRateLimit = 25;
        if (filterConfig.getInitParameter("maxRequestsPerSec") != null) {
            baseRateLimit = Integer.parseInt(filterConfig.getInitParameter("maxRequestsPerSec"));
        }
        this._maxRequestsPerSec = baseRateLimit;
        long delay = 100L;
        if (filterConfig.getInitParameter("delayMs") != null) {
            delay = Integer.parseInt(filterConfig.getInitParameter("delayMs"));
        }
        this._delayMs = delay;
        int throttledRequests = 5;
        if (filterConfig.getInitParameter("throttledRequests") != null) {
            throttledRequests = Integer.parseInt(filterConfig.getInitParameter("throttledRequests"));
        }
        this._passes = new Semaphore(throttledRequests, true);
        this._throttledRequests = throttledRequests;
        long wait = 50L;
        if (filterConfig.getInitParameter("maxWaitMs") != null) {
            wait = Integer.parseInt(filterConfig.getInitParameter("maxWaitMs"));
        }
        this._maxWaitMs = wait;
        long suspend = 30000L;
        if (filterConfig.getInitParameter("throttleMs") != null) {
            suspend = Integer.parseInt(filterConfig.getInitParameter("throttleMs"));
        }
        this._throttleMs = suspend;
        long maxRequestMs = 30000L;
        if (filterConfig.getInitParameter("maxRequestMs") != null) {
            maxRequestMs = Long.parseLong(filterConfig.getInitParameter("maxRequestMs"));
        }
        this._maxRequestMs = maxRequestMs;
        long maxIdleTrackerMs = 30000L;
        if (filterConfig.getInitParameter("maxIdleTrackerMs") != null) {
            maxIdleTrackerMs = Long.parseLong(filterConfig.getInitParameter("maxIdleTrackerMs"));
        }
        this._maxIdleTrackerMs = maxIdleTrackerMs;
        this._whitelistStr = "";
        if (filterConfig.getInitParameter("ipWhitelist") != null) {
            this._whitelistStr = filterConfig.getInitParameter("ipWhitelist");
        }
        this.initWhitelist();
        String tmp = filterConfig.getInitParameter("insertHeaders");
        this._insertHeaders = (tmp == null || Boolean.parseBoolean(tmp));
        tmp = filterConfig.getInitParameter("trackSessions");
        this._trackSessions = (tmp == null || Boolean.parseBoolean(tmp));
        tmp = filterConfig.getInitParameter("remotePort");
        this._remotePort = (tmp != null && Boolean.parseBoolean(tmp));
        this._requestTimeoutQ.setNow();
        this._requestTimeoutQ.setDuration(this._maxRequestMs);
        this._trackerTimeoutQ.setNow();
        this._trackerTimeoutQ.setDuration(this._maxIdleTrackerMs);
        this._running = true;
        (this._timerThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (DoSFilter.this._running) {
                        final long now;
                        synchronized (DoSFilter.this._requestTimeoutQ) {
                            now = DoSFilter.this._requestTimeoutQ.setNow();
                            DoSFilter.this._requestTimeoutQ.tick();
                        }
                        synchronized (DoSFilter.this._trackerTimeoutQ) {
                            DoSFilter.this._trackerTimeoutQ.setNow(now);
                            DoSFilter.this._trackerTimeoutQ.tick();
                        }
                        try {
                            Thread.sleep(100L);
                        }
                        catch (InterruptedException e) {
                            DoSFilter.LOG.ignore(e);
                        }
                    }
                }
                finally {
                    DoSFilter.LOG.info("DoSFilter timer exited", new Object[0]);
                }
            }
        }).start();
        if (this._context != null && Boolean.parseBoolean(filterConfig.getInitParameter("managedAttr"))) {
            this._context.setAttribute(filterConfig.getFilterName(), this);
        }
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterchain) throws IOException, ServletException {
        final HttpServletRequest srequest = (HttpServletRequest)request;
        final HttpServletResponse sresponse = (HttpServletResponse)response;
        final long now = this._requestTimeoutQ.getNow();
        RateTracker tracker = (RateTracker)request.getAttribute("DoSFilter.Tracker");
        if (tracker == null) {
            tracker = this.getRateTracker(request);
            final boolean overRateLimit = tracker.isRateExceeded(now);
            if (!overRateLimit) {
                this.doFilterChain(filterchain, srequest, sresponse);
                return;
            }
            DoSFilter.LOG.warn("DOS ALERT: ip=" + srequest.getRemoteAddr() + ",session=" + srequest.getRequestedSessionId() + ",user=" + srequest.getUserPrincipal(), new Object[0]);
            switch ((int)this._delayMs) {
                case -1: {
                    if (this._insertHeaders) {
                        ((HttpServletResponse)response).addHeader("DoSFilter", "unavailable");
                    }
                    ((HttpServletResponse)response).sendError(503);
                    return;
                }
                case 0: {
                    request.setAttribute("DoSFilter.Tracker", tracker);
                    break;
                }
                default: {
                    if (this._insertHeaders) {
                        ((HttpServletResponse)response).addHeader("DoSFilter", "delayed");
                    }
                    final Continuation continuation = ContinuationSupport.getContinuation(request);
                    request.setAttribute("DoSFilter.Tracker", tracker);
                    if (this._delayMs > 0L) {
                        continuation.setTimeout(this._delayMs);
                    }
                    continuation.suspend();
                    return;
                }
            }
        }
        boolean accepted = false;
        try {
            accepted = this._passes.tryAcquire(this._maxWaitMs, TimeUnit.MILLISECONDS);
            if (!accepted) {
                final Continuation continuation = ContinuationSupport.getContinuation(request);
                final Boolean throttled = (Boolean)request.getAttribute("DoSFilter.Throttled");
                if (throttled != Boolean.TRUE && this._throttleMs > 0L) {
                    final int priority = this.getPriority(request, tracker);
                    request.setAttribute("DoSFilter.Throttled", Boolean.TRUE);
                    if (this._insertHeaders) {
                        ((HttpServletResponse)response).addHeader("DoSFilter", "throttled");
                    }
                    if (this._throttleMs > 0L) {
                        continuation.setTimeout(this._throttleMs);
                    }
                    continuation.suspend();
                    continuation.addContinuationListener(this._listener[priority]);
                    this._queue[priority].add(continuation);
                    return;
                }
                if (request.getAttribute("javax.servlet.resumed") == Boolean.TRUE) {
                    this._passes.acquire();
                    accepted = true;
                }
            }
            if (accepted) {
                this.doFilterChain(filterchain, srequest, sresponse);
            }
            else {
                if (this._insertHeaders) {
                    ((HttpServletResponse)response).addHeader("DoSFilter", "unavailable");
                }
                ((HttpServletResponse)response).sendError(503);
            }
        }
        catch (InterruptedException e) {
            this._context.log("DoS", e);
            ((HttpServletResponse)response).sendError(503);
        }
        finally {
            if (accepted) {
                int p = this._queue.length;
                while (p-- > 0) {
                    final Continuation continuation2 = this._queue[p].poll();
                    if (continuation2 != null && continuation2.isSuspended()) {
                        continuation2.resume();
                        break;
                    }
                }
                this._passes.release();
            }
        }
    }
    
    protected void doFilterChain(final FilterChain chain, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final Thread thread = Thread.currentThread();
        final Timeout.Task requestTimeout = new Timeout.Task() {
            @Override
            public void expired() {
                DoSFilter.this.closeConnection(request, response, thread);
            }
        };
        try {
            synchronized (this._requestTimeoutQ) {
                this._requestTimeoutQ.schedule(requestTimeout);
            }
            chain.doFilter(request, response);
        }
        finally {
            synchronized (this._requestTimeoutQ) {
                requestTimeout.cancel();
            }
        }
    }
    
    protected void closeConnection(final HttpServletRequest request, final HttpServletResponse response, final Thread thread) {
        while (true) {
            if (!response.isCommitted()) {
                response.setHeader("Connection", "close");
                try {
                    try {
                        response.getWriter().close();
                    }
                    catch (IllegalStateException e2) {
                        response.getOutputStream().close();
                    }
                }
                catch (IOException e) {
                    DoSFilter.LOG.warn(e);
                }
                thread.interrupt();
                return;
            }
            continue;
        }
    }
    
    protected int getPriority(final ServletRequest request, final RateTracker tracker) {
        if (this.extractUserId(request) != null) {
            return 2;
        }
        if (tracker != null) {
            return tracker.getType();
        }
        return 0;
    }
    
    protected int getMaxPriority() {
        return 2;
    }
    
    public RateTracker getRateTracker(final ServletRequest request) {
        final HttpServletRequest srequest = (HttpServletRequest)request;
        final HttpSession session = srequest.getSession(false);
        String loadId = this.extractUserId(request);
        int type;
        if (loadId != null) {
            type = 2;
        }
        else if (this._trackSessions && session != null && !session.isNew()) {
            loadId = session.getId();
            type = 2;
        }
        else {
            loadId = (this._remotePort ? (request.getRemoteAddr() + request.getRemotePort()) : request.getRemoteAddr());
            type = 1;
        }
        RateTracker tracker = this._rateTrackers.get(loadId);
        if (tracker == null) {
            RateTracker t;
            if (this._whitelist.contains(request.getRemoteAddr())) {
                t = new FixedRateTracker(loadId, type, this._maxRequestsPerSec);
            }
            else {
                t = new RateTracker(loadId, type, this._maxRequestsPerSec);
            }
            tracker = this._rateTrackers.putIfAbsent(loadId, t);
            if (tracker == null) {
                tracker = t;
            }
            if (type == 1) {
                synchronized (this._trackerTimeoutQ) {
                    this._trackerTimeoutQ.schedule(tracker);
                }
            }
            else if (session != null) {
                session.setAttribute("DoSFilter.Tracker", tracker);
            }
        }
        return tracker;
    }
    
    public void destroy() {
        this._running = false;
        this._timerThread.interrupt();
        synchronized (this._requestTimeoutQ) {
            this._requestTimeoutQ.cancelAll();
        }
        synchronized (this._trackerTimeoutQ) {
            this._trackerTimeoutQ.cancelAll();
        }
        this._rateTrackers.clear();
        this._whitelist.clear();
    }
    
    protected String extractUserId(final ServletRequest request) {
        return null;
    }
    
    protected void initWhitelist() {
        this._whitelist.clear();
        final StringTokenizer tokenizer = new StringTokenizer(this._whitelistStr, ",");
        while (tokenizer.hasMoreTokens()) {
            this._whitelist.add(tokenizer.nextToken().trim());
        }
        DoSFilter.LOG.info("Whitelisted IP addresses: {}", this._whitelist.toString());
    }
    
    public int getMaxRequestsPerSec() {
        return this._maxRequestsPerSec;
    }
    
    public void setMaxRequestsPerSec(final int value) {
        this._maxRequestsPerSec = value;
    }
    
    public long getDelayMs() {
        return this._delayMs;
    }
    
    public void setDelayMs(final long value) {
        this._delayMs = value;
    }
    
    public long getMaxWaitMs() {
        return this._maxWaitMs;
    }
    
    public void setMaxWaitMs(final long value) {
        this._maxWaitMs = value;
    }
    
    public int getThrottledRequests() {
        return this._throttledRequests;
    }
    
    public void setThrottledRequests(final int value) {
        this._passes = new Semaphore(value - this._throttledRequests + this._passes.availablePermits(), true);
        this._throttledRequests = value;
    }
    
    public long getThrottleMs() {
        return this._throttleMs;
    }
    
    public void setThrottleMs(final long value) {
        this._throttleMs = value;
    }
    
    public long getMaxRequestMs() {
        return this._maxRequestMs;
    }
    
    public void setMaxRequestMs(final long value) {
        this._maxRequestMs = value;
    }
    
    public long getMaxIdleTrackerMs() {
        return this._maxIdleTrackerMs;
    }
    
    public void setMaxIdleTrackerMs(final long value) {
        this._maxIdleTrackerMs = value;
    }
    
    public boolean isInsertHeaders() {
        return this._insertHeaders;
    }
    
    public void setInsertHeaders(final boolean value) {
        this._insertHeaders = value;
    }
    
    public boolean isTrackSessions() {
        return this._trackSessions;
    }
    
    public void setTrackSessions(final boolean value) {
        this._trackSessions = value;
    }
    
    public boolean isRemotePort() {
        return this._remotePort;
    }
    
    public void setRemotePort(final boolean value) {
        this._remotePort = value;
    }
    
    public String getWhitelist() {
        return this._whitelistStr;
    }
    
    public void setWhitelist(final String value) {
        this._whitelistStr = value;
        this.initWhitelist();
    }
    
    static {
        LOG = Log.getLogger(DoSFilter.class);
    }
    
    class RateTracker extends Timeout.Task implements HttpSessionBindingListener, HttpSessionActivationListener
    {
        protected final transient String _id;
        protected final transient int _type;
        protected final transient long[] _timestamps;
        protected transient int _next;
        
        public RateTracker(final String id, final int type, final int maxRequestsPerSecond) {
            this._id = id;
            this._type = type;
            this._timestamps = new long[maxRequestsPerSecond];
            this._next = 0;
        }
        
        public boolean isRateExceeded(final long now) {
            final long last;
            synchronized (this) {
                last = this._timestamps[this._next];
                this._timestamps[this._next] = now;
                this._next = (this._next + 1) % this._timestamps.length;
            }
            final boolean exceeded = last != 0L && now - last < 1000L;
            return exceeded;
        }
        
        public String getId() {
            return this._id;
        }
        
        public int getType() {
            return this._type;
        }
        
        public void valueBound(final HttpSessionBindingEvent event) {
            if (DoSFilter.LOG.isDebugEnabled()) {
                DoSFilter.LOG.debug("Value bound:" + this._id, new Object[0]);
            }
        }
        
        public void valueUnbound(final HttpSessionBindingEvent event) {
            if (DoSFilter.this._rateTrackers != null) {
                DoSFilter.this._rateTrackers.remove(this._id);
            }
            if (DoSFilter.LOG.isDebugEnabled()) {
                DoSFilter.LOG.debug("Tracker removed: " + this._id, new Object[0]);
            }
        }
        
        public void sessionWillPassivate(final HttpSessionEvent se) {
            if (DoSFilter.this._rateTrackers != null) {
                DoSFilter.this._rateTrackers.remove(this._id);
            }
            se.getSession().removeAttribute("DoSFilter.Tracker");
            if (DoSFilter.LOG.isDebugEnabled()) {
                DoSFilter.LOG.debug("Value removed: " + this._id, new Object[0]);
            }
        }
        
        public void sessionDidActivate(final HttpSessionEvent se) {
            DoSFilter.LOG.warn("Unexpected session activation", new Object[0]);
        }
        
        @Override
        public void expired() {
            if (DoSFilter.this._rateTrackers != null && DoSFilter.this._trackerTimeoutQ != null) {
                final long now = DoSFilter.this._trackerTimeoutQ.getNow();
                final int latestIndex = (this._next == 0) ? 3 : ((this._next - 1) % this._timestamps.length);
                final long last = this._timestamps[latestIndex];
                final boolean hasRecentRequest = last != 0L && now - last < 1000L;
                if (hasRecentRequest) {
                    this.reschedule();
                }
                else {
                    DoSFilter.this._rateTrackers.remove(this._id);
                }
            }
        }
        
        @Override
        public String toString() {
            return "RateTracker/" + this._id + "/" + this._type;
        }
    }
    
    class FixedRateTracker extends RateTracker
    {
        public FixedRateTracker(final String id, final int type, final int numRecentRequestsTracked) {
            super(id, type, numRecentRequestsTracked);
        }
        
        @Override
        public boolean isRateExceeded(final long now) {
            synchronized (this) {
                this._timestamps[this._next] = now;
                this._next = (this._next + 1) % this._timestamps.length;
            }
            return false;
        }
        
        @Override
        public String toString() {
            return "Fixed" + super.toString();
        }
    }
}
