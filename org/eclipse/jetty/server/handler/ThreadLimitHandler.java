// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.http.QuotedCSV;
import java.util.ArrayDeque;
import java.util.Deque;
import org.eclipse.jetty.util.thread.Locker;
import org.eclipse.jetty.util.log.Log;
import java.util.Iterator;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HostPortHttpField;
import org.eclipse.jetty.http.HttpField;
import java.net.InetSocketAddress;
import java.io.IOException;
import javax.servlet.AsyncContext;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javax.servlet.ServletException;
import java.io.Closeable;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.util.InetAddressSet;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.annotation.Name;
import java.util.concurrent.ConcurrentMap;
import java.net.InetAddress;
import org.eclipse.jetty.util.IncludeExcludeSet;
import org.eclipse.jetty.util.log.Logger;

public class ThreadLimitHandler extends HandlerWrapper
{
    private static final Logger LOG;
    private static final String REMOTE = "o.e.j.s.h.TLH.REMOTE";
    private static final String PERMIT = "o.e.j.s.h.TLH.PASS";
    private final boolean _rfc7239;
    private final String _forwardedHeader;
    private final IncludeExcludeSet<String, InetAddress> _includeExcludeSet;
    private final ConcurrentMap<String, Remote> _remotes;
    private volatile boolean _enabled;
    private int _threadLimit;
    
    public ThreadLimitHandler() {
        this(null, false);
    }
    
    public ThreadLimitHandler(@Name("forwardedHeader") final String forwardedHeader) {
        this(forwardedHeader, HttpHeader.FORWARDED.is(forwardedHeader));
    }
    
    public ThreadLimitHandler(@Name("forwardedHeader") final String forwardedHeader, @Name("rfc7239") final boolean rfc7239) {
        this._includeExcludeSet = new IncludeExcludeSet<String, InetAddress>((Class<SET>)InetAddressSet.class);
        this._remotes = new ConcurrentHashMap<String, Remote>();
        this._threadLimit = 10;
        this._rfc7239 = rfc7239;
        this._forwardedHeader = forwardedHeader;
        this._enabled = true;
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        ThreadLimitHandler.LOG.info(String.format("ThreadLimitHandler enable=%b limit=%d include=%s", this._enabled, this._threadLimit, this._includeExcludeSet), new Object[0]);
    }
    
    @ManagedAttribute("true if this handler is enabled")
    public boolean isEnabled() {
        return this._enabled;
    }
    
    public void setEnabled(final boolean enabled) {
        this._enabled = enabled;
        ThreadLimitHandler.LOG.info(String.format("ThreadLimitHandler enable=%b limit=%d include=%s", this._enabled, this._threadLimit, this._includeExcludeSet), new Object[0]);
    }
    
    @ManagedAttribute("The maximum threads that can be dispatched per remote IP")
    public int getThreadLimit() {
        return this._threadLimit;
    }
    
    public void setThreadLimit(final int threadLimit) {
        if (threadLimit <= 0) {
            throw new IllegalArgumentException("limit must be >0");
        }
        this._threadLimit = threadLimit;
    }
    
    @ManagedOperation("Include IP in thread limits")
    public void include(final String inetAddressPattern) {
        this._includeExcludeSet.include(inetAddressPattern);
    }
    
