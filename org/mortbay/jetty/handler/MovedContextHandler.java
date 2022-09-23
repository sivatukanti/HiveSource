// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.handler;

import javax.servlet.ServletException;
import java.io.IOException;
import org.mortbay.util.URIUtil;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.mortbay.jetty.HandlerContainer;
import org.mortbay.jetty.Handler;

public class MovedContextHandler extends ContextHandler
{
    String _newContextURL;
    boolean _discardPathInfo;
    boolean _discardQuery;
    boolean _permanent;
    Redirector _redirector;
    
    public MovedContextHandler() {
        this.addHandler(this._redirector = new Redirector());
    }
    
    public MovedContextHandler(final HandlerContainer parent, final String contextPath, final String newContextURL) {
        super(parent, contextPath);
        this._newContextURL = newContextURL;
        this.addHandler(this._redirector = new Redirector());
    }
    
    public boolean isDiscardPathInfo() {
        return this._discardPathInfo;
    }
    
    public void setDiscardPathInfo(final boolean discardPathInfo) {
        this._discardPathInfo = discardPathInfo;
    }
    
    public String getNewContextURL() {
        return this._newContextURL;
    }
    
    public void setNewContextURL(final String newContextURL) {
        this._newContextURL = newContextURL;
    }
    
    public boolean isPermanent() {
        return this._permanent;
    }
    
    public void setPermanent(final boolean permanent) {
        this._permanent = permanent;
    }
    
    public boolean isDiscardQuery() {
        return this._discardQuery;
    }
    
    public void setDiscardQuery(final boolean discardQuery) {
        this._discardQuery = discardQuery;
    }
    
    private class Redirector extends AbstractHandler
    {
        public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response, final int dispatch) throws IOException, ServletException {
            if (MovedContextHandler.this._newContextURL == null) {
                return;
            }
            final Request base_request = (Request)((request instanceof Request) ? request : HttpConnection.getCurrentConnection().getRequest());
            String url = MovedContextHandler.this._newContextURL;
            if (!MovedContextHandler.this._discardPathInfo && request.getPathInfo() != null) {
                url = URIUtil.addPaths(url, request.getPathInfo());
            }
            if (!MovedContextHandler.this._discardQuery && request.getQueryString() != null) {
                url = url + "?" + request.getQueryString();
            }
            response.sendRedirect(url);
            if (MovedContextHandler.this._permanent) {
                response.setStatus(301);
            }
            base_request.setHandled(true);
        }
    }
}
