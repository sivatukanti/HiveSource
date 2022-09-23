// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.AbstractConnector;
import org.eclipse.jetty.util.RolloverFileOutputStream;
import org.eclipse.jetty.server.Response;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;
import java.util.Locale;
import java.io.PrintStream;
import java.io.OutputStream;
import org.eclipse.jetty.util.DateCache;
import org.eclipse.jetty.io.Connection;

public class DebugHandler extends HandlerWrapper implements Connection.Listener
{
    private DateCache _date;
    private OutputStream _out;
    private PrintStream _print;
    
    public DebugHandler() {
        this._date = new DateCache("HH:mm:ss", Locale.US);
    }
    
    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final Response base_response = baseRequest.getResponse();
        final Thread thread = Thread.currentThread();
        final String old_name = thread.getName();
        boolean suspend = false;
        boolean retry = false;
        String name = (String)request.getAttribute("org.eclipse.jetty.thread.name");
        if (name == null) {
            name = old_name + ":" + baseRequest.getHttpURI();
        }
        else {
            retry = true;
        }
        String ex = null;
        try {
            if (retry) {
                this.print(name, "RESUME");
            }
            else {
                this.print(name, "REQUEST " + baseRequest.getRemoteAddr() + " " + request.getMethod() + " " + baseRequest.getHeader("Cookie") + "; " + baseRequest.getHeader("User-Agent"));
            }
            thread.setName(name);
            this.getHandler().handle(target, baseRequest, request, response);
        }
        catch (IOException ioe) {
            ex = ioe.toString();
            throw ioe;
        }
        catch (ServletException se) {
            ex = se.toString() + ":" + se.getCause();
            throw se;
        }
        catch (RuntimeException rte) {
            ex = rte.toString();
            throw rte;
        }
        catch (Error e) {
            ex = e.toString();
            throw e;
        }
        finally {
            thread.setName(old_name);
            suspend = baseRequest.getHttpChannelState().isSuspended();
            if (suspend) {
                request.setAttribute("org.eclipse.jetty.thread.name", name);
                this.print(name, "SUSPEND");
            }
            else {
                this.print(name, "RESPONSE " + base_response.getStatus() + ((ex == null) ? "" : ("/" + ex)) + " " + base_response.getContentType());
            }
        }
    }
    
    private void print(final String name, final String message) {
        final long now = System.currentTimeMillis();
        final String d = this._date.formatNow(now);
        final int ms = (int)(now % 1000L);
        this._print.println(d + ((ms > 99) ? "." : ((ms > 9) ? ".0" : ".00")) + ms + ":" + name + " " + message);
    }
    
    @Override
    protected void doStart() throws Exception {
        if (this._out == null) {
            this._out = new RolloverFileOutputStream("./logs/yyyy_mm_dd.debug.log", true);
        }
        this._print = new PrintStream(this._out);
        for (final Connector connector : this.getServer().getConnectors()) {
            if (connector instanceof AbstractConnector) {
                ((AbstractConnector)connector).addBean(this, false);
            }
        }
        super.doStart();
    }
    
    @Override
    protected void doStop() throws Exception {
        super.doStop();
        this._print.close();
        for (final Connector connector : this.getServer().getConnectors()) {
            if (connector instanceof AbstractConnector) {
                ((AbstractConnector)connector).removeBean(this);
            }
        }
    }
    
    public OutputStream getOutputStream() {
        return this._out;
    }
    
    public void setOutputStream(final OutputStream out) {
        this._out = out;
    }
    
    @Override
    public void onOpened(final Connection connection) {
        this.print(Thread.currentThread().getName(), "OPENED " + connection.toString());
    }
    
    @Override
    public void onClosed(final Connection connection) {
        this.print(Thread.currentThread().getName(), "CLOSED " + connection.toString());
    }
}
