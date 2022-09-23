// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client;

public class UniformInterfaceException extends RuntimeException
{
    private final transient ClientResponse r;
    
    public UniformInterfaceException(final ClientResponse r) {
        this(r, true);
    }
    
    public UniformInterfaceException(final ClientResponse r, final boolean bufferResponseEntity) {
        super(r.toString());
        if (bufferResponseEntity) {
            r.bufferEntity();
        }
        this.r = r;
    }
    
    public UniformInterfaceException(final String message, final ClientResponse r) {
        this(message, r, true);
    }
    
    public UniformInterfaceException(final String message, final ClientResponse r, final boolean bufferResponseEntity) {
        super(message);
        if (bufferResponseEntity) {
            r.bufferEntity();
        }
        this.r = r;
    }
    
    public ClientResponse getResponse() {
        return this.r;
    }
}
