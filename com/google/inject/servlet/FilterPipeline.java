// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import com.google.inject.ImplementedBy;

@ImplementedBy(DefaultFilterPipeline.class)
interface FilterPipeline
{
    void initPipeline(final ServletContext p0) throws ServletException;
    
    void destroyPipeline();
    
    void dispatch(final ServletRequest p0, final ServletResponse p1, final FilterChain p2) throws IOException, ServletException;
}
