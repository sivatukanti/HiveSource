// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.client.proxy;

import com.sun.jersey.api.client.ClientResponse;
import java.util.concurrent.Future;
import com.sun.jersey.api.client.async.AsyncClientHandler;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientRequest;

public interface ViewProxy<T>
{
    T view(final Class<T> p0, final ClientRequest p1, final ClientHandler p2);
    
    T view(final T p0, final ClientRequest p1, final ClientHandler p2);
    
    Future<T> asyncView(final Class<T> p0, final ClientRequest p1, final AsyncClientHandler p2);
    
    Future<T> asyncView(final T p0, final ClientRequest p1, final AsyncClientHandler p2);
    
    T view(final Class<T> p0, final ClientResponse p1);
    
    T view(final T p0, final ClientResponse p1);
}
