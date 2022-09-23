// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.util.Locale;
import org.eclipse.jetty.util.log.Log;
import java.util.EventListener;
import javax.servlet.ServletContextEvent;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import javax.servlet.ServletContext;
import javax.servlet.DispatcherType;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletRequestEvent;
import java.io.IOException;
import javax.servlet.AsyncEvent;
import org.eclipse.jetty.util.annotation.Name;
import java.io.OutputStream;
import org.eclipse.jetty.server.handler.ContextHandler;
import javax.servlet.ServletRequestListener;
import javax.servlet.AsyncListener;
import java.io.PrintStream;
import org.eclipse.jetty.util.DateCache;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.annotation.ManagedObject;
import javax.servlet.ServletContextListener;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

@ManagedObject("Debug Listener")
public class DebugListener extends AbstractLifeCycle implements ServletContextListener
{
    private static final Logger LOG;
    private static final DateCache __date;
    private final String _attr;
    private final PrintStream _out;
    private boolean _renameThread;
    private boolean _showHeaders;
    private boolean _dumpContext;
    final AsyncListener _asyncListener;
    final ServletRequestListener _servletRequestListener;
    final ContextHandler.ContextScopeListener _contextScopeListener;
    
    public DebugListener() {
        this(null, false, false, false);
    }
    
    public DebugListener(@Name("renameThread") final boolean renameThread, @Name("showHeaders") final boolean showHeaders, @Name("dumpContext") final boolean dumpContext) {
        this(null, renameThread, showHeaders, dumpContext);
    }
    
    public DebugListener(@Name("outputStream") final OutputStream out, @Name("renameThread") final boolean renameThread, @Name("showHeaders") final boolean showHeaders, @Name("dumpContext") final boolean dumpContext) {
        this._attr = String.format("__R%s@%x", this.getClass().getSimpleName(), System.identityHashCode(this));
        this._asyncListener = new AsyncListener() {
            @Override
            public void onTimeout(final AsyncEvent event) throws IOException {
                final String cname = DebugListener.this.findContextName(((AsyncContextEvent)event).getServletContext());
                final String rname = DebugListener.this.findRequestName(event.getAsyncContext().getRequest());
                DebugListener.this.log("!  ctx=%s r=%s onTimeout %s", cname, rname, ((AsyncContextEvent)event).getHttpChannelState());
            }
            
            @Override
            public void onStartAsync(final AsyncEvent event) throws IOException {
                final String cname = DebugListener.this.findContextName(((AsyncContextEvent)event).getServletContext());
                final String rname = DebugListener.this.findRequestName(event.getAsyncContext().getRequest());
                DebugListener.this.log("!  ctx=%s r=%s onStartAsync %s", cname, rname, ((AsyncContextEvent)event).getHttpChannelState());
            }
            
            @Override
            public void onError(final AsyncEvent event) throws IOException {
                final String cname = DebugListener.this.findContextName(((AsyncContextEvent)event).getServletContext());
                final String rname = DebugListener.this.findRequestName(event.getAsyncContext().getRequest());
                DebugListener.this.log("!! ctx=%s r=%s onError %s %s", cname, rname, event.getThrowable(), ((AsyncContextEvent)event).getHttpChannelState());
            }
            
            @Override
            public void onComplete(final AsyncEvent event) throws IOException {
                final AsyncContextEvent ace = (AsyncContextEvent)event;
                final String cname = DebugListener.this.findContextName(ace.getServletContext());
                final String rname = DebugListener.this.findRequestName(ace.getAsyncContext().getRequest());
                final Request br = Request.getBaseRequest(ace.getAsyncContext().getRequest());
                final Response response = br.getResponse();
                final String headers = DebugListener.this._showHeaders ? ("\n" + response.getHttpFields().toString()) : "";
                DebugListener.this.log("!  ctx=%s r=%s onComplete %s %d%s", cname, rname, ace.getHttpChannelState(), response.getStatus(), headers);
            }
        };
        this._servletRequestListener = new ServletRequestListener() {
            @Override
            public void requestInitialized(final ServletRequestEvent sre) {
                final String cname = DebugListener.this.findContextName(sre.getServletContext());
                final HttpServletRequest r = (HttpServletRequest)sre.getServletRequest();
                final String rname = DebugListener.this.findRequestName(r);
                final DispatcherType d = r.getDispatcherType();
                if (d == DispatcherType.REQUEST) {
                    final Request br = Request.getBaseRequest(r);
                    final String headers = DebugListener.this._showHeaders ? ("\n" + br.getMetaData().getFields().toString()) : "";
                    final StringBuffer url = r.getRequestURL();
                    if (r.getQueryString() != null) {
                        url.append('?').append(r.getQueryString());
                    }
                    DebugListener.this.log(">> %s ctx=%s r=%s %s %s %s %s %s%s", d, cname, rname, d, r.getMethod(), url.toString(), r.getProtocol(), br.getHttpChannel(), headers);
                }
                else {
                    DebugListener.this.log(">> %s ctx=%s r=%s", d, cname, rname);
                }
            }
            
            @Override
            public void requestDestroyed(final ServletRequestEvent sre) {
                final String cname = DebugListener.this.findContextName(sre.getServletContext());
                final HttpServletRequest r = (HttpServletRequest)sre.getServletRequest();
                final String rname = DebugListener.this.findRequestName(r);
                final DispatcherType d = r.getDispatcherType();
                if (sre.getServletRequest().isAsyncStarted()) {
                    sre.getServletRequest().getAsyncContext().addListener(DebugListener.this._asyncListener);
                    DebugListener.this.log("<< %s ctx=%s r=%s async=true", d, cname, rname);
                }
                else {
                    final Request br = Request.getBaseRequest(r);
                    final String headers = DebugListener.this._showHeaders ? ("\n" + br.getResponse().getHttpFields().toString()) : "";
                    DebugListener.this.log("<< %s ctx=%s r=%s async=false %d%s", d, cname, rname, Request.getBaseRequest(r).getResponse().getStatus(), headers);
                }
            }
        };
        this._contextScopeListener = new ContextHandler.ContextScopeListener() {
            @Override
            public void enterScope(final ContextHandler.Context context, final Request request, final Object reason) {
                final String cname = DebugListener.this.findContextName(context);
                if (request == null) {
                    DebugListener.this.log(">  ctx=%s %s", cname, reason);
                }
                else {
                    final String rname = DebugListener.this.findRequestName(request);
                    if (DebugListener.this._renameThread) {
                        final Thread thread = Thread.currentThread();
                        thread.setName(String.format("%s#%s", thread.getName(), rname));
                    }
                    DebugListener.this.log(">  ctx=%s r=%s %s", cname, rname, reason);
                }
            }
            
            @Override
            public void exitScope(final ContextHandler.Context context, final Request request) {
                final String cname = DebugListener.this.findContextName(context);
                if (request == null) {
                    DebugListener.this.log("<  ctx=%s", cname);
                }
                else {
                    final String rname = DebugListener.this.findRequestName(request);
                    DebugListener.this.log("<  ctx=%s r=%s", cname, rname);
                    if (DebugListener.this._renameThread) {
                        final Thread thread = Thread.currentThread();
                        if (thread.getName().endsWith(rname)) {
                            thread.setName(thread.getName().substring(0, thread.getName().length() - rname.length() - 1));
                        }
                    }
                }
            }
        };
        this._out = ((out == null) ? null : new PrintStream(out));
        this._renameThread = renameThread;
        this._showHeaders = showHeaders;
        this._dumpContext = dumpContext;
    }
    
