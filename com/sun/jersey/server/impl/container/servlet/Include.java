// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.container.servlet;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.io.Writer;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletConfig;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class Include extends SimpleTagSupport
{
    private Class<?> resolvingClass;
    private String page;
    
    public void setPage(final String page) {
        this.page = page;
    }
    
    public void setResolvingClass(final Class<?> resolvingClass) {
        this.resolvingClass = resolvingClass;
    }
    
    private Object getPageObject(final String name) {
        return this.getJspContext().getAttribute(name, 1);
    }
    
    @Override
    public void doTag() throws JspException, IOException {
        final Class<?> oldResolvingClass;
        Class<?> resolvingClass = oldResolvingClass = (Class<?>)this.getJspContext().getAttribute("resolvingClass", 2);
        if (this.resolvingClass != null) {
            resolvingClass = this.resolvingClass;
        }
        final ServletConfig cfg = (ServletConfig)this.getPageObject("javax.servlet.jsp.jspConfig");
        final ServletContext sc = cfg.getServletContext();
        final String basePath = (String)this.getJspContext().getAttribute("_basePath", 2);
        for (Class c = resolvingClass; c != Object.class; c = c.getSuperclass()) {
            final String name = basePath + "/" + c.getName().replace('.', '/') + '/' + this.page;
            if (sc.getResource(name) != null) {
                final RequestDispatcher disp = sc.getRequestDispatcher(name);
                if (disp != null) {
                    this.getJspContext().setAttribute("resolvingClass", resolvingClass, 2);
                    try {
                        final HttpServletRequest request = (HttpServletRequest)this.getPageObject("javax.servlet.jsp.jspRequest");
                        disp.include(request, new Wrapper((HttpServletResponse)this.getPageObject("javax.servlet.jsp.jspResponse"), new PrintWriter(this.getJspContext().getOut())));
                    }
                    catch (ServletException e) {
                        throw new JspException(e);
                    }
                    finally {
                        this.getJspContext().setAttribute("resolvingClass", oldResolvingClass, 2);
                    }
                    return;
                }
            }
        }
        throw new JspException("Unable to find '" + this.page + "' for " + resolvingClass);
    }
}
