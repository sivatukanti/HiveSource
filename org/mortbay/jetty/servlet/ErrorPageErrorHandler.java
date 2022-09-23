// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.servlet;

import org.mortbay.jetty.handler.ContextHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;
import org.mortbay.log.Log;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.mortbay.util.TypeUtil;
import javax.servlet.ServletException;
import org.mortbay.jetty.HttpConnection;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.mortbay.jetty.handler.ErrorHandler;

public class ErrorPageErrorHandler extends ErrorHandler
{
    protected ServletContext _servletContext;
    protected Map _errorPages;
    protected List _errorPageList;
    
    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response, final int dispatch) throws IOException {
        final String method = request.getMethod();
        if (!method.equals("GET") && !method.equals("POST") && !method.equals("HEAD")) {
            HttpConnection.getCurrentConnection().getRequest().setHandled(true);
            return;
        }
        if (this._errorPages != null) {
            String error_page = null;
            Class exClass = (Class)request.getAttribute("javax.servlet.error.exception_type");
            if (ServletException.class.equals(exClass)) {
                error_page = this._errorPages.get(exClass.getName());
                if (error_page == null) {
                    Throwable th;
                    for (th = (Throwable)request.getAttribute("javax.servlet.error.exception"); th instanceof ServletException; th = ((ServletException)th).getRootCause()) {}
                    if (th != null) {
                        exClass = th.getClass();
                    }
                }
            }
            while (error_page == null && exClass != null) {
                error_page = this._errorPages.get(exClass.getName());
                exClass = exClass.getSuperclass();
            }
            if (error_page == null) {
                final Integer code = (Integer)request.getAttribute("javax.servlet.error.status_code");
                if (code != null) {
                    error_page = this._errorPages.get(TypeUtil.toString(code));
                    if (error_page == null && this._errorPageList != null) {
                        for (int i = 0; i < this._errorPageList.size(); ++i) {
                            final ErrorCodeRange errCode = this._errorPageList.get(i);
                            if (errCode.isInRange(code)) {
                                error_page = errCode.getUri();
                                break;
                            }
                        }
                    }
                }
            }
            if (error_page != null) {
                final String old_error_page = (String)request.getAttribute("org.mortbay.jetty.error_page");
                if (old_error_page == null || !old_error_page.equals(error_page)) {
                    request.setAttribute("org.mortbay.jetty.error_page", error_page);
                    final Dispatcher dispatcher = (Dispatcher)this._servletContext.getRequestDispatcher(error_page);
                    try {
                        if (dispatcher != null) {
                            dispatcher.error(request, response);
                            return;
                        }
                        Log.warn("No error page " + error_page);
                    }
                    catch (ServletException e) {
                        Log.warn("EXCEPTION ", e);
                        return;
                    }
                }
            }
        }
        super.handle(target, request, response, dispatch);
    }
    
    public Map getErrorPages() {
        return this._errorPages;
    }
    
    public void setErrorPages(final Map errorPages) {
        this._errorPages = errorPages;
    }
    
    public void addErrorPage(final Class exception, final String uri) {
        if (this._errorPages == null) {
            this._errorPages = new HashMap();
        }
        this._errorPages.put(exception.getName(), uri);
    }
    
    public void addErrorPage(final int code, final String uri) {
        if (this._errorPages == null) {
            this._errorPages = new HashMap();
        }
        this._errorPages.put(TypeUtil.toString(code), uri);
    }
    
    public void addErrorPage(final int from, final int to, final String uri) {
        if (this._errorPageList == null) {
            this._errorPageList = new ArrayList();
        }
        this._errorPageList.add(new ErrorCodeRange(from, to, uri));
    }
    
    protected void doStart() throws Exception {
        super.doStart();
        this._servletContext = ContextHandler.getCurrentContext();
    }
    
    protected void doStop() throws Exception {
        super.doStop();
    }
    
    private class ErrorCodeRange
    {
        private int _from;
        private int _to;
        private String _uri;
        
        ErrorCodeRange(final int from, final int to, final String uri) throws IllegalArgumentException {
            if (from > to) {
                throw new IllegalArgumentException("from>to");
            }
            this._from = from;
            this._to = to;
            this._uri = uri;
        }
        
        boolean isInRange(final int value) {
            return value >= this._from && value <= this._to;
        }
        
        String getUri() {
            return this._uri;
        }
        
        public String toString() {
            return "from: " + this._from + ",to: " + this._to + ",uri: " + this._uri;
        }
    }
}