    @ManagedOperation("Exclude IP from thread limits")
    public void exclude(final String inetAddressPattern) {
        this._includeExcludeSet.exclude(inetAddressPattern);
    }
    
    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        if (!this._enabled) {
            super.handle(target, baseRequest, request, response);
        }
        else {
            final Remote remote = this.getRemote(baseRequest);
            if (remote == null) {
                super.handle(target, baseRequest, request, response);
            }
            else {
                Closeable permit = (Closeable)baseRequest.getAttribute("o.e.j.s.h.TLH.PASS");
                try {
                    if (permit != null) {
                        baseRequest.removeAttribute("o.e.j.s.h.TLH.PASS");
                    }
                    else {
                        final CompletableFuture<Closeable> future_permit = remote.acquire();
                        if (!future_permit.isDone()) {
                            if (ThreadLimitHandler.LOG.isDebugEnabled()) {
                                ThreadLimitHandler.LOG.debug("Threadlimited {} {}", remote, target);
                            }
                            final AsyncContext async = baseRequest.startAsync();
                            async.setTimeout(0L);
                            final AsyncContext asyncContext;
                            future_permit.thenAccept(c -> {
                                baseRequest.setAttribute("o.e.j.s.h.TLH.PASS", c);
                                asyncContext.dispatch();
                            });
                            return;
                        }
                        permit = future_permit.get();
                    }
                    super.handle(target, baseRequest, request, response);
                }
                catch (InterruptedException ex) {}
                catch (ExecutionException e) {
                    throw new ServletException(e);
                }
                finally {
                    if (permit != null) {
                        permit.close();
                    }
                }
            }
        }
    }
    
    protected int getThreadLimit(final String ip) {
        if (!this._includeExcludeSet.isEmpty()) {
            try {
                if (!this._includeExcludeSet.test(InetAddress.getByName(ip))) {
                    ThreadLimitHandler.LOG.debug("excluded {}", ip);
                    return 0;
                }
            }
            catch (Exception e) {
                ThreadLimitHandler.LOG.ignore(e);
            }
        }
        return this._threadLimit;
    }
    
    protected Remote getRemote(final Request baseRequest) {
        Remote remote = (Remote)baseRequest.getAttribute("o.e.j.s.h.TLH.REMOTE");
        if (remote != null) {
            return remote;
        }
        final String ip = this.getRemoteIP(baseRequest);
        ThreadLimitHandler.LOG.debug("ip={}", ip);
        if (ip == null) {
            return null;
        }
        final int limit = this.getThreadLimit(ip);
        if (limit <= 0) {
            return null;
        }
        remote = this._remotes.get(ip);
        if (remote == null) {
            final Remote r = new Remote(ip, limit);
            remote = this._remotes.putIfAbsent(ip, r);
            if (remote == null) {
                remote = r;
            }
        }
        baseRequest.setAttribute("o.e.j.s.h.TLH.REMOTE", remote);
        return remote;
    }
    
    protected String getRemoteIP(final Request baseRequest) {
        if (this._forwardedHeader != null && !this._forwardedHeader.isEmpty()) {
            final String remote = this._rfc7239 ? this.getForwarded(baseRequest) : this.getXForwardedFor(baseRequest);
            if (remote != null && !remote.isEmpty()) {
                return remote;
            }
        }
        final InetSocketAddress inet_addr = baseRequest.getHttpChannel().getRemoteAddress();
        if (inet_addr != null && inet_addr.getAddress() != null) {
            return inet_addr.getAddress().getHostAddress();
        }
        return null;
    }
    
    private String getForwarded(final Request request) {
        final RFC7239 rfc7239 = new RFC7239();
        final HttpFields httpFields = request.getHttpFields();
        for (final HttpField field : httpFields) {
            if (this._forwardedHeader.equalsIgnoreCase(field.getName())) {
                rfc7239.addValue(field.getValue());
            }
        }
        if (rfc7239.getFor() != null) {
            return new HostPortHttpField(rfc7239.getFor()).getHost();
        }
        return null;
    }
    
    private String getXForwardedFor(final Request request) {
        String forwarded_for = null;
        final HttpFields httpFields = request.getHttpFields();
        for (final HttpField field : httpFields) {
            if (this._forwardedHeader.equalsIgnoreCase(field.getName())) {
                forwarded_for = field.getValue();
            }
        }
        if (forwarded_for == null || forwarded_for.isEmpty()) {
            return null;
        }
        final int comma = forwarded_for.lastIndexOf(44);
        return (comma >= 0) ? forwarded_for.substring(comma + 1).trim() : forwarded_for;
    }
    
    static {
        LOG = Log.getLogger(ThreadLimitHandler.class);
    }
    
    private final class Remote implements Closeable
    {
        private final String _ip;
        private final int _limit;
        private final Locker _locker;
        private int _permits;
        private Deque<CompletableFuture<Closeable>> _queue;
        private final CompletableFuture<Closeable> _permitted;
        
        public Remote(final String ip, final int limit) {
            this._locker = new Locker();
            this._queue = new ArrayDeque<CompletableFuture<Closeable>>();
            this._permitted = (CompletableFuture<Closeable>)CompletableFuture.completedFuture(this);
            this._ip = ip;
            this._limit = limit;
        }
        
        public CompletableFuture<Closeable> acquire() {
            final Locker.Lock lock = this._locker.lock();
            Throwable x0 = null;
            try {
                if (this._permits < this._limit) {
                    ++this._permits;
                    return this._permitted;
                }
                final CompletableFuture<Closeable> pass = new CompletableFuture<Closeable>();
                this._queue.addLast(pass);
                return pass;
            }
            catch (Throwable t) {
                x0 = t;
                throw t;
            }
            finally {
                if (lock != null) {
                    $closeResource(x0, lock);
                }
            }
        }
        
        @Override
        public void close() throws IOException {
            final Locker.Lock lock = this._locker.lock();
            Throwable x0 = null;
            try {
                --this._permits;
                while (true) {
                    final CompletableFuture<Closeable> permit = this._queue.pollFirst();
                    if (permit == null) {
                        break;
                    }
                    if (permit.complete(this)) {
                        ++this._permits;
                        break;
                    }
                }
            }
            catch (Throwable t) {
                x0 = t;
                throw t;
            }
            finally {
                if (lock != null) {
                    $closeResource(x0, lock);
                }
            }
        }
        
        @Override
        public String toString() {
            final Locker.Lock lock = this._locker.lock();
            Throwable x0 = null;
            try {
                return String.format("R[ip=%s,p=%d,l=%d,q=%d]", this._ip, this._permits, this._limit, this._queue.size());
            }
            catch (Throwable t) {
                x0 = t;
                throw t;
            }
            finally {
                if (lock != null) {
                    $closeResource(x0, lock);
                }
            }
        }
        
        private static /* synthetic */ void $closeResource(final Throwable x0, final AutoCloseable x1) {
            if (x0 != null) {
                try {
                    x1.close();
                }
                catch (Throwable exception) {
                    x0.addSuppressed(exception);
                }
            }
            else {
                x1.close();
            }
        }
    }
    
    private final class RFC7239 extends QuotedCSV
    {
        String _for;
        
        private RFC7239() {
            super(false, new String[0]);
        }
        
        String getFor() {
            return this._for;
        }
        
        @Override
        protected void parsedParam(final StringBuffer buffer, final int valueLength, final int paramName, final int paramValue) {
            if (valueLength == 0 && paramValue > paramName) {
                final String name = StringUtil.asciiToLowerCase(buffer.substring(paramName, paramValue - 1));
                if ("for".equalsIgnoreCase(name)) {
                    final String value = buffer.substring(paramValue);
                    if ("unknown".equalsIgnoreCase(value)) {
                        this._for = null;
                    }
                    else {
                        this._for = value;
                    }
                }
            }
        }
    }
}