    @ManagedAttribute("Rename thread within context scope")
    public boolean isRenameThread() {
        return this._renameThread;
    }
    
    public void setRenameThread(final boolean renameThread) {
        this._renameThread = renameThread;
    }
    
    @ManagedAttribute("Show request headers")
    public boolean isShowHeaders() {
        return this._showHeaders;
    }
    
    public void setShowHeaders(final boolean showHeaders) {
        this._showHeaders = showHeaders;
    }
    
    @ManagedAttribute("Dump contexts at start")
    public boolean isDumpContext() {
        return this._dumpContext;
    }
    
    public void setDumpContext(final boolean dumpContext) {
        this._dumpContext = dumpContext;
    }
    
    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        sce.getServletContext().addListener(this._servletRequestListener);
        final ContextHandler handler = ContextHandler.getContextHandler(sce.getServletContext());
        handler.addEventListener(this._contextScopeListener);
        final String cname = this.findContextName(sce.getServletContext());
        this.log("^  ctx=%s %s", cname, sce.getServletContext());
        if (this._dumpContext) {
            if (this._out == null) {
                handler.dumpStdErr();
            }
            else {
                try {
                    handler.dump(this._out);
                }
                catch (Exception e) {
                    DebugListener.LOG.warn(e);
                }
            }
        }
    }
    
    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
        final String cname = this.findContextName(sce.getServletContext());
        this.log("v  ctx=%s %s", cname, sce.getServletContext());
    }
    
    protected String findContextName(final ServletContext context) {
        if (context == null) {
            return null;
        }
        String n = (String)context.getAttribute(this._attr);
        if (n == null) {
            n = String.format("%s@%x", context.getContextPath(), context.hashCode());
            context.setAttribute(this._attr, n);
        }
        return n;
    }
    
    protected String findRequestName(final ServletRequest request) {
        if (request == null) {
            return null;
        }
        final HttpServletRequest r = (HttpServletRequest)request;
        String n = (String)request.getAttribute(this._attr);
        if (n == null) {
            n = String.format("%s@%x", r.getRequestURI(), request.hashCode());
            request.setAttribute(this._attr, n);
        }
        return n;
    }
    
    protected void log(final String format, final Object... arg) {
        if (!this.isRunning()) {
            return;
        }
        final String s = String.format(format, arg);
        final long now = System.currentTimeMillis();
        final long ms = now % 1000L;
        if (this._out != null) {
            this._out.printf("%s.%03d:%s%n", DebugListener.__date.formatNow(now), ms, s);
        }
        if (DebugListener.LOG.isDebugEnabled()) {
            DebugListener.LOG.info(s, new Object[0]);
        }
    }
    
    static {
        LOG = Log.getLogger(DebugListener.class);
        __date = new DateCache("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    }
}
