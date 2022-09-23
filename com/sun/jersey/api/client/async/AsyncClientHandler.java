// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client.async;

import java.util.concurrent.Future;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientRequest;

public interface AsyncClientHandler
{
    Future<ClientResponse> handle(final ClientRequest p0, final FutureListener<ClientResponse> p1);
}
