// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.servlet;

import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.UnavailableException;
import org.mortbay.util.LazyList;
import org.mortbay.log.Log;
import org.mortbay.util.URIUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import org.mortbay.jetty.Handler;
import javax.servlet.ServletContext;
import java.util.HashMap;
import org.mortbay.jetty.handler.HandlerWrapper;
import java.util.Map;
import org.mortbay.jetty.handler.ContextHandler;
import javax.servlet.http.HttpServlet;

public class Invoker extends HttpServlet
{
    private ContextHandler _contextHandler;
    private ServletHandler _servletHandler;
    private Map.Entry _invokerEntry;
    private Map _parameters;
    private boolean _nonContextServlets;
    private boolean _verbose;
    
    public void init() {
        final ServletContext config = this.getServletContext();
        this._contextHandler = ((ContextHandler.SContext)config).getContextHandler();
        Handler handler;
        for (handler = this._contextHandler.getHandler(); handler != null && !(handler instanceof ServletHandler) && handler instanceof HandlerWrapper; handler = ((HandlerWrapper)handler).getHandler()) {}
        this._servletHandler = (ServletHandler)handler;
        final Enumeration e = this.getInitParameterNames();
        while (e.hasMoreElements()) {
            final String param = e.nextElement();
            final String value = this.getInitParameter(param);
            final String lvalue = value.toLowerCase();
            if ("nonContextServlets".equals(param)) {
                this._nonContextServlets = (value.length() > 0 && lvalue.startsWith("t"));
            }
            if ("verbose".equals(param)) {
                this._verbose = (value.length() > 0 && lvalue.startsWith("t"));
            }
            else {
                if (this._parameters == null) {
                    this._parameters = new HashMap();
                }
                this._parameters.put(param, value);
            }
        }
    }
    
    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        boolean included = false;
        String servlet_path = (String)request.getAttribute("javax.servlet.include.servlet_path");
        if (servlet_path == null) {
            servlet_path = request.getServletPath();
        }
        else {
            included = true;
        }
        String path_info = (String)request.getAttribute("javax.servlet.include.path_info");
        if (path_info == null) {
            path_info = request.getPathInfo();
        }
        String servlet = path_info;
        if (servlet == null || servlet.length() <= 1) {
            response.sendError(404);
            return;
        }
        final int i0 = (servlet.charAt(0) == '/') ? 1 : 0;
        final int i2 = servlet.indexOf(47, i0);
        servlet = ((i2 < 0) ? servlet.substring(i0) : servlet.substring(i0, i2));
        final ServletHolder[] holders = this._servletHandler.getServlets();
        ServletHolder holder = this.getHolder(holders, servlet);
        if (holder != null) {
            Log.debug("Adding servlet mapping for named servlet:" + servlet + ":" + URIUtil.addPaths(servlet_path, servlet) + "/*");
            final ServletMapping mapping = new ServletMapping();
            mapping.setServletName(servlet);
            mapping.setPathSpec(URIUtil.addPaths(servlet_path, servlet) + "/*");
            this._servletHandler.setServletMappings((ServletMapping[])LazyList.addToArray(this._servletHandler.getServletMappings(), mapping, ServletMapping.class));
        }
        else {
            if (servlet.endsWith(".class")) {
                servlet = servlet.substring(0, servlet.length() - 6);
            }
            if (servlet == null || servlet.length() == 0) {
                response.sendError(404);
                return;
            }
            synchronized (this._servletHandler) {
                this._invokerEntry = this._servletHandler.getHolderEntry(servlet_path);
                final String path = URIUtil.addPaths(servlet_path, servlet);
                final Map.Entry entry = this._servletHandler.getHolderEntry(path);
                if (entry != null && !entry.equals(this._invokerEntry)) {
                    holder = entry.getValue();
                }
                else {
                    Log.debug("Making new servlet=" + servlet + " with path=" + path + "/*");
                    holder = this._servletHandler.addServletWithMapping(servlet, path + "/*");
                    if (this._parameters != null) {
                        holder.setInitParameters(this._parameters);
                    }
                    try {
                        holder.start();
                    }
                    catch (Exception e) {
                        Log.debug(e);
                        throw new UnavailableException(e.toString());
                    }
                    if (!this._nonContextServlets) {
                        final Object s = holder.getServlet();
                        if (this._contextHandler.getClassLoader() != s.getClass().getClassLoader()) {
                            try {
                                holder.stop();
                            }
                            catch (Exception e2) {
                                Log.ignore(e2);
                            }
                            Log.warn("Dynamic servlet " + s + " not loaded from context " + request.getContextPath());
                            throw new UnavailableException("Not in context");
                        }
                    }
                    if (this._verbose) {
                        Log.debug("Dynamic load '" + servlet + "' at " + path);
                    }
                }
            }
        }
        if (holder != null) {
            holder.handle(new Request(request, included, servlet, servlet_path, path_info), response);
        }
        else {
            Log.info("Can't find holder for servlet: " + servlet);
            response.sendError(404);
        }
    }
    
    private ServletHolder getHolder(final ServletHolder[] holders, final String servlet) {
        if (holders == null) {
            return null;
        }
        ServletHolder holder = null;
        for (int i = 0; holder == null && i < holders.length; ++i) {
            if (holders[i].getName().equals(servlet)) {
                holder = holders[i];
            }
        }
        return holder;
    }
    
    class Request extends HttpServletRequestWrapper
    {
        String _servletPath;
        String _pathInfo;
        boolean _included;
        
        Request(final HttpServletRequest request, final boolean included, final String name, final String servletPath, final String pathInfo) {
            super(request);
            this._included = included;
            this._servletPath = URIUtil.addPaths(servletPath, name);
            this._pathInfo = pathInfo.substring(name.length() + 1);
            if (this._pathInfo.length() == 0) {
                this._pathInfo = null;
            }
        }
        
        public String getServletPath() {
            if (this._included) {
                return super.getServletPath();
            }
            return this._servletPath;
        }
        
        public String getPathInfo() {
            if (this._included) {
                return super.getPathInfo();
            }
            return this._pathInfo;
        }
        
        public Object getAttribute(final String name) {
            if (this._included) {
                if (name.equals("javax.servlet.include.request_uri")) {
                    return URIUtil.addPaths(URIUtil.addPaths(this.getContextPath(), this._servletPath), this._pathInfo);
                }
                if (name.equals("javax.servlet.include.path_info")) {
                    return this._pathInfo;
                }
                if (name.equals("javax.servlet.include.servlet_path")) {
                    return this._servletPath;
                }
            }
            return super.getAttribute(name);
        }
    }
}
