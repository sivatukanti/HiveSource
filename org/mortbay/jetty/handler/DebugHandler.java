// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.handler;

import org.mortbay.util.RolloverFileOutputStream;
import javax.servlet.ServletException;
import java.io.IOException;
import org.mortbay.jetty.RetryRequest;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.Request;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.io.PrintStream;
import java.io.OutputStream;
import org.mortbay.util.DateCache;

public class DebugHandler extends HandlerWrapper
{
    private DateCache _date;
    private OutputStream _out;
    private PrintStream _print;
    
    public DebugHandler() {
        this._date = new DateCache("HH:mm:ss", Locale.US);
    }
    
    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response, final int dispatch) throws IOException, ServletException {
        final Request srequest = (Request)request;
        final Response sresponse = (Response)response;
        final Thread thread = Thread.currentThread();
        final String old_name = thread.getName();
        boolean suspend = false;
        boolean retry = false;
        String name = (String)request.getAttribute("org.mortbay.jetty.thread.name");
        if (name == null) {
            name = old_name + "://" + srequest.getHeader("Host") + srequest.getUri();
        }
        else {
            retry = true;
        }
        String ex = null;
        try {
            final String d = this._date.now();
            final int ms = this._date.lastMs();
            if (retry) {
                this._print.println(d + ((ms > 99) ? "." : ((ms > 9) ? ".0" : ".00")) + ms + ":" + name + " RETRY");
            }
            else {
                this._print.println(d + ((ms > 99) ? "." : ((ms > 9) ? ".0" : ".00")) + ms + ":" + name + " " + srequest.getRemoteAddr() + " " + request.getMethod() + " " + srequest.getHeader("Cookie") + "; " + srequest.getHeader("User-Agent"));
            }
            thread.setName(name);
            super.handle(target, request, response, dispatch);
        }
        catch (RetryRequest r) {
            suspend = true;
            request.setAttribute("org.mortbay.jetty.thread.name", name);
            throw r;
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
            final String d2 = this._date.now();
            final int ms2 = this._date.lastMs();
            if (suspend) {
                this._print.println(d2 + ((ms2 > 99) ? "." : ((ms2 > 9) ? ".0" : ".00")) + ms2 + ":" + name + " SUSPEND");
            }
            else {
                this._print.println(d2 + ((ms2 > 99) ? "." : ((ms2 > 9) ? ".0" : ".00")) + ms2 + ":" + name + " " + sresponse.getStatus() + " " + sresponse.getContentType() + " " + sresponse.getContentCount() + ((ex == null) ? "" : ("/" + ex)));
            }
        }
    }
    
    protected void doStart() throws Exception {
        if (this._out == null) {
            this._out = new RolloverFileOutputStream("./logs/yyyy_mm_dd.debug.log", true);
        }
        this._print = new PrintStream(this._out);
        super.doStart();
    }
    
    protected void doStop() throws Exception {
        super.doStop();
        this._print.close();
    }
    
    public OutputStream getOutputStream() {
        return this._out;
    }
    
    public void setOutputStream(final OutputStream out) {
        this._out = out;
    }
}
