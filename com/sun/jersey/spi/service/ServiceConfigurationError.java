// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.service;

public class ServiceConfigurationError extends Error
{
    public ServiceConfigurationError(final String msg) {
        super(msg);
    }
    
    public ServiceConfigurationError(final Throwable x) {
        super(x);
    }
}
