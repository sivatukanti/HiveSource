// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlet;

import java.io.IOException;
import org.eclipse.jetty.util.resource.Resource;
import java.util.Locale;
import org.eclipse.jetty.util.URIUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import org.eclipse.jetty.server.handler.ContextHandler;
import javax.servlet.GenericServlet;

public class JspPropertyGroupServlet extends GenericServlet
{
    private static final long serialVersionUID = 3681783214726776945L;
    public static final String NAME = "__org.eclipse.jetty.servlet.JspPropertyGroupServlet__";
    private final ServletHandler _servletHandler;
    private final ContextHandler _contextHandler;
    private ServletHolder _dftServlet;
    private ServletHolder _jspServlet;
    private boolean _starJspMapped;
    
    public JspPropertyGroupServlet(final ContextHandler context, final ServletHandler servletHandler) {
        this._contextHandler = context;
        this._servletHandler = servletHandler;
    }
    
    @Override
    public void init() throws ServletException {
        String jsp_name = "jsp";
        ServletMapping servlet_mapping = this._servletHandler.getServletMapping("*.jsp");
        if (servlet_mapping != null) {
            this._starJspMapped = true;
            final ServletMapping[] servletMappings;
            final ServletMapping[] mappings = servletMappings = this._servletHandler.getServletMappings();
            for (final ServletMapping m : servletMappings) {
                final String[] paths = m.getPathSpecs();
                if (paths != null) {
                    for (final String path : paths) {
                        if ("*.jsp".equals(path) && !"__org.eclipse.jetty.servlet.JspPropertyGroupServlet__".equals(m.getServletName())) {
                            servlet_mapping = m;
                        }
                    }
                }
            }
            jsp_name = servlet_mapping.getServletName();
        }
        this._jspServlet = this._servletHandler.getServlet(jsp_name);
        String dft_name = "default";
        final ServletMapping default_mapping = this._servletHandler.getServletMapping("/");
        if (default_mapping != null) {
            dft_name = default_mapping.getServletName();
        }
        this._dftServlet = this._servletHandler.getServlet(dft_name);
    }
    
    @Override
    public void service(final ServletRequest req, final ServletResponse res) throws ServletException, IOException {
        HttpServletRequest request = null;
        if (req instanceof HttpServletRequest) {
            request = (HttpServletRequest)req;
            String servletPath = null;
            String pathInfo = null;
            if (request.getAttribute("javax.servlet.include.request_uri") != null) {
                servletPath = (String)request.getAttribute("javax.servlet.include.servlet_path");
                pathInfo = (String)request.getAttribute("javax.servlet.include.path_info");
                if (servletPath == null) {
                    servletPath = request.getServletPath();
                    pathInfo = request.getPathInfo();
                }
            }
            else {
                servletPath = request.getServletPath();
                pathInfo = request.getPathInfo();
            }
            final String pathInContext = URIUtil.addPaths(servletPath, pathInfo);
            if (pathInContext.endsWith("/")) {
                this._dftServlet.getServlet().service(req, res);
            }
            else if (this._starJspMapped && pathInContext.toLowerCase(Locale.ENGLISH).endsWith(".jsp")) {
                this._jspServlet.getServlet().service(req, res);
            }
            else {
                final Resource resource = this._contextHandler.getResource(pathInContext);
                if (resource != null && resource.isDirectory()) {
                    this._dftServlet.getServlet().service(req, res);
                }
                else {
                    this._jspServlet.getServlet().service(req, res);
                }
            }
            return;
        }
        throw new ServletException("Request not HttpServletRequest");
    }
}
