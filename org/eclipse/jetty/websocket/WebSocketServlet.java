// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import javax.servlet.http.HttpServlet;

public abstract class WebSocketServlet extends HttpServlet implements WebSocketFactory.Acceptor
{
    private final Logger LOG;
    private WebSocketFactory _webSocketFactory;
    
    public WebSocketServlet() {
        this.LOG = Log.getLogger(this.getClass());
    }
    
    @Override
    public void init() throws ServletException {
        try {
            final String bs = this.getInitParameter("bufferSize");
            (this._webSocketFactory = new WebSocketFactory(this, (bs == null) ? 8192 : Integer.parseInt(bs))).start();
            String max = this.getInitParameter("maxIdleTime");
            if (max != null) {
                this._webSocketFactory.setMaxIdleTime(Integer.parseInt(max));
            }
            max = this.getInitParameter("maxTextMessageSize");
            if (max != null) {
                this._webSocketFactory.setMaxTextMessageSize(Integer.parseInt(max));
            }
            max = this.getInitParameter("maxBinaryMessageSize");
            if (max != null) {
                this._webSocketFactory.setMaxBinaryMessageSize(Integer.parseInt(max));
            }
        }
        catch (ServletException x) {
            throw x;
        }
        catch (Exception x2) {
            throw new ServletException(x2);
        }
    }
    
    @Override
    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (this._webSocketFactory.acceptWebSocket(request, response) || response.isCommitted()) {
            return;
        }
        super.service(request, response);
    }
    
    public boolean checkOrigin(final HttpServletRequest request, final String origin) {
        return true;
    }
    
    @Override
    public void destroy() {
        try {
            this._webSocketFactory.stop();
        }
        catch (Exception x) {
            this.LOG.ignore(x);
        }
    }
}
