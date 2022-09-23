// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.FilterChain;

class FilterChainInvocation implements FilterChain
{
    private final FilterDefinition[] filterDefinitions;
    private final FilterChain proceedingChain;
    private final ManagedServletPipeline servletPipeline;
    private int index;
    
    public FilterChainInvocation(final FilterDefinition[] filterDefinitions, final ManagedServletPipeline servletPipeline, final FilterChain proceedingChain) {
        this.index = -1;
        this.filterDefinitions = filterDefinitions;
        this.servletPipeline = servletPipeline;
        this.proceedingChain = proceedingChain;
    }
    
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IOException, ServletException {
        ++this.index;
        if (this.index < this.filterDefinitions.length) {
            this.filterDefinitions[this.index].doFilter(servletRequest, servletResponse, this);
        }
        else {
            final boolean serviced = this.servletPipeline.service(servletRequest, servletResponse);
            if (!serviced) {
                this.proceedingChain.doFilter(servletRequest, servletResponse);
            }
        }
    }
}
