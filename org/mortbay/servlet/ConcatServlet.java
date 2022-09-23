// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.servlet;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

public class ConcatServlet extends HttpServlet
{
    boolean _development;
    long _lastModified;
    ServletContext _context;
    
    public void init() throws ServletException {
        this._lastModified = System.currentTimeMillis();
        this._context = this.getServletContext();
        this._development = "true".equals(this.getInitParameter("development"));
    }
    
    protected long getLastModified(final HttpServletRequest req) {
        return this._development ? -1L : this._lastModified;
    }
    
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String q = req.getQueryString();
        if (q == null) {
            resp.sendError(204);
            return;
        }
        final String[] parts = q.split("\\&");
        String type = null;
        for (int i = 0; i < parts.length; ++i) {
            final String t = this._context.getMimeType(parts[i]);
            if (t != null) {
                if (type == null) {
                    type = t;
                }
                else if (!type.equals(t)) {
                    resp.sendError(415);
                    return;
                }
            }
        }
        if (type != null) {
            resp.setContentType(type);
        }
        for (int i = 0; i < parts.length; ++i) {
            final RequestDispatcher dispatcher = this._context.getRequestDispatcher(parts[i]);
            if (dispatcher != null) {
                dispatcher.include(req, resp);
            }
        }
    }
}
