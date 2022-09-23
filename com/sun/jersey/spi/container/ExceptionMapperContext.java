// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container;

import javax.ws.rs.ext.ExceptionMapper;

public interface ExceptionMapperContext
{
    ExceptionMapper find(final Class<? extends Throwable> p0);
}
