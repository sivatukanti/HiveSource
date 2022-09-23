// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client.filter;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientHandler;

public abstract class ClientFilter implements ClientHandler
{
    private ClientHandler next;
    
    final void setNext(final ClientHandler next) {
        this.next = next;
    }
    
    public final ClientHandler getNext() {
        return this.next;
    }
    
    @Override
    public abstract ClientResponse handle(final ClientRequest p0) throws ClientHandlerException;
}
