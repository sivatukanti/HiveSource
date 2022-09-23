// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.container.servlet;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponseWrapper;

class Wrapper extends HttpServletResponseWrapper
{
    private final PrintWriter pw;
    
    public Wrapper(final HttpServletResponse httpServletResponse, final PrintWriter w) {
        super(httpServletResponse);
        this.pw = w;
    }
    
    @Override
    public PrintWriter getWriter() throws IOException {
        return this.pw;
    }
}
