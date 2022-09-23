// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.http;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import javax.servlet.Filter;

public class NoCacheFilter implements Filter
{
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        final HttpServletResponse httpRes = (HttpServletResponse)res;
        httpRes.setHeader("Cache-Control", "no-cache");
        final long now = System.currentTimeMillis();
        httpRes.addDateHeader("Expires", now);
        httpRes.addDateHeader("Date", now);
        httpRes.addHeader("Pragma", "no-cache");
        chain.doFilter(req, res);
    }
    
    @Override
    public void destroy() {
    }
}
