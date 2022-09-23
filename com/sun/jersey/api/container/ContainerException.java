// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.container;

public class ContainerException extends RuntimeException
{
    public ContainerException() {
    }
    
    public ContainerException(final String message) {
        super(message);
    }
    
    public ContainerException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ContainerException(final Throwable cause) {
        super(cause);
    }
}
