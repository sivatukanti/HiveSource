// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.container.servlet;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.core.reflection.ReflectionHelper;
import java.io.OutputStream;
import com.sun.jersey.api.view.Viewable;
import java.net.MalformedURLException;
import com.sun.jersey.api.core.ResourceConfig;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.template.ViewProcessor;

public class JSPTemplateProcessor implements ViewProcessor<String>
{
    @Context
    private HttpContext hc;
    @Context
    private ServletContext servletContext;
    @Context
    private ThreadLocal<HttpServletRequest> requestInvoker;
    @Context
    private ThreadLocal<HttpServletResponse> responseInvoker;
    private final String basePath;
    
    public JSPTemplateProcessor(@Context final ResourceConfig resourceConfig) {
        final String path = resourceConfig.getProperties().get("com.sun.jersey.config.property.JSPTemplatesBasePath");
        if (path == null) {
            this.basePath = "";
        }
        else if (path.charAt(0) == '/') {
            this.basePath = path;
        }
        else {
            this.basePath = "/" + path;
        }
    }
    
    @Override
    public String resolve(String path) {
        if (this.servletContext == null) {
            return null;
        }
        if (this.basePath != "") {
            path = this.basePath + path;
        }
        try {
            if (this.servletContext.getResource(path) != null) {
                return path;
            }
            if (!path.endsWith(".jsp")) {
                path += ".jsp";
                if (this.servletContext.getResource(path) != null) {
                    return path;
                }
            }
        }
        catch (MalformedURLException ex) {}
        return null;
    }
    
    @Override
    public void writeTo(final String resolvedPath, final Viewable viewable, final OutputStream out) throws IOException {
        if (this.hc.isTracingEnabled()) {
            this.hc.trace(String.format("forwarding view to JSP page: \"%s\", it = %s", resolvedPath, ReflectionHelper.objectToString(viewable.getModel())));
        }
        out.flush();
        RequestDispatcher d = this.servletContext.getRequestDispatcher(resolvedPath);
        if (d == null) {
            throw new ContainerException("No request dispatcher for: " + resolvedPath);
        }
        d = new RequestDispatcherWrapper(d, this.basePath, this.hc, viewable);
        try {
            d.forward(this.requestInvoker.get(), this.responseInvoker.get());
        }
        catch (Exception e) {
            throw new ContainerException(e);
        }
    }
}
