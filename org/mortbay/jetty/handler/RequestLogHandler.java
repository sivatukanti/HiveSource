// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.handler;

import org.mortbay.jetty.Server;
import org.mortbay.log.Log;
import javax.servlet.ServletException;
import java.io.IOException;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.Request;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.mortbay.jetty.RequestLog;

public class RequestLogHandler extends HandlerWrapper
{
    private RequestLog _requestLog;
    
    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response, final int dispatch) throws IOException, ServletException {
        super.handle(target, request, response, dispatch);
        if (dispatch == 1 && this._requestLog != null) {
            this._requestLog.log((Request)request, (Response)response);
        }
    }
    
    public void setRequestLog(final RequestLog requestLog) {
        try {
            if (this._requestLog != null) {
                this._requestLog.stop();
            }
        }
        catch (Exception e) {
            Log.warn(e);
        }
        if (this.getServer() != null) {
            this.getServer().getContainer().update(this, this._requestLog, requestLog, "logimpl", true);
        }
        this._requestLog = requestLog;
        try {
            if (this.isStarted() && this._requestLog != null) {
                this._requestLog.start();
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setServer(final Server server) {
        if (this._requestLog != null) {
            if (this.getServer() != null && this.getServer() != server) {
                this.getServer().getContainer().update(this, this._requestLog, null, "logimpl", true);
            }
            super.setServer(server);
            if (server != null && server != this.getServer()) {
                server.getContainer().update(this, null, this._requestLog, "logimpl", true);
            }
        }
        else {
            super.setServer(server);
        }
    }
    
    public RequestLog getRequestLog() {
        return this._requestLog;
    }
    
    protected void doStart() throws Exception {
        super.doStart();
        if (this._requestLog != null) {
            this._requestLog.start();
        }
    }
    
    protected void doStop() throws Exception {
        super.doStop();
        if (this._requestLog != null) {
            this._requestLog.stop();
        }
    }
}
