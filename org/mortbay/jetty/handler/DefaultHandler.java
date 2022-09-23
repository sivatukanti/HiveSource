// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.handler;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.OutputStream;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.util.StringUtil;
import org.mortbay.util.ByteArrayISO8859Writer;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import org.mortbay.log.Log;
import org.mortbay.util.IO;

public class DefaultHandler extends AbstractHandler
{
    long _faviconModified;
    byte[] _favicon;
    boolean _serveIcon;
    
    public DefaultHandler() {
        this._faviconModified = System.currentTimeMillis() / 1000L * 1000L;
        this._serveIcon = true;
        try {
            final URL fav = this.getClass().getClassLoader().getResource("org/mortbay/jetty/favicon.ico");
            if (fav != null) {
                this._favicon = IO.readBytes(fav.openStream());
            }
        }
        catch (Exception e) {
            Log.warn(e);
        }
    }
    
    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response, final int dispatch) throws IOException, ServletException {
        final Request base_request = (Request)((request instanceof Request) ? request : HttpConnection.getCurrentConnection().getRequest());
        if (response.isCommitted() || base_request.isHandled()) {
            return;
        }
        base_request.setHandled(true);
        final String method = request.getMethod();
        if (this._serveIcon && this._favicon != null && method.equals("GET") && request.getRequestURI().equals("/favicon.ico")) {
            if (request.getDateHeader("If-Modified-Since") == this._faviconModified) {
                response.setStatus(304);
            }
            else {
                response.setStatus(200);
                response.setContentType("image/x-icon");
                response.setContentLength(this._favicon.length);
                response.setDateHeader("Last-Modified", this._faviconModified);
                response.getOutputStream().write(this._favicon);
            }
            return;
        }
        if (!method.equals("GET") || !request.getRequestURI().equals("/")) {
            response.sendError(404);
            return;
        }
        response.setStatus(404);
        response.setContentType("text/html");
        final ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer(1500);
        String uri = request.getRequestURI();
        uri = StringUtil.replace(uri, "<", "&lt;");
        uri = StringUtil.replace(uri, ">", "&gt;");
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
                    writer.write("http://" + context.getVirtualHosts()[0] + ":" + request.getLocalPort());
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
        for (int i = 0; i < 10; ++i) {
            writer.write("\n<!-- Padding for IE                  -->");
        }
        writer.write("\n</BODY>\n</HTML>\n");
        writer.flush();
        response.setContentLength(writer.size());
        final OutputStream out = response.getOutputStream();
        writer.writeTo(out);
        out.close();
    }
    
    public boolean getServeIcon() {
        return this._serveIcon;
    }
    
    public void setServeIcon(final boolean serveIcon) {
        this._serveIcon = serveIcon;
    }
}
