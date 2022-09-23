// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client;

public class ClientHandlerException extends RuntimeException
{
    public ClientHandlerException() {
    }
    
    public ClientHandlerException(final String message) {
        super(message);
    }
    
    public ClientHandlerException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ClientHandlerException(final Throwable cause) {
        super(cause);
    }
}
