// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.container.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.api.core.HttpContext;
import javax.servlet.RequestDispatcher;

public final class RequestDispatcherWrapper implements RequestDispatcher
{
    private final RequestDispatcher d;
    private final String basePath;
    private final HttpContext hc;
    private final Viewable v;
    
    public RequestDispatcherWrapper(final RequestDispatcher d, final String basePath, final HttpContext hc, final Viewable v) {
        this.d = d;
        this.basePath = basePath;
        this.hc = hc;
        this.v = v;
    }
    
    @Override
    public void forward(final ServletRequest req, final ServletResponse rsp) throws ServletException, IOException {
        final Object oldIt = req.getAttribute("it");
        final Object oldResolvingClass = req.getAttribute("resolvingClass");
        req.setAttribute("resolvingClass", this.v.getResolvingClass());
        req.setAttribute("it", this.v.getModel());
        req.setAttribute("httpContext", this.hc);
        req.setAttribute("_basePath", this.basePath);
        req.setAttribute("_request", req);
        req.setAttribute("_response", rsp);
        this.d.forward(req, rsp);
        req.setAttribute("resolvingClass", oldResolvingClass);
        req.setAttribute("it", oldIt);
    }
    
    @Override
    public void include(final ServletRequest req, final ServletResponse rsp) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }
}
