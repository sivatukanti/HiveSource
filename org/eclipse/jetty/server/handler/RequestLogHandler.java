// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;

public class RequestLogHandler extends HandlerWrapper
{
    private RequestLog _requestLog;
    
    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        if (baseRequest.getDispatcherType() == DispatcherType.REQUEST) {
            baseRequest.getHttpChannel().addRequestLog(this._requestLog);
        }
        if (this._handler != null) {
            this._handler.handle(target, baseRequest, request, response);
        }
    }
    
    public void setRequestLog(final RequestLog requestLog) {
        this.updateBean(this._requestLog, requestLog);
        this._requestLog = requestLog;
    }
    
    public RequestLog getRequestLog() {
        return this._requestLog;
    }
}
