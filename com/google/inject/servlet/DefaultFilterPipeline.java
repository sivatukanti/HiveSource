// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletContext;
import com.google.inject.Singleton;

@Singleton
class DefaultFilterPipeline implements FilterPipeline
{
    public void initPipeline(final ServletContext context) {
    }
    
    public void destroyPipeline() {
    }
    
    public void dispatch(final ServletRequest request, final ServletResponse response, final FilterChain proceedingFilterChain) throws IOException, ServletException {
        proceedingFilterChain.doFilter(request, response);
    }
}
