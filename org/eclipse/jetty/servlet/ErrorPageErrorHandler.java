// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlet;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.server.handler.ContextHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.server.handler.ErrorHandler;

public class ErrorPageErrorHandler extends ErrorHandler implements ErrorPageMapper
{
    public static final String GLOBAL_ERROR_PAGE = "org.eclipse.jetty.server.error_page.global";
    private static final Logger LOG;
    protected ServletContext _servletContext;
    private final Map<String, String> _errorPages;
    private final List<ErrorCodeRange> _errorPageList;
    
    public ErrorPageErrorHandler() {
        this._errorPages = new HashMap<String, String>();
        this._errorPageList = new ArrayList<ErrorCodeRange>();
    }
    
    @Override
    public String getErrorPage(final HttpServletRequest request) {
        String error_page = null;
        PageLookupTechnique pageSource = null;
        Class<?> matchedThrowable = null;
        Throwable th;
        for (th = (Throwable)request.getAttribute("javax.servlet.error.exception"); error_page == null && th != null; th = ((th instanceof ServletException) ? ((ServletException)th).getRootCause() : null)) {
            pageSource = PageLookupTechnique.THROWABLE;
            Class<?> exClass;
            for (exClass = th.getClass(), error_page = this._errorPages.get(exClass.getName()); error_page == null; error_page = this._errorPages.get(exClass.getName())) {
                exClass = exClass.getSuperclass();
                if (exClass == null) {
                    break;
                }
            }
            if (error_page != null) {
                matchedThrowable = exClass;
            }
        }
        Integer errorStatusCode = null;
        if (error_page == null) {
            pageSource = PageLookupTechnique.STATUS_CODE;
            errorStatusCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
            if (errorStatusCode != null) {
                error_page = this._errorPages.get(Integer.toString(errorStatusCode));
                if (error_page == null && this._errorPageList != null) {
                    for (int i = 0; i < this._errorPageList.size(); ++i) {
                        final ErrorCodeRange errCode = this._errorPageList.get(i);
                        if (errCode.isInRange(errorStatusCode)) {
                            error_page = errCode.getUri();
                            break;
                        }
                    }
                }
            }
        }
        if (error_page == null) {
            pageSource = PageLookupTechnique.GLOBAL;
            error_page = this._errorPages.get("org.eclipse.jetty.server.error_page.global");
        }
        if (ErrorPageErrorHandler.LOG.isDebugEnabled()) {
            final StringBuilder dbg = new StringBuilder();
            dbg.append("getErrorPage(");
            dbg.append(request.getMethod()).append(' ');
            dbg.append(request.getRequestURI());
            dbg.append(") => error_page=").append(error_page);
            switch (pageSource) {
                case THROWABLE: {
                    dbg.append(" (using matched Throwable ");
                    dbg.append(matchedThrowable.getName());
                    dbg.append(" / actually thrown as ");
                    final Throwable originalThrowable = (Throwable)request.getAttribute("javax.servlet.error.exception");
                    dbg.append(originalThrowable.getClass().getName());
                    dbg.append(')');
                    ErrorPageErrorHandler.LOG.debug(dbg.toString(), th);
                    break;
                }
                case STATUS_CODE: {
                    dbg.append(" (from status code ");
                    dbg.append(errorStatusCode);
                    dbg.append(')');
                    ErrorPageErrorHandler.LOG.debug(dbg.toString(), new Object[0]);
                    break;
                }
                case GLOBAL: {
                    dbg.append(" (from global default)");
                    ErrorPageErrorHandler.LOG.debug(dbg.toString(), new Object[0]);
                    break;
                }
            }
        }
        return error_page;
    }
    
    public Map<String, String> getErrorPages() {
        return this._errorPages;
    }
    
    public void setErrorPages(final Map<String, String> errorPages) {
        this._errorPages.clear();
        if (errorPages != null) {
            this._errorPages.putAll(errorPages);
        }
    }
    
    public void addErrorPage(final Class<? extends Throwable> exception, final String uri) {
        this._errorPages.put(exception.getName(), uri);
    }
    
    public void addErrorPage(final String exceptionClassName, final String uri) {
        this._errorPages.put(exceptionClassName, uri);
    }
    
    public void addErrorPage(final int code, final String uri) {
        this._errorPages.put(Integer.toString(code), uri);
    }
    
    public void addErrorPage(final int from, final int to, final String uri) {
        this._errorPageList.add(new ErrorCodeRange(from, to, uri));
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        this._servletContext = ContextHandler.getCurrentContext();
    }
    
    static {
        LOG = Log.getLogger(ErrorPageErrorHandler.class);
    }
    
    enum PageLookupTechnique
    {
        THROWABLE, 
        STATUS_CODE, 
        GLOBAL;
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
        
        @Override
        public String toString() {
            return "from: " + this._from + ",to: " + this._to + ",uri: " + this._uri;
        }
    }
}
