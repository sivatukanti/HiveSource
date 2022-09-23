// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import org.eclipse.jetty.util.log.Log;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.OutputStream;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ByteArrayISO8859Writer;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;
import java.net.URL;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.log.Logger;

public class DefaultHandler extends AbstractHandler
{
    private static final Logger LOG;
    final long _faviconModified;
    byte[] _favicon;
    boolean _serveIcon;
    boolean _showContexts;
    
    public DefaultHandler() {
        this._faviconModified = System.currentTimeMillis() / 1000L * 1000L;
        this._serveIcon = true;
        this._showContexts = true;
        try {
            final URL fav = this.getClass().getClassLoader().getResource("org/eclipse/jetty/favicon.ico");
            if (fav != null) {
                final Resource r = Resource.newResource(fav);
                this._favicon = IO.readBytes(r.getInputStream());
            }
        }
        catch (Exception e) {
            DefaultHandler.LOG.warn(e);
        }
    }
    
    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        if (response.isCommitted() || baseRequest.isHandled()) {
            return;
        }
        baseRequest.setHandled(true);
        final String method = request.getMethod();
        if (this._serveIcon && this._favicon != null && HttpMethod.GET.is(method) && request.getRequestURI().equals("/favicon.ico")) {
            if (request.getDateHeader(HttpHeader.IF_MODIFIED_SINCE.toString()) == this._faviconModified) {
                response.setStatus(304);
            }
            else {
                response.setStatus(200);
                response.setContentType("image/x-icon");
                response.setContentLength(this._favicon.length);
                response.setDateHeader(HttpHeader.LAST_MODIFIED.toString(), this._faviconModified);
                response.setHeader(HttpHeader.CACHE_CONTROL.toString(), "max-age=360000,public");
                response.getOutputStream().write(this._favicon);
            }
            return;
        }
        if (!this._showContexts || !HttpMethod.GET.is(method) || !request.getRequestURI().equals("/")) {
            response.sendError(404);
            return;
        }
        response.setStatus(404);
        response.setContentType(MimeTypes.Type.TEXT_HTML.toString());
        final ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer(1500);
        Throwable x0 = null;
        try {
            writer.write("<HTML>\n<HEAD>\n<TITLE>Error 404 - Not Found");
            writer.write("</TITLE>\n<BODY>\n<H2>Error 404 - Not Found.</H2>\n");
            writer.write("No context on this server matched or handled this request.<BR>");
            writer.write("Contexts known to this server are: <ul>");
            final Server server = this.getServer();
            final Handler[] handlers = (Handler[])((server == null) ? null : server.getChildHandlersByClass(ContextHandler.class));
            for (int i = 0; handlers != null && i < handlers.length; ++i) {
                final ContextHandler context = (ContextHandler)handlers[i];
                if (context.isRunning()) {
                    writer.write("<li><a href=\"");
                    if (context.getVirtualHosts() != null && context.getVirtualHosts().length > 0) {
                        writer.write(request.getScheme() + "://" + context.getVirtualHosts()[0] + ":" + request.getLocalPort());
                    }
                    writer.write(context.getContextPath());
                    if (context.getContextPath().length() > 1 && context.getContextPath().endsWith("/")) {
                        writer.write("/");
                    }
                    writer.write("\">");
                    writer.write(context.getContextPath());
                    if (context.getVirtualHosts() != null && context.getVirtualHosts().length > 0) {
                        writer.write("&nbsp;@&nbsp;" + context.getVirtualHosts()[0] + ":" + request.getLocalPort());
                    }
                    writer.write("&nbsp;--->&nbsp;");
                    writer.write(context.toString());
                    writer.write("</a></li>\n");
                }
                else {
                    writer.write("<li>");
                    writer.write(context.getContextPath());
                    if (context.getVirtualHosts() != null && context.getVirtualHosts().length > 0) {
                        writer.write("&nbsp;@&nbsp;" + context.getVirtualHosts()[0] + ":" + request.getLocalPort());
                    }
                    writer.write("&nbsp;--->&nbsp;");
                    writer.write(context.toString());
                    if (context.isFailed()) {
                        writer.write(" [failed]");
                    }
                    if (context.isStopped()) {
                        writer.write(" [stopped]");
                    }
                    writer.write("</li>\n");
                }
            }
            writer.write("</ul><hr>");
            baseRequest.getHttpChannel().getHttpConfiguration().writePoweredBy(writer, "<a href=\"http://eclipse.org/jetty\"><img border=0 src=\"/favicon.ico\"/></a>&nbsp;", "<hr/>\n");
            writer.write("\n</BODY>\n</HTML>\n");
            writer.flush();
            response.setContentLength(writer.size());
            final OutputStream out = response.getOutputStream();
            Throwable x2 = null;
            try {
                writer.writeTo(out);
            }
            catch (Throwable t) {
                x2 = t;
                throw t;
            }
            finally {
                if (out != null) {
                    $closeResource(x2, out);
                }
            }
        }
        catch (Throwable t2) {
            x0 = t2;
            throw t2;
        }
        finally {
            $closeResource(x0, writer);
        }
    }
    
    public boolean getServeIcon() {
        return this._serveIcon;
    }
    
    public void setServeIcon(final boolean serveIcon) {
        this._serveIcon = serveIcon;
    }
    
    public boolean getShowContexts() {
        return this._showContexts;
    }
    
    public void setShowContexts(final boolean show) {
        this._showContexts = show;
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
    
    static {
        LOG = Log.getLogger(DefaultHandler.class);
    }
}
