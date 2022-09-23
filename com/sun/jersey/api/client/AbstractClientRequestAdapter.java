// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client;

public abstract class AbstractClientRequestAdapter implements ClientRequestAdapter
{
    private final ClientRequestAdapter cra;
    
    protected AbstractClientRequestAdapter(final ClientRequestAdapter cra) {
        this.cra = cra;
    }
    
    public ClientRequestAdapter getAdapter() {
        return this.cra;
    }
}
